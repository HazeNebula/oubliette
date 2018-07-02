package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class FieldObjectPane extends GridPane {
    private Canvas resizeCanvas;

    private int maxSize;

    public FieldObjectPane(Grid grid) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);

        // highptodo: add file scanner
        maxSize = 3;

        resizeCanvas = new Canvas();
        resizeCanvas.widthProperty().bind(grid.sizeProperty()
                .add(Grid.GRID_SIZE).multiply(maxSize));
        resizeCanvas.heightProperty().bind(resizeCanvas.widthProperty());

        Button increaseSizeButton = new Button("+");
        Button decreaseSizeButton = new Button("-");
        Button rotateClockwiseButton = new Button("\u21BB");
        Button rotateAntiClockwiseButton = new Button("\u21BA");

        GridPane resizeWrapper = new GridPane();
        resizeWrapper.setPadding(new Insets(5, 5, 5, 5));
        resizeWrapper.add(resizeCanvas, 0, 0);

        add(resizeWrapper, 0, 0, 2, 2);
        add(increaseSizeButton, 2, 0);
        add(decreaseSizeButton, 2, 1);
        add(rotateClockwiseButton, 0, 3);
        add(rotateAntiClockwiseButton, 1, 3);
    }
}
