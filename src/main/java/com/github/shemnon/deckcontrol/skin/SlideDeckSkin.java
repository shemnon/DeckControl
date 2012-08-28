package com.github.shemnon.deckcontrol.skin;

import com.github.shemnon.deckcontrol.Deck;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
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
 * Date: 27 Aug 2012
 * Time: 6:34 PM
 */
public class SlideDeckSkin  implements Skin<Deck> {

    protected Node oldNode;
    protected Node currentNode;
    int shownIndex = 0;

    TranslateTransition translateTransition;

    protected StackPane stack;

    Deck deck;

    public SlideDeckSkin (final Deck deck) {
        this.deck = deck;
        stack = new StackPane();
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
        if (oldNode == currentNode) {
            return; // nothing to do
        } else if (lastIndex < shownIndex) {
            if (currentNode != null) {
                // slide next slide from right
                translateTransition = TranslateTransitionBuilder.create()
                        .node(currentNode)
                        .fromX(deck.getWidth())
                        .toX(0)
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
            } else if (oldNode != null) {
                // slide last slide to left
                translateTransition = TranslateTransitionBuilder.create()
                        .node(oldNode)
                        .fromX(0)
                        .toX(-deck.getWidth())
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
            }
        } else {
            if (oldNode != null) {
                // slide old slide to right
                translateTransition = TranslateTransitionBuilder.create()
                        .node(oldNode)
                        .fromX(0)
                        .toX(deck.getWidth())
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
            } else if (currentNode != null) {
                // slide current slide from left
                translateTransition = TranslateTransitionBuilder.create()
                        .node(currentNode)
                        .fromX(-deck.getWidth())
                        .toX(0)
                        .duration(Duration.seconds(1))
                        .onFinished(hideOldNode)
                        .build();
            }
        }
        if (currentNode != null) {
            currentNode.setVisible(true);
        }
        translateTransition.play();
    }


    public void addListeners() {
        deck.nodes().addListener(new ChangeListener<ObservableList<Node>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Node>> observableValue, ObservableList<Node> oldNodes, ObservableList<Node> newNodes) {
                stack.getChildren().setAll(newNodes);
                lockDeckValues();
            }
        });

        deck.primaryNodeIndex().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                slideNewValue();
            }
        });

        // clip to normal bounds, for animations
        final Rectangle clip = new Rectangle();
        stack.layoutBoundsProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                clip.setWidth(stack.getLayoutBounds().getWidth());
                clip.setHeight(stack.getLayoutBounds().getHeight());
            }
        });
        stack.setClip(clip);

    }

}
