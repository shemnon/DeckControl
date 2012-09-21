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
import com.sun.javafx.css.StyleableDoubleProperty;
import com.sun.javafx.css.StyleableProperty;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Skin;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Highly derivative of the DisplayShelf example from teh standard JavaFX samples.
 * Now you see why I went with the 3 clause BSD license.
 */
public class ShelfDeckSkin extends Region implements Skin<Deck> {

    private static List<StyleableProperty> STYLEABLES;

    private static final StyleableProperty<ShelfDeckSkin,Number> BACK_ANGLE =
            new StyleableProperty<ShelfDeckSkin,Number>("-x-back-angle",
                    SizeConverter.getInstance(), 45.0) {

                @Override
                public boolean isSettable(ShelfDeckSkin deck) {
                    return deck.backAngle == null || !deck.backAngle.isBound();
                }

                @Override
                public WritableValue<Number> getWritableValue(ShelfDeckSkin deck) {
                    return deck.backAngleProperty();
                }
            };

    private static final StyleableProperty<ShelfDeckSkin,Number> BACK_OFFSET =
            new StyleableProperty<ShelfDeckSkin,Number>("-x-back-offset",
                    SizeConverter.getInstance(), 0.25) {

                @Override
                public boolean isSettable(ShelfDeckSkin deck) {
                    return deck.backOffset == null || !deck.backOffset.isBound();
                }

                @Override
                public WritableValue<Number> getWritableValue(ShelfDeckSkin deck) {
                    return deck.backOffsetProperty();
                }
            };

    private static final StyleableProperty<ShelfDeckSkin,Number> BACK_SCALE =
            new StyleableProperty<ShelfDeckSkin,Number>("-x-back-scale",
                    SizeConverter.getInstance(), 0.7) {

                @Override
                public boolean isSettable(ShelfDeckSkin deck) {
                    return deck.backScale == null || !deck.backScale.isBound();
                }

                @Override
                public WritableValue<Number> getWritableValue(ShelfDeckSkin deck) {
                    return deck.backScaleProperty();
                }
            };

    private static final StyleableProperty<ShelfDeckSkin,Number> BACK_SPACING =
            new StyleableProperty<ShelfDeckSkin,Number>("-x-back-spacing",
                    SizeConverter.getInstance(), 0.5) {

                @Override
                public boolean isSettable(ShelfDeckSkin deck) {
                    return deck.backSpacing == null || !deck.backSpacing.isBound();
                }

                @Override
                public WritableValue<Number> getWritableValue(ShelfDeckSkin deck) {
                    return deck.backSpacingProperty();
                }
            };


