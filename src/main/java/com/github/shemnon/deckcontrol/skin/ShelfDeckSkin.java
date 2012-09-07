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
    private static final double SCALE_SMALL = 0.7;
    private List<Item> items;
    private Group centered = new Group();
    private Group left = new Group();
    private Group center = new Group();
    private Group right = new Group();
    private int centerIndex = 0;
    private Timeline timeline;
    private InvalidationListener shiftListener;

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
        layoutChildren();
        update();
    }

    private void createItems() {
        List<Node> nodes = deck.getNodes();
        items = new ArrayList<Item>(nodes.size());
        for (Node n : nodes) {
            n.setVisible(true);
            items.add(new Item(n));
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
    }

    protected void addListeners() {
        shiftListener = new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                shift(deck.getPrimaryNodeIndex() - centerIndex);
            }
        };

        deck.primaryNodeIndexProperty().addListener(shiftListener);
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
        centered.setLayoutY((getHeight() - maxHeight) / 2);
        centered.setLayoutX((getWidth() - maxWidth) / 2);
        setPrefSize(maxWidth * 2, maxHeight * 1.5);
        leftOffset = -maxWidth / 2;
        rightOffset = maxWidth / 2;
        spacing = maxWidth / 4;
    }

    private void update() {
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
            double newX = -left.getChildren().size() * spacing + spacing * i + leftOffset;
            keyFrames.add(new KeyFrame(DURATION,
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.angle, 45.0, INTERPOLATOR)));
        }
        // add keyframe for center item
        final Item centerItem = items.get(centerIndex);
        keyFrames.add(new KeyFrame(DURATION,
                new KeyValue(centerItem.translateXProperty(), 0, INTERPOLATOR),
                new KeyValue(centerItem.scaleXProperty(), 1.0, INTERPOLATOR),
                new KeyValue(centerItem.scaleYProperty(), 1.0, INTERPOLATOR),
                new KeyValue(centerItem.angle, 90.0, INTERPOLATOR)));
        // add keyframes for right items
        for (int i = 0; i < right.getChildren().size(); i++) {
            final Item it = items.get(items.size() - i - 1);
            final double newX = right.getChildren().size() * spacing - spacing * i + rightOffset;
            keyFrames.add(new KeyFrame(DURATION,
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.angle, 135.0, INTERPOLATOR)));
        }
        // play animation
        timeline.play();
    }

    public void shift(int shiftAmount) {
        if (centerIndex <= 0 && shiftAmount < 0) return;
        if (centerIndex >= items.size() - 1 && shiftAmount > 0) return;
        centerIndex += shiftAmount;
        update();
    }
}

class Item extends Parent {

    Node node;

    double reflectionSize = 0.25;
    double itemWidth;
    double itemHeight;
    double radiusH;
    double back;

    private PerspectiveTransform transform = new PerspectiveTransform();
    /**
     * Angle Non-Observable Property
     */
    public final DoubleProperty angle = new SimpleDoubleProperty(45.0);
    private InvalidationListener resizeListener;

    public Item(Node node) {
        this.node = node;

        // create content
        setupTransform();
        Reflection reflection = new Reflection();
        reflection.setFraction(reflectionSize);
        node.setEffect(reflection);
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

    private void updateAngle() {
        // calculate new transform
        double lx = (radiusH - Math.sin(Math.toRadians(angle.get())) * radiusH - 1);
        double rx = (radiusH + Math.sin(Math.toRadians(angle.get())) * radiusH + 1);
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
        itemWidth = node.prefWidth(-1);
        itemHeight = node.prefHeight(-1) * (1.0 + reflectionSize);
        radiusH = itemWidth / 2;
        back = itemWidth / 10;
        updateAngle();
    }

    public void dispose() {
        node.boundsInParentProperty().removeListener(resizeListener);
    }
}
