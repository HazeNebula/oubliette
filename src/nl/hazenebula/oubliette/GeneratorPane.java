package nl.hazenebula.oubliette;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

public class GeneratorPane extends GridPane {
    private CanvasPane canvasPane;

    private Selection selection;

    public GeneratorPane(CanvasPane canvasPane) {
        this.canvasPane = canvasPane;

        selection = new Selection();
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

        // todo: add cave generator
        // todo: add room generator
        // todo: add maze generator
        // todo: add room and maze generator

        add(selectionXLabel, 0, 0);
//        add(xSpinner, 1, 0);
        add(selectionX, 1, 0);
        add(selectionYLabel, 0, 1);
//        add(ySpinner, 1, 1);
        add(selectionY, 1, 1);
        add(selectionWidthLabel, 0, 2);
//        add(widthSpinner, 1, 2);
        add(selectionWidth, 1, 2);
        add(selectionHeightLabel, 0, 3);
//        add(heightSpinner, 1, 3);
        add(selectionHeight, 1, 3);
        add(selectAllButton, 0, 4, 2, 1);
    }
}
