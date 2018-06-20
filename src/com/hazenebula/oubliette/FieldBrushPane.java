package com.hazenebula.oubliette;

import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;

public class FieldBrushPane extends FlowPane {
    public static final double BRUSH_SIZE = 100.0d;

    public FieldBrushPane() {
        setMinWidth(2 * BRUSH_SIZE);
        setPrefWrapLength(2 * BRUSH_SIZE);

        for (Field field : Field.values()) {
            Button button = new Button();
            button.setMinSize(BRUSH_SIZE, BRUSH_SIZE);
            button.setMaxSize(Double.POSITIVE_INFINITY,
                    Double.POSITIVE_INFINITY);

            button.setGraphic(new Rectangle(0.75 * BRUSH_SIZE, 0.75 * BRUSH_SIZE,
                    field.getColor()));
            getChildren().add(button);
        }
    }
}
