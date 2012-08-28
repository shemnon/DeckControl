package com.github.shemnon.deckcontrol;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;

/**
 * Created with IntelliJ IDEA.
 * User: shemnon
 * Date: 27 Aug 2012
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Deck extends Control {

    private ListProperty<Node> nodes = new SimpleListProperty<Node>(this, "nodes", FXCollections.<Node>observableArrayList());
    private IntegerProperty primaryNodeIndex = new SimpleIntegerProperty(this, "primaryNodeIndex", -1);

    public Deck() {
        getStyleClass().add("deck");
    }

    public ObservableList<Node> getNodes() {
        return nodes.get();
    }

    public void setNodes(ObservableList<Node> nodes) {
        this.nodes.set(nodes);
    }

    public int getPrimaryNodeIndex() {
        return primaryNodeIndex.get();
    }

    public void setPrimaryNodeIndex(int primaryNodeIndex) {
        this.primaryNodeIndex.set(primaryNodeIndex);
    }

    public ListProperty<Node> nodes() {
        return nodes;
    }

    public IntegerProperty primaryNodeIndex() {
        return primaryNodeIndex;
    }

    @Override
    protected String getUserAgentStylesheet() {
        return getClass().getResource("/com/github/shemnon/deckcontrol/" + this.getClass().getSimpleName() + ".css").toString();
    }

    public void nextNode() {
        if (primaryNodeIndex.get() + 1 < nodes.size()) {
            primaryNodeIndex.set(primaryNodeIndex().get() + 1);
        }
    }

    public void previousNode() {
        if (primaryNodeIndex.get() > 0) {
            primaryNodeIndex.set(primaryNodeIndex().get() - 1);
        }
    }
}
