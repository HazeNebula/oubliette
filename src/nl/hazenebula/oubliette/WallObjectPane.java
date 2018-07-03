package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class WallObjectPane extends GridPane {
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

        List<WallObjectImage> images = loadObjects();

        // highptodo: add wall object preview

        // highptodo: add wall object buttons
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
}
