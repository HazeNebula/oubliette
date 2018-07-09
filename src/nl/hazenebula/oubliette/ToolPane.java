package nl.hazenebula.oubliette;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ToolPane extends TabPane {
    public ToolPane(MapPane mapPane, CanvasPane canvasPane) {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));

        FieldColorPane fieldColorPane = new FieldColorPane(canvasPane);
        Tab fieldTab = new Tab("Fields", fieldColorPane);
        getTabs().add(fieldTab);
        fieldTab.setOnSelectionChanged(e -> {
            if (fieldTab.isSelected()) {
                canvasPane.setBrush(Tool.FIELD);
                canvasPane.drawAll();
            }
        });

        FieldObjectPane fieldObjectPane = new FieldObjectPane(canvasPane);
        Tab fieldObjectTab = new Tab("Objects", fieldObjectPane);
        getTabs().add(fieldObjectTab);
        fieldObjectTab.setOnSelectionChanged(e -> {
            if (fieldObjectTab.isSelected()) {
                canvasPane.setBrush(Tool.FIELD_OBJECT);
                canvasPane.drawAll();
            }
        });

        WallPane wallObjectPane = new WallPane(canvasPane);
        Tab wallObjectTab = new Tab("Walls", wallObjectPane);
        getTabs().add(wallObjectTab);
        wallObjectTab.setOnSelectionChanged(e -> {
            if (wallObjectTab.isSelected()) {
                canvasPane.setBrush(Tool.WALL);
                canvasPane.drawAll();
            }
        });

        GeneratorPane generatorPane = new GeneratorPane(mapPane, canvasPane);
        Tab generatorTab = new Tab("Generators", generatorPane);
        getTabs().add(generatorTab);
        generatorTab.setOnSelectionChanged(e -> {
            if (generatorTab.isSelected()) {
                canvasPane.setBrush(Tool.SELECTION);
                canvasPane.drawAll();
            }
        });
    }
}
