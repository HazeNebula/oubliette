package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldObjectPane extends GridPane {
    private Canvas resizeCanvas;

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

                    try {
                        img.setImage(sizes[0], sizes[1], objFile);
                    } catch (IOException e) {
                        System.err.println("Could not open file: " + objFile
                                .toString());
                    }
                }
            }

            if (img.completed()) {
                objects.add(img);
            }
        }

        Collections.sort(objects);
    }

    public FieldObjectPane(Grid grid) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(5, 5, 5, 5));
        setVgap(5.0d);
        setHgap(5.0d);

        loadObjects();

        resizeCanvas = new Canvas();
        resizeCanvas.widthProperty().bind(grid.sizeProperty()
                .add(Grid.GRID_SIZE).multiply(maxSize));
        resizeCanvas.heightProperty().bind(resizeCanvas.widthProperty());

        Button increaseSizeButton = new Button("+");
        Button decreaseSizeButton = new Button("-");
        Button rotateClockwiseButton = new Button("\u21BB");
        Button rotateAntiClockwiseButton = new Button("\u21BA");

        GridPane resizeWrapper = new GridPane();
        resizeWrapper.setPadding(new Insets(5, 5, 5, 5));
        resizeWrapper.add(resizeCanvas, 0, 0);

        // highptodo: add buttons for field objects
        // highptodo: make button press change field object brush
        FlowPane buttonPane = new FlowPane();

        add(resizeWrapper, 0, 0, 2, 2);
        add(increaseSizeButton, 2, 0);
        add(decreaseSizeButton, 2, 1);
        add(rotateClockwiseButton, 0, 3);
        add(rotateAntiClockwiseButton, 1, 3);
    }
}
