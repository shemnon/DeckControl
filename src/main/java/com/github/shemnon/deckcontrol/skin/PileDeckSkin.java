/*
 * Copyright (c) 2012, Danno Ferrin
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions are met:
 *       * Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *       * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *       * Neither the name of Danno Ferrin nor the
 *         names of contributors may be used to endorse or promote products
 *         derived from this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.shemnon.deckcontrol.skin;

import com.github.shemnon.deckcontrol.Deck;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Danno Ferrin
 * Date: 29 Aug 2012
 * Time: 6:39 PM
 */
public class PileDeckSkin extends AbstractDeckSkin {

    private static List<CssMetaData<? extends Styleable,?>> STYLEABLES;

    private static final CssMetaData<PileDeckSkin,Number> SPREAD_ANGLE =
            new CssMetaData<PileDeckSkin,Number>("-x-spread-angle",
                    SizeConverter.getInstance(), 45.0) {

                @Override
                public boolean isSettable(PileDeckSkin deck) {
                    return deck.spreadAngle == null || !deck.spreadAngle.isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(PileDeckSkin deck) {
                    return deck.spreadAngleProperty();
                }
            };

    @Override
    @Deprecated
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        if (STYLEABLES == null) {
            final List<CssMetaData<? extends Styleable,?>> styleables = new ArrayList<>(super.getCssMetaData());
            Collections.addAll(styleables,
                    SPREAD_ANGLE);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;    //To change body of overridden methods use File | Settings | File Templates.
    }


    private ChangeListener<Number> selectedIndexListener;
    private int shownIndex;
    SequentialTransition sequentialTransition;

    double maxWidth, maxHeight;

    StyleableDoubleProperty spreadAngle;

    public PileDeckSkin(final Deck deck) {
        super(deck);
        getStyleClass().add("pileSkin");
        shownIndex = deck.getPrimaryNodeIndex();
        sequentialTransition = new SequentialTransition();
        sequentialTransition.setOnFinished(actionEvent -> sequentialTransition.getChildren().clear());
        addListeners();
    }

    @Override
    public void dispose() {
        deck.primaryNodeIndexProperty().removeListener(selectedIndexListener);
        for (Node n : deck.getNodes()) {
            n.setRotate(0);
            n.setScaleX(1.0);
            n.setScaleY(1.0);
            n.setScaleZ(1.0);
            n.setOpacity(1.0);
            n.setVisible(true);
        }
        super.dispose();
    }

    public final void setSpreadAngle(double value) {
        spreadAngleProperty().set(value);
    }
    public final double getSpreadAngle() {
        return spreadAngle == null ? 45.0 : spreadAngle.get();
    }

    public final StyleableDoubleProperty spreadAngleProperty() {
        if (spreadAngle == null) {
            spreadAngle = new StyleableDoubleProperty(45.0) {

                @Override
                protected void invalidated() {
                    styleablePropertyInvalidated();
                }

                @Override
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return SPREAD_ANGLE;
                }

                @Override
                public Object getBean() {
                    return PileDeckSkin.this;
                }

                @Override
                public String getName() {
                    return "spreadAngle";
                }
            };
        }
        return spreadAngle;
    }

    private void styleablePropertyInvalidated() {
        positionDeck();
        updateNewNode();
    }



    protected void updateNewNode() {
        int lastIndex = shownIndex;
        shownIndex = deck.getPrimaryNodeIndex();

        List<Animation> transitions = new ArrayList<>(Math.abs(lastIndex - shownIndex));
        Duration duration = Duration.millis(250);
        if (lastIndex < shownIndex) {
            // drop cards in
            for (int i = lastIndex+1; i <= shownIndex; i++) {
                Node node = deck.getNodes().get(i);
                node.setOpacity(0.0);
                node.setScaleX(4.0);
                node.setScaleY(4.0);
                node.setScaleZ(4.0);
                node.setVisible(true);
                transitions.add(
                        new ParallelTransition(
                                FadeTransitionBuilder.create()
                                    .toValue(1.0)
                                    .node(node)
                                    .duration(duration)
                                    .build(),
                                ScaleTransitionBuilder.create()
                                    .fromX(4.0).fromY(4.0).fromZ(4.0)
                                    .toX(1.0).toY(1.0).toZ(1.0)
                                    .node(node)
                                    .duration(duration)
                                    .build()
                        )
                );

            }
        } else if (lastIndex > shownIndex) {
            // flop cards out
            for (int i = shownIndex+1; i <= lastIndex; i++) {
                final Node node = deck.getNodes().get(i);
                node.setOpacity(1.0);
                node.setScaleX(1.0);
                node.setScaleY(1.0);
                node.setScaleZ(1.0);
                node.setVisible(true);
                transitions.add(
                        new ParallelTransition(
                                FadeTransitionBuilder.create()
                                        .fromValue(1.0)
                                        .toValue(0.0)
                                        .onFinished(actionEvent -> node.setVisible(false))
                                        .node(node)
                                        .duration(duration)
                                        .build(),
                                ScaleTransitionBuilder.create()
                                        .fromX(1.0).fromY(1.0).fromZ(1.0)
                                        .toX(4.0).toY(4.0).toZ(4.0)
                                        .node(node)
                                        .duration(duration)
                                        .build()
                        )
                );
            }
        }

        if (sequentialTransition.getStatus() == Animation.Status.RUNNING) {
            Duration time = sequentialTransition.getCurrentTime();
            sequentialTransition.stop();
            sequentialTransition.getChildren().addAll(transitions);
            sequentialTransition.playFrom(time);
        } else {
            sequentialTransition.getChildren().setAll(transitions);
            sequentialTransition.playFrom(Duration.ZERO);
        }
    }

    @Override
    protected void positionDeck() {
        List<Node> nodes = deck.getNodes();
        int size = nodes.size();
        int pos = deck.getPrimaryNodeIndex();
        int i = size;
        while (i > 0) {
            double sobol = SobolSequences.sobel(i--);
            nodes.get(i).setRotate((sobol-0.5)*getSpreadAngle());
        }

        super.positionDeck();
        FXCollections.reverse(getChildren());

        // customize visibility
        for (i = size-1; i > pos; i--) {
            nodes.get(i).setVisible(false);
        }
        for (i = pos; i >= 0; i--) {
            nodes.get(i).setVisible(true);
        }
    }

    public void addListeners() {
        selectedIndexListener = (observableValue, oldNumber, newNumber) -> updateNewNode();
        deck.primaryNodeIndexProperty().addListener(selectedIndexListener);
    }

}
