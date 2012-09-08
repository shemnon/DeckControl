/*
 * Copyright (c) 2008, 2011 Oracle and/or its affiliates.
 * Copyright (c) 2012 Danno Ferrin.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.shemnon.deckcontrol.skin;

import com.github.shemnon.deckcontrol.Deck;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Skin;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Highly derivative of the DisplayShelf example from teh standard JavaFX samples.
 * Now you see why I went with the 3 clause BSD license.
 */
public class ShelfDeckSkin extends Region implements Skin<Deck> {

    private static final Duration DURATION = Duration.millis(500);
    private static final Interpolator INTERPOLATOR = Interpolator.EASE_BOTH;

    Deck deck;

    double spacing = 50;
    double leftOffset = -110;
    double rightOffset = 110;
    double deckHeight = 0;
    double scaleSmall = 0.7;
    double reflectionSize = 0.25;
    private List<Item> items;
    private Group centered = new Group();
    private Group left = new Group();
    private Group center = new Group();
    private Group right = new Group();
    private int centerIndex = 0;
    private Timeline timeline;
    private InvalidationListener shiftListener;
    private InvalidationListener positionTrigger;

    public ShelfDeckSkin(Deck deck) {
        this.deck = deck;
        getStyleClass().add("displayshelf");

        // create items
        createItems();
        centerIndex = deck.getPrimaryNodeIndex();
        addListeners();
        // create content
        centered.getChildren().addAll(left, right, center);
        getChildren().addAll(centered);
        // update
        positionDeck();
        layoutChildren();
        update(false);
    }

    private void createItems() {
        List<Node> nodes = deck.getNodes();
        items = new ArrayList<Item>(nodes.size());
        for (Node n : nodes) {
            n.setVisible(true);
            items.add(new Item(n, reflectionSize));
        }
    }

    @Override
    public Deck getSkinnable() {
        return deck;
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void dispose() {
        for (Item item : items) {
            item.dispose();
        }
        deck.primaryNodeIndexProperty().removeListener(shiftListener);
        deck.nodesProperty().removeListener(positionTrigger);
        deck.alignmentProperty().removeListener(positionTrigger);

    }

    protected void addListeners() {
        shiftListener = new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                shift(deck.getPrimaryNodeIndex() - centerIndex);
            }
        };

        deck.primaryNodeIndexProperty().addListener(shiftListener);

        positionTrigger = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                createItems();
                positionDeck();
                update(false);
            }
        };
        deck.nodesProperty().addListener(positionTrigger);
        deck.alignmentProperty().addListener(positionTrigger);

    }

    @Override
    protected void layoutChildren() {
        // keep centered centered
        double maxWidth = 0;
        double maxHeight = 0;
        for (Item item : items) {
            maxWidth = Math.max(item.prefWidth(-1), maxWidth);
            maxHeight = Math.max(item.prefHeight(-1), maxHeight);
        }
        centered.setLayoutY((getHeight()  - (maxHeight * 1.25)) / 2);
        centered.setLayoutX((getWidth() - maxWidth) / 2);
        setPrefSize(maxWidth * 2, maxHeight * 1.25);
        setMinSize(maxWidth, maxHeight * 1.25);
        leftOffset = -maxWidth / 2;
        rightOffset = maxWidth / 2;
        spacing = maxWidth / 4;
    }

    private void update(boolean animate) {
        // move items to new homes in groups
        left.getChildren().clear();
        center.getChildren().clear();
        right.getChildren().clear();
        for (int i = 0; i < centerIndex; i++) {
            left.getChildren().add(items.get(i));
        }
        center.getChildren().add(items.get(centerIndex));
        for (int i = items.size() - 1; i > centerIndex; i--) {
            right.getChildren().add(items.get(i));
        }
        // stop old timeline if there is one running
        if (timeline != null) timeline.stop();
        // create timeline to animate to new positions
        timeline = new Timeline();
        // add keyframes for left items
        final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
        for (int i = 0; i < left.getChildren().size(); i++) {
            final Item it = items.get(i);
            double newX = -left.getChildren().size() * spacing + spacing * i + leftOffset + it.left;
            double newY = it.top;// * scaleSmall  + deckHeight * (1- scaleSmall) / 2;
            keyFrames.add(new KeyFrame(DURATION,
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.translateYProperty(), newY, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), scaleSmall, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), scaleSmall, INTERPOLATOR),
                    new KeyValue(it.angle, 45.0, INTERPOLATOR)));
        }
        // add keyframe for center item
        final Item centerItem = items.get(centerIndex);
        keyFrames.add(new KeyFrame(DURATION,
                new KeyValue(centerItem.translateXProperty(), centerItem.left, INTERPOLATOR),
                new KeyValue(centerItem.translateYProperty(), centerItem.top, INTERPOLATOR),
                new KeyValue(centerItem.scaleXProperty(), 1.0, INTERPOLATOR),
                new KeyValue(centerItem.scaleYProperty(), 1.0, INTERPOLATOR),
                new KeyValue(centerItem.angle, 90.0, INTERPOLATOR)));
        // add keyframes for right items
        for (int i = 0; i < right.getChildren().size(); i++) {
            final Item it = items.get(items.size() - i - 1);
            final double newX = right.getChildren().size() * spacing - spacing * i + rightOffset + it.left;
            double newY = it.top;// * scaleSmall * 2;
            keyFrames.add(new KeyFrame(DURATION,
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.translateYProperty(), newY, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), scaleSmall, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), scaleSmall, INTERPOLATOR),
                    new KeyValue(it.angle, 135.0, INTERPOLATOR)));
        }
        // play animation
        timeline.playFrom(animate ? Duration.ZERO : DURATION);
    }

    public void shift(int shiftAmount) {
        if (centerIndex <= 0 && shiftAmount < 0) return;
        if (centerIndex >= items.size() - 1 && shiftAmount > 0) return;
        centerIndex += shiftAmount;
        update(true);
    }

    protected void positionDeck() {
        Pos pos = deck.getAlignment();
        double top = 0;
        double bottom = 0;
        double height = 0;
        double width = 0;
        for (Item item : items) {
            //Bounds nBounds = n.getBoundsInParent();
            item.layout();
            width = Math.max(width, item.prefWidth(-1));
            double nBaseline = item.getBaselineOffset();
            double nHeight = item.prefHeight(-1);
            height = Math.max(height, nHeight);
            top = Math.max(top, nBaseline);
            bottom = Math.max(bottom, nHeight - nBaseline);
        }

         deckHeight = height;

        if (pos == null) {
            for (Item item : items) {
                item.fill(width, height);
                item.layoutChildren();
                item.setTranslateX(0);
                item.setTranslateY(0);
            }
            return;
        }

        for (Item item : items) {
            item.fit();
            double x = 0;
            double y = 0;
            double w = item.prefWidth(-1);
            double h = item.prefHeight(-1);
            switch (pos.getHpos()) {
                case CENTER:
                    x = (width - w) / 2;
                    break;
                case RIGHT:
                    x = width - w;
                    break;
            }

            switch (pos.getVpos()) {
                case BASELINE:
                    y = top - item.getBaselineOffset();
                    break;
                case CENTER:
                    y = (height - h) / 2;
                    break;
                case BOTTOM:
                    y = height - h;
                    break;
            }

            item.left = x;
            item.top = y;
        }
    }

}

