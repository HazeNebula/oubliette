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
import javafx.scene.transform.Affine;

import java.util.LinkedList;
import java.util.List;

public class CanvasPane extends ScrollPane {
    private static final Color HIGHLIGHT_COLOR = Color.DARKGRAY;

    public static final int GRIDLINE_SIZE = 1;
    public static final int MAX_SQUARE_SIZE = 60;
    public static final int MIN_SQUARE_SIZE = 10;
    public static final int INIT_SQUARE_SIZE = 30;

    private final IntegerProperty size;

    private Field[][] fieldGrid;
    private List<FieldObject> fieldObjects;
    private WallObject[][][] wallGrid;
    private boolean prevHighlight;
    private int prevX;
    private int prevY;
    private int prevWidth;
    private int prevHeight;
    private boolean prevWallHighlight;
    private int prevWallX;
    private int prevWallY;
    private int prevWallWidth;
    private Direction prevWallDir;

    private Canvas canvas;
    private IntegerProperty fieldWidthProperty;
    private IntegerProperty fieldHeightProperty;
    private double hoffset;
    private double voffset;
    private final MouseDrawHandler drawHandler;

    private Brush curBrush;
    private Field curField;
    private Color gridColor;
    private FieldObject curFieldObject;
    private WallObject curWallObject;

