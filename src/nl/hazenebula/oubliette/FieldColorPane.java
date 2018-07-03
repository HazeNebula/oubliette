package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

public class FieldColorPane extends FlowPane {
    public static final double BRUSH_SIZE = 100.0d;

    public FieldColorPane(Grid grid) {
        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);
        setAlignment(Pos.TOP_CENTER);
        // lowptodo: set default color and select button
        ToggleGroup group = new ToggleGroup();
        for (Field field : Field.values()) {
            FieldColorButton button = new FieldColorButton(field, BRUSH_SIZE,
                    grid);
            button.setToggleGroup(group);
            getChildren().add(button);
        }
    }
}
