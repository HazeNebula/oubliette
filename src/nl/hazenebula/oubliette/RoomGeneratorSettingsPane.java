package nl.hazenebula.oubliette;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import nl.hazenebula.terraingeneration.RoomGenerator;

public class RoomGeneratorSettingsPane extends GridPane {
//    public static final int NUMBER_OF_ATTEMPTS = 100;
//    public static final int MIN_WIDTH = 4;
//    public static final int MAX_WIDTH = 8;
//    public static final int MIN_HEIGHT = 4;
//    public static final int MAX_HEIGHT = 8;
//    public static final Field FLOOR_TILE = Field.WHITE;

    private Spinner<Integer> attemptsSpinner;
    private Spinner<Integer> minWidthSpinner;
    private Spinner<Integer> maxWidthSpinner;
    private Spinner<Integer> minHeightSpinner;
    private Spinner<Integer> maxHeightSpinner;
    private ComboBox<Field> floorTileBox;

    public RoomGeneratorSettingsPane() {
        Label attemptsLabel = new Label("Attempts:");
        attemptsLabel.setTooltip(new Tooltip("The number of attempts that " +
                "the generator will do to spawn rooms."));
        GridPane.setHgrow(attemptsLabel, Priority.ALWAYS);
        attemptsSpinner = new Spinner<>(1, Integer.MAX_VALUE,
                RoomGenerator.NUMBER_OF_ATTEMPTS, 1);
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
                RoomGenerator.MIN_WIDTH, 1);
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
                RoomGenerator.MAX_WIDTH, 1);
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
                RoomGenerator.MIN_HEIGHT, 1);
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
                RoomGenerator.MAX_HEIGHT, 1);
        maxHeightSpinner.setEditable(true);
        maxHeightSpinner.focusedProperty().addListener((observable, oldValue,
                                                        newValue) -> {
            if (!newValue) {
                maxHeightSpinner.increment(0);
            }
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
        floorTileBox.getItems().addAll(Field.values());
        floorTileBox.getSelectionModel().select(RoomGenerator.FLOOR_TILE);

        Button explanationButton = new Button("Explanation");
        explanationButton.setOnAction(e -> {
            Alert explanation = new Alert(Alert.AlertType.INFORMATION);
            explanation.setTitle("Explanation");
            explanation.setHeaderText(null);
            explanation.setGraphic(null);
            explanation.setContentText("This generator will generate random," +
                    " rectangular, non-overlapping rooms. It does so by " +
                    "generating a rectangle with a certain width and height." +
                    " It will then try to generate random x and y attributes" +
                    " that make it fit within the selected area. If no part" +
                    " of this area is occupied by another room, the room is " +
                    "placed. The generator will attempt to place a certain " +
                    "number of rooms, after which it terminates.\n\nIf " +
                    "anything exists of the floor color within the selected " +
                    "area, no room will be placed within the tiles of that " +
                    "color.\n\nHover over an item to see a short explanation " +
                    "of that parameter.");
            explanation.showAndWait();
        });

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
        add(floorTileLabel, 0, 5);
        add(floorTileBox, 1, 5);
        add(explanationButton, 0, 6);
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

    public Field getFloorTile() {
        return floorTileBox.getValue();
    }
}
