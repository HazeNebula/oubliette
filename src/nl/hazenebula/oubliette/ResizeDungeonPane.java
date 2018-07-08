package nl.hazenebula.oubliette;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ResizeDungeonPane extends GridPane {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = Integer.MAX_VALUE;

    public ResizeDungeonPane(MapPane mapPane, CanvasPane canvasPane) {
        Text instructions = new Text("Changes in size happen at the left and " +
                "bottom side of the map.");
        TextFlow textFlow = new TextFlow(instructions);

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

        Label fillLabel = new Label("Fill:");
        GridPane.setHgrow(fillLabel, Priority.ALWAYS);
        ComboBox<String> fill = new ComboBox<>();
        for (Field field : Field.values()) {
            fill.getItems().add(field.toString());
        }
        fill.getSelectionModel().select(0);

        Button newMapButton = new Button("Resize");
        newMapButton.setOnAction(e -> {
            canvasPane.resize(widthSpinner.getValue(), heightSpinner.getValue(),
                    Field.fromString(fill.getSelectionModel()
                            .getSelectedItem()));
            mapPane.updateMinimap();

            getScene().getWindow().hide();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> getScene().getWindow().hide());

        HBox buttonPane = new HBox();
        buttonPane.setSpacing(5);
        buttonPane.getChildren().addAll(newMapButton, cancelButton);

        add(textFlow, 0, 0, 2, 1);
        add(widthLabel, 0, 1);
        add(widthSpinner, 1, 1);
        add(heightLabel, 0, 2);
        add(heightSpinner, 1, 2);
        add(fillLabel, 0, 3);
        add(fill, 1, 3);
        add(buttonPane, 0, 4);
    }
}
