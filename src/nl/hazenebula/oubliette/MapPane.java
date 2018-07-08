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

    private final CanvasPane canvasPane;
    private final Minimap minimap;

    public MapPane(CanvasPane canvasPane) {
        this.canvasPane = canvasPane;
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,
                null)));
        setPadding(new Insets(5, 5, 5, 5));
        setHgap(5);
        setVgap(5);

        minimap = new Minimap(canvasPane.snapshot());
        minimap.widthProperty().bind(widthProperty().subtract(
                getInsets().getLeft() + getInsets().getRight()));
        minimap.heightProperty().bind(minimap.widthProperty());

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(
                UPDATE_SECONDS), e -> {
            if (!canvasPane.isDrawing()) {
                updateMinimap();
            }
        }));
        timeline.playFromStart();

        Label sizeLabel = new Label("Size:");

        Spinner<Integer> sizeField = new Spinner<>(CanvasPane.MIN_SQUARE_SIZE,
                CanvasPane.MAX_SQUARE_SIZE, CanvasPane.INIT_SQUARE_SIZE, 1);
        sizeField.setEditable(true);
        sizeField.valueProperty().addListener((observable, oldValue, newValue)
                -> canvasPane.setSquareSize(newValue));

        add(minimap, 0, 0);
        add(sizeLabel, 0, 1);
        add(sizeField, 1, 1);
    }

    public void updateMinimap() {
        minimap.update(canvasPane.snapshot());
    }
}
