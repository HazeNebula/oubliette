package nl.hazenebula.oubliette;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

public class Grid extends ScrollPane {
    public static final Color BACK_COLOR = Color.rgb(255, 255, 255);
    public static final Color FRONT_COLOR = Color.rgb(51, 153, 204);

    private static final double SQUARE_SIZE = 20.0d;
    private static final double GRID_SIZE = 1.0d;

    private final IntegerProperty scale;

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
                    fieldGrid[x][y] = Field.EMPTY;
                } else {
                    fieldGrid[x][y] = Field.FILLED;
                }
            }
        }

        scale = new SimpleIntegerProperty(100);

        canvas = new Canvas(fieldGrid.length * (SQUARE_SIZE + GRID_SIZE)
                * (scale.get() / 100),
                fieldGrid[0].length * (SQUARE_SIZE + GRID_SIZE)
                        * (scale.get() / 100));

        setContent(canvas);
        drawGrid();
    }

    public void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(FRONT_COLOR);

        // draw grid lines
        for (int x = 0; x < fieldGrid.length; ++x) {
            double xPos = x * (scale.get() / 100.0d) *
                    (SQUARE_SIZE + GRID_SIZE);
            gc.fillRect(xPos, 0, 1.0d, canvas.getHeight());
        }

        for (int y = 0; y < fieldGrid[0].length; ++y) {
            double yPos = y * (scale.get() / 100.0d) *
                    (SQUARE_SIZE + GRID_SIZE);
            gc.fillRect(0, yPos, canvas.getWidth(), GRID_SIZE);
        }

        // draw fields
        for (int x = 0; x < fieldGrid.length; ++x) {
            for (int y = 0; y < fieldGrid[x].length; ++y) {
                double xPos = x * (scale.get() / 100.0d) *
                        (SQUARE_SIZE + GRID_SIZE);
                double yPos = y * (scale.get() / 100.0d) *
                        (SQUARE_SIZE + GRID_SIZE);

                gc.setFill(fieldGrid[x][y].color());
                gc.fillRect(xPos + 1, yPos + 1, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        // todo: draw wall objects

        // todo: draw objects
    }

    public void setFieldColor(Field field) {
        curField = field;
    }
}
