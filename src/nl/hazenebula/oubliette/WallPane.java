package nl.hazenebula.oubliette;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

public class WallPane extends GridPane {
    private static final Color BACK_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = Color.BLACK;
    private static final double BUTTON_SIZE = 80.0d;

    private CanvasPane canvasPane;

    private Canvas resizeCanvas;
    private int maxSize;
    private List<ToggleButton> wallButtons;
    private WallImage curImg;
    private Wall curObject;
    private StringProperty eraseOrient;

    public WallPane(CanvasPane canvasPane) {
        this.canvasPane = canvasPane;

        eraseOrient = new SimpleStringProperty("Horizontal");

        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);

        resizeCanvas = new Canvas();
        resizeCanvas.widthProperty().bind(widthProperty().multiply(0.9d));
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

                canvasPane.setWallObject(curObject);
            }
        });
        Button decreaseSizeButton = new Button("-");
        decreaseSizeButton.setOnAction(e -> {
            if (curObject.getWidth() > 1) {
                curObject.setWidth(curObject.getWidth() - 1);
                curObject.setImage(curImg.getImage(curObject.getWidth()));
                drawCanvas();

                canvasPane.setWallObject(curObject);
            }
        });
        Label rotateLabel = new Label("Rotate:");
        GridPane.setHgrow(rotateLabel, Priority.ALWAYS);
        Button rotateClockwiseButton = new Button("\u21BB");
        rotateClockwiseButton.setOnAction(e -> {
            curObject.setDir(curObject.getDir().next());
            drawCanvas();

            canvasPane.setWallObject(curObject);

            if (curObject.getDir() == Direction.NORTH
                    || curObject.getDir() == Direction.SOUTH) {
                eraseOrient.setValue("Horizontal");
            } else {
                eraseOrient.setValue("Vertical");
            }
        });
        Button rotateCounterclockwiseButton = new Button("\u21BA");
        rotateCounterclockwiseButton.setOnAction(e -> {
            curObject.setDir(curObject.getDir().prev());
            drawCanvas();

            canvasPane.setWallObject(curObject);

            if (curObject.getDir() == Direction.NORTH
                    || curObject.getDir() == Direction.SOUTH) {
                eraseOrient.setValue("Horizontal");
            } else {
                eraseOrient.setValue("Vertical");
            }
        });

        GridPane shapeButtonPane = new GridPane();
        shapeButtonPane.add(sizeLabel, 0, 0);
        shapeButtonPane.add(increaseSizeButton, 1, 0);
        shapeButtonPane.add(decreaseSizeButton, 2, 0);
        shapeButtonPane.add(rotateClockwiseButton, 1, 1);
        shapeButtonPane.add(rotateCounterclockwiseButton, 2, 1);

        ToggleGroup toggleGroup = new ToggleGroup();

        Label eraseLabel = new Label();
        eraseLabel.textProperty().bind(new
                SimpleStringProperty("Erase Orientation: ").concat(
                eraseOrient));
        setHgrow(eraseLabel, Priority.ALWAYS);
        setHalignment(eraseLabel, HPos.CENTER);

        ToggleButton eraseButton = new ToggleButton("Erase");
        eraseButton.setToggleGroup(toggleGroup);
        eraseButton.setMaxWidth(Double.MAX_VALUE);
        eraseButton.setOnAction(e -> canvasPane.setBrush(Tool.WALL_ERASE));
        setHgrow(eraseButton, Priority.ALWAYS);
        setHalignment(eraseButton, HPos.CENTER);

        Label colorLabel = new Label("Object Color:");
        ComboBox<String> colorBox = new ComboBox<>();
        for (Tile tile : Tile.values()) {
            if (tile != Tile.WHITE) {
                colorBox.getItems().add(tile.toString());
            }
        }
        colorBox.getSelectionModel().select("Blue");
        GridPane.setHgrow(colorLabel, Priority.ALWAYS);

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

        List<WallImage> objects = loadWalls();
        wallButtons = new LinkedList<>();

        setVgrow(buttonPane, Priority.ALWAYS);

        colorBox.valueProperty().addListener((observable, oldValue, newValue) ->
                loadButtons(buttons, toggleGroup, objects, newValue));

        curImg = objects.get(0);
        curObject = new Wall(curImg.getImage(1), 1, Direction.NORTH);
        loadButtons(buttons, toggleGroup, objects, Tile.BLUE.toString());
        drawCanvas();

        add(resizeWrapper, 0, 0, 2, 1);
        add(shapeButtonPane, 0, 1, 2, 1);
        add(eraseLabel, 0, 2, 2, 1);
        add(eraseButton, 0, 3, 2, 1);
        add(colorLabel, 0, 4);
        add(colorBox, 1, 4);
        add(buttonPane, 0, 5, 2, 1);
    }

    private List<WallImage> loadWalls() {
        List<List<File>> files = ObjectFileSearcher.getObjects(
                ObjectFileSearcher.WALLOBJECT_DIR);
        maxSize = 1;
        List<WallImage> objects = new LinkedList<>();

        for (List<File> objectFiles : files) {
            int size = objectFiles.size();
            WallImage img = new WallImage(size);

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

    private void loadButtons(FlowPane buttons, ToggleGroup group,
                             List<WallImage> allObjects,
                             String color) {
        for (ToggleButton button : wallButtons) {
            group.getToggles().remove(button);
            buttons.getChildren().remove(button);
        }

        List<WallImage> objects = new LinkedList<>();
        for (WallImage img : allObjects) {
            if (img.getName().toLowerCase().endsWith(color.toLowerCase())) {
                objects.add(img);
            }
        }

        ToggleButton firstButton = new ToggleButton();
        for (WallImage img : objects) {
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
                canvasPane.setBrush(Tool.WALL);

                if (curImg != img) {
                    curImg = img;
                    curObject = new Wall(curImg.getImage(
                            curObject.getWidth()), curObject.getWidth(),
                            curObject.getDir());
                    drawCanvas();

                    canvasPane.setWallObject(new Wall(curObject));
                }
            });

            button.setToggleGroup(group);
            wallButtons.add(button);
            buttons.getChildren().add(button);
        }

        int lastSpaceIndex = curImg.getName().lastIndexOf(' ');
        if (lastSpaceIndex != -1) {
            for (WallImage obj : objects) {
                if (obj.getName().startsWith(curImg.getName().substring(0,
                        lastSpaceIndex))) {
                    curImg = obj;
                }
            }
        }

        curObject = new Wall(curImg.getImage(curObject.getWidth()),
                curObject.getWidth(), curObject.getDir());
        canvasPane.setWallObject(curObject);
        firstButton.setSelected(true);

        drawCanvas();
    }

    private void drawCanvas() {
        GraphicsContext gc = resizeCanvas.getGraphicsContext2D();
        double gridSize = resizeCanvas.getWidth() / maxSize;

        // clear canvas
        gc.setFill(BACK_COLOR);
        gc.fillRect(0, 0, resizeCanvas.getWidth(), resizeCanvas.getHeight());

        // draw canvasPane lines
        gc.setFill(GRID_COLOR);
        for (int x = 0; x < maxSize; ++x) {
            double xPos = x * gridSize;
            gc.fillRect(xPos, 0, 1.0d, resizeCanvas.getHeight());
        }

        for (int y = 0; y < maxSize; ++y) {
            double yPos = y * gridSize;
            gc.fillRect(0, yPos, resizeCanvas.getWidth(), CanvasPane.GRIDLINE_SIZE);
        }

        // draw object
        gc.save();

        double width = curObject.getWidth() * gridSize;

        Affine a = new Affine();
        a.appendRotation(curObject.getDir().angle(), width / 2, gridSize / 2);
        gc.setTransform(a);

        double xoffset = Math.sin(Math.toRadians(curObject.getDir().angle()))
                * (width - gridSize) / 2;
        double yoffset = Math.sin(Math.toRadians(curObject.getDir().angle()))
                * (width - gridSize) / 2
                - Math.sin(Math.toRadians(curObject.getDir().angle()))
                * gridSize / 2
                + Math.cos(Math.toRadians(curObject.getDir().angle()))
                * gridSize / 2;

        gc.drawImage(curObject.getImage(), xoffset, yoffset, width, gridSize);

        gc.restore();
    }
}
