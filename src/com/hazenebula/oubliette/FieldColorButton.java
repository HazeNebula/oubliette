package com.hazenebula.oubliette;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FieldColorButton extends Button {
    private final Color color;

    public FieldColorButton(Field field, double size) {
        super();

        color = field.color();

        setGraphic(new Rectangle(0.9d * size, 0.9d * size, color));
    }
}
