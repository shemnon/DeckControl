package com.github.shemnon.deckcontrol;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
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
        final Deck deck = new Deck();


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
                                        ).build()
                                ).build())
                .width(300)
                .height(300)
                .build());


        stage.setWidth(300);
        stage.setHeight(300);

        stage.show();
        deck.getNodes().add(createTestNode("First"));
        deck.getNodes().add(createTestNode("Second"));
        deck.getNodes().add(createTestNode("Third"));
        deck.setPrimaryNodeIndex(0);
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
