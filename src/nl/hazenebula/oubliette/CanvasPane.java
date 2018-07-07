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

public class CanvasPane extends ScrollPane {
    private static final Color HIGHLIGHT_COLOR = Color.DARKGRAY;

    public static final int GRIDLINE_SIZE = 1;
    public static final int MAX_SQUARE_SIZE = 60;
    public static final int MIN_SQUARE_SIZE = 10;
    public static final int INIT_SQUARE_SIZE = 30;

    private final IntegerProperty size;

    private Map map;

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
    private FieldObject curObject;
    private Wall curWall;

    public CanvasPane(Map map) {
        setStyle("-fx-focus-color: transparent;\n-fx-background: #D3D3D3");

        this.map = map;

        fieldWidthProperty = new SimpleIntegerProperty(this.map.getWidth());
        fieldHeightProperty = new SimpleIntegerProperty(this.map.getHeight());

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

        canvas = new Canvas(map.getWidth() * (size.get() + GRIDLINE_SIZE),
                map.getHeight() * (size.get() + GRIDLINE_SIZE));
        canvas.setStyle("-fx-focus-color: transparent;");

        setContent(canvas);

        curBrush = Brush.FIELD;
        curField = null;
        gridColor = Field.BLUE.color();
        curObject = null;

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
                FieldObject newObj = new FieldObject(curObject);
                newObj.setX(x);
                newObj.setY(y);
                map.getObjects().add(newObj);

                drawObject(newObj);
            } else if (curBrush == Brush.WALL_OBJECT) {
                double gridSize = size.get() + GRIDLINE_SIZE;
                Direction orient = (curWall.getDir()
                        == Direction.NORTH || curWall.getDir()
                        == Direction.SOUTH) ? Direction.NORTH :
                        Direction.EAST;
                if (curWall.getDir() == Direction.NORTH
                        || curWall.getDir() == Direction.SOUTH) {
                    double y1 = y * gridSize;
                    double y2 = (y + 1) * gridSize;
                    y = (e.getY() - y1 <= y2 - e.getY()) ? y - 1 : y;
                } else {
                    double x1 = x * gridSize;
                    double x2 = (x + 1) * gridSize;
                    x = (e.getX() - x1 <= x2 - e.getX()) ? x - 1 : x;
                }

                Wall wall = new Wall(curWall, x, y);

                if (orient == Direction.NORTH) {
                    for (int xIndex = x; xIndex < x + wall.getWidth();
                         ++xIndex) {
                        if (xIndex + 1 >= 0 && xIndex + 1
                                < map.getWallWidth()) {
                            Wall prevWall = map.getWall(xIndex + 1, y + 1,
                                    orient);
                            if (prevWall != null) {
                                for (int pX = prevWall.getX();
                                     pX < prevWall.getX() + prevWall.getWidth();
                                     ++pX) {
                                    if (pX + 1 >= 0
                                            && pX + 1 < map.getWallWidth()) {
                                        map.setWall(pX + 1, y + 1, orient,
                                                null);
                                    }
                                }
                            }

                            map.setWall(xIndex + 1, y + 1, orient, wall);
                        }
                    }
                } else {
                    for (int yIndex = y; yIndex < y + wall.getWidth();
                         ++yIndex) {
                        if (yIndex + 1 >= 0
                                && yIndex + 1 < map.getWallHeight()) {
                            Wall prevWall = map.getWall(x + 1, yIndex + 1,
                                    orient);
                            if (prevWall != null) {
                                for (int pY = prevWall.getY();
                                     pY < prevWall.getY() + prevWall.getWidth();
                                     ++pY) {
                                    if (pY + 1 >= 0 && pY + 1
                                            < map.getWallHeight()) {
                                        map.setWall(x + 1, pY + 1, orient,
                                                null);
                                    }
                                }
                            }

                            map.setWall(x + 1, yIndex + 1, orient, wall);
                        }
                    }
                }

                drawAll();
            } else if (curBrush == Brush.FIELD_OBJECT_ERASE) {
                for (FieldObject obj : map.getObjects()) {
                    if (obj.inBounds(x, y)) {
                        map.getObjects().remove(obj);
                        drawAll();
                        break;
                    }
                }
            } else if (curBrush == Brush.WALL_OBJECT_ERASE) {
                double gridSize = size.get() + GRIDLINE_SIZE;
                Direction orient = (curWall.getDir()
                        == Direction.NORTH || curWall.getDir()
                        == Direction.SOUTH) ? Direction.NORTH :
                        Direction.EAST;
                double x1 = x * gridSize;
                double x2 = (x + 1) * gridSize;
                x = (e.getX() - x1 <= x2 - e.getX()) ? x - 1 : x;

                double y1 = y * gridSize;
                double y2 = (y + 1) * gridSize;
                y = (e.getY() - y1 <= y2 - e.getY()) ? y - 1 : y;

                Wall wall = map.getWall(x + 1, y + 1, orient);

                if (wall != null) {
                    if (orient == Direction.NORTH) {
                        for (int pX = wall.getX(); pX < wall.getX()
                                + wall.getWidth(); ++pX) {
                            map.setWall(pX + 1, y + 1, orient, null);
                        }
                    } else {
                        for (int pY = wall.getY();
                             pY < wall.getY() + wall.getWidth(); ++pY) {
                            map.setWall(x + 1, pY + 1, orient, null);
                        }
                    }
                }

                drawAll();
            }
        }, e -> {
            Bounds bounds = new BoundingBox(hoffset, voffset,
                    getViewportBounds().getWidth(),
                    getViewportBounds().getHeight());

            if (bounds.contains(e.getX(), e.getY())) {
                int x = (int)(e.getX() / (size.get() + GRIDLINE_SIZE));
                int y = (int)(e.getY() / (size.get() + GRIDLINE_SIZE));

                if (x >= 0 && x < map.getWidth() && y >= 0
                        && y < map.getHeight()) {
                    if (curBrush == Brush.FIELD) {
                        map.setField(x, y, curField);
                        drawField(x, y);
                    }
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
                    double width = curObject.getWidth()
                            * (size.get() + GRIDLINE_SIZE);
                    double height = curObject.getHeight()
                            * (size.get() + GRIDLINE_SIZE);

                    Affine a = new Affine();
                    a.appendRotation(curObject.getDir().angle(),
                            xPos + width / 2, yPos + height / 2);
                    gc.setTransform(a);
                    gc.setGlobalAlpha(0.5d);

                    double xoffset = Math.sin(Math.toRadians(curObject
                            .getDir().angle())) * (width - height) / 2;
                    double yoffset = Math.sin(Math.toRadians(curObject
                            .getDir().angle())) * (width - height) / 2;

                    gc.drawImage(curObject.getImage(), xPos + xoffset,
                            yPos + yoffset, width, height);

                    gc.restore();

                    prevX = x;
                    prevY = y;
                    double c = Math.abs(Math.cos(Math.toRadians(curObject
                            .getDir().angle())));
                    double s = Math.abs(Math.sin(Math.toRadians(curObject
                            .getDir().angle())));
                    prevWidth = (int)(c * curObject.getWidth()
                            + s * curObject.getHeight());
                    prevHeight = (int)(c * curObject.getHeight()
                            + s * curObject.getWidth());
                    prevHighlight = true;
                } else if (curBrush == Brush.WALL_OBJECT) {
                    double gridSize = size.get() + GRIDLINE_SIZE;
                    if (curWall.getDir() == Direction.NORTH
                            || curWall.getDir() == Direction.SOUTH) {
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

                    double width = curWall.getWidth() * gridSize;

                    Affine a = new Affine();
                    a.appendRotation(curWall.getDir().angle(),
                            width / 2, gridSize / 2);
                    gc.setTransform(a);
                    gc.setGlobalAlpha(0.5d);

                    double xoffset = Math.sin(Math.toRadians(curWall
                            .getDir().angle())) * (width - gridSize) / 2 +
                            Math.cos(Math.toRadians(curWall.getDir()
                                    .angle())) * xPos
                            + Math.sin(Math.toRadians(curWall
                            .getDir().angle())) * yPos;
                    double yoffset = Math.sin(Math.toRadians(curWall
                            .getDir().angle())) * (width - gridSize) / 2
                            + Math.cos(Math.toRadians(curWall.getDir()
                            .angle())) * yPos
                            - Math.sin(Math.toRadians(curWall.getDir()
                            .angle())) * xPos
                            - Math.sin(Math.toRadians(curWall.getDir()
                            .angle())) * gridSize / 2
                            + Math.cos(Math.toRadians(curWall.getDir()
                            .angle())) * gridSize / 2;

                    gc.drawImage(curWall.getImage(), xoffset, yoffset,
                            width, gridSize);

                    gc.restore();

                    prevWallX = x;
                    prevWallY = y;
                    prevWallWidth = curWall.getWidth();
                    prevWallDir = curWall.getDir();
                    prevWallHighlight = true;
                }

                gc.restore();
            }
        });
        canvas.addEventHandler(MouseEvent.ANY, drawHandler);

        drawAll();

    }

    private void cleanHighlight() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double gridSize = size.get() + GRIDLINE_SIZE;

        if (prevHighlight && prevX >= 0 && prevX < map.getWidth()
                && prevY >= 0 && prevY < map.getHeight()) {
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

            for (FieldObject obj : map.getObjects()) {
                drawObject(obj);
            }

            for (int x = 0; x < map.getWallWidth(); ++x) {
                for (int y = 0; y < map.getWallHeight(); ++y) {
                    drawWall(map.getWall(x, y, Direction.NORTH));
                    drawWall(map.getWall(x, y, Direction.EAST));
                }
            }

            prevHighlight = false;
        }

        if (prevWallHighlight) {
            gc.setFill(gridColor);
            if (prevWallDir == Direction.NORTH
                    || prevWallDir == Direction.SOUTH) {
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

                for (FieldObject obj : map.getObjects()) {
                    for (int x = prevWallX; x < prevWallX + prevWallWidth;
                         ++x) {
                        for (int y = prevWallY; y <= prevWallY + 1; ++y) {
                            if (obj.inBounds(x, y)) {
                                drawObject(obj);
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

                for (FieldObject obj : map.getObjects()) {
                    for (int x = prevWallX; x <= prevWallX + 1; ++x) {
                        for (int y = prevWallY; y < prevWallY + prevWallWidth; ++y) {
                            if (obj.inBounds(x, y)) {
                                drawObject(obj);
                            }
                        }
                    }
                }
            }

            for (int x = 0; x < map.getWallWidth(); ++x) {
                for (int y = 0; y < map.getWallHeight(); ++y) {
                    drawWall(map.getWall(x, y, Direction.NORTH));
                    drawWall(map.getWall(x, y, Direction.EAST));
                }
            }

            prevWallHighlight = false;
        }
    }

    private void drawField(int x, int y) {
        if (x >= 0 && x < map.getWidth()
                && y >= 0 && y < map.getHeight()) {
            double xPos = x * (size.get() + GRIDLINE_SIZE);
            double yPos = y * (size.get() + GRIDLINE_SIZE);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(map.getField(x, y).color());
            gc.fillRect(xPos + 1, yPos + 1, size.get(), size.get());

            for (FieldObject obj : map.getObjects()) {
                if (obj.inBounds(x, y)) {
                    drawObject(obj);
                }
            }

            int xMin = Math.max(-1, x - 1);
            int yMin = Math.max(-1, y - 1);
            int xMax = Math.min(map.getWallWidth() - 1, x + 1);
            int yMax = Math.min(map.getWallHeight() - 1, y + 1);
            for (int xIndex = xMin; xIndex < xMax; ++xIndex) {
                for (int yIndex = yMin; yIndex < yMax; ++yIndex) {
                    drawWall(map.getWall(xIndex + 1, yIndex + 1,
                            Direction.NORTH));
                    drawWall(map.getWall(xIndex + 1, yIndex + 1,
                            Direction.EAST));
                }
            }
        }
    }

    private void drawObject(FieldObject obj) {
        double c = Math.abs(Math.cos(Math.toRadians(obj.getDir().angle())));
        double s = Math.abs(Math.sin(Math.toRadians(obj.getDir().angle())));
        int actualWidth = (int)(c * obj.getWidth() + s * obj.getHeight());
        int actualHeight = (int)(c * obj.getHeight() + s * obj.getWidth());
        if (obj.getX() >= 0
                && obj.getX() + actualWidth - 1 < map.getWidth()
                && obj.getY() >= 0
                && obj.getY() + actualHeight - 1
                < map.getHeight()) {
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

    private void drawWall(Wall wall) {
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

    public void drawAll() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(gridColor);

        for (int x = 0; x < map.getWidth(); ++x) {
            double xPos = x * (size.get() + GRIDLINE_SIZE);
            gc.fillRect(xPos, 0, 1.0d, canvas.getHeight());
        }

        for (int y = 0; y < map.getHeight(); ++y) {
            double yPos = y * (size.get() + GRIDLINE_SIZE);
            gc.fillRect(0, yPos, canvas.getWidth(), GRIDLINE_SIZE);
        }

        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                drawField(x, y);
            }
        }

        for (FieldObject obj : map.getObjects()) {
            drawObject(obj);
        }

        for (int x = 0; x < map.getWallWidth(); ++x) {
            for (int y = 0; y < map.getWallHeight(); ++y) {
                drawWall(map.getWall(x, y, Direction.NORTH));
                drawWall(map.getWall(x, y, Direction.EAST));
            }
        }
    }

    public void resize(int width, int height, Field fill) {
        map.resize(width, height, fill);

        fieldWidthProperty.setValue(width);
        fieldHeightProperty.setValue(height);

        canvas.setWidth(map.getWidth() * (size.get() + GRIDLINE_SIZE));
        canvas.setHeight(map.getHeight() * (size.get() + GRIDLINE_SIZE));
        drawAll();
    }

    public void setFieldColor(Field field) {
        curField = field;
    }

    public void setFieldObject(FieldObject object) {
        this.curObject = object;
    }

    public void setWallObject(Wall wall) {
        this.curWall = wall;
    }

    public void setGridColor(Color color) {
        gridColor = color;
    }

    // todo: change setGridSize to setSquareSize
    public void setGridSize(int newSize) {
        if (newSize >= MIN_SQUARE_SIZE && newSize <= MAX_SQUARE_SIZE) {
            size.set(newSize);

            canvas.setWidth(map.getWidth() * (size.get() + GRIDLINE_SIZE));
            canvas.setHeight(map.getHeight() * (size.get() + GRIDLINE_SIZE));
            drawAll();
        }
    }

    public void setBrush(Brush newBrush) {
        curBrush = newBrush;
    }

    public void setMap(Map map) {
        this.map.setFields(map.getFields());
        this.map.setObjects(map.getObjects());
        this.map.setWalls(map.getWalls());

        fieldWidthProperty().setValue(map.getWidth());
        fieldHeightProperty().setValue(map.getHeight());
        canvas.setWidth(map.getWidth() * (size.get() + GRIDLINE_SIZE));
        canvas.setHeight(map.getHeight() * (size.get() + GRIDLINE_SIZE));
        drawAll();
    }

    public boolean isDrawing() {
        return drawHandler.isPressing();
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

    public Map getMap() {
        return map;
    }
}
