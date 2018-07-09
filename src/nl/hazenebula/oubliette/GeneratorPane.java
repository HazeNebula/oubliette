package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import nl.hazenebula.terraingeneration.*;

public class GeneratorPane extends GridPane {
    private Generator curGen;

    public GeneratorPane(MapPane mapPane, CanvasPane canvasPane) {
        Selection selection = canvasPane.getSelection();

        Label selectionXLabel = new Label("Selection X:");
        GridPane.setHgrow(selectionXLabel, Priority.SOMETIMES);
        Label selectionX = new Label("");
        selectionX.textProperty().bind(selection.xProperty().asString());
        selectionX.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionX, Priority.SOMETIMES);

        Label selectionYLabel = new Label("Selection Y:");
        GridPane.setHgrow(selectionYLabel, Priority.SOMETIMES);
        Label selectionY = new Label("");
        selectionY.textProperty().bind(selection.yProperty().asString());
        selectionY.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionY, Priority.SOMETIMES);

        Label selectionWidthLabel = new Label("Selection Width:");
        GridPane.setHgrow(selectionWidthLabel, Priority.SOMETIMES);
        Label selectionWidth = new Label("");
        selectionWidth.textProperty().bind(selection.widthProperty()
                .asString());
        selectionWidth.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionWidth, Priority.SOMETIMES);

        Label selectionHeightLabel = new Label("Selection Height:");
        GridPane.setHgrow(selectionHeightLabel, Priority.SOMETIMES);
        Label selectionHeight = new Label("");
        selectionHeight.textProperty().bind(selection.heightProperty()
                .asString());
        selectionHeight.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHgrow(selectionHeight, Priority.SOMETIMES);

        Button selectAllButton = new Button("Select Entire Map");
        selectAllButton.setMaxWidth(Double.MAX_VALUE);
        selectAllButton.setOnAction(e -> {
            selection.setSelecting(true);
            selection.setX(0);
            selection.setY(0);
            selection.setWidth(canvasPane.fieldWidthProperty().get());
            selection.setHeight(canvasPane.fieldHeightProperty().get());
            canvasPane.drawAll();
        });

        Label generatorSettingsLabel = new Label("Generator Settings:");
        GridPane.setHgrow(generatorSettingsLabel, Priority.ALWAYS);
        ScrollPane settingsPane = new ScrollPane();
        settingsPane.setStyle("-fx-focus-color: transparent;\n" +
                "-fx-background: #D3D3D3");
        GridPane.setHgrow(settingsPane, Priority.ALWAYS);
        GridPane.setVgrow(settingsPane, Priority.ALWAYS);
        settingsPane.setFitToWidth(true);

        FillSettingsPane fillSettingsPane = new FillSettingsPane();
        CaveGeneratorSettingsPane caveGeneratorSettingsPane =
                new CaveGeneratorSettingsPane();
        RoomGeneratorSettingsPane roomGeneratorSettingsPane =
                new RoomGeneratorSettingsPane();
        MazeGeneratorSettingsPane mazeGeneratorSettingsPane =
                new MazeGeneratorSettingsPane();

        Label generatorLabel = new Label("Procedural Generators:");
        GridPane.setHgrow(generatorLabel, Priority.ALWAYS);
        generatorLabel.setPadding(new Insets(5, 0, 0, 0));

        VBox buttonPane = new VBox();
        buttonPane.setSpacing(5);
        buttonPane.setPadding(new Insets(5, 5, 5, 5));

        ToggleGroup toggleGroup = new ToggleGroup();

        // todo: add an option for the room generator to generate rooms that have odd widths/positions
        ToggleButton fillButton = new ToggleButton("Fill");
        fillButton.setMaxWidth(Double.MAX_VALUE);
        fillButton.setToggleGroup(toggleGroup);
        fillButton.setOnAction(e -> {
            settingsPane.setContent(fillSettingsPane);
            curGen = Generator.FILL;
        });
        buttonPane.getChildren().add(fillButton);

        ToggleButton caveGeneratorButton = new ToggleButton("Cave Generator");
        caveGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        caveGeneratorButton.setToggleGroup(toggleGroup);
        caveGeneratorButton.setOnAction(e -> {
            settingsPane.setContent(caveGeneratorSettingsPane);
            curGen = Generator.CAVE;
        });
        buttonPane.getChildren().add(caveGeneratorButton);

        ToggleButton roomGeneratorButton = new ToggleButton("Room Generator");
        roomGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        roomGeneratorButton.setToggleGroup(toggleGroup);
        roomGeneratorButton.setOnAction(e -> {
            settingsPane.setContent(roomGeneratorSettingsPane);
            curGen = Generator.ROOM;
        });
        buttonPane.getChildren().add(roomGeneratorButton);

        ToggleButton mazeGeneratorButton = new ToggleButton("Maze Generator");
        mazeGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        mazeGeneratorButton.setToggleGroup(toggleGroup);
        mazeGeneratorButton.setOnAction(e -> {
            settingsPane.setContent(mazeGeneratorSettingsPane);
            curGen = Generator.MAZE;
        });
        buttonPane.getChildren().add(mazeGeneratorButton);

        ToggleButton compoundGeneratorButton = new ToggleButton("Compound " +
                "Generator");
        compoundGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        compoundGeneratorButton.setToggleGroup(toggleGroup);
        compoundGeneratorButton.setOnAction(e -> {
            // todo: add compound generator
        });
        buttonPane.getChildren().add(compoundGeneratorButton);

        // todo: generate button should stand out more
        Button generateButton = new Button("Generate");
        generateButton.setMaxWidth(Double.MAX_VALUE);
        generateButton.setOnAction(e -> {
            if (selection.isSelecting()) {
                Map map = canvasPane.getMap();
                TerrainGenerator gen = new CaveGenerator(0.45d, 3, 4, 5,
                        Tile.BLUE, Tile.WHITE);
                if (curGen == Generator.FILL) {
                    gen = new Fill(fillSettingsPane.getColor());
                } else if (curGen == Generator.CAVE) {
                    gen = new CaveGenerator(
                            caveGeneratorSettingsPane.getOnProb(),
                            caveGeneratorSettingsPane.getOffThreshold(),
                            caveGeneratorSettingsPane.getOnThreshold(),
                            caveGeneratorSettingsPane.getNumberOfSteps(),
                            caveGeneratorSettingsPane.getBackTile(),
                            caveGeneratorSettingsPane.getFloorTile());
                } else if (curGen == Generator.ROOM) {
                    try {
                        gen = new RoomGenerator(
                                roomGeneratorSettingsPane.getSnapToOddPos(),
                                roomGeneratorSettingsPane.getAttempts(),
                                roomGeneratorSettingsPane.getMinWidthValue(),
                                roomGeneratorSettingsPane.getMaxWidthValue(),
                                roomGeneratorSettingsPane.getMinHeightValue(),
                                roomGeneratorSettingsPane.getMaxHeightValue(),
                                roomGeneratorSettingsPane.getFloorTile()
                        );
                    } catch (IllegalArgumentException ex) {
                        showErrorMessage(ex.getMessage());
                        return;
                    }
                } else if (curGen == Generator.MAZE) {
                    gen = new MazeGenerator(
                            mazeGeneratorSettingsPane.getElementPicker(),
                            mazeGeneratorSettingsPane.getFloorTile()
                    );
                } else if (curGen == Generator.COMPOUND) {

                }

                try {
                    canvasPane.setMap(gen.generate(selection.getX(),
                            selection.getY(), selection.getWidth(),
                            selection.getHeight(), map));
                } catch (IllegalArgumentException ex) {
                    showErrorMessage(ex.getMessage());
                }
                mapPane.updateMinimap();
            }
        });

        settingsPane.setContent(caveGeneratorSettingsPane);
        curGen = Generator.CAVE;
        toggleGroup.getToggles().get(0).setSelected(true);

        add(selectionXLabel, 0, 0);
        add(selectionX, 1, 0);
        add(selectionYLabel, 0, 1);
        add(selectionY, 1, 1);
        add(selectionWidthLabel, 0, 2);
        add(selectionWidth, 1, 2);
        add(selectionHeightLabel, 0, 3);
        add(selectionHeight, 1, 3);
        add(selectAllButton, 0, 4, 2, 1);
        add(generatorLabel, 0, 5, 2, 1);
        add(buttonPane, 0, 6, 2, 1);
        add(generateButton, 0, 7, 2, 1);
        add(generatorSettingsLabel, 0, 8, 2, 1);
        add(settingsPane, 0, 9, 2, 1);
    }

    private static void showErrorMessage(String errorMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(errorMsg);
        alert.showAndWait();
    }
}
