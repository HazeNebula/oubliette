package nl.hazenebula.oubliette;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ToolPane extends TabPane {
    private TileColorPane tileColorPane;
    private Tab tileTab;
    private FieldObjectPane fieldObjectPane;
    private Tab fieldObjectTab;
    private WallPane wallPane;
    private Tab wallTab;
    private GeneratorPane generatorPane;
    private Tab generatorTab;

    public ToolPane(MapPane mapPane, CanvasPane canvasPane) {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));

        tileColorPane = new TileColorPane(canvasPane);
        tileTab = new Tab("Colors", tileColorPane);
        getTabs().add(tileTab);
        tileTab.setOnSelectionChanged(e -> {
            if (tileTab.isSelected()) {
                canvasPane.setBrush(Tool.FIELD);
                canvasPane.drawAll();
            }
        });

        fieldObjectPane = new FieldObjectPane(canvasPane);
        fieldObjectTab = new Tab("Objects", fieldObjectPane);
        getTabs().add(fieldObjectTab);
        fieldObjectTab.setOnSelectionChanged(e -> {
            if (fieldObjectTab.isSelected()) {
                canvasPane.setBrush(Tool.FIELD_OBJECT);
                canvasPane.drawAll();
            }
        });

        wallPane = new WallPane(canvasPane);
        wallTab = new Tab("Walls", wallPane);
        getTabs().add(wallTab);
        wallTab.setOnSelectionChanged(e -> {
            if (wallTab.isSelected()) {
                canvasPane.setBrush(Tool.WALL);
                canvasPane.drawAll();
            }
        });

        generatorPane = new GeneratorPane(mapPane, canvasPane);
        generatorTab = new Tab("Generators", generatorPane);
        getTabs().add(generatorTab);
        generatorTab.setOnSelectionChanged(e -> {
            if (generatorTab.isSelected()) {
                canvasPane.setBrush(Tool.SELECTION);
                canvasPane.drawAll();
            }
        });
    }

    public void setupHotkeys(Scene mainScene) {
        mainScene.setOnKeyTyped(e -> {
            if (fieldObjectTab.isSelected()) {
                fieldObjectPane.handleHotkeys(e.getCharacter());
            } else if (wallTab.isSelected()) {
                wallPane.handleHotkeys(e.getCharacter());
            }
        });
    }
}
