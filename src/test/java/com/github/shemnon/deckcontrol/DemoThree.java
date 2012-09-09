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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: shemnon
 * Date: 27 Aug 2012
 * Time: 8:05 PM
 */
public class DemoThree extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        final SpinPane spinPane = createSpinPane();

        Slider slider;
        stage.setScene(SceneBuilder.create()
                .root(
                        VBoxBuilder.create()
                                .fillWidth(false)
                                .children(
                                        spinPane,
                                        HBoxBuilder.create().children(
                                                slider = SliderBuilder.create()
                                                        .min(-360)
                                                        .max(360)
                                                        .value(90)
                                                        .build()
                                        ).build()
                                ).build())
                .width(300)
                .height(300)
                .build());
         slider.valueProperty().addListener(new ChangeListener<Number>() {
             @Override
             public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                 spinPane.setAngle(newValue.doubleValue());
             }
         });

        stage.setWidth(300);
        stage.setHeight(300);

        stage.show();
    }

    private SpinPane createSpinPane() {
        final SpinPane spinPane = new SpinPane();
        final Node front = createTestNode("Front");
        final Node back = createTestNode("Back");
        spinPane.getChildren().add(back);
        spinPane.getChildren().add(front);
        spinPane.angleProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                // normalize the angle
                double angle = newNumber.doubleValue();
                if (angle < 0 || angle > 360) {
                    angle = Math.abs(360 - Math.abs(angle));
                }
                front.setVisible(angle < 180);
                back.setVisible(angle > 180);
            }
        });
        spinPane.setAngle(90);
        return spinPane;
    }

    public Node createTestNode(String text) {
        Pane pane = new StackPane();
        pane.getChildren().add(new Button(text));

        String color = "000000" + Integer.toHexString(text.hashCode() & 0xffffff);
        color = color.substring(color.length() - 6);
        pane.setStyle("-fx-background-color: #" + color + "; -fx-padding: 50;");
        return pane;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
