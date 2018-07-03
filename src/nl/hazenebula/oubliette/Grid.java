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
import javafx.scene.transform.Affine;

import java.util.List;

public class Grid extends ScrollPane {
    private static final Color HIGHLIGHT_COLOR = Color.DARKGRAY;

    public static final int GRIDLINE_SIZE = 1;
    public static final int MAX_SQUARE_SIZE = 50;
    public static final int MIN_SQUARE_SIZE = 10;
    public static final int INIT_SQUARE_SIZE = 20;

    private final IntegerProperty size;

    private Field[][] fieldGrid;
    private List<FieldObject> fieldObjects;
    private boolean prevHighlight;
    private int prevX;
    private int prevY;
    private int prevWidth;
    private int prevHeight;

    private Canvas canvas;
    private double hoffset;
    private double voffset;
    private final MouseDrawHandler drawHandler;

    private Brush curBrush;
    private Field curField;
    private Color gridColor;
    private FieldObject curFieldObject;

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

        canvas = new Canvas(fieldGrid.length * (size.get() + GRIDLINE_SIZE),
                fieldGrid[0].length * (size.get() + GRIDLINE_SIZE));
        canvas.setStyle("-fx-focus-color: transparent;");

        setContent(canvas);

        curBrush = Brush.FIELD;
        curField = Field.EMPTY;
        gridColor = Field.FILLED.color();
        curFieldObject = null;

        hvalueProperty().addListener((observable, oldValue, newValue) -> {
            double hmin = getHmin();
            double hmax = getHmax();
            double hvalue = getHvalue();
            double contentWidth = canvas.getLayoutBounds().getWidth();
            double viewportWidth = getViewportBounds().getWidth();

            hoffset = Math.max(0, contentWidth - viewportWidth) *
                    (hvalue - hmin) / (hmax - hmin);

            // remove possible highlights
            cleanHighlight();
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
            cleanHighlight();
        });

        drawHandler = new MouseDrawHandler(e -> {
            int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
            int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));

            if (curBrush == Brush.FIELD_OBJECT) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                // highptodo: draw field object
            }
        }, e -> {
            Bounds bounds = new BoundingBox(hoffset, voffset,
                    getViewportBounds().getWidth(),
                    getViewportBounds().getHeight());

            if (bounds.contains(e.getX(), e.getY())) {
                if (curBrush == Brush.FIELD) {
                    int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
                    int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));

                    fieldGrid[x][y] = curField;
                    drawField(x, y);
                }
            }
        }, e -> {
            Bounds bounds = new BoundingBox(hoffset, voffset,
                    getViewportBounds().getWidth(),
                    getViewportBounds().getHeight());

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.save();

            cleanHighlight();

            if (bounds.contains(e.getX(), e.getY())) {
                if (curBrush == Brush.FIELD) {
                    int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
                    int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));
                    double xPos = x * (size.get() + GRIDLINE_SIZE);
                    double yPos = y * (size.get() + GRIDLINE_SIZE);

                    gc.setGlobalAlpha(0.5d);
                    gc.setFill(HIGHLIGHT_COLOR);
                    gc.fillRect(xPos + 1, yPos + 1, size.get(), size.get());

                    prevX = x;
                    prevY = y;
                    prevWidth = 1;
                    prevHeight = 1;
                    prevHighlight = true;
                } else if (curBrush == Brush.FIELD_OBJECT) {
                    // highptodo: add rotation to highlight
                    int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
                    int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));
                    double xPos = x * (size.get() + GRIDLINE_SIZE);
                    double yPos = y * (size.get() + GRIDLINE_SIZE);
                    double width = curFieldObject.getWidth()
                            * (size.get() + GRIDLINE_SIZE);
                    double height = curFieldObject.getHeight()
                            * (size.get() + GRIDLINE_SIZE);

                    Affine a = new Affine();
                    a.appendRotation(curFieldObject.getDir().angle(),
                            xPos + width / 2, yPos + height / 2);
                    gc.setTransform(a);
                    gc.setGlobalAlpha(0.5d);

                    double xoffset = Math.sin(Math.toRadians(curFieldObject
                            .getDir().angle())) * (width - height) / 2;
                    double yoffset = Math.sin(Math.toRadians(curFieldObject
                            .getDir().angle())) * (width - height) / 2;

                    gc.drawImage(curFieldObject.getImage(), xPos + xoffset,
                            yPos + yoffset, width, height);

                    gc.restore();

                    prevX = x;
                    prevY = y;
                    double c = Math.abs(Math.cos(Math.toRadians(curFieldObject
                            .getDir().angle())));
                    double s = Math.abs(Math.sin(Math.toRadians(curFieldObject
                            .getDir().angle())));
                    prevWidth = (int)(c * curFieldObject.getWidth()
                            + s * curFieldObject.getHeight());
                    prevHeight = (int)(c * curFieldObject.getHeight()
                            + s * curFieldObject.getWidth());
                    prevHighlight = true;
                }

                gc.restore();
            }
        });
        canvas.addEventHandler(MouseEvent.ANY, drawHandler);

        drawFullGrid();
    }

    private void cleanHighlight() {
        if (prevHighlight) {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(gridColor);
            for (int y = prevY; y < prevY + prevHeight; ++y) {
                double yPos = y * (size.get() + GRIDLINE_SIZE);
                gc.fillRect(0, yPos, canvas.getWidth(), GRIDLINE_SIZE);
            }

            for (int i = prevX; i < prevX + prevWidth; ++i) {
                for (int j = prevY; j < prevY + prevHeight; ++j) {
                    drawField(i, j);
                }

                gc.setFill(gridColor);
                double xPos = i * (size.get() + GRIDLINE_SIZE);
                gc.fillRect(xPos, 0, GRIDLINE_SIZE, canvas.getHeight());
            }
        }
    }

    private void drawField(int x, int y) {
        if (x >= 0 && x < fieldGrid.length
                && y >= 0 && y < fieldGrid[y].length) {
            double xPos = x * (size.get() + GRIDLINE_SIZE);
            double yPos = y * (size.get() + GRIDLINE_SIZE);
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
            double xPos = x * (size.get() + GRIDLINE_SIZE);
            gc.fillRect(xPos, 0, 1.0d, canvas.getHeight());
        }

        for (int y = 0; y < fieldGrid[0].length; ++y) {
            double yPos = y * (size.get() + GRIDLINE_SIZE);
            gc.fillRect(0, yPos, canvas.getWidth(), GRIDLINE_SIZE);
        }

        // draw fields
        for (int x = 0; x < fieldGrid.length; ++x) {
            for (int y = 0; y < fieldGrid[x].length; ++y) {
                drawField(x, y);
            }
        }

        // highptodo: draw objects

        // todo: draw wall objects
    }

    public void setFieldColor(Field field) {
        curField = field;
    }

    public void setFieldObject(FieldObject fieldObject) {
        this.curFieldObject = fieldObject;
    }

    public void setGridColor(Color color) {
        gridColor = color;
    }

    public void setGridSize(int newSize) {
        if (newSize >= MIN_SQUARE_SIZE && newSize <= MAX_SQUARE_SIZE) {
            size.set(newSize);

            canvas.setWidth(fieldGrid.length * (size.get() + GRIDLINE_SIZE));
            canvas.setHeight(fieldGrid[0].length * (size.get() + GRIDLINE_SIZE));
            drawFullGrid();
        }
    }

    public void setBrush(Brush newBrush) {
        curBrush = newBrush;
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

    public IntegerProperty sizeProperty() {
        return size;
    }
}
