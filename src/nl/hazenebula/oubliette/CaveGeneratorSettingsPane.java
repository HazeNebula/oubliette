package nl.hazenebula.oubliette;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import nl.hazenebula.terraingeneration.CaveGenerator;

public class CaveGeneratorSettingsPane extends GridPane {
    private Spinner<Double> onProbSpinner;
    private Spinner<Integer> offThresholdSpinner;
    private Spinner<Integer> onThresholdSpinner;
    private Spinner<Integer> stepsSpinner;
    private ComboBox<Tile> backTileBox;
    private ComboBox<Tile> floorTileBox;

    public CaveGeneratorSettingsPane() {
        Label onProbLabel = new Label("On probability:");
        onProbLabel.setTooltip(new Tooltip("The probability that any cell " +
                "will be turned on before the first step." +
                "\nIncreasing this value should create wider caverns, though " +
                "large changes may not give good results."));
        GridPane.setHgrow(onProbLabel, Priority.ALWAYS);
        onProbSpinner = new Spinner<>(0.0d, 1.0d, CaveGenerator.ON_PROB,
                0.05d);
        onProbSpinner.setEditable(true);
        onProbSpinner.focusedProperty().addListener((observable, oldValue,
                                                     newValue) -> {
            if (!newValue) {
                onProbSpinner.increment(0);
            }
        });

        Label offThresholdLabel = new Label("Off Threshold:");
        offThresholdLabel.setTooltip(new Tooltip("Any cell that is turned " +
                "on with fewer neighbors that are turned on than this number " +
                "is turned off."));
        GridPane.setHgrow(offThresholdLabel, Priority.ALWAYS);
        offThresholdSpinner = new Spinner<>(0, 8, CaveGenerator.OFF_THRESHOLD,
                1);
        offThresholdSpinner.setEditable(true);
        offThresholdSpinner.focusedProperty().addListener((observable, oldValue,
                                                           newValue) -> {
            if (!newValue) {
                offThresholdSpinner.increment(0);
            }
        });

        Label onThresholdLabel = new Label("On Threshold:");
        onThresholdLabel.setTooltip(new Tooltip("Any cell that is turned " +
                "off with more neighbors that are turned on that this number " +
                "is turned on."));
        GridPane.setHgrow(onThresholdLabel, Priority.ALWAYS);
        onThresholdSpinner = new Spinner<>(0, 8, CaveGenerator.ON_THRESHOLD,
                1);
        onThresholdSpinner.setEditable(true);
        onThresholdSpinner.focusedProperty().addListener((observable, oldValue,
                                                          newValue) -> {
            if (!newValue) {
                onThresholdSpinner.increment(0);
            }
        });

        Label stepsLabel = new Label("Number of steps:");
        stepsLabel.setTooltip(new Tooltip("The number of simulation steps.\n" +
                "During each simulation step each cell is updated once."));
        GridPane.setHgrow(stepsLabel, Priority.ALWAYS);
        stepsSpinner = new Spinner<>(0, 1000, CaveGenerator.NUMBER_OF_STEPS, 1);
        stepsSpinner.setEditable(true);
        stepsSpinner.focusedProperty().addListener((observable, oldValue,
                                                    newValue) -> {
            if (!newValue) {
                stepsSpinner.increment(0);
            }
        });

        Tooltip backTileTooltip = new Tooltip("The background color.\nThis " +
                "color is used for the cave walls.");
        Label backTileLabel = new Label("Background tile:");
        backTileLabel.setTooltip(backTileTooltip);
        GridPane.setHgrow(backTileLabel, Priority.ALWAYS);
        backTileBox = new ComboBox<>();
        backTileBox.setTooltip(backTileTooltip);
        backTileBox.getItems().addAll(Tile.values());
        backTileBox.getSelectionModel().select(CaveGenerator.BACK_TILE);

        Tooltip floorTileTooltip = new Tooltip("This color is used for the " +
                "cave floor.");
        Label floorTileLabel = new Label("Floor color:");
        floorTileLabel.setTooltip(floorTileTooltip);
        GridPane.setHgrow(floorTileLabel, Priority.ALWAYS);
        floorTileBox = new ComboBox<>();
        floorTileBox.setTooltip(floorTileTooltip);
        floorTileBox.getItems().addAll(Tile.values());
        floorTileBox.getSelectionModel().select(CaveGenerator.FLOOR_TILE);

        Button explanationButton = new Button("Explanation");
        explanationButton.setOnAction(e -> {
            Alert explanation = new Alert(Alert.AlertType.INFORMATION);
            explanation.setTitle("Explanation");
            explanation.setHeaderText(null);
            explanation.setGraphic(null);
            explanation.setContentText("This generator generates caves uses " +
                    "so-called cellular automata. Basically, we start with a " +
                    "grid of cells. Each cell is either on or off. If a cell " +
                    "that is off is surrounded by enough cells that are on, " +
                    "it will turn on. If a cell that is on is surrounded by " +
                    "enough cells that are off, it will turn off. By " +
                    "starting with a grid where each cell is turned on with " +
                    "a certain probability, after we update each cell a " +
                    "number of times we end up with a configuration. Every " +
                    "cell that is on is turned into a floor tile, and every " +
                    "other cell is turned into a tile of the background " +
                    "color.\n\n\n\nTake note: this generator will " +
                    "overwrite everything inside the selected area.\n\nHover " +
                    "over an item to see a short explanation of that " +
                    "parameter.");
            explanation.showAndWait();
        });

        add(onProbLabel, 0, 0);
        add(onProbSpinner, 0, 1);
        add(offThresholdLabel, 0, 2);
        add(offThresholdSpinner, 0, 3);
        add(onThresholdLabel, 0, 4);
        add(onThresholdSpinner, 0, 5);
        add(stepsLabel, 0, 6);
        add(stepsSpinner, 0, 7);
        add(backTileLabel, 0, 8);
        add(backTileBox, 0, 9);
        add(floorTileLabel, 0, 10);
        add(floorTileBox, 0, 11);
        add(explanationButton, 0, 12);
    }

    public double getOnProb() {
        return onProbSpinner.getValue();
    }

    public int getOffThreshold() {
        return offThresholdSpinner.getValue();
    }

    public int getOnThreshold() {
        return onThresholdSpinner.getValue();
    }

    public int getNumberOfSteps() {
        return stepsSpinner.getValue();
    }

    public Tile getBackTile() {
        return backTileBox.getValue();
    }

    public Tile getFloorTile() {
        return floorTileBox.getValue();
    }
}