class Item extends Parent {

    Node node;

    double reflectionSize;
    double itemWidth;
    double itemHeight;
    double radiusH;
    double back;

    double top;
    double left;
    double w;
    double h;

    private PerspectiveTransform transform = new PerspectiveTransform();
    /**
     * Angle Non-Observable Property
     */
    public final DoubleProperty angle = new SimpleDoubleProperty(45.0);
    private InvalidationListener resizeListener;

    public Item(Node node, double reflectionSize) {
        this.node = node;
        this.reflectionSize = reflectionSize;

        // create content
        setupTransform();
        Reflection reflection = new Reflection();
        reflection.setFraction(reflectionSize);
        transform.setInput(reflection);
        setEffect(transform);
        getChildren().addAll(node);
        angle.set(45.0);
        angle.addListener(new InvalidationListener() {
            public void invalidated(Observable vm) {
                updateAngle();
            }
        });
        addListeners();
    }

    @Override
    public double getBaselineOffset() {
        return node.getBaselineOffset();
    }

    private void updateAngle() {
        // calculate new transform
        double lx = (radiusH - Math.sin(Math.toRadians(angle.get())) * radiusH);
        double rx = (radiusH + Math.sin(Math.toRadians(angle.get())) * radiusH);
        double uly = (-Math.cos(Math.toRadians(angle.get())) * back);
        double ury = -uly;
        transform.setUlx(lx);
        transform.setUly(uly);
        transform.setUrx(rx);
        transform.setUry(ury);
        transform.setLrx(rx);
        transform.setLry(itemHeight + uly);
        transform.setLlx(lx);
        transform.setLly(itemHeight + ury);
    }

    public void addListeners() {
        resizeListener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                setupTransform();
            }
        };

        node.boundsInParentProperty().addListener(resizeListener);
    }

    private void setupTransform() {
        if (w == 0 || h == 0) {
            itemWidth = node.prefWidth(-1);
            itemHeight = node.prefHeight(-1)   * (1.0 + reflectionSize);
        } else {
            itemHeight = h  * (1.0 + reflectionSize);
            itemWidth = w ;
        }
        radiusH = itemWidth / 2;
        back = itemWidth / 10;
        updateAngle();
    }

    public void dispose() {
        node.boundsInParentProperty().removeListener(resizeListener);
    }

    @Override
    protected void layoutChildren() {
                super.layoutChildren();
        if (node.isManaged() && node.isResizable()) {
            if (w != 0 && h != 0) {
                node.resize(w, h);
                setupTransform();
            }
        }
    }

    public void fill(double w, double h) {
        this.w = w;
        this.h = h;
    }

    public void fit() {
        w = 0;
        h = 0;
    }
}
