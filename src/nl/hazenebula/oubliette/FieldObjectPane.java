package nl.hazenebula.oubliette;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldObjectPane extends GridPane {
    private static final int MIN_NUM = 0;
    private static final int MAX_NUM = 99;
    private static final int INIT_NUM = 1;
    private static final Color BACK_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = Color.BLACK;

    private static final String INCREASE_WIDTH_BUTTON = "s";
    private static final String DECREASE_WIDTH_BUTTON = "a";
    private static final String INCREASE_HEIGHT_BUTTON = "w";
    private static final String DECREASE_HEIGHT_BUTTON = "q";
    private static final String ROTATE_CLOCKWISE_BUTTON = "x";
    private static final String ROTATE_COUNTERCLOCKWISE_BUTTON = "z";

    private static final double BUTTON_SIZE = 80.0d;

    private CanvasPane canvasPane;

    private Canvas resizeCanvas;
    private int maxSize;
    private List<ToggleButton> objectButtons;
    private FieldObjectImage curImg;
    private NumberObjectImage curNumImg;
    private FieldObject curObject;
    private boolean drawingNumbers;

    private Spinner<Integer> numberSpinner;

    public FieldObjectPane(CanvasPane canvasPane) {
        this.canvasPane = canvasPane;

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

        Label widthLabel = new Label("Width:");
        GridPane.setHgrow(widthLabel, Priority.ALWAYS);
        Button increaseWidthButton = new Button("+");
        increaseWidthButton.setOnAction(e -> increaseWidth());
        Button decreaseWidthButton = new Button("-");
        decreaseWidthButton.setOnAction(e -> decreaseWidth());
        Label heightLabel = new Label("Height:");
        GridPane.setHgrow(heightLabel, Priority.ALWAYS);
        Button increaseHeightButton = new Button("+");
        increaseHeightButton.setOnAction(e -> increaseHeight());
        Button decreaseHeightButton = new Button("-");
        decreaseHeightButton.setOnAction(e -> decreaseHeight());
        Label rotateLabel = new Label("Rotate:");
        GridPane.setHgrow(rotateLabel, Priority.ALWAYS);
        Button rotateClockwiseButton = new Button("\u21BB");
        rotateClockwiseButton.setOnAction(e -> rotateClockwise());
        Button rotateCounterclockwiseButton = new Button("\u21BA");
        rotateCounterclockwiseButton.setOnAction(e -> rotateCounterclockwise());

        GridPane resizeWrapper = new GridPane();
        resizeWrapper.add(resizeCanvas, 0, 0);

        setHgrow(resizeWrapper, Priority.ALWAYS);
        setHalignment(resizeWrapper, HPos.CENTER);

        GridPane shapeButtonPane = new GridPane();
        shapeButtonPane.add(widthLabel, 0, 0);
        shapeButtonPane.add(increaseWidthButton, 1, 0);
        shapeButtonPane.add(decreaseWidthButton, 2, 0);
        shapeButtonPane.add(heightLabel, 0, 1);
        shapeButtonPane.add(increaseHeightButton, 1, 1);
        shapeButtonPane.add(decreaseHeightButton, 2, 1);
        shapeButtonPane.add(rotateLabel, 0, 3);
        shapeButtonPane.add(rotateClockwiseButton, 1, 3);
        shapeButtonPane.add(rotateCounterclockwiseButton, 2, 3);

        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton eraseButton = new ToggleButton("Erase");
        eraseButton.setToggleGroup(toggleGroup);
        eraseButton.setMaxWidth(Double.MAX_VALUE);
        eraseButton.setOnAction(e -> canvasPane.setBrush(
                Tool.FIELD_OBJECT_ERASE));
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

        List<FieldObjectImage> objects = loadObjects();
        objectButtons = new LinkedList<>();

        setVgrow(buttonPane, Priority.ALWAYS);

        drawingNumbers = false;
        curNumImg = new NumberObjectImage("Times New Roman",
                Tile.BLUE.color());
        GridPane numberPane = new GridPane();

        numberSpinner = new Spinner<>(MIN_NUM, MAX_NUM,
                INIT_NUM, 1);
        numberSpinner.setEditable(true);
        numberSpinner.focusedProperty().addListener((observable, oldValue,
                                                     newValue) -> {
            if (!newValue) {
                numberSpinner.increment(0);
            }
        });

        ToggleButton numberButton = new ToggleButton();
        ImageView graphic = new ImageView(curNumImg.getImage(INIT_NUM));
        graphic.setFitWidth(BUTTON_SIZE);
        graphic.setFitHeight(BUTTON_SIZE);
        numberButton.setGraphic(graphic);
        numberButton.setTooltip(new Tooltip("Number"));
        numberButton.setToggleGroup(toggleGroup);
        GridPane.setHgrow(numberButton, Priority.ALWAYS);

        numberPane.add(numberButton, 0, 0);
        numberPane.add(numberSpinner, 1, 0);

        numberSpinner.valueProperty().addListener((observable, oldValue,
                                                   newValue) -> {
            drawingNumbers = true;
            canvasPane.setBrush(Tool.FIELD_OBJECT);
            numberButton.setSelected(true);

            curObject = new FieldObject(curNumImg.getImage(numberSpinner
                    .getValue()), 0, 0, 1, 1, Direction.NORTH);
            drawCanvas();

            canvasPane.setFieldObject(new FieldObject(curObject));
        });

        numberButton.setOnAction(e -> {
            drawingNumbers = true;
            canvasPane.setBrush(Tool.FIELD_OBJECT);

            curObject = new FieldObject(curNumImg.getImage(numberSpinner
                    .getValue()), 0, 0, 1, 1, Direction.NORTH);
            drawCanvas();

            canvasPane.setFieldObject(new FieldObject(curObject));
        });

        colorBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadButtons(buttons, toggleGroup, objects, newValue);
            curNumImg.setColor(Tile.fromString(newValue).color());

            ImageView img = new ImageView(curNumImg.getImage(INIT_NUM));
            img.setFitWidth(BUTTON_SIZE);
            img.setFitHeight(BUTTON_SIZE);
            numberButton.setGraphic(img);

            if (drawingNumbers) {
                numberButton.setSelected(true);
            }
        });

        curImg = objects.get(0);
        curObject = new FieldObject(curImg.getImage(1, 1), 0, 0, 1, 1,
                Direction.NORTH);
        loadButtons(buttons, toggleGroup, objects, Tile.BLUE.toString());
        drawCanvas();

        add(resizeWrapper, 0, 0, 2, 1);
        add(shapeButtonPane, 0, 1, 2, 1);
        add(eraseButton, 0, 2, 2, 1);
        add(colorLabel, 0, 3);
        add(colorBox, 1, 3);
        add(buttonPane, 0, 4, 2, 1);
        add(numberPane, 0, 5, 2, 1);
    }

    private void increaseWidth() {
        if (!drawingNumbers) {
            if (curObject.getWidth() < maxSize) {
                curObject.setWidth(curObject.getWidth() + 1);
                curObject.setImage(curImg.getImage(curObject.getWidth(),
                        curObject.getHeight()));
                drawCanvas();

                canvasPane.setFieldObject(curObject);
                canvasPane.drawAll();
            }
        }
    }

    private void decreaseWidth() {
        if (!drawingNumbers) {
            if (curObject.getWidth() > 1) {
                curObject.setWidth(curObject.getWidth() - 1);
                curObject.setImage(curImg.getImage(curObject.getWidth(),
                        curObject.getHeight()));
                drawCanvas();

                canvasPane.setFieldObject(curObject);
                canvasPane.drawAll();
            }
        }
    }

    private void increaseHeight() {
        if (!drawingNumbers) {
            if (curObject.getHeight() < maxSize) {
                curObject.setHeight(curObject.getHeight() + 1);
                curObject.setImage(curImg.getImage(curObject.getWidth(),
                        curObject.getHeight()));
                drawCanvas();

                canvasPane.setFieldObject(curObject);
                canvasPane.drawAll();
            }
        }
    }

    private void decreaseHeight() {
        if (!drawingNumbers) {
            if (curObject.getHeight() > 1) {
                curObject.setHeight(curObject.getHeight() - 1);
                curObject.setImage(curImg.getImage(curObject.getWidth(),
                        curObject.getHeight()));
                drawCanvas();

                canvasPane.setFieldObject(curObject);
                canvasPane.drawAll();
            }
        }
    }

    private void rotateClockwise() {
        if (!drawingNumbers) {
            curObject.setDir(curObject.getDir().next());
            drawCanvas();

            canvasPane.setFieldObject(curObject);
            canvasPane.drawAll();
        }
    }

    private void rotateCounterclockwise() {
        if (!drawingNumbers) {
            curObject.setDir(curObject.getDir().prev());
            drawCanvas();

            canvasPane.setFieldObject(curObject);
            canvasPane.drawAll();
        }
    }

    public void handleHotkeys(String character) {
        switch (character) {
            case DECREASE_HEIGHT_BUTTON:
                decreaseHeight();
                break;
            case INCREASE_HEIGHT_BUTTON:
                increaseHeight();
                break;
            case DECREASE_WIDTH_BUTTON:
                decreaseWidth();
                break;
            case INCREASE_WIDTH_BUTTON:
                increaseWidth();
                break;
            case ROTATE_COUNTERCLOCKWISE_BUTTON:
                rotateCounterclockwise();
                break;
            case ROTATE_CLOCKWISE_BUTTON:
                rotateClockwise();
                break;
        }
    }

    private List<FieldObjectImage> loadObjects() {
        List<List<String>> files = ObjectFileSearcher.getIndexedPaths(
                ObjectFileSearcher.FIELDOBJECT_DIR);
        maxSize = 1;
        List<FieldObjectImage> objects = new LinkedList<>();

        for (List<String> objectFiles : files) {
            int size = (int)Math.sqrt(objectFiles.size());
            FieldObjectImage img = new FieldObjectImage(size, size);

            if (size > maxSize) {
                maxSize = size;
            }

            for (String objFile : objectFiles) {
                String filename = objFile.substring(objFile.lastIndexOf('/')
                        + 1, objFile.length());
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
                    int[] sizes = new int[2];

                    for (int i = 0; i < sizes.length; ++i) {
                        try {
                            sizes[i] = Integer.parseInt(sizesStr[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Could not format as number: " +
                                    sizesStr[i]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println("Could not parse file name: " +
                                    filename);
                        }
                    }

                    img.setImage(sizes[0], sizes[1], new Image(getClass()
                            .getResourceAsStream(objFile)));
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
                             List<FieldObjectImage> allObjects,
                             String color) {
        for (ToggleButton button : objectButtons) {
            group.getToggles().remove(button);
            buttons.getChildren().remove(button);
        }

        List<FieldObjectImage> objects = new LinkedList<>();
        for (FieldObjectImage img : allObjects) {
            if (img.getName().toLowerCase().endsWith(color.toLowerCase())) {
                objects.add(img);
            }
        }

        ToggleButton firstButton = new ToggleButton();
        for (FieldObjectImage img : objects) {
            ToggleButton button = new ToggleButton();

            if (img == objects.get(0)) {
                firstButton = button;
            }

            ImageView graphic = new ImageView(img.getImage(1, 1));
            graphic.setFitWidth(BUTTON_SIZE);
            graphic.setFitHeight(BUTTON_SIZE);
            button.setGraphic(graphic);
            button.setTooltip(new Tooltip(img.getName()));

            button.setOnAction(e -> {
                drawingNumbers = false;
                canvasPane.setBrush(Tool.FIELD_OBJECT);

                if (curImg != img) {
                    curImg = img;
                    curObject = new FieldObject(curImg.getImage(
                            curObject.getWidth(), curObject.getHeight()),
                            curObject.getX(), curObject.getY(),
                            curObject.getWidth(), curObject.getHeight(),
                            curObject.getDir());
                    drawCanvas();

                    canvasPane.setFieldObject(new FieldObject(curObject));
                }
            });

            button.setToggleGroup(group);
            objectButtons.add(button);
            buttons.getChildren().add(button);
        }

        int lastSpaceIndex = curImg.getName().lastIndexOf(' ');
        if (lastSpaceIndex != -1) {
            for (FieldObjectImage obj : objects) {
                if (obj.getName().startsWith(curImg.getName().substring(0,
                        lastSpaceIndex))) {
                    curImg = obj;
                }
            }
        }

        if (drawingNumbers) {
            for (Tile tile : Tile.values()) {
                if (tile.toString().toLowerCase().equals(color.toLowerCase())) {
                    curNumImg.setColor(tile.color());
                    break;
                }
            }

            curObject = new FieldObject(curNumImg.getImage(
                    numberSpinner.getValue()), 0, 0, 1, 1, Direction.NORTH);
            canvasPane.setFieldObject(curObject);
        } else {
            curObject = new FieldObject(curImg.getImage(curObject.getWidth(),
                    curObject.getHeight()), 0, 0, curObject.getWidth(),
                    curObject.getHeight(), curObject.getDir());
            canvasPane.setFieldObject(curObject);
            firstButton.setSelected(true);
        }

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
        double height = curObject.getHeight() * gridSize;

        Affine a = new Affine();
        a.appendRotation(curObject.getDir().angle(), width / 2, height / 2);
        gc.setTransform(a);

        double xoffset = Math.sin(Math.toRadians(curObject.getDir().angle()))
                * (width - height) / 2;
        double yoffset = Math.sin(Math.toRadians(curObject.getDir().angle()))
                * (width - height) / 2;

        gc.drawImage(curObject.getImage(), xoffset, yoffset, width, height);

        gc.restore();
    }
}
