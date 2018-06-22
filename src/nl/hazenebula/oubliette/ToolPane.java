package nl.hazenebula.oubliette;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ToolPane extends TabPane {
    private final Grid grid;

    // todo: add wall object tab

    // todo: add object tab
    // todo: add object resizing
    // todo: add canvas to show resized image (on grid)
    public ToolPane(Grid grid) {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));

        this.grid = grid;

        FieldColorPane fieldBrushes = new FieldColorPane();
        Tab fieldTab = new Tab("Field Brushes", fieldBrushes);
        getTabs().add(fieldTab);
    }
}
