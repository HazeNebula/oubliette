package nl.hazenebula.oubliette;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MapPane extends GridPane {
    private static final int UPDATE_SECONDS = 10;

    private final Minimap minimap;

    public MapPane(Grid grid) {
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));
        setPadding(new Insets(5, 5, 5, 5));
        setHgap(5);
        setVgap(5);

        minimap = new Minimap(grid.snapshot());
        minimap.widthProperty().bind(widthProperty().subtract(
                getInsets().getLeft() + getInsets().getRight()));
        minimap.heightProperty().bind(minimap.widthProperty());

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(
                UPDATE_SECONDS), e -> {
            if (!grid.isDrawing()) {
                minimap.update(grid.snapshot());
            }
        }));
        timeline.playFromStart();

        Label sizeLabel = new Label("Size:");

        Spinner<Integer> sizeField = new Spinner<>(Grid.MIN_SQUARE_SIZE,
                Grid.MAX_SQUARE_SIZE, Grid.INIT_SQUARE_SIZE, 1);
        sizeField.setEditable(true);
        sizeField.valueProperty().addListener((observable, oldValue, newValue)
                -> grid.setGridSize(newValue));

        add(minimap, 0, 0);
        add(sizeLabel, 0, 1);
        add(sizeField, 1, 1);
    }
}
