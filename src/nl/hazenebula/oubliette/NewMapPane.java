package nl.hazenebula.oubliette;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class NewMapPane extends GridPane {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = Integer.MAX_VALUE;

    public NewMapPane(MapPane mapPane, CanvasPane canvasPane,
                      Stage primaryStage) {
        Label widthLabel = new Label("Width: ");
        GridPane.setHgrow(widthLabel, Priority.ALWAYS);
        Spinner<Integer> widthSpinner = new Spinner<>(MIN_SIZE, MAX_SIZE,
                canvasPane.fieldWidthProperty().get(), 1);
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
                canvasPane.fieldHeightProperty().get(), 1);
        heightSpinner.setEditable(true);
        heightSpinner.focusedProperty().addListener((observable, oldValue,
                                                     newValue) -> {
            if (!newValue) {
                heightSpinner.increment(0);
            }
        });

        Label methodLabel = new Label("Fill:");
        GridPane.setHgrow(methodLabel, Priority.ALWAYS);
        ComboBox<String> methodBox = new ComboBox<>();
        for (Tile tile : Tile.values()) {
            methodBox.getItems().add(tile.toString());
        }
        methodBox.getSelectionModel().select(0);

        Button newMapButton = new Button("New");
        newMapButton.setOnAction(e -> {
            for (Tile tile : Tile.values()) {
                if (methodBox.getValue().equals(tile.toString())) {
                    canvasPane.setMap(new Map(widthSpinner.getValue(),
                            heightSpinner.getValue(), tile));
                }
            }
            primaryStage.setTitle("New File");
            mapPane.updateMinimap();

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
        add(methodBox, 1, 2);
        add(buttonPane, 0, 3);
    }
}
