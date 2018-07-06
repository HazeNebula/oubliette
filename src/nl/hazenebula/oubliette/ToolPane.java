package nl.hazenebula.oubliette;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ToolPane extends TabPane {
    public ToolPane(CanvasPane canvasPane) {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));

        FieldColorPane fieldColors = new FieldColorPane(canvasPane);
        Tab fieldTab = new Tab("Fields", fieldColors);
        getTabs().add(fieldTab);
        fieldTab.setOnSelectionChanged(e -> {
            if (fieldTab.isSelected()) {
                canvasPane.setBrush(Brush.FIELD);
            }
        });

        ObjectPane fieldObjects = new ObjectPane(canvasPane);
        Tab fieldObjectTab = new Tab("Objects", fieldObjects);
        getTabs().add(fieldObjectTab);
        fieldObjectTab.setOnSelectionChanged(e -> {
            if (fieldObjectTab.isSelected()) {
                canvasPane.setBrush(Brush.FIELD_OBJECT);
            }
        });

        WallPane wallObjects = new WallPane(canvasPane);
        Tab wallObjectTab = new Tab("Walls", wallObjects);
        getTabs().add(wallObjectTab);
        wallObjectTab.setOnSelectionChanged(e -> {
            if (wallObjectTab.isSelected()) {
                canvasPane.setBrush(Brush.WALL_OBJECT);
            }
        });
    }
}
