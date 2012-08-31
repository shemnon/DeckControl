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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Created with IntelliJ IDEA.
 * User: Danno Ferrin
 * Date: 30 Aug 2012
 * Time: 6:36 PM
 */
public class AbstractDeckSkin implements Skin<Deck> {
    protected Node currentNode;
    private Pane stack;
    protected Deck deck;
    private ChangeListener<ObservableList<Node>> nodesListener;

    public AbstractDeckSkin(final Deck deck) {
        this.deck = deck;
        stack = new Pane();
        stack.getChildren().setAll(deck.getNodes());
        positionDeck();
        addListeners();
    }

    protected void positionDeck() {
        int shownIndex = deck.getPrimaryNodeIndex();
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


    @Override
    public Deck getSkinnable() {
        return deck;
    }

    @Override
    public Node getNode() {
        return stack;
    }

    public void dispose() {
        deck.nodes().removeListener(nodesListener);
        currentNode = null;
        stack = null;
        deck = null;
    }

    private void addListeners() {
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
                positionDeck();
            }
        };
        deck.nodes().addListener(nodesListener);
    }

}
