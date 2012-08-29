package com.github.shemnon.deckcontrol.skin;

import com.github.shemnon.deckcontrol.Deck;
import javafx.animation.*;
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
 * Date: 28 Aug 2012
 * Time: 6:34 AM
 */
public class ShiftDeckSkin implements Skin<Deck> {

    protected Node oldNode;
    protected Node currentNode;
    int shownIndex = 0;

    protected StackPane stack;

    SequentialTransition transitions;

    Deck deck;
    private ChangeListener<ObservableList<Node>> nodesListener;
    private ChangeListener<Number> primaryIndexListener;

    public ShiftDeckSkin(final Deck deck) {
        this.deck = deck;
        stack = new StackPane();
        stack.getChildren().setAll(deck.getNodes());
        transitions = new SequentialTransition();
        transitions.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                transitions.getChildren().clear();
            }
        });
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
        deck.nodes().remove(nodesListener);
        deck.primaryNodeIndex().removeListener(primaryIndexListener);
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
                }
            }
        };
        ParallelTransition transition = new ParallelTransition();
        if (oldNode == currentNode) {
            return; // nothing to do
        } else if (lastIndex < shownIndex) {
            if (oldNode != null) {
                // slide last slide to left
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                                .node(oldNode)
                                .fromX(0)
                                .toX(-deck.getWidth())
                                .duration(Duration.seconds(1))
                                .onFinished(hideOldNode)
                                .build());
            }
            if (currentNode != null) {
                // slide next slide from right
                currentNode.setTranslateX(deck.getWidth());
                currentNode.setVisible(true);
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                            .node(currentNode)
                            .fromX(deck.getWidth())
                            .toX(0)
                            .duration(Duration.seconds(1))
                            .build());
            }
        } else {
            if (oldNode != null) {
                // slide last slide to right
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                            .node(oldNode)
                            .fromX(0)
                            .toX(deck.getWidth())
                            .duration(Duration.seconds(1))
                            .onFinished(hideOldNode)
                            .build());
            }
            if (currentNode != null) {
                // slide next slide from left
                currentNode.setTranslateX(-deck.getWidth());
                currentNode.setVisible(true);
                transition.getChildren().add(
                        TranslateTransitionBuilder.create()
                            .node(currentNode)
                            .fromX(-deck.getWidth())
                            .toX(0)
                            .duration(Duration.seconds(1))
                            .build());
            }
        }
        if (transitions.getStatus() == Animation.Status.RUNNING) {
            Duration time = transitions.getCurrentTime();
            transitions.stop();
            transitions.getChildren().add(transition);
            transitions.playFrom(time);
        } else {
            transitions.getChildren().setAll(transition);
            transitions.playFrom(Duration.ZERO);
        }
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

        primaryIndexListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                slideNewValue();
            }
        };
        deck.primaryNodeIndex().addListener(primaryIndexListener);
    }

}
