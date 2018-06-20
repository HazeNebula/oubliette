package com.hazenebula.oubliette;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ToolPane extends TabPane {
    private final Grid grid;

    // todo: set minimum size in a better way
    // todo: make toolpane resizable
    // todo: add wall object tab
    // todo: add object tab
    public ToolPane(Grid grid) {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setSide(Side.RIGHT);

        this.grid = grid;

        FieldBrushPane fieldBrushes = new FieldBrushPane();
        Tab fieldTab = new Tab("Field Brushes", fieldBrushes);
        getTabs().add(fieldTab);

        setMinWidth(fieldBrushes.getMinWidth());
    }
}
