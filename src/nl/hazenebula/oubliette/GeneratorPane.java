package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class GeneratorPane extends GridPane {
    public GeneratorPane(CanvasPane canvasPane) {
        Selection selection = new Selection();
        canvasPane.setSelection(selection);

        Label selectionXLabel = new Label("Selection X:");
        GridPane.setHgrow(selectionXLabel, Priority.SOMETIMES);
        Label selectionX = new Label("");
        selectionX.textProperty().bind(selection.xProperty().asString());
        selectionX.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionX, Priority.SOMETIMES);

        Label selectionYLabel = new Label("Selection Y:");
        GridPane.setHgrow(selectionYLabel, Priority.SOMETIMES);
        Label selectionY = new Label("");
        selectionY.textProperty().bind(selection.yProperty().asString());
        selectionY.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionY, Priority.SOMETIMES);

        Label selectionWidthLabel = new Label("Selection Width:");
        GridPane.setHgrow(selectionWidthLabel, Priority.SOMETIMES);
        Label selectionWidth = new Label("");
        selectionWidth.textProperty().bind(selection.widthProperty()
                .asString());
        selectionWidth.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionWidth, Priority.SOMETIMES);

        Label selectionHeightLabel = new Label("Selection Height:");
        GridPane.setHgrow(selectionHeightLabel, Priority.SOMETIMES);
        Label selectionHeight = new Label("");
        selectionHeight.textProperty().bind(selection.heightProperty()
                .asString());
        selectionHeight.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionHeight, Priority.SOMETIMES);

        Button selectAllButton = new Button("Select Entire Map");
        selectAllButton.setMaxWidth(Double.MAX_VALUE);
        selectAllButton.setOnAction(e -> {
            selection.setSelecting(true);
            selection.setX(0);
            selection.setY(0);
            selection.setWidth(canvasPane.fieldWidthProperty().get());
            selection.setHeight(canvasPane.fieldHeightProperty().get());
            canvasPane.drawAll();
        });

        Label generatorLabel = new Label("Procedural Generators:");
        GridPane.setHgrow(generatorLabel, Priority.ALWAYS);
        generatorLabel.setPadding(new Insets(5, 5, 5, 5));

        VBox buttonPane = new VBox();
        buttonPane.setSpacing(5);

        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton caveGeneratorButton = new ToggleButton("Cave Generator");
        caveGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        caveGeneratorButton.setToggleGroup(toggleGroup);
        caveGeneratorButton.setOnAction(e -> {
            // todo: add cave generator
        });
        buttonPane.getChildren().add(caveGeneratorButton);

        ToggleButton roomGeneratorButton = new ToggleButton("Room Generator");
        roomGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        roomGeneratorButton.setToggleGroup(toggleGroup);
        roomGeneratorButton.setOnAction(e -> {
            // todo: add room generator
        });
        buttonPane.getChildren().add(roomGeneratorButton);

        ToggleButton mazeGeneratorButton = new ToggleButton("Maze Generator");
        mazeGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        mazeGeneratorButton.setToggleGroup(toggleGroup);
        mazeGeneratorButton.setOnAction(e -> {
            // todo: add maze generator
        });
        buttonPane.getChildren().add(mazeGeneratorButton);

        ToggleButton compoundGeneratorButton = new ToggleButton("Compound " +
                "Generator");
        compoundGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        compoundGeneratorButton.setToggleGroup(toggleGroup);
        compoundGeneratorButton.setOnAction(e -> {
            // todo: add compound generator
        });
        buttonPane.getChildren().add(compoundGeneratorButton);

        add(selectionXLabel, 0, 0);
        add(selectionX, 1, 0);
        add(selectionYLabel, 0, 1);
        add(selectionY, 1, 1);
        add(selectionWidthLabel, 0, 2);
        add(selectionWidth, 1, 2);
        add(selectionHeightLabel, 0, 3);
        add(selectionHeight, 1, 3);
        add(selectAllButton, 0, 4, 2, 1);
        add(generatorLabel, 0, 5, 2, 1);
        add(buttonPane, 0, 6, 2, 1);
    }
}
