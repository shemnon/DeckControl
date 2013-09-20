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
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Created with IntelliJ IDEA.
 * User: Danno Ferrin
 * Date: 28 Aug 2012
 * Time: 6:34 AM
 */
public class ShiftDeckSkin extends AbstractDeckSkin {

    int shownIndex = 0;

    SequentialTransition sequentialTransition;

    private ChangeListener<Number> primaryIndexListener;

    public ShiftDeckSkin(final Deck deck) {
        super(deck);
        shownIndex = deck.getPrimaryNodeIndex();
        sequentialTransition = new SequentialTransition();
        sequentialTransition.setOnFinished(actionEvent -> sequentialTransition.getChildren().clear());
        addListeners();
    }


    @Override
    public void dispose() {
        deck.primaryNodeIndexProperty().removeListener(primaryIndexListener);
        super.dispose();
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

        EventHandler<ActionEvent> hideOldNode = actionEvent -> {
            if (oldNode != null) {
                oldNode.setVisible(false);
                oldNode.setTranslateX(0);
            }
        };
        ParallelTransition transition = new ParallelTransition();
        if (oldNode == currentNode) {
            return; // nothing to do
        } else if (lastIndex < shownIndex) {
            if (oldNode != null) {
                // slide last slide to left
                TranslateTransition translate = new TranslateTransition(Duration.seconds(1), oldNode);
                translate.setFromX(0);
                translate.setToX(-deck.getWidth());
                translate.setOnFinished(hideOldNode);

                transition.getChildren().add(translate);
            }
            if (currentNode != null) {
                // slide next slide from right
                currentNode.setTranslateX(deck.getWidth());
                currentNode.setVisible(true);

                TranslateTransition translate = new TranslateTransition(Duration.seconds(1), currentNode);
                translate.setFromX(deck.getWidth());
                translate.setToX(0);

                transition.getChildren().add(translate);
            }
        } else {
            if (oldNode != null) {
                // slide last slide to right
                TranslateTransition translate = new TranslateTransition(Duration.seconds(1), oldNode);
                translate.setFromX(0);
                translate.setToX(deck.getWidth());
                translate.setOnFinished(hideOldNode);

                transition.getChildren().add(translate);
            }
            if (currentNode != null) {
                // slide next slide from left
                currentNode.setTranslateX(-deck.getWidth());
                currentNode.setVisible(true);

                TranslateTransition translate = new TranslateTransition(Duration.seconds(1), currentNode);
                translate.setFromX(-deck.getWidth());
                translate.setToX(0);

                transition.getChildren().add(translate);
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
        primaryIndexListener = (observableValue, oldNumber, newNumber) -> shiftNewValue();
        deck.primaryNodeIndexProperty().addListener(primaryIndexListener);
    }

}
