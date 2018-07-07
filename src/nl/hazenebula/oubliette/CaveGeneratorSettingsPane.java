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
    private ComboBox<Field> backColorBox;
    private ComboBox<Field> floorColorBox;

    public CaveGeneratorSettingsPane() {
        Label onProbLabel = new Label("On probability:");
        onProbLabel.setTooltip(new Tooltip("The probability that any cell " +
                "will be turned on (traversable) before the first step." +
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
        offThresholdSpinner = new Spinner<>(0, 8, CaveGenerator.OFF_LIMIT, 1);
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

        Tooltip backColorTooltip = new Tooltip("The background color.\nThis " +
                "color is used for the cave walls.");
        Label backColorLabel = new Label("Background color:");
        backColorLabel.setTooltip(backColorTooltip);
        GridPane.setHgrow(backColorLabel, Priority.ALWAYS);
        backColorBox = new ComboBox<>();
        backColorBox.setTooltip(backColorTooltip);
        backColorBox.getItems().addAll(Field.values());
        backColorBox.getSelectionModel().select(Field.BLUE);

        Tooltip floorColorTooltip = new Tooltip("This color is used for the " +
                "cave floor.");
        Label floorColorLabel = new Label("Floor color:");
        floorColorLabel.setTooltip(floorColorTooltip);
        GridPane.setHgrow(floorColorLabel, Priority.ALWAYS);
        floorColorBox = new ComboBox<>();
        floorColorBox.setTooltip(floorColorTooltip);
        floorColorBox.getItems().addAll(Field.values());
        floorColorBox.getSelectionModel().select(Field.WHITE);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> getScene().getWindow().hide());

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
                    "number of times we end up with a configuration that can" +
                    " look like a cave system.\n\nHover over an item to see a" +
                    " short explanation.");
            explanation.showAndWait();
        });

        add(onProbLabel, 0, 0);
        add(onProbSpinner, 1, 0);
        add(offThresholdLabel, 0, 1);
        add(offThresholdSpinner, 1, 1);
        add(onThresholdLabel, 0, 2);
        add(onThresholdSpinner, 1, 2);
        add(stepsLabel, 0, 3);
        add(stepsSpinner, 1, 3);
        add(backColorLabel, 0, 4);
        add(backColorBox, 1, 4);
        add(floorColorLabel, 0, 5);
        add(floorColorBox, 1, 5);
        add(confirmButton, 0, 6);
        add(explanationButton, 1, 6);
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

    public Field getBackColor() {
        return backColorBox.getValue();
    }

    public Field getFloor() {
        return floorColorBox.getValue();
    }
}
