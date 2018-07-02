package nl.hazenebula.oubliette;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldObjectPane extends GridPane {
    public static final double BUTTON_SIZE = 80.0d;

    private List<FieldObjectImage> objects;
    private int maxSize;

    private void loadObjects() {
        List<List<File>> files = ObjectFileSearcher.getObjects(
                ObjectFileSearcher.FIELDOBJECT_DIR);
        maxSize = 1;
        objects = new LinkedList<>();

        for (List<File> objectFiles : files) {
            int size = (int)Math.sqrt(objectFiles.size());
            FieldObjectImage img = new FieldObjectImage(size, size);

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

                    img.setImage(sizes[0], sizes[1], objFile);
                }
            }

            if (img.completed()) {
                objects.add(img);
            }
        }

        Collections.sort(objects);
    }

    public FieldObjectPane(Grid grid) {
        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);

        loadObjects();

        Canvas resizeCanvas = new Canvas();
        resizeCanvas.widthProperty().bind(grid.sizeProperty()
                .add(Grid.GRID_SIZE).multiply(2).multiply(maxSize));
        resizeCanvas.heightProperty().bind(resizeCanvas.widthProperty());
        GridPane.setHgrow(resizeCanvas, Priority.ALWAYS);
        GridPane.setHalignment(resizeCanvas, HPos.CENTER);

        GraphicsContext gc = resizeCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, resizeCanvas.getWidth(), resizeCanvas.getHeight());

        Button increaseSizeButton = new Button("+");
        Button decreaseSizeButton = new Button("-");
        Button rotateClockwiseButton = new Button("\u21BB");
        Button rotateAntiClockwiseButton = new Button("\u21BA");

        GridPane resizeWrapper = new GridPane();
        resizeWrapper.setPadding(new Insets(5, 5, 5, 5));
        resizeWrapper.add(resizeCanvas, 1, 1);
        resizeWrapper.add(increaseSizeButton, 3, 0);
        resizeWrapper.add(decreaseSizeButton, 3, 2);
        resizeWrapper.add(rotateClockwiseButton, 0, 3);
        resizeWrapper.add(rotateAntiClockwiseButton, 2, 3);

        setHgrow(resizeWrapper, Priority.ALWAYS);
        setHalignment(resizeWrapper, HPos.CENTER);

        // highptodo: make button press change field object brush
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

        ToggleGroup fieldObjectToggleGroup = new ToggleGroup();
        for (FieldObjectImage img : objects) {
            ToggleButton button = new ToggleButton();

            ImageView graphic = new ImageView(img.getImage(1, 1));
            graphic.setFitWidth(BUTTON_SIZE);
            graphic.setFitHeight(BUTTON_SIZE);
            button.setGraphic(graphic);

            button.setToggleGroup(fieldObjectToggleGroup);
            buttons.getChildren().add(button);
        }

        setVgrow(buttonPane, Priority.ALWAYS);

        add(resizeWrapper, 0, 0);
        add(buttonPane, 0, 4);
    }
}
