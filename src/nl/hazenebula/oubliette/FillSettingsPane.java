package nl.hazenebula.oubliette;

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

        add(colorLabel, 0, 0);
        add(colorBox, 0, 1);
    }

    public Tile getColor() {
        return colorBox.getValue();
    }
}
