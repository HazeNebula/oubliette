package nl.hazenebula.oubliette;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Rectangle;

public class FieldColorButton extends ToggleButton {
    private final Tile tile;

    public FieldColorButton(Tile tile, double size, CanvasPane canvasPane) {
        super();

        this.tile = tile;

        setGraphic(new Rectangle(0.9d * size, 0.9d * size, tile.color()));
        setTooltip(new Tooltip(tile.toString()));

        setOnAction(e -> canvasPane.setFieldColor(tile));
    }
}
