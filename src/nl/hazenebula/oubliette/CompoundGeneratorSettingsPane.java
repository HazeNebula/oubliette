package nl.hazenebula.oubliette;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import nl.hazenebula.terraingeneration.CompoundGenerator;
import nl.hazenebula.terraingeneration.ElementPicker;
import nl.hazenebula.terraingeneration.MazeType;
import nl.hazenebula.terraingeneration.Point;

public class CompoundGeneratorSettingsPane extends GridPane {
    private Spinner<Integer> attemptsSpinner;
    private Spinner<Integer> minWidthSpinner;
    private Spinner<Integer> maxWidthSpinner;
    private Spinner<Integer> minHeightSpinner;
    private Spinner<Integer> maxHeightSpinner;
    private ComboBox<MazeType> mazeTypeBox;
    private Spinner<Double> connectionProbSpinner;
    private ComboBox<Tile> floorTileBox;

    public CompoundGeneratorSettingsPane() {
        Label attemptsLabel = new Label("Attempts:");
        attemptsLabel.setTooltip(new Tooltip("The number of attempts that " +
                "the generator will do to spawn rooms."));
        GridPane.setHgrow(attemptsLabel, Priority.ALWAYS);
        attemptsSpinner = new Spinner<>(1, Integer.MAX_VALUE,
                CompoundGenerator.NUMBER_OF_ATTEMPTS, 1);
        attemptsSpinner.setEditable(true);
        attemptsSpinner.focusedProperty().addListener((observable, oldValue,
                                                       newValue) -> {
            if (!newValue) {
                attemptsSpinner.increment(0);
            }
        });

        Label minWidthLabel = new Label("Min. Width:");
        minWidthLabel.setTooltip(new Tooltip("The minimum width of a room " +
                "(inclusive)."));
        GridPane.setHgrow(minWidthLabel, Priority.ALWAYS);
        minWidthSpinner = new Spinner<>(1, Integer.MAX_VALUE,
                CompoundGenerator.MIN_WIDTH, 1);
        minWidthSpinner.setEditable(true);
        minWidthSpinner.focusedProperty().addListener((observable, oldValue,
                                                       newValue) -> {
            if (!newValue) {
                minWidthSpinner.increment(0);
            }
        });

        Label maxWidthLabel = new Label("Max. Width:");
        maxWidthLabel.setTooltip(new Tooltip("The maximum width of a " +
                "room (inclusive)"));
        GridPane.setHgrow(maxWidthLabel, Priority.ALWAYS);
        maxWidthSpinner = new Spinner<>(0, Integer.MAX_VALUE,
                CompoundGenerator.MAX_WIDTH, 1);
        maxWidthSpinner.setEditable(true);
        maxWidthSpinner.focusedProperty().addListener((observable, oldValue,
                                                       newValue) -> {
            if (!newValue) {
                maxWidthSpinner.increment(0);
            }
        });

        Label minHeightLabel = new Label("Min. Height:");
        minHeightLabel.setTooltip(new Tooltip("The minimum height of a " +
                "room (inclusive)"));
        GridPane.setHgrow(minHeightLabel, Priority.ALWAYS);
        minHeightSpinner = new Spinner<>(0, Integer.MAX_VALUE,
                CompoundGenerator.MIN_HEIGHT, 1);
        minHeightSpinner.setEditable(true);
        minHeightSpinner.focusedProperty().addListener((observable, oldValue,
                                                        newValue) -> {
            if (!newValue) {
                minHeightSpinner.increment(0);
            }
        });

        Label maxHeightLabel = new Label("Max. Height:");
        maxHeightLabel.setTooltip(new Tooltip("The maximum height of a " +
                "room (inclusive)"));
        GridPane.setHgrow(maxHeightLabel, Priority.ALWAYS);
        maxHeightSpinner = new Spinner<>(0, Integer.MAX_VALUE,
                CompoundGenerator.MAX_HEIGHT, 1);
        maxHeightSpinner.setEditable(true);
        maxHeightSpinner.focusedProperty().addListener((observable, oldValue,
                                                        newValue) -> {
            if (!newValue) {
                maxHeightSpinner.increment(0);
            }
        });

        Label connectionProbLabel = new Label("Connection probability:");
        connectionProbLabel.setTooltip(new Tooltip("The probability that " +
                "two squares separated by a wall will be joined."));
        GridPane.setHgrow(connectionProbLabel, Priority.ALWAYS);
        connectionProbSpinner = new Spinner<>(0.0d, 1.0d,
                CompoundGenerator.CONNECTION_PROB, 0.05d);
        connectionProbSpinner.setEditable(true);
        connectionProbSpinner.focusedProperty().addListener((observable, oldValue,
                                                             newValue) -> {
            if (!newValue) {
                connectionProbSpinner.increment(0);
            }
        });

        Tooltip mazeTypeTooltip = new Tooltip("The method by which corridors " +
                "will be generated.\nLast will usually give longer, winding " +
                "corridors,\nRandom can have shorter corridors.");
        Label mazeTypeLabel = new Label("Maze Type:");
        mazeTypeLabel.setTooltip(mazeTypeTooltip);
        GridPane.setHgrow(mazeTypeLabel, Priority.ALWAYS);
        mazeTypeBox = new ComboBox<>();
        mazeTypeBox.setTooltip(mazeTypeTooltip);
        mazeTypeBox.getItems().addAll(MazeType.values());
        mazeTypeBox.getSelectionModel().select(CompoundGenerator.MAZE_TYPE);

        Button explanationButton = new Button("Explanation");
        explanationButton.setOnAction(e -> {
            Alert explanation = new Alert(Alert.AlertType.INFORMATION);
            explanation.setTitle("Explanation");
            explanation.setHeaderText(null);
            explanation.setGraphic(null);
            explanation.setContentText("This generator will generate random " +
                    "rooms connected by random corridors. Rooms must have an " +
                    "odd min/max width/height. First, attempts are made to " +
                    "generate rooms. After that has finished, the rest of " +
                    "the available space is filled up by random mazes. All " +
                    "separate areas are then connected, and corridors that " +
                    "lead nowhere are removed.");
            explanation.showAndWait();
        });

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
        floorTileBox.getSelectionModel().select(CompoundGenerator.FLOOR_TILE);

        add(attemptsLabel, 0, 0);
        add(attemptsSpinner, 1, 0);
        add(minWidthLabel, 0, 1);
        add(minWidthSpinner, 1, 1);
        add(maxWidthLabel, 0, 2);
        add(maxWidthSpinner, 1, 2);
        add(minHeightLabel, 0, 3);
        add(minHeightSpinner, 1, 3);
        add(maxHeightLabel, 0, 4);
        add(maxHeightSpinner, 1, 4);
        add(mazeTypeLabel, 0, 5);
        add(mazeTypeBox, 1, 5);
        add(connectionProbLabel, 0, 6);
        add(connectionProbSpinner, 1, 6);
        add(explanationButton, 0, 7, 2, 1);
    }

    public int getAttempts() {
        return attemptsSpinner.getValue();
    }

    public int getMinWidthValue() {
        return minWidthSpinner.getValue();
    }

    public int getMaxWidthValue() {
        return maxWidthSpinner.getValue();
    }

    public int getMinHeightValue() {
        return minHeightSpinner.getValue();
    }

    public int getMaxHeightValue() {
        return maxHeightSpinner.getValue();
    }

    public ElementPicker<Point> getElementPicker() {
        return mazeTypeBox.getValue().getElementPicker();
    }

    public double getConnectionProb() {
        return connectionProbSpinner.getValue();
    }

    public Tile getFloorTile() {
        return floorTileBox.getValue();
    }
}
