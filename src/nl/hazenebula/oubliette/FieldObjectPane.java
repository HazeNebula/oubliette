package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class FieldObjectPane extends GridPane {
    public FieldObjectPane(Grid grid) {
        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);
    }
}
