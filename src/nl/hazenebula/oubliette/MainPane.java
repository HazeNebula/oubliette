package nl.hazenebula.oubliette;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MainPane extends GridPane {
    private MenuBar menuBar;
    private GridPane leftBar;
    private Grid grid;
    private ToolPane toolPane;

    // hightodo: add left bar
    // todo: add minimap to left bar
    // todo: add scale controls to left bar

    public MainPane(Stage primaryStage) {
        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.ALWAYS);

        getRowConstraints().add(rc);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.NEVER);
        cc1.setPercentWidth(20);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHgrow(Priority.ALWAYS);
        ColumnConstraints cc3 = new ColumnConstraints();
        cc3.setHgrow(Priority.NEVER);
        cc3.setPercentWidth(20);
        getColumnConstraints().addAll(cc1, cc2, cc3);

        // todo: add file saving functionality
        MenuItem saveFile = new MenuItem("Save File");
        // lowtodo: add separate window that lets user change file resolution
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

        leftBar = new GridPane();
        grid = new Grid();
        toolPane = new ToolPane(grid);

        leftBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,
                null, null)));
        Button b = new Button("abc");
        b.setOnAction(e -> grid.drawFullGrid());
        leftBar.add(b, 0, 0);

        add(menuBar, 0, 0, 3, 1);
        add(leftBar, 0, 1);
        add(grid, 1, 1);
        add(toolPane, 2, 1);
    }
}
