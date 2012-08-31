package com.github.shemnon.deckcontrol;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;

/**
 * Created with IntelliJ IDEA.
 * User: Danno Ferrin
 * Date: 27 Aug 2012
 * Time: 5:43 PM
 */
public class Deck extends Control {

    private ListProperty<Node> nodes = new SimpleListProperty<Node>(this, "nodes", FXCollections.<Node>observableArrayList());
    private IntegerProperty primaryNodeIndex = new SimpleIntegerProperty(this, "primaryNodeIndex", -1);
    private ObjectProperty<Pos> alignment = new SimpleObjectProperty<Pos>(this, "algnment", null);

    public Deck() {
        getStyleClass().add("deck");
    }

    public ObservableList<Node> getNodes() {
        return nodes.get();
    }

    public void setNodes(ObservableList<Node> nodes) {
        this.nodes.set(nodes);
    }

    public ListProperty<Node> nodes() {
        return nodes;
    }

    public int getPrimaryNodeIndex() {
        return primaryNodeIndex.get();
    }

    public void setPrimaryNodeIndex(int primaryNodeIndex) {
        this.primaryNodeIndex.set(primaryNodeIndex);
    }

    public IntegerProperty primaryNodeIndex() {
        return primaryNodeIndex;
    }

    public Pos getAlignment() {
        return alignment.get();
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }

    public ObjectProperty<Pos> alignment() {
        return alignment;
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
