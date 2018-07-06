package nl.hazenebula.oubliette;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ExportSettingsPane extends GridPane {
    private static final int MIN_SQUARES_PER_INCH = 1;
    private static final int MAX_SQUARES_PER_INCH = 100;
    private static final int INIT_SQUARES_PER_INCH = 1;

    private static final int MAX_DPI = 1000;
    private static final int MIN_DPI = 25;

    public ExportSettingsPane(CanvasPane canvasPane) {
        Label squaresPerInchLabel = new Label("Squares per inch:");
        GridPane.setHgrow(squaresPerInchLabel, Priority.ALWAYS);
        Spinner<Integer> squaresPerInchField = new Spinner<>(
                MIN_SQUARES_PER_INCH, MAX_SQUARES_PER_INCH,
                INIT_SQUARES_PER_INCH, 1);
        squaresPerInchField.setEditable(true);

        Label dpiLabel = new Label("Dots per inch:");
        GridPane.setHgrow(dpiLabel, Priority.ALWAYS);
        Spinner<Integer> dpiField = new Spinner<>(MIN_DPI, MAX_DPI,
                canvasPane.getGridSize() + 1, 1);
        dpiField.setEditable(true);

        IntegerProperty gridSize = new SimpleIntegerProperty((
                dpiField.getValue() - squaresPerInchField.getValue()
                        * CanvasPane.GRIDLINE_SIZE) / squaresPerInchField.getValue());
        squaresPerInchField.valueProperty().addListener((observable, oldValue,
                                                         newValue) ->
                gridSize.setValue((dpiField.getValue()
                        - squaresPerInchField.getValue() * CanvasPane.GRIDLINE_SIZE)
                        / squaresPerInchField.getValue()));
        dpiField.valueProperty().addListener((observable, oldValue, newValue) ->
                gridSize.setValue((dpiField.getValue()
                        - squaresPerInchField.getValue() * CanvasPane.GRIDLINE_SIZE)
                        / squaresPerInchField.getValue()));

        Label pngWidthTextLabel = new Label("PNG Width:");
        Label pngWidthLabel = new Label();
        pngWidthLabel.textProperty().bind(gridSize.add(CanvasPane.GRIDLINE_SIZE)
                .multiply(canvasPane.fieldWidthProperty()).asString());
        GridPane.setHgrow(pngWidthTextLabel, Priority.ALWAYS);
        Label pngHeightTextLabel = new Label("PNG Height:");
        Label pngHeightLabel = new Label();
        pngHeightLabel.textProperty().bind(gridSize.add(CanvasPane.GRIDLINE_SIZE)
                .multiply(canvasPane.fieldHeightProperty()).asString());
        GridPane.setHgrow(pngHeightTextLabel, Priority.ALWAYS);

        Button exportButton = new Button("Export");
        exportButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(Paths.get(".").toAbsolutePath().toFile());
            fc.setInitialFileName("map.png");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Portable Network Graphics", "*.png"));

            File file = fc.showSaveDialog(this.getScene().getWindow());
            if (file != null) {
                int size = canvasPane.getGridSize();
                canvasPane.setGridSize(gridSize.get());
                canvasPane.drawFullGrid();

                WritableImage img = canvasPane.snapshot();
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png",
                            file);
                } catch (IOException ex) {
                    System.err.println("Could not save file " +
                            file.toString() + ": " + ex.getMessage());
                }

                canvasPane.setGridSize(size);
                canvasPane.drawFullGrid();

                this.getScene().getWindow().hide();
            }
        });

        add(squaresPerInchLabel, 0, 0);
        add(squaresPerInchField, 1, 0);
        add(dpiLabel, 0, 1);
        add(dpiField, 1, 1);
        add(pngWidthTextLabel, 0, 2);
        add(pngWidthLabel, 1, 2);
        add(pngHeightTextLabel, 0, 3);
        add(pngHeightLabel, 1, 3);
        add(exportButton, 1, 4);
    }
}
