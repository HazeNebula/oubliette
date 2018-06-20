package com.hazenebula.oubliette;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class View extends GridPane {
    private GridPane leftBar;
    private Grid grid;
    private ToolPane toolPane;

    // todo: add left bar
    // todo: add minimap to left bar
    // todo: add scale controls to left bar
    // todo: add menu bar

    public View() {
        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.ALWAYS);

        getRowConstraints().add(rc);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.SOMETIMES);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHgrow(Priority.NEVER);
        ColumnConstraints cc3 = new ColumnConstraints();
        cc3.setHgrow(Priority.SOMETIMES);
        getColumnConstraints().addAll(cc1, cc2, cc3);

        leftBar = new GridPane();
        grid = new Grid();
        toolPane = new ToolPane(grid);

        leftBar.setBackground(new Background(new BackgroundFill(Field.BLANK.getColor(), null, null)));
        Button b = new Button("abc");
        b.setOnAction(e -> grid.drawGrid());
        leftBar.add(b, 0, 0);

        add(leftBar, 0, 0);
        add(grid, 1, 0);
        add(toolPane, 2, 0);
    }
}
