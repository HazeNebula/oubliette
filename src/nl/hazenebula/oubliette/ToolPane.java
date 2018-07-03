package nl.hazenebula.oubliette;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ToolPane extends TabPane {
    public ToolPane(Grid grid) {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));

        FieldColorPane fieldColors = new FieldColorPane(grid);
        Tab fieldTab = new Tab("Fields", fieldColors);
        getTabs().add(fieldTab);
        fieldTab.setOnSelectionChanged(e -> {
            if (fieldTab.isSelected()) {
                grid.setBrush(Brush.FIELD);
            }
        });

        FieldObjectPane fieldObjects = new FieldObjectPane(grid);
        Tab fieldObjectTab = new Tab("Objects", fieldObjects);
        getTabs().add(fieldObjectTab);
        fieldObjectTab.setOnSelectionChanged(e -> {
            if (fieldObjectTab.isSelected()) {
                grid.setBrush(Brush.FIELD_OBJECT);
            }
        });

        WallObjectPane wallObjects = new WallObjectPane(grid);
        Tab wallObjectTab = new Tab("Walls", wallObjects);
        getTabs().add(wallObjectTab);
        wallObjectTab.setOnSelectionChanged(e -> {
            if (wallObjectTab.isSelected()) {
                grid.setBrush(Brush.WALL_OBJECT);
            }
        });
    }
}