    public CanvasPane(int gridWidth, int gridHeight) {
        setStyle("-fx-focus-color: transparent;\n-fx-background: #D3D3D3");

        fieldWidthProperty = new SimpleIntegerProperty(gridWidth);
        fieldHeightProperty = new SimpleIntegerProperty(gridHeight);
        fieldGrid = new Field[gridWidth][gridHeight];
        for (int x = 0; x < fieldGrid.length; ++x) {
            for (int y = 0; y < fieldGrid[x].length; ++y) {
                fieldGrid[x][y] = Field.EMPTY;
            }
        }

        wallGrid = new WallObject[gridWidth + 1][gridHeight + 1][2];

        prevHighlight = false;
        prevX = 0;
        prevY = 0;
        prevWidth = 1;
        prevHeight = 1;

        prevWallHighlight = false;
        prevWallX = 0;
        prevWallY = 0;
        prevWallWidth = 1;
        prevWallDir = Direction.NORTH;

        size = new SimpleIntegerProperty(INIT_SQUARE_SIZE);

        canvas = new Canvas(fieldGrid.length * (size.get() + GRIDLINE_SIZE),
                fieldGrid[0].length * (size.get() + GRIDLINE_SIZE));
        canvas.setStyle("-fx-focus-color: transparent;");

        setContent(canvas);

        curBrush = Brush.FIELD;
        curField = null;
        gridColor = Field.FILLED.color();
        curFieldObject = null;
        fieldObjects = new LinkedList<>();

        hvalueProperty().addListener((observable, oldValue, newValue) -> {
            double hmin = getHmin();
            double hmax = getHmax();
            double hvalue = getHvalue();
            double contentWidth = canvas.getLayoutBounds().getWidth();
            double viewportWidth = getViewportBounds().getWidth();

            hoffset = Math.max(0, contentWidth - viewportWidth) *
                    (hvalue - hmin) / (hmax - hmin);

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

            cleanHighlight();
        });

        drawHandler = new MouseDrawHandler(e -> {
            int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
            int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));

            if (curBrush == Brush.FIELD_OBJECT) {
                FieldObject newObj = new FieldObject(curFieldObject);
                newObj.setX(x);
                newObj.setY(y);
                fieldObjects.add(newObj);

                drawFieldObject(newObj);
            } else if (curBrush == Brush.WALL_OBJECT) {
                double gridSize = size.get() + GRIDLINE_SIZE;
                Direction orient = (curWallObject.getDir()
                        == Direction.NORTH || curWallObject.getDir()
                        == Direction.SOUTH) ? Direction.NORTH :
                        Direction.EAST;
                if (curWallObject.getDir() == Direction.NORTH
                        || curWallObject.getDir() == Direction.SOUTH) {
                    double y1 = y * gridSize;
                    double y2 = (y + 1) * gridSize;
                    y = (e.getY() - y1 <= y2 - e.getY()) ? y - 1 : y;
                } else {
                    double x1 = x * gridSize;
                    double x2 = (x + 1) * gridSize;
                    x = (e.getX() - x1 <= x2 - e.getX()) ? x - 1 : x;
                }

                WallObject wall = new WallObject(curWallObject, x, y);

                if (orient == Direction.NORTH) {
                    for (int xIndex = x; xIndex < x + wall.getWidth();
                         ++xIndex) {
                        if (xIndex + 1 >= 0 && xIndex + 1 < wallGrid.length) {
                            WallObject prevWall = wallGrid[xIndex + 1][y + 1]
                                    [orient.id()];
                            if (prevWall != null) {
                                for (int pX = prevWall.getX();
                                     pX < prevWall.getX() + prevWall.getWidth();
                                     ++pX) {
                                    if (pX + 1 >= 0
                                            && pX + 1 < wallGrid.length) {
                                        wallGrid[pX + 1][y + 1][orient.id()] =
                                                null;
                                    }
                                }
                            }

                            wallGrid[xIndex + 1][y + 1][orient.id()] = wall;
                        }
                    }
                } else {
                    for (int yIndex = y; yIndex < y + wall.getWidth();
                         ++yIndex) {
                        if (yIndex + 1 >= 0
                                && yIndex + 1 < wallGrid[x].length) {
                            WallObject prevWall = wallGrid[x + 1][yIndex + 1]
                                    [orient.id()];
                            if (prevWall != null) {
                                for (int pY = prevWall.getY();
                                     pY < prevWall.getY() + prevWall.getWidth();
                                     ++pY) {
                                    if (pY + 1 >= 0 && pY + 1
                                            < wallGrid[x + 1].length) {
                                        wallGrid[x + 1][pY + 1][orient.id()] =
                                                null;
                                    }
                                }
                            }

                            wallGrid[x + 1][yIndex + 1][orient.id()] = wall;
                        }
                    }
                }

                drawFullGrid();
            } else if (curBrush == Brush.FIELD_OBJECT_ERASE) {
                for (FieldObject obj : fieldObjects) {
                    if (obj.inBounds(x, y)) {
                        fieldObjects.remove(obj);
                        drawFullGrid();
                        break;
                    }
                }
            } else if (curBrush == Brush.WALL_OBJECT_ERASE) {
                double gridSize = size.get() + GRIDLINE_SIZE;
                Direction orient = (curWallObject.getDir()
                        == Direction.NORTH || curWallObject.getDir()
                        == Direction.SOUTH) ? Direction.NORTH :
                        Direction.EAST;
                double x1 = x * gridSize;
                double x2 = (x + 1) * gridSize;
                x = (e.getX() - x1 <= x2 - e.getX()) ? x - 1 : x;

                double y1 = y * gridSize;
                double y2 = (y + 1) * gridSize;
                y = (e.getY() - y1 <= y2 - e.getY()) ? y - 1 : y;

                if (orient == Direction.NORTH) {
                    WallObject wall = wallGrid[x + 1][y + 1][orient.id()];
                    if (wall != null) {
                        for (int pX = wall.getX(); pX < wall.getX()
                                + wall.getWidth(); ++pX) {
                            wallGrid[pX + 1][y + 1][orient.id()] = null;
                        }
                    }
                } else {
                    WallObject wall = wallGrid[x + 1][y + 1][orient.id()];
                    if (wall != null) {
                        for (int pY = wall.getY();
                             pY < wall.getY() + wall.getWidth(); ++pY) {
                            wallGrid[x + 1][pY + 1][orient.id()] = null;
                        }
                    }
                }

                drawFullGrid();
            }
        }, e -> {
            Bounds bounds = new BoundingBox(hoffset, voffset,
                    getViewportBounds().getWidth(),
                    getViewportBounds().getHeight());

            if (bounds.contains(e.getX(), e.getY())) {
                int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
                int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));

