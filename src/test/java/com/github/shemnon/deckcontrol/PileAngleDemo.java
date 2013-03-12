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

import com.github.shemnon.deckcontrol.skin.PileDeckSkin;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.SceneBuilder;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: shemnon
 * Date: 27 Aug 2012
 * Time: 8:05 PM
 */
public class PileAngleDemo extends Application {
    @Override
    public void start(final Stage stage) throws Exception {
        final Deck deck = createDeck();

        Slider deckSlider;
        Slider angleSlider;
        stage.setScene(SceneBuilder.create()
                .stylesheets("BigPile.css")
                .root(
                        VBoxBuilder.create()
                                .fillWidth(false)
                                .children(
                                        deck,
                                        HBoxBuilder.create().children(
                                                ButtonBuilder.create()
                                                        .text("<<")
                                                        .onAction(actionEvent -> deck.previousNode()).build(),
                                                ButtonBuilder.create()
                                                        .text(">>")
                                                        .onAction(actionEvent -> deck.nextNode()).build()
                                        ).build(),
                                        HBoxBuilder.create().children(
                                                new Label("Deck Position:"),
                                                deckSlider = SliderBuilder.create()
                                                        .min(0)
                                                        .max(14)
                                                        .value(1)
                                                        .build()
                                        ).build(),
                                        HBoxBuilder.create().children(
                                                new Label("Spread Angle:"),
                                                angleSlider = SliderBuilder.create()
                                                        .min(0)
                                                        .max(90)
                                                        .value(45)
                                                        .build()
                                        ).build()
                                ).build())
                .width(300)
                .height(300)
                .build());

        angleSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> ((PileDeckSkin) deck.getSkin()).setSpreadAngle(newValue.doubleValue()));
        deckSlider.valueProperty().bindBidirectional(deck.primaryNodeIndexProperty());

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
        deck.getNodes().add(createTestNode("Eleventh"));
        deck.getNodes().add(createTestNode("Twelfth"));
        deck.getNodes().add(createTestNode("Thirteenth"));
        deck.getNodes().add(createTestNode("Fourteenth"));
        deck.getNodes().add(createTestNode("Fifteenth"));
        deck.setPrimaryNodeIndex(0);
        //deck.setAlignment(Pos.BASELINE_CENTER);
        deck.getStyleClass().setAll("deck");
        return deck;
    }

    public Node createTestNode(String text) {
        Pane pane = new StackPane();
        Text t = new Text(text);
        pane.getChildren().add(t);
        t.rotateProperty().bind(pane.rotateProperty().negate());

        int hash = text.hashCode();
        String color = "000000" + Integer.toHexString(hash & 0xffffff);
        int bottom = 10 + ((hash & 0xff) % 2) * 40;
        int right = 20 + ((hash & 0xff) % 3) * 15;
        int top = 14 + ((hash & 0xff) % 4) * 12;
        int left = 10 + ((hash & 0xff) % 5) * 10;
        color = color.substring(color.length() - 6);
        pane.setStyle("-fx-background-color: #" + color + "; -fx-padding: " + top + " " + right + " " + bottom + " " + left + ";");
        return pane;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
