package nl.hazenebula.oubliette;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class MainPane extends GridPane {
    private MenuBar menuBar;
    private MapPane mapPane;
    private Grid grid;
    private ToolPane toolPane;

    // todo: add menu screen before mainpane that shows settings
    public MainPane(Stage primaryStage) {
        MenuItem saveFile = new MenuItem("Save File");
        saveFile.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(Paths.get(".").toAbsolutePath().toFile());
            fc.setInitialFileName("map.oubl");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Oubliette Map File", ".oubl"));

            File file = fc.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    writeFile(grid, file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        // lowptodo: add separate window that lets user change file resolution
        MenuItem pngExport = new MenuItem("Export as PNG");
        pngExport.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(Paths.get(".").toAbsolutePath().toFile());
            fc.setInitialFileName("map.png");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Portable Network Graphics", ".png"));

            File file = fc.showSaveDialog(primaryStage);
            if (file != null) {
                WritableImage img = grid.snapshot();

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png",
                            file);
                } catch (IOException ex) {
                    System.err.println("Could not save file " +
                            file.toString());
                }
            }
        });
        Menu file = new Menu("File", null, saveFile, pngExport);

        MenuItem blueGrid = new MenuItem("Blue");
        blueGrid.setOnAction(e -> {
            grid.setGridColor(Color.rgb(51, 153, 204));
            grid.drawFullGrid();
        });
        MenuItem whiteGrid = new MenuItem("White");
        whiteGrid.setOnAction(e -> {
            grid.setGridColor(Color.WHITE);
            grid.drawFullGrid();
        });
        MenuItem blackGrid = new MenuItem("Black");
        blackGrid.setOnAction(e -> {
            grid.setGridColor(Color.BLACK);
            grid.drawFullGrid();
        });
        Menu options = new Menu("Options", null, new Menu("Grid Color", null,
                blueGrid, whiteGrid, blackGrid));

        menuBar = new MenuBar(file, options);
        GridPane.setVgrow(menuBar, Priority.NEVER);

        grid = new Grid(50, 50);
        GridPane.setHgrow(grid, Priority.ALWAYS);
        GridPane.setVgrow(grid, Priority.ALWAYS);

        toolPane = new ToolPane(grid);
        GridPane.setVgrow(toolPane, Priority.ALWAYS);

        mapPane = new MapPane(grid);
        GridPane.setVgrow(mapPane, Priority.ALWAYS);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(20);
        ColumnConstraints cc2 = new ColumnConstraints();
        ColumnConstraints cc3 = new ColumnConstraints();
        cc3.setPercentWidth(20);
        getColumnConstraints().addAll(cc1, cc2, cc3);

        add(menuBar, 0, 0, 3, 1);
        add(mapPane, 0, 1);
        add(grid, 1, 1);
        add(toolPane, 2, 1);
    }

    private void writeFile(Grid grid, File file) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(file)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(fout)) {
                oos.writeObject(grid.getFields());
                oos.writeObject(grid.getFieldObjects());
                oos.writeObject(grid.getWallGrid());
            }
        }
    }

    private void readFile(File file) throws IOException {
        try (FileInputStream fin = new FileInputStream(file)) {
            try (ObjectInputStream ois = new ObjectInputStream(fin)) {
                Field[][] fieldGrid = (Field[][])ois.readObject();
                List<FieldObject> fieldObjects = (List<FieldObject>)
                        ois.readObject();
                WallObject[][][] wallGrid = (WallObject[][][])
                        ois.readObject();

                grid.setFields(fieldGrid);
                grid.setFieldObjects(fieldObjects);
                grid.setWallGrid(wallGrid);
                grid.drawFullGrid();
            } catch (ClassNotFoundException e) {
                System.err.println("Could not find file");
            }
        }
    }
}
