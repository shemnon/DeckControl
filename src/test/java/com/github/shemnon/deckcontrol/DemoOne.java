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
package com.github.shemnon.deckcontrol;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SceneBuilder;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: shemnon
 * Date: 27 Aug 2012
 * Time: 8:05 PM
 */
public class DemoOne extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        final Deck deck = createDeck();

        stage.setScene(SceneBuilder.create()
                .root(
                        VBoxBuilder.create()
                                .fillWidth(false)
                                .children(
                                        deck,
                                        HBoxBuilder.create().children(
                                                ButtonBuilder.create()
                                                        .text("<<")
                                                        .onAction(new EventHandler<ActionEvent>() {
                                                            @Override
                                                            public void handle(ActionEvent actionEvent) {
                                                                deck.previousNode();
                                                            }
                                                        }).build(),
                                                ButtonBuilder.create()
                                                        .text(">>")
                                                        .onAction(new EventHandler<ActionEvent>() {
                                                            @Override
                                                            public void handle(ActionEvent actionEvent) {
                                                                deck.nextNode();
                                                            }
                                                        }).build()
                                        ).build(),
                                        HBoxBuilder.create().children(
                                                ButtonBuilder.create()
                                                        .text("Slide")
                                                        .onAction(new EventHandler<ActionEvent>() {
                                                            @Override
                                                            public void handle(ActionEvent actionEvent) {
                                                                deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.SlideDeckSkin'");
                                                            }
                                                        }).build(),
                                                ButtonBuilder.create()
                                                        .text("Fade")
                                                        .onAction(new EventHandler<ActionEvent>() {
                                                            @Override
                                                            public void handle(ActionEvent actionEvent) {
                                                                deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.FadeDeckSkin'");
                                                            }
                                                        }).build(),
                                                ButtonBuilder.create()
                                                        .text("Shift")
                                                        .onAction(new EventHandler<ActionEvent>() {
                                                            @Override
                                                            public void handle(ActionEvent actionEvent) {
                                                                deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.ShiftDeckSkin'");
                                                            }
                                                        }).build(),
                                                ButtonBuilder.create()
                                                        .text("Shelf")
                                                        .onAction(new EventHandler<ActionEvent>() {
                                                            @Override
                                                            public void handle(ActionEvent actionEvent) {
                                                                deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.ShelfDeckSkin'");
                                                            }
                                                        }).build()
                                        ).build()
                                ).build())
                .width(300)
                .height(300)
                .build());


        stage.setWidth(300);
        stage.setHeight(300);

        stage.show();
    }

    private Deck createDeck() {
        final Deck deck = new Deck();
        deck.getNodes().add(createTestNode("First"));
        deck.getNodes().add(createTestNode("Second"));
        deck.getNodes().add(createTestNode("Third"));
        deck.getNodes().add(createTestNode("Fourth"));
        deck.getNodes().add(createTestNode("Fifth"));
        deck.getNodes().add(createTestNode("Sixth"));
        deck.getNodes().add(createTestNode("Seventh"));
        deck.getNodes().add(createTestNode("Eighth"));
        deck.getNodes().add(createTestNode("Ninth"));
        deck.getNodes().add(createTestNode("Tenth"));
        deck.setPrimaryNodeIndex(0);
        return deck;
    }

    public Node createTestNode(String text) {
        Pane pane = new StackPane();
        pane.getChildren().add(new Text(text));

        String color = "000000" + Integer.toHexString(text.hashCode() & 0xffffff);
        color = color.substring(color.length() - 6);
        pane.setStyle("-fx-background-color: #" + color + "; -fx-padding: 50;");
        return pane;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
