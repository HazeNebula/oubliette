package nl.hazenebula.oubliette;

import javafx.beans.property.DoubleProperty;
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
    private static final Color HIGHLIGHT_COLOR = Color.DARKGRAY;
    private static final int GRID_SIZE = 1;
    public static final int MAX_SQUARE_SIZE = 50;
    public static final int MIN_SQUARE_SIZE = 10;
    public static final int INIT_SQUARE_SIZE = 20;

    private final IntegerProperty size;

    private Field[][] fieldGrid;
    private boolean prevHighlight;
    private int prevX;
    private int prevY;
    private int prevWidth;
    private int prevHeight;

    private Canvas canvas;
    private double hoffset;
    private double voffset;
    private final MouseDrawHandler drawHandler;

    private Field curField;
    private Brush curBrush;
    private Color gridColor;

    // todo: add highlighted object when placing objects
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

        prevHighlight = false;
        prevX = 0;
        prevY = 0;
        prevWidth = 1;
        prevHeight = 1;

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

            // remove possible highlights
            if (prevHighlight) {
                for (int i = prevX; i < prevX + prevWidth; ++i) {
                    for (int j = prevY; j < prevY + prevHeight; ++j) {
                        drawField(i, j);
                    }
                }
            }
        });
        vvalueProperty().addListener((observable, oldValue, newValue) -> {
            double vmin = getVmin();
            double vmax = getVmax();
            double vvalue = getVvalue();
            double contentHeight = canvas.getLayoutBounds().getHeight();
            double viewportHeight = getViewportBounds().getHeight();

            voffset = Math.max(0, contentHeight - viewportHeight) *
                    (vvalue - vmin) / (vmax - vmin);

            // remove possible highlights
            if (prevHighlight) {
                for (int i = prevX; i < prevX + prevWidth; ++i) {
                    for (int j = prevY; j < prevY + prevHeight; ++j) {
                        drawField(i, j);
                    }
                }
            }
        });

        drawHandler = new MouseDrawHandler(e -> {
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
        }, e -> {
            Bounds bounds = new BoundingBox(hoffset, voffset,
                    getViewportBounds().getWidth(),
                    getViewportBounds().getHeight());

            if (prevHighlight) {
                for (int i = prevX; i < prevX + prevWidth; ++i) {
                    for (int j = prevY; j < prevY + prevHeight; ++j) {
                        drawField(i, j);
                    }
                }
            }

            if (bounds.contains(e.getX(), e.getY())) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.save();

                if (curBrush == Brush.FIELD) {
                    int x = (int)(e.getX() / (size.get() + GRID_SIZE));
                    int y = (int)(e.getY() / (size.get() + GRID_SIZE));
                    double xPos = x * (size.get() + GRID_SIZE);
                    double yPos = y * (size.get() + GRID_SIZE);

                    gc.setGlobalAlpha(0.5d);
                    gc.setFill(HIGHLIGHT_COLOR);
                    gc.fillRect(xPos + 1, yPos + 1, size.get(), size.get());

                    prevX = x;
                    prevY = y;
                    prevHighlight = true;
                }

                gc.restore();
            }
        });
        canvas.addEventHandler(MouseEvent.ANY, drawHandler);

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

    public boolean isDrawing() {
        return drawHandler.isPressing();
    }

    public WritableImage snapshot() {
        return canvas.snapshot(null, null);
    }

    public DoubleProperty canvasWidthProperty() {
        return canvas.widthProperty();
    }

    public DoubleProperty canvasHeightProperty() {
        return canvas.heightProperty();
    }
}
