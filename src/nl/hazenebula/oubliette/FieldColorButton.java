package nl.hazenebula.oubliette;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Rectangle;

public class FieldColorButton extends ToggleButton {
    private final Field field;

    public FieldColorButton(Field field, double size, CanvasPane canvasPane) {
        super();

        this.field = field;

        setGraphic(new Rectangle(0.9d * size, 0.9d * size, field.color()));
        setTooltip(new Tooltip(field.toString()));

        setOnAction(e -> canvasPane.setFieldColor(field));
    }
}
