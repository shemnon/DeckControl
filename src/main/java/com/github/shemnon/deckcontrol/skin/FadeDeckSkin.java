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
import javafx.animation.FadeTransitionBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Created with IntelliJ IDEA.
 * User: Danno Ferrin
 * Date: 29 Aug 2012
 * Time: 6:39 PM
 */
public class FadeDeckSkin extends AbstractDeckSkin {

    private ChangeListener<Number> selectedIndexListener;

    public FadeDeckSkin(final Deck deck) {
        super(deck);
        addListeners();
    }
    @Override
    public void dispose() {
        deck.primaryNodeIndexProperty().removeListener(selectedIndexListener);
        super.dispose();
    }

    protected void updateNewNode() {
        final Node oldNode = currentNode;
        int shownIndex = deck.getPrimaryNodeIndex();
        if (shownIndex >= 0 && shownIndex < deck.getNodes().size()) {
            currentNode = deck.getNodes().get(shownIndex);
        } else {
            currentNode = null;
        }

        if (oldNode != currentNode) {
            if (currentNode != null) {
                currentNode.setOpacity(0.0);
                currentNode.setVisible(true);
                FadeTransitionBuilder.create()
                        .node(currentNode)
                        .fromValue(0.0)
                        .toValue(1.0)
                        .duration(Duration.seconds(1))
                        .build()
                        .play();
            }
            if (oldNode != null) {
                // slide last slide to left
                FadeTransitionBuilder.create()
                        .node(oldNode)
                        .toValue(0.0)
                        .duration(Duration.seconds(1))
                        .onFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                oldNode.setVisible(false);
                                oldNode.setOpacity(1.0);
                            }
                        })
                        .build()
                        .play();
            }
        }
    }


    public void addListeners() {
        selectedIndexListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                updateNewNode();
            }
        };
        deck.primaryNodeIndexProperty().addListener(selectedIndexListener);
    }

}
