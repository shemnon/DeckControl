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
import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Created with IntelliJ IDEA.
 * User: Danno Ferrin
 * Date: 28 Aug 2012
 * Time: 6:34 AM
 */
public class ShiftDeckSkin implements Skin<Deck> {

    protected Node currentNode;
    int shownIndex = 0;

    protected StackPane stack;

    SequentialTransition sequentialTransition;

    Deck deck;
    private ChangeListener<ObservableList<Node>> nodesListener;
    private ChangeListener<Number> primaryIndexListener;

    public ShiftDeckSkin(final Deck deck) {
        this.deck = deck;
        stack = new StackPane();
        stack.getChildren().setAll(deck.getNodes());
        sequentialTransition = new SequentialTransition();
        sequentialTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sequentialTransition.getChildren().clear();
            }
        });
        lockDeckValues();
        addListeners();
    }


    @Override
    public Deck getSkinnable() {
        return deck;
    }

    @Override
    public Node getNode() {
        return stack;
    }

    @Override
    public void dispose() {
        deck.nodes().removeListener(nodesListener);
        deck.primaryNodeIndex().removeListener(primaryIndexListener);
        currentNode = null;
        stack = null;
        deck = null;
    }

    protected void lockDeckValues() {
        shownIndex = deck.getPrimaryNodeIndex();
        if (shownIndex >= 0 && shownIndex < deck.getNodes().size()) {
            currentNode = deck.getNodes().get(shownIndex);
        } else {
            currentNode = null;
        }
        for (Node n : stack.getChildren()) {
            if (n != null) {
                n.setVisible(n == currentNode);
            }
        }
    }

    protected void shiftNewValue() {
        final Node oldNode = currentNode;
        int lastIndex = shownIndex;
        shownIndex = deck.getPrimaryNodeIndex();
        if (shownIndex >= 0 && shownIndex < deck.getNodes().size()) {
            currentNode = deck.getNodes().get(shownIndex);
        } else {
            currentNode = null;
        }

        EventHandler<ActionEvent> hideOldNode = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (oldNode != null) {
                    oldNode.setVisible(false);
                    oldNode.setTranslateX(0);
                }
            }
        };
        ParallelTransition transition = new ParallelTransition();
        if (oldNode == currentNode) {
            return; // nothing to do
        } else if (lastIndex < shownIndex) {
            if (oldNode != null) {
                // slide last slide to left
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                                .node(oldNode)
                                .fromX(0)
                                .toX(-deck.getWidth())
                                .duration(Duration.seconds(1))
                                .onFinished(hideOldNode)
                                .build());
            }
            if (currentNode != null) {
                // slide next slide from right
                currentNode.setTranslateX(deck.getWidth());
                currentNode.setVisible(true);
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                            .node(currentNode)
                            .fromX(deck.getWidth())
                            .toX(0)
                            .duration(Duration.seconds(1))
                            .build());
            }
        } else {
            if (oldNode != null) {
                // slide last slide to right
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                            .node(oldNode)
                            .fromX(0)
                            .toX(deck.getWidth())
                            .duration(Duration.seconds(1))
                            .onFinished(hideOldNode)
                            .build());
            }
            if (currentNode != null) {
                // slide next slide from left
                currentNode.setTranslateX(-deck.getWidth());
                currentNode.setVisible(true);
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                            .node(currentNode)
                            .fromX(-deck.getWidth())
                            .toX(0)
                            .duration(Duration.seconds(1))
                            .build());
            }
        }
        if (sequentialTransition.getStatus() == Animation.Status.RUNNING) {
            Duration time = sequentialTransition.getCurrentTime();
            sequentialTransition.stop();
            sequentialTransition.getChildren().add(transition);
            sequentialTransition.playFrom(time);
        } else {
            sequentialTransition.getChildren().setAll(transition);
            sequentialTransition.playFrom(Duration.ZERO);
        }
    }


    public void addListeners() {
        // clip to normal bounds, for animations
        final Rectangle clip = new Rectangle();

        stack.setClip(clip);

        stack.layoutBoundsProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                clip.setWidth(stack.getLayoutBounds().getWidth());
                clip.setHeight(stack.getLayoutBounds().getHeight());
            }
        });

        nodesListener = new ChangeListener<ObservableList<Node>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Node>> observableValue, ObservableList<Node> oldNodes, ObservableList<Node> newNodes) {
                stack.getChildren().setAll(newNodes);
                lockDeckValues();
            }
        };
        deck.nodes().addListener(nodesListener);

        primaryIndexListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                shiftNewValue();
            }
        };
        deck.primaryNodeIndex().addListener(primaryIndexListener);
    }

}