    @Override
    @Deprecated
    public List<StyleableProperty> impl_getStyleableProperties() {
        if (STYLEABLES == null) {
            final List<StyleableProperty> styleables = new ArrayList<StyleableProperty>(super.impl_getStyleableProperties());
            Collections.addAll(styleables,
                    BACK_ANGLE,
                    BACK_OFFSET,
                    BACK_SCALE,
                    BACK_SPACING);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;    //To change body of overridden methods use File | Settings | File Templates.
    }


    private static final Duration DURATION = Duration.millis(500);
    private static final Interpolator INTERPOLATOR = Interpolator.EASE_BOTH;

    Deck deck;

    StyleableDoubleProperty backAngle;
    StyleableDoubleProperty backOffset;
    StyleableDoubleProperty backScale;
    StyleableDoubleProperty backSpacing;
    double maxWidth = 200;
    double maxHeight = 200;
    double deckHeight = 0;
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
        getStyleClass().add("shelfDeck");

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

    public final void setBackAngle(double value) {
        backAngleProperty().set(value);
    }
    public final double getBackAngle() {
        return backAngle == null ? 45 : backAngle.get();
    }

    public final DoubleProperty backAngleProperty() {
        if (backAngle == null) {
            backAngle = new StyleableDoubleProperty(45.0) {

                @Override
                protected void invalidated() {
                    styleablePropertyInvalidated();
                }

                @Override
                public StyleableProperty getStyleableProperty() {
                    return BACK_ANGLE;
                }

                @Override
                public Object getBean() {
                    return ShelfDeckSkin.this;
                }

                @Override
                public String getName() {
                    return "backAngle";
                }
            };
        }
        return backAngle;
    }

    private void styleablePropertyInvalidated() {
        calculateChildSizes();
        update(false);
    }

    public final void setBackOffset(double value) {
        backOffsetProperty().set(value);
    }
    public final double getBackOffset() {
        return backOffset == null ? 0.5 : backOffset.get();
    }

    public final DoubleProperty backOffsetProperty() {
        if (backOffset == null) {
            backOffset = new StyleableDoubleProperty(0.5) {

                @Override
                protected void invalidated() {
                    styleablePropertyInvalidated();
                }

                @Override
                public StyleableProperty getStyleableProperty() {
                    return BACK_OFFSET;
                }

                @Override
                public Object getBean() {
                    return ShelfDeckSkin.this;
                }

                @Override
                public String getName() {
                    return "backOffset";
                }
            };
        }
        return backOffset;
    }

    public final void setBackScale(double value) {
        backScaleProperty().set(value);
    }
    public final double getBackScale() {
        return backScale == null ? 0.7 : backScale.get();
    }

    public final DoubleProperty backScaleProperty() {
        if (backScale == null) {
            backScale = new StyleableDoubleProperty(0.7) {

                @Override
                protected void invalidated() {
                    styleablePropertyInvalidated();
                }

                @Override
                public StyleableProperty getStyleableProperty() {
                    return BACK_SCALE;
                }

                @Override
                public Object getBean() {
                    return ShelfDeckSkin.this;
                }

                @Override
                public String getName() {
                    return "backScale";
                }
            };
        }
        return backScale;
    }

    public final void setBackSpacing(double value) {
        backSpacingProperty().set(value);
    }
    public final double getBackSpacing() {
        return backSpacing == null ? 0.25: backSpacing.get();
    }

    public final DoubleProperty backSpacingProperty() {
        if (backSpacing == null) {
            backSpacing = new StyleableDoubleProperty(0.25) {

                @Override
                protected void invalidated() {
                    styleablePropertyInvalidated();
                }

                @Override
                public StyleableProperty getStyleableProperty() {
                    return BACK_SPACING;
                }

                @Override
                public Object getBean() {
                    return ShelfDeckSkin.this;
                }

                @Override
                public String getName() {
                    return "backSpacing";
                }
            };
        }
        return backSpacing;
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
        super.layoutChildren();
        // keep centered centered
        calculateChildSizes();
        centered.setLayoutY((getHeight() - (maxHeight * 1.25)) / 2);
        centered.setLayoutX((getWidth() - maxWidth) / 2);
    }

    private void calculateChildSizes() {
        double oldMaxWidth = maxWidth;
        double oldMaxHeight = maxHeight;
        maxWidth = 0;
        maxHeight = 0;
        for (Item item : items) {
            item.layout();
            maxWidth = Math.max(item.prefWidth(-1), maxWidth);
            maxHeight = Math.max(item.prefHeight(-1), maxHeight);
        }
        if (maxWidth != oldMaxWidth || maxHeight != oldMaxHeight) {
            update(false);
        }
    }

    @Override protected double computePrefHeight(double width) {
        calculateChildSizes();
        final Insets insets = getInsets();
        return insets.getTop() + maxHeight + insets.getBottom();
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
        double spacing = maxWidth * getBackSpacing();
        double rightOffset = maxWidth * getBackOffset();
        double leftOffset = -rightOffset;
        double scale = getBackScale();
        double angle = getBackAngle();
        double leftAngle = 90 - angle;
        double rightAngle = 90 + angle;
        final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
        for (int i = 0; i < left.getChildren().size(); i++) {
            final Item it = items.get(i);
            double newX = -left.getChildren().size() * spacing + spacing * i + leftOffset + it.left;
            double newY = it.top;// * scaleSmall  + deckHeight * (1- scaleSmall) / 2;
            keyFrames.add(new KeyFrame(DURATION,
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.translateYProperty(), newY, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), scale, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), scale, INTERPOLATOR),
                    new KeyValue(it.angle, leftAngle, INTERPOLATOR)));
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
                    new KeyValue(it.scaleXProperty(), scale, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), scale, INTERPOLATOR),
                    new KeyValue(it.angle, rightAngle, INTERPOLATOR)));
        }
        if (angle < 0) {
            FXCollections.reverse(left.getChildren());
            FXCollections.reverse(right.getChildren());
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
            Bounds nBounds = item.getLayoutBounds();
            width = Math.max(width, nBounds.getWidth());
            double nBaseline = item.getBaselineOffset();
            double nHeight = nBounds.getHeight();
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

    public Item(Node node) {
        this.node = node;

        // create content
        setupTransform();
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
            itemHeight = node.prefHeight(-1)   * (1.0);
        } else {
            itemHeight = h  * (1.0);
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
