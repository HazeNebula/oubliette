package nl.hazenebula.oubliette;

import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class View extends GridPane {
    private MenuBar menuBar;
    private GridPane leftBar;
    private Grid grid;
    private ToolPane toolPane;

    // todo: add left bar
    // todo: add minimap to left bar
    // todo: add scale controls to left bar

    public View() {
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

        Menu file = new Menu("File", null, new MenuItem("Save File"),
                new MenuItem("Export as PNG"));
        menuBar = new MenuBar(file);

        leftBar = new GridPane();
        grid = new Grid();
        toolPane = new ToolPane(grid);

        leftBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        Button b = new Button("abc");
        b.setOnAction(e -> grid.drawFullGrid());
        leftBar.add(b, 0, 0);

        add(menuBar, 0, 0);
        add(leftBar, 0, 1);
        add(grid, 1, 1);
        add(toolPane, 2, 1);
    }
}
