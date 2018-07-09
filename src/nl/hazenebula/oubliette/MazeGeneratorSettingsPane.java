package nl.hazenebula.oubliette;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import nl.hazenebula.terraingeneration.*;

public class MazeGeneratorSettingsPane extends GridPane {
    private ComboBox<MazeType> mazeTypeBox;
    private ComboBox<Tile> floorTileBox;

    public MazeGeneratorSettingsPane() {
        Tooltip mazeTypeTooltip = new Tooltip("The method by which mazes " +
                "will be generated.\nLast will mostly generate long, winding " +
                "mazes,\nwhile Random will generate mazes with lots of short " +
                "cul-de-sacs (dead ends).");
        Label mazeTypeLabel = new Label("Maze Type:");
        mazeTypeLabel.setTooltip(mazeTypeTooltip);
        GridPane.setHgrow(mazeTypeLabel, Priority.ALWAYS);
        mazeTypeBox = new ComboBox<>();
        mazeTypeBox.setTooltip(mazeTypeTooltip);
        mazeTypeBox.getItems().addAll(MazeType.values());
        mazeTypeBox.getSelectionModel().select(MazeGenerator.MAZE_TYPE);

        Tooltip floorTileTooltip = new Tooltip("The floor color.\nThis " +
                "color is used for the floor of rooms. If any squares are " +
                "this color in the selected area already, they won't be " +
                "overwritten.");
        Label floorTileLabel = new Label("Floor color:");
        floorTileLabel.setTooltip(floorTileTooltip);
        GridPane.setHgrow(floorTileLabel, Priority.ALWAYS);
        floorTileBox = new ComboBox<>();
        floorTileBox.setTooltip(floorTileTooltip);
        floorTileBox.getItems().addAll(Tile.values());
        floorTileBox.getSelectionModel().select(RoomGenerator.FLOOR_TILE);

        add(mazeTypeLabel, 0, 0);
        add(mazeTypeBox, 1, 0);
        add(floorTileLabel, 0, 1);
        add(floorTileBox, 1, 1);
    }

    public ElementPicker<Point> getElementPicker() {
        return mazeTypeBox.getValue().getElementPicker();
    }

    public Tile getFloorTile() {
        return floorTileBox.getValue();
    }
}
