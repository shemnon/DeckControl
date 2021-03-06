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

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Created with IntelliJ IDEA.
 * User: Danno Ferrin
 * Date: 30 Aug 2012
 * Time: 6:36 PM
 */
public class AbstractDeckSkin  extends StackPane implements Skin<Deck> {
    protected Node currentNode;
    private Pane stack;
    protected Deck deck;
    private InvalidationListener positionTrigger;

    public AbstractDeckSkin(final Deck deck) {
        this.deck = deck;
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

        Pos pos = deck.getAlignment();
        double top = 0;
        double bottom = 0;
        double height = 0;
        double width = 0;
        double deckPrefWidth = deck.getPrefWidth();
        double deckPrefHeight = deck.getPrefHeight();
        if (pos == null) {
            stack = new StackPane();
            stack.getChildren().setAll(deck.getNodes());
            stack.layout(); // ??
            getChildren().setAll(stack);
            for (Node n : stack.getChildren()) {
                if (n == null) continue;

                n.setVisible(n == currentNode);
            }
            return;
        }
        stack = new Pane();
        stack.getChildren().setAll(deck.getNodes());
        stack.layout(); // ??
        getChildren().setAll(stack);
        for (Node n : stack.getChildren()) {
            if (n == null) continue;

            n.setVisible(n == currentNode);
            //n.setManaged(false);

            Bounds nBounds = n.getBoundsInParent();
            width = Math.max(width, nBounds.getWidth());
            double nBaseline = n.getBaselineOffset();
            double nHeight = nBounds.getHeight();
            height = Math.max(height, nHeight);
            top = Math.max(top, nBaseline);
            bottom = Math.max(bottom, nHeight - nBaseline);
        }

        for (Node n : stack.getChildren()) {
            if (n == null) continue;

            double x = 0;
            double y = 0;
            double w = n.prefWidth(deckPrefHeight);
            double h = n.prefHeight(deckPrefWidth);
            switch (pos.getHpos()) {
                case CENTER:
                    x = (width - w)/2;
                    break;
                case RIGHT:
                    x = width - w;
                    break;
            }

            switch (pos.getVpos()) {
                case BASELINE:
                    y = top - n.getBaselineOffset();
                    break;
                case CENTER:
                    y = (height - h)/2;
                    break;
                case BOTTOM:
                    y = height - h;
                    break;
            }

            n.relocate(x, y);

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

    public void dispose() {
        deck.nodesProperty().removeListener(positionTrigger);
        deck.alignmentProperty().removeListener(positionTrigger);
        currentNode = null;
        getChildren().clear();
        stack = null;
        deck = null;
    }

    private void addListeners() {
        // clip to normal bounds, for animations
        final Rectangle clip = new Rectangle();

        setClip(clip);

        layoutBoundsProperty().addListener(observable -> {
            positionDeck();
            clip.setWidth(getLayoutBounds().getWidth());
            clip.setHeight(getLayoutBounds().getHeight());
        });

        positionTrigger = observable -> positionDeck();
        deck.nodesProperty().addListener(positionTrigger);
        deck.alignmentProperty().addListener(positionTrigger);
    }

}
