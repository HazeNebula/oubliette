package nl.hazenebula.oubliette;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

public class MainPane extends GridPane {
    private MenuBar menuBar;
    private MapPane mapPane;
    private CanvasPane canvasPane;
    private ToolPane toolPane;

    public MainPane(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> {
            Alert closeConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
            closeConfirmation.setTitle("Confirm Close");
            closeConfirmation.setGraphic(null);
            closeConfirmation.setHeaderText(null);
            closeConfirmation.setContentText("If the current window is closed, "
                    + "all unsaved changes will be lost.");
            Optional<ButtonType> result = closeConfirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                e.consume();
            }
        });

        canvasPane = new CanvasPane(new Map(50, 50, Field.BLUE));
        GridPane.setHgrow(canvasPane, Priority.ALWAYS);
        GridPane.setVgrow(canvasPane, Priority.ALWAYS);

        mapPane = new MapPane(canvasPane);
        GridPane.setVgrow(mapPane, Priority.ALWAYS);

        toolPane = new ToolPane(mapPane, canvasPane);
        GridPane.setVgrow(toolPane, Priority.ALWAYS);

        primaryStage.setTitle("New File");

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(20);
        ColumnConstraints cc2 = new ColumnConstraints();
        ColumnConstraints cc3 = new ColumnConstraints();
        cc3.setPercentWidth(20);
        getColumnConstraints().addAll(cc1, cc2, cc3);

        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("New");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getScene().getWindow());
            stage.setScene(new Scene(new NewMapPane(mapPane,
                    canvasPane, primaryStage)));
            stage.show();
        });

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
                    errorMsg.setHeaderText(null);
                    errorMsg.setGraphic(null);
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
                    writeFile(canvasPane, file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        MenuItem pngExport = new MenuItem("Export as PNG");
        pngExport.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Export");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getScene().getWindow());
            stage.setScene(new Scene(new ExportSettingsPane(canvasPane)));
            stage.show();
        });

        Menu file = new Menu("File", null, newFile, loadFile, saveFile,
                pngExport);

        Menu gridColor = new Menu("Grid Color");
        for (Field field : Field.values()) {
            MenuItem color = new MenuItem(field.toString());
            color.setOnAction(e -> {
                canvasPane.setGridColor(field.color());
                canvasPane.drawAll();
            });

            gridColor.getItems().add(color);
        }

        MenuItem resizeDungeon = new MenuItem("Resize Map");
        resizeDungeon.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Resize");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getScene().getWindow());
            stage.setScene(new Scene(new ResizeDungeonPane(mapPane,
                    canvasPane)));
            stage.show();
        });

        Menu edit = new Menu("Edit", null, gridColor, resizeDungeon);
        menuBar = new MenuBar(file, edit);
        GridPane.setVgrow(menuBar, Priority.NEVER);

        add(menuBar, 0, 0, 3, 1);
        add(mapPane, 0, 1);
        add(canvasPane, 1, 1);
        add(toolPane, 2, 1);
    }

    private void writeFile(CanvasPane canvasPane, File file)
            throws IOException {
        try (FileOutputStream fout = new FileOutputStream(file)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(fout)) {
                oos.writeObject(canvasPane.getMap());
            }
        }
    }

    private void readFile(File file) throws IOException {
        try (FileInputStream fin = new FileInputStream(file)) {
            try (ObjectInputStream ois = new ObjectInputStream(fin)) {
                Map map = (Map)ois.readObject();

                canvasPane.setMap(map);
                mapPane.updateMinimap();
            } catch (ClassNotFoundException e) {
                System.err.println("Could not find file");
            }
        }
    }
}
