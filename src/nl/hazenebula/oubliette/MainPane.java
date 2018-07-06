package nl.hazenebula.oubliette;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class MainPane extends GridPane {
    private MenuBar menuBar;
    private MapPane mapPane;
    private Grid grid;
    private ToolPane toolPane;

    public MainPane(Stage primaryStage) {
        // todo: add unsaved changes window
        // todo: add new map option with starting width/height
        // todo: add a cave generator
        // todo: add a dungeon style generator
        // todo: add a trace map option
        // todo: add an option to expand/shrink the map
        // todo: add undo/redo
        MenuItem loadFile = new MenuItem("Load File");
        loadFile.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(Paths.get(".").toAbsolutePath().toFile());
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Oubliette Map File", "*.oubl"));

            File file = fc.showOpenDialog(getScene().getWindow());
            if (file != null) {
                try {
                    readFile(file);
                    primaryStage.setTitle(file.getName());
                } catch (IOException ex) {
                    Alert errorMsg = new Alert(Alert.AlertType.ERROR,
                            "Could not open file: " + file);
                    errorMsg.showAndWait();
                }
            }
        });

        MenuItem saveFile = new MenuItem("Save File");
        saveFile.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(Paths.get(".").toAbsolutePath().toFile());
            fc.setInitialFileName("map.oubl");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Oubliette Map File", "*.oubl"));

            File file = fc.showSaveDialog(getScene().getWindow());
            if (file != null) {
                try {
                    writeFile(grid, file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        MenuItem pngExport = new MenuItem("Export as PNG");
        pngExport.setOnAction(e -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getScene().getWindow());
            stage.setScene(new Scene(new ExportSettingsPane(grid)));
            stage.show();
        });

        Menu file = new Menu("File", null, loadFile, saveFile, pngExport);

        MenuItem blueGrid = new MenuItem("Blue");
        blueGrid.setOnAction(e -> {
            grid.setGridColor(Field.FILLED.color());
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

        primaryStage.setTitle("New File");

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
