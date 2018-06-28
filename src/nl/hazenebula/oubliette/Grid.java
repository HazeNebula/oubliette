package nl.hazenebula.oubliette;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Grid extends ScrollPane {
    private static final int GRID_SIZE = 1;
    public static final int MAX_SQUARE_SIZE = 50;
    public static final int MIN_SQUARE_SIZE = 10;
    public static final int INIT_SQUARE_SIZE = 20;

    private final IntegerProperty size;

    private Field[][] fieldGrid;

    private Canvas canvas;
    private double hoffset;
    private double voffset;

    private Field curField;
    private Brush curBrush;
    private Color gridColor;

    // todo: add tool to draw with wall objects
    // todo: add tool to draw with objects
    public Grid() {
        setStyle("-fx-focus-color: transparent;");

        fieldGrid = new Field[100][100];
        for (int x = 0; x < fieldGrid.length; ++x) {
            for (int y = 0; y < fieldGrid[x].length; ++y) {
                fieldGrid[x][y] = Field.EMPTY;
            }
        }

        size = new SimpleIntegerProperty(20);

        canvas = new Canvas(fieldGrid.length * (size.get() + GRID_SIZE),
                fieldGrid[0].length * (size.get() + GRID_SIZE));
        canvas.setStyle("-fx-focus-color: transparent;");

        setContent(canvas);

        curBrush = Brush.FIELD;
        curField = Field.EMPTY;
        gridColor = Field.FILLED.color();

        hvalueProperty().addListener((observable, oldValue, newValue) -> {
            double hmin = getHmin();
            double hmax = getHmax();
            double hvalue = getHvalue();
            double contentWidth = canvas.getLayoutBounds().getWidth();
            double viewportWidth = getViewportBounds().getWidth();

            hoffset = Math.max(0, contentWidth - viewportWidth) *
                    (hvalue - hmin) / (hmax - hmin);
        });
        vvalueProperty().addListener((observable, oldValue, newValue) -> {
            double vmin = getVmin();
            double vmax = getVmax();
            double vvalue = getVvalue();
            double contentHeight = canvas.getLayoutBounds().getHeight();
            double viewportHeight = getViewportBounds().getHeight();

            voffset = Math.max(0, contentHeight - viewportHeight) *
                    (vvalue - vmin) / (vmax - vmin);
        });

        canvas.addEventHandler(MouseEvent.ANY, new MouseDrawHandler(e -> {
            Bounds bounds = new BoundingBox(hoffset, voffset,
                    getViewportBounds().getWidth(),
                    getViewportBounds().getHeight());

            if (bounds.contains(e.getX(), e.getY())) {
                if (curBrush == Brush.FIELD) {
                    int x = (int)(e.getX() / (size.get() + GRID_SIZE));
                    int y = (int)(e.getY() / (size.get() + GRID_SIZE));

                    fieldGrid[x][y] = curField;
                    drawField(x, y);
                }
            }
        }));

        drawFullGrid();
    }

    public void drawField(int x, int y) {
        if (x >= 0 && x < fieldGrid.length
                && y >= 0 && y < fieldGrid[y].length) {
            double xPos = x * (size.get() + GRID_SIZE);
            double yPos = y * (size.get() + GRID_SIZE);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(fieldGrid[x][y].color());
            gc.fillRect(xPos + 1, yPos + 1, size.get(), size.get());
        }
    }

    public void drawFullGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(gridColor);

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

    public void setGridColor(Color color) {
        gridColor = color;
    }

    public void setGridSize(int newSize) {
        if (newSize >= MIN_SQUARE_SIZE && newSize <= MAX_SQUARE_SIZE) {
            size.set(newSize);

            canvas.setWidth(fieldGrid.length * (size.get() + GRID_SIZE));
            canvas.setHeight(fieldGrid[0].length * (size.get() + GRID_SIZE));
            drawFullGrid();
        }
    }

    public WritableImage snapshot() {
        return canvas.snapshot(null, null);
    }
}
