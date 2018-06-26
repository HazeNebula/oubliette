package nl.hazenebula.oubliette;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Minimap extends Canvas {
    public Minimap() {
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, width, height);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public double minWidth(double height) {
        return 0.0d;
    }

    @Override
    public double minHeight(double width) {
        return 0.0d;
    }
}
