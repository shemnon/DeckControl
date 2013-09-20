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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

        Button left = new Button("<<");
        left.setOnAction(actionEvent -> deck.previousNode());
        Button right = new Button("<<");
        right.setOnAction(actionEvent -> deck.nextNode());

        Button slide = new Button("Slide");
        slide.setOnAction(actionEvent -> deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.SlideDeckSkin'"));
        Button fade = new Button("Fade");
        fade.setOnAction(actionEvent -> deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.FadeDeckSkin'"));
        Button shift = new Button("Shift");
        shift.setOnAction(actionEvent -> deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.ShiftDeckSkin'"));
        Button shelf = new Button("Shelf");
        shelf.setOnAction(actionEvent -> deck.setStyle("-fx-skin: 'com.github.shemnon.deckcontrol.skin.ShelfDeckSkin'"));

        VBox box = new VBox(
                deck,
                new HBox(left, right),
                new HBox(slide, fade, shift, shelf)
        );
        box.setFillWidth(false);
        box.setPrefWidth(300);
        box.setPrefHeight(300);

        Scene scene = new Scene(box);
        stage.setScene(scene);

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
