package nl.hazenebula.oubliette;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;

public class Grid extends ScrollPane {
    private static final int GRID_SIZE = 1;

    private final IntegerProperty size;

    private Field[][] fieldGrid;

    private Canvas canvas;

    private Field curField;
    private Brush curBrush;

    // todo: add tool to draw with fields
    // todo: add tool to draw with wall objects
    // todo: add tool to draw with objects
    public Grid() {
        fieldGrid = new Field[100][100];
        for (int x = 0; x < fieldGrid.length; ++x) {
            for (int y = 0; y < fieldGrid[x].length; ++y) {
                if (x % 2 == 0 && y % 2 != 0) {
                    fieldGrid[x][y] = Field.FILLED;
                } else {
                    fieldGrid[x][y] = Field.EMPTY;
                }
            }
        }

        size = new SimpleIntegerProperty(20);

        canvas = new Canvas(fieldGrid.length * (size.get() + GRID_SIZE),
                fieldGrid[0].length * (size.get() + GRID_SIZE));

        setContent(canvas);
        drawGrid();

        curField = Field.EMPTY;
    }

    public void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Field.FILLED.color());

        // draw grid lines
        for (int x = 0; x < fieldGrid.length; ++x) {
            double xPos = x * (size.get() + GRID_SIZE);
            gc.fillRect(xPos, 0, 1.0d, canvas.getHeight());
        }

        for (int y = 0; y < fieldGrid[0].length; ++y) {
            double yPos = y * (size.get() + GRID_SIZE);
            gc.fillRect(0, yPos, canvas.getWidth(), GRID_SIZE);
        }

        // draw fields
        for (int x = 0; x < fieldGrid.length; ++x) {
            for (int y = 0; y < fieldGrid[x].length; ++y) {
                double xPos = x * (size.get() + GRID_SIZE);
                double yPos = y * (size.get() + GRID_SIZE);

                gc.setFill(fieldGrid[x][y].color());
                gc.fillRect(xPos + 1, yPos + 1, size.get(),
                        size.get());
            }
        }

        // todo: draw wall objects

        // todo: draw objects
    }

    public void setFieldColor(Field field) {
        curField = field;
    }
}
