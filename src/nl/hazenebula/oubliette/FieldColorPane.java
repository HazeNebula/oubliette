package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

public class FieldColorPane extends FlowPane {
    public static final double BRUSH_SIZE = 100.0d;

    public FieldColorPane(CanvasPane canvasPane) {
        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);
        setAlignment(Pos.TOP_CENTER);

        ToggleGroup group = new ToggleGroup();
        for (Tile tile : Tile.values()) {
            FieldColorButton button = new FieldColorButton(tile, BRUSH_SIZE,
                    canvasPane);

            if (tile == Tile.values()[0]) {
                button.setSelected(true);
                canvasPane.setFieldColor(tile);
            }

            button.setToggleGroup(group);
            getChildren().add(button);
        }
    }
}
