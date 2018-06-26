package nl.hazenebula.oubliette;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MapBar extends GridPane {
    private final Canvas minimap;

    // hightodo: change minimap imageview to Canvas
    public MapBar(Grid grid) {
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));
        setPadding(new Insets(5, 5, 5, 5));

        minimap = new Minimap();
        minimap.widthProperty().bind(widthProperty().subtract(
                getInsets().getLeft() + getInsets().getRight()));
        minimap.heightProperty().bind(minimap.widthProperty());

        add(minimap, 0, 0);
    }
}
