package nl.hazenebula.oubliette;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

public class FieldColorPane extends FlowPane {
    public static final double BRUSH_SIZE = 100.0d;

    public FieldColorPane(Grid grid) {
        setVgap(5.0d);
        setHgap(5.0d);
        setAlignment(Pos.TOP_CENTER);

        ToggleGroup group = new ToggleGroup();
        for (Field field : Field.values()) {
            FieldColorButton button = new FieldColorButton(field, BRUSH_SIZE,
                    grid);
            button.setToggleGroup(group);
            getChildren().add(button);
        }
    }
}
