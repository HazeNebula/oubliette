package nl.hazenebula.oubliette;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Minimap extends Canvas {
    private WritableImage img;

    public Minimap(WritableImage img) {
        this.img = img;

        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, width, height);

        double imgWidth = img.getWidth();
        double imgHeight = img.getHeight();

        if (imgWidth >= imgHeight) {
            double ratio = imgHeight / imgWidth;
            gc.drawImage(img, 0, (height - ratio * height) / 2, width,
                    ratio * height);
        } else {
            double ratio = imgWidth / imgHeight;
            gc.drawImage(img, (width - ratio * width) / 2, 0, ratio * width,
                    height);
        }
    }

    public void update(WritableImage img) {
        this.img = img;
        draw();
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
