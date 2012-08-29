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
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
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
 * Date: 27 Aug 2012
 * Time: 6:34 PM
 */
public class SlideDeckSkin  implements Skin<Deck> {

    protected Node oldNode;
    protected Node currentNode;
    int shownIndex = 0;

    TranslateTransition translateTransition;

    protected StackPane stack;

    Deck deck;
    private ChangeListener<ObservableList<Node>> nodesListener;
    private ChangeListener<Number> selectedIndexListener;

    public SlideDeckSkin (final Deck deck) {
        this.deck = deck;
        stack = new StackPane();
        stack.getChildren().setAll(deck.getNodes());
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
        deck.primaryNodeIndex().removeListener(selectedIndexListener);
        oldNode = null;
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

    protected void slideNewValue() {
        oldNode = currentNode;
        int lastIndex = shownIndex;
        shownIndex = deck.getPrimaryNodeIndex();
        if (shownIndex >= 0 && shownIndex < deck.getNodes().size()) {
            currentNode = deck.getNodes().get(shownIndex);
        } else {
            currentNode = null;
        }

        EventHandler<ActionEvent> hideOldNode = new EventHandler<ActionEvent>() {
            Node hideNode = oldNode;

            @Override
            public void handle(ActionEvent actionEvent) {
                if (hideNode != null) {
                    hideNode.setVisible(false);
                    hideNode.setTranslateX(0);
                }
            }
        };
        if (oldNode == currentNode) {
            return; // nothing to do
        } else if (lastIndex < shownIndex) {
            if (currentNode != null) {
                // slide next slide from right
                translateTransition = TranslateTransitionBuilder.create()
                        .node(currentNode)
                        .fromX(deck.getWidth())
                        .toX(0)
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
            } else if (oldNode != null) {
                // slide last slide to left
                translateTransition = TranslateTransitionBuilder.create()
                        .node(oldNode)
                        .fromX(0)
                        .toX(-deck.getWidth())
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
            }
        } else {
            if (oldNode != null) {
                // slide old slide to right
                translateTransition = TranslateTransitionBuilder.create()
                        .node(oldNode)
                        .fromX(0)
                        .toX(deck.getWidth())
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
                currentNode.setTranslateX(0);
            } else if (currentNode != null) {
                // slide current slide from left
                translateTransition = TranslateTransitionBuilder.create()
                        .node(currentNode)
                        .fromX(-deck.getWidth())
                        .toX(0)
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
            }
        }
        if (currentNode != null) {
            currentNode.setVisible(true);
        }
        translateTransition.play();
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

        selectedIndexListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                slideNewValue();
            }
        };
        deck.primaryNodeIndex().addListener(selectedIndexListener);
    }

}
