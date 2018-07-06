package nl.hazenebula.oubliette;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class NewMapChooser extends GridPane {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = Integer.MAX_VALUE;
    private static final int INIT_SIZE = 50;

    public NewMapChooser(CanvasPane canvasPane, Stage primaryStage) {
        Label widthLabel = new Label("Width: ");
        GridPane.setHgrow(widthLabel, Priority.ALWAYS);
        Spinner<Integer> widthSpinner = new Spinner<>(MIN_SIZE, MAX_SIZE,
                INIT_SIZE, 1);
        widthSpinner.setEditable(true);
        widthSpinner.focusedProperty().addListener((observable, oldValue,
                                                    newValue) -> {
            if (!newValue) {
                widthSpinner.increment(0);
            }
        });

        Label heightLabel = new Label("Height:");
        GridPane.setHgrow(heightLabel, Priority.ALWAYS);
        Spinner<Integer> heightSpinner = new Spinner<>(MIN_SIZE, MAX_SIZE,
                INIT_SIZE, 1);
        heightSpinner.setEditable(true);
        heightSpinner.focusedProperty().addListener((observable, oldValue,
                                                     newValue) -> {
            if (!newValue) {
                heightSpinner.increment(0);
            }
        });

        Button optionsButton = new Button("Generation Options");
        optionsButton.setDisable(true);
        // todo: add a new window with generation options
        // todo: add a cave generator (cellular automata)
        // todo: add a generator for random rooms
        // todo: add a generator for random mazes
        // todo: add a generator for a random layout of rooms and corridors

        Label methodLabel = new Label("Fill:");
        GridPane.setHgrow(methodLabel, Priority.ALWAYS);
        ComboBox<String> methods = new ComboBox<>();
        for (Field field : Field.values()) {
            methods.getItems().add(field.toString());
        }
        methods.getItems().add("Generated");
        methods.getSelectionModel().select(0);
        methods.valueProperty().addListener((observable, oldValue,
                                             newValue) -> {
            if (newValue.equals("Generated")) {
                optionsButton.setDisable(false);
            } else {
                optionsButton.setDisable(true);
            }
        });

        Button newMapButton = new Button("New");
        newMapButton.setOnAction(e -> {
            if (methods.getSelectionModel().getSelectedItem()
                    .equals("Generated")) {

            } else {
                for (Field field : Field.values()) {
                    if (methods.getSelectionModel().getSelectedItem()
                            .equals(field.toString())) {
                        canvasPane.setMap(new Map(widthSpinner.getValue(),
                                heightSpinner.getValue(), field));
                    }
                }
            }
            primaryStage.setTitle("New File");

            getScene().getWindow().hide();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> getScene().getWindow().hide());

        HBox buttonPane = new HBox();
        buttonPane.setSpacing(5);
        buttonPane.getChildren().addAll(newMapButton, cancelButton);
        add(widthLabel, 0, 0);
        add(widthSpinner, 1, 0);
        add(heightLabel, 0, 1);
        add(heightSpinner, 1, 1);
        add(methodLabel, 0, 2);
        add(methods, 1, 2);
        add(optionsButton, 1, 3);
        add(buttonPane, 0, 4);
    }
}
