package nl.hazenebula.oubliette;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class WallObjectPane extends GridPane {
    private static final Color BACK_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = Color.BLACK;
    private static final double BUTTON_SIZE = 80.0d;

    private int maxSize;

    private Grid grid;
    private Canvas resizeCanvas;
    private WallObjectImage curImg;
    private FieldObject curObject;

    public WallObjectPane(Grid grid) {
        this.grid = grid;

        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);

        resizeCanvas = new Canvas();
        resizeCanvas.widthProperty().bind(widthProperty().divide(2.0d));
        resizeCanvas.heightProperty().bind(resizeCanvas.widthProperty());
        resizeCanvas.widthProperty().addListener((observable, oldValue,
                                                  newValue) -> drawCanvas());
        GridPane.setHgrow(resizeCanvas, Priority.ALWAYS);
        GridPane.setHalignment(resizeCanvas, HPos.CENTER);

        GridPane resizeWrapper = new GridPane();
        resizeWrapper.add(resizeCanvas, 0, 0);

        setHgrow(resizeWrapper, Priority.ALWAYS);
        setHalignment(resizeWrapper, HPos.CENTER);

        Label sizeLabel = new Label("Size:");
        GridPane.setHgrow(sizeLabel, Priority.ALWAYS);
        Button increaseSizeButton = new Button("+");
        increaseSizeButton.setOnAction(e -> {
            if (curObject.getWidth() < maxSize) {
                curObject.setWidth(curObject.getWidth() + 1);
                curObject.setImage(curImg.getImage(curObject.getWidth()));
                drawCanvas();

                grid.setFieldObject(curObject);
            }
        });
        Button decreaseSizeButton = new Button("-");
        decreaseSizeButton.setOnAction(e -> {
            if (curObject.getWidth() > 1) {
                curObject.setWidth(curObject.getWidth() - 1);
                curObject.setImage(curImg.getImage(curObject.getWidth()));
                drawCanvas();

                grid.setFieldObject(curObject);
            }
        });
        Label rotateLabel = new Label("Rotate:");
        GridPane.setHgrow(rotateLabel, Priority.ALWAYS);
        Button rotateClockwiseButton = new Button("\u21BB");
        rotateClockwiseButton.setOnAction(e -> {
            curObject.setDir(curObject.getDir().next());
            drawCanvas();

            grid.setFieldObject(curObject);
        });
        Button rotateCounterclockwiseButton = new Button("\u21BA");
        rotateCounterclockwiseButton.setOnAction(e -> {
            curObject.setDir(curObject.getDir().prev());
            drawCanvas();

            grid.setFieldObject(curObject);
        });

        GridPane shapeButtonPane = new GridPane();
        shapeButtonPane.add(sizeLabel, 0, 0);
        shapeButtonPane.add(increaseSizeButton, 1, 0);
        shapeButtonPane.add(decreaseSizeButton, 2, 0);
        shapeButtonPane.add(rotateClockwiseButton, 1, 1);
        shapeButtonPane.add(rotateCounterclockwiseButton, 2, 1);

        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton eraseButton = new ToggleButton("Erase");
        eraseButton.setToggleGroup(toggleGroup);
        eraseButton.setMaxWidth(Double.MAX_VALUE);
        eraseButton.setOnAction(e -> grid.setBrush(Brush.WALL_OBJECT_ERASE));
        setHgrow(eraseButton, Priority.ALWAYS);
        setHalignment(eraseButton, HPos.CENTER);

        ScrollPane buttonPane = new ScrollPane();
        buttonPane.setFitToWidth(true);
        buttonPane.setStyle("-fx-background: #D3D3D3");

        FlowPane buttons = new FlowPane();
        buttons.setPadding(new Insets(5, 5, 5, 5));
        setVgap(5);
        setHgap(5);
        buttons.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY, null, null)));
        buttonPane.setContent(buttons);

        List<WallObjectImage> objects = loadObjects();

        ToggleButton firstButton = new ToggleButton();
        for (WallObjectImage img : objects) {
            ToggleButton button = new ToggleButton();

            if (img == objects.get(0)) {
                firstButton = button;
            }

            ImageView graphic = new ImageView(img.getImage(1));
            graphic.setFitWidth(BUTTON_SIZE);
            graphic.setFitHeight(BUTTON_SIZE);
            button.setGraphic(graphic);
            button.setTooltip(new Tooltip(img.getName()));

            button.setOnAction(e -> {
                grid.setBrush(Brush.WALL_OBJECT);

                if (curImg != img) {
                    curImg = img;
                    curObject = new FieldObject(curImg.getImage(
                            curObject.getWidth()),
                            curObject.getX(), curObject.getY(),
                            curObject.getWidth(), curObject.getHeight(),
                            curObject.getDir());
                    drawCanvas();

                    grid.setFieldObject(new FieldObject(curObject));
                }
            });

            button.setToggleGroup(toggleGroup);
            buttons.getChildren().add(button);
        }

        setVgrow(buttonPane, Priority.ALWAYS);

        curImg = objects.get(0);
        curObject = new FieldObject(curImg.getImage(1), 0, 0, 1, 1,
                Direction.NORTH);
        grid.setFieldObject(curObject);
        firstButton.setSelected(true);

        drawCanvas();

        add(resizeWrapper, 0, 0);
        add(shapeButtonPane, 0, 1);
        add(eraseButton, 0, 2);
        add(buttonPane, 0, 3);
    }

    private List<WallObjectImage> loadObjects() {
        List<List<File>> files = ObjectFileSearcher.getObjects(
                ObjectFileSearcher.WALLOBJECT_DIR);
        maxSize = 1;
        List<WallObjectImage> objects = new LinkedList<>();

        for (List<File> objectFiles : files) {
            int size = objectFiles.size();
            WallObjectImage img = new WallObjectImage(size);

            if (size > maxSize) {
                maxSize = size;
            }

            for (File objFile : objectFiles) {
                String filename = objFile.getName();
                int lastHyphenIndex = filename.lastIndexOf('-');

                if (img.getName() == null) {
                    String[] words = filename.substring(0, lastHyphenIndex)
                            .split("-");
                    String name = String.join(" ", Arrays.stream(words).map(s ->
                    {
                        char[] chars = s.toCharArray();
                        chars[0] = Character.toUpperCase(chars[0]);
                        return String.valueOf(chars);
                    }).collect(Collectors.toList()));

                    img.setName(name);
                }

                if (lastHyphenIndex > -1) {
                    String[] sizesStr = filename.substring(lastHyphenIndex + 1,
                            filename.indexOf('.')).split("x");
                    int width = 1;

                    try {
                        width = Integer.parseInt(sizesStr[0]);
                    } catch (NumberFormatException e) {
                        System.err.println("Could not format as number: " +
                                sizesStr[0]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Could not parse file name: " +
                                filename);
                    }

                    img.setImage(width, objFile);
                }
            }

            if (img.completed()) {
                objects.add(img);
            }
        }

        Collections.sort(objects);

        return objects;
    }

    private void drawCanvas() {
        GraphicsContext gc = resizeCanvas.getGraphicsContext2D();
        double gridSize = resizeCanvas.getWidth() / maxSize;

        // clear canvas
        gc.setFill(BACK_COLOR);
        gc.fillRect(0, 0, resizeCanvas.getWidth(), resizeCanvas.getHeight());

        // draw grid lines
        gc.setFill(GRID_COLOR);
        for (int x = 0; x < maxSize; ++x) {
            double xPos = x * gridSize;
            gc.fillRect(xPos, 0, 1.0d, resizeCanvas.getHeight());
        }

        for (int y = 0; y < maxSize; ++y) {
            double yPos = y * gridSize;
            gc.fillRect(0, yPos, resizeCanvas.getWidth(), Grid.GRIDLINE_SIZE);
        }

        // draw object
        gc.save();

        double width = curObject.getWidth() * gridSize;
        double height = curObject.getHeight() * gridSize;

        Affine a = new Affine();
        a.appendRotation(curObject.getDir().angle(), width / 2, height / 2);
        gc.setTransform(a);

        double xoffset = Math.sin(Math.toRadians(curObject.getDir().angle()))
                * (width - height) / 2;
        double yoffset = Math.sin(Math.toRadians(curObject.getDir().angle()))
                * (width - height) / 2
                - Math.sin(Math.toRadians(curObject.getDir().angle()))
                * gridSize / 2
                + Math.cos(Math.toRadians(curObject.getDir().angle()))
                * gridSize / 2;

        gc.drawImage(curObject.getImage(), xoffset, yoffset, width, height);

        gc.restore();
    }
}
