package nl.hazenebula.oubliette;

import javafx.geometry.HPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import nl.hazenebula.terraingeneration.Fill;

public class FillSettingsPane extends GridPane {
    private ComboBox<Tile> colorBox;

    public FillSettingsPane() {
        Label colorLabel = new Label("Fill color:");
        GridPane.setHgrow(colorLabel, Priority.ALWAYS);
        colorBox = new ComboBox<>();
        colorBox.getItems().addAll(Tile.values());
        colorBox.getSelectionModel().select(Fill.FILL_COLOR);
        GridPane.setHgrow(colorBox, Priority.ALWAYS);
        GridPane.setHalignment(colorBox, HPos.RIGHT);

        add(colorLabel, 0, 0);
        add(colorBox, 1, 0);
    }

    public Tile getColor() {
        return colorBox.getValue();
    }
}
