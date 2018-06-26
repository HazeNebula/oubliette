package nl.hazenebula.oubliette;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MapBar extends GridPane {
    private static final int UPDATE_SECONDS = 10;

    private final Minimap minimap;

    // todo: add scale controls to left bar
    public MapBar(Grid grid) {
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));
        setPadding(new Insets(5, 5, 5, 5));

        minimap = new Minimap(grid.snapshot());
        minimap.widthProperty().bind(widthProperty().subtract(
                getInsets().getLeft() + getInsets().getRight()));
        minimap.heightProperty().bind(minimap.widthProperty());

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(
                UPDATE_SECONDS), e -> minimap.update(grid.snapshot())));
        timeline.playFromStart();

        add(minimap, 0, 0);
    }
}