                if (curBrush == Brush.FIELD) {
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
                int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
                int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));

                if (curBrush == Brush.FIELD) {
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
                } else if (curBrush == Brush.WALL_OBJECT) {
                    double gridSize = size.get() + GRIDLINE_SIZE;
                    if (curWallObject.getDir() == Direction.NORTH
                            || curWallObject.getDir() == Direction.SOUTH) {
                        double y1 = y * gridSize;
                        double y2 = (y + 1) * gridSize;
                        y = (e.getY() - y1 <= y2 - e.getY()) ? y - 1 : y;
                    } else {
                        double x1 = x * gridSize;
                        double x2 = (x + 1) * gridSize;
                        x = (e.getX() - x1 <= x2 - e.getX()) ? x - 1 : x;
                    }

                    double xPos = x * gridSize;
                    double yPos = y * gridSize;

                    gc.save();

                    double width = curWallObject.getWidth() * gridSize;

                    Affine a = new Affine();
                    a.appendRotation(curWallObject.getDir().angle(),
                            width / 2, gridSize / 2);
                    gc.setTransform(a);
                    gc.setGlobalAlpha(0.5d);

                    double xoffset = Math.sin(Math.toRadians(curWallObject
                            .getDir().angle())) * (width - gridSize) / 2 +
                            Math.cos(Math.toRadians(curWallObject.getDir()
                                    .angle())) * xPos
                            + Math.sin(Math.toRadians(curWallObject
                            .getDir().angle())) * yPos;
                    double yoffset = Math.sin(Math.toRadians(curWallObject
                            .getDir().angle())) * (width - gridSize) / 2
                            + Math.cos(Math.toRadians(curWallObject.getDir()
                            .angle())) * yPos
                            - Math.sin(Math.toRadians(curWallObject.getDir()
                            .angle())) * xPos
                            - Math.sin(Math.toRadians(curWallObject.getDir()
                            .angle())) * gridSize / 2
                            + Math.cos(Math.toRadians(curWallObject.getDir()
                            .angle())) * gridSize / 2;

                    gc.drawImage(curWallObject.getImage(), xoffset, yoffset,
                            width, gridSize);

                    gc.restore();

                    prevWallX = x;
                    prevWallY = y;
                    prevWallWidth = curWallObject.getWidth();
                    prevWallDir = curWallObject.getDir();
                    prevWallHighlight = true;
                }

                gc.restore();
            }
        });
        canvas.addEventHandler(MouseEvent.ANY, drawHandler);

        drawFullGrid();

    }

    private void cleanHighlight() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double gridSize = size.get() + GRIDLINE_SIZE;

        if (prevHighlight && prevX >= 0 && prevX < fieldGrid.length
                && prevY >= 0 && prevY < fieldGrid[prevX].length) {
            gc.setFill(gridColor);
            double x1 = prevX * gridSize;
            double x2 = prevWidth * gridSize;
            for (int y = prevY; y < prevY + prevHeight; ++y) {
                double yPos = y * gridSize;
                gc.fillRect(x1, yPos, x2, GRIDLINE_SIZE);
            }

            double y1 = prevY * gridSize;
            double y2 = prevHeight * gridSize;
            for (int i = prevX; i < prevX + prevWidth; ++i) {
                for (int j = prevY; j < prevY + prevHeight; ++j) {
                    drawField(i, j);
                }

                gc.setFill(gridColor);
                double xPos = i * gridSize;
                gc.fillRect(xPos, y1, GRIDLINE_SIZE, y2);
            }

            for (FieldObject obj : fieldObjects) {
                drawFieldObject(obj);
            }

            for (int x = 0; x < wallGrid.length; ++x) {
                for (int y = 0; y < wallGrid[x].length; ++y) {
                    drawWallObject(wallGrid[x][y][Direction.NORTH.id()]);
                    drawWallObject(wallGrid[x][y][Direction.EAST.id()]);
                }
            }

            prevHighlight = false;
        }

        if (prevWallHighlight) {
            gc.setFill(gridColor);
            if (prevWallDir == Direction.NORTH
                    || prevWallDir == Direction.SOUTH) {
                // draw grid lines
                for (int x = prevWallX; x < prevWallX + prevWallWidth; ++x) {
                    gc.fillRect(x * gridSize, prevWallY * gridSize,
                            GRIDLINE_SIZE, 2 * gridSize);
                }
                gc.fillRect(prevWallX * gridSize, (prevWallY + 1) * gridSize,
                        prevWallWidth * gridSize, GRIDLINE_SIZE);

                for (int x = prevWallX; x < prevWallX + prevWallWidth; ++x) {
                    for (int y = prevWallY; y <= prevWallY + 1; ++y) {
                        drawField(x, y);
                    }
                }

                // draw field objects
                for (FieldObject obj : fieldObjects) {
                    for (int x = prevWallX; x < prevWallX + prevWallWidth;
                         ++x) {
                        for (int y = prevWallY; y <= prevWallY + 1; ++y) {
                            if (obj.inBounds(x, y)) {
                                drawFieldObject(obj);
                            }
                        }
                    }
                }
            } else {
                for (int y = prevWallY; y < prevWallY + prevWallWidth; ++y) {
                    gc.fillRect(prevWallX * gridSize, y * gridSize,
                            2 * gridSize, GRIDLINE_SIZE);
                }
                gc.fillRect((prevWallX + 1) * gridSize, prevWallY * gridSize,
                        GRIDLINE_SIZE, prevWallWidth * gridSize);

                for (int x = prevWallX; x <= prevWallX + 1; ++x) {
                    for (int y = prevWallY; y < prevWallY + prevWallWidth; ++y) {
                        drawField(x, y);
                    }
                }

                for (FieldObject obj : fieldObjects) {
                    for (int x = prevWallX; x <= prevWallX + 1; ++x) {
                        for (int y = prevWallY; y < prevWallY + prevWallWidth; ++y) {
                            if (obj.inBounds(x, y)) {
                                drawFieldObject(obj);
                            }
                        }
                    }
                }
            }

            for (int x = 0; x < wallGrid.length; ++x) {
                for (int y = 0; y < wallGrid[x].length; ++y) {
                    drawWallObject(wallGrid[x][y][Direction.NORTH.id()]);
                    drawWallObject(wallGrid[x][y][Direction.EAST.id()]);
                }
            }

            prevWallHighlight = false;
        }
    }

    private void drawField(int x, int y) {
        if (x >= 0 && x < fieldGrid.length
                && y >= 0 && y < fieldGrid[x].length) {
            double xPos = x * (size.get() + GRIDLINE_SIZE);
            double yPos = y * (size.get() + GRIDLINE_SIZE);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(fieldGrid[x][y].color());
            gc.fillRect(xPos + 1, yPos + 1, size.get(), size.get());

            for (FieldObject obj : fieldObjects) {
                if (obj.inBounds(x, y)) {
                    drawFieldObject(obj);
                }
            }

            int xMin = Math.max(-1, x - 1);
            int yMin = Math.max(-1, y - 1);
            int xMax = Math.min(wallGrid.length - 1, x + 1);
            int yMax = Math.min(wallGrid[0].length - 1, y + 1);
            for (int xIndex = xMin; xIndex < xMax; ++xIndex) {
                for (int yIndex = yMin; yIndex < yMax; ++yIndex) {
                    drawWallObject(wallGrid[xIndex + 1][yIndex + 1]
                            [Direction.NORTH.id()]);
                    drawWallObject(wallGrid[xIndex + 1][yIndex + 1]
                            [Direction.EAST.id()]);
                }
            }
        }
    }

    private void drawFieldObject(FieldObject obj) {
        double c = Math.abs(Math.cos(Math.toRadians(obj.getDir().angle())));
        double s = Math.abs(Math.sin(Math.toRadians(obj.getDir().angle())));
        int actualWidth = (int)(c * obj.getWidth() + s * obj.getHeight());
        int actualHeight = (int)(c * obj.getHeight() + s * obj.getWidth());
        if (obj.getX() >= 0
                && obj.getX() + actualWidth - 1 < fieldGrid.length
                && obj.getY() >= 0
                && obj.getY() + actualHeight - 1
                < fieldGrid[obj.getX()].length) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.save();

            double xPos = obj.getX() * (size.get() + GRIDLINE_SIZE);
            double yPos = obj.getY() * (size.get() + GRIDLINE_SIZE);
            double width = obj.getWidth() * (size.get() + GRIDLINE_SIZE);
            double height = obj.getHeight() * (size.get() + GRIDLINE_SIZE);

            Affine a = new Affine();
            a.appendRotation(obj.getDir().angle(), xPos + width / 2,
                    yPos + height / 2);
            gc.setTransform(a);

            double xoffset = Math.sin(Math.toRadians(obj.getDir().angle()))
                    * (width - height) / 2;
            double yoffset = Math.sin(Math.toRadians(obj.getDir().angle()))
                    * (width - height) / 2;

            gc.drawImage(obj.getImage(), xPos + xoffset, yPos + yoffset, width,
                    height);

            gc.restore();
        }
    }

    private void drawWallObject(WallObject wall) {
        if (wall != null) {
            double gridSize = size.get() + GRIDLINE_SIZE;
            double xPos = wall.getX() * gridSize;
            double yPos = wall.getY() * gridSize;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.save();

            double width = wall.getWidth() * gridSize;

            Affine a = new Affine();
            a.appendRotation(wall.getDir().angle(),
                    width / 2, gridSize / 2);
            gc.setTransform(a);

            double xoffset = Math.sin(Math.toRadians(wall.getDir().angle()))
                    * (width - gridSize) / 2 + Math.cos(Math.toRadians(
                    wall.getDir().angle())) * xPos + Math.sin(Math.toRadians(
                    wall.getDir().angle())) * yPos;
            double yoffset = Math.sin(Math.toRadians(wall.getDir().angle()))
                    * (width - gridSize) / 2 + Math.cos(Math.toRadians(
                    wall.getDir().angle())) * yPos - Math.sin(Math.toRadians(
                    wall.getDir().angle())) * xPos - Math.sin(Math.toRadians(
                    wall.getDir().angle())) * gridSize / 2 + Math.cos(
                    Math.toRadians(wall.getDir().angle())) * gridSize / 2;

            gc.drawImage(wall.getImage(), xoffset, yoffset,
                    width, gridSize);

            gc.restore();
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

        // draw field objects
        for (FieldObject obj : fieldObjects) {
            drawFieldObject(obj);
        }

        // draw wall objects
        for (WallObject[][] wallGridRow : wallGrid) {
            for (WallObject[] wallGridSquare : wallGridRow) {
                drawWallObject(wallGridSquare[Direction.NORTH.id()]);
                drawWallObject(wallGridSquare[Direction.EAST.id()]);
            }
        }
    }

    public void setFieldColor(Field field) {
        curField = field;
    }

    public void setFieldObject(FieldObject fieldObject) {
        this.curFieldObject = fieldObject;
    }

    public void setWallObject(WallObject wallObject) {
        this.curWallObject = wallObject;
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

    public void setFields(Field[][] fieldGrid) {
        this.fieldGrid = fieldGrid;
    }

    public void setFieldObjects(List<FieldObject> fieldObjects) {
        this.fieldObjects = fieldObjects;
    }

    public void setWallGrid(WallObject[][][] wallGrid) {
        this.wallGrid = wallGrid;
    }

    public WritableImage snapshot() {
        return canvas.snapshot(null, null);
    }

    public IntegerProperty fieldWidthProperty() {
        return fieldWidthProperty;
    }

    public IntegerProperty fieldHeightProperty() {
        return fieldHeightProperty;
    }

    public int getGridSize() {
        return size.get();
    }

    public Field[][] getFields() {
        return fieldGrid;
    }

    public List<FieldObject> getFieldObjects() {
        return fieldObjects;
    }

    public WallObject[][][] getWallGrid() {
        return wallGrid;
    }
}
