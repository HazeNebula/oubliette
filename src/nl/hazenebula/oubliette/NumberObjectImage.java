package nl.hazenebula.oubliette;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

public class NumberObjectImage {
    private static final int SIZE = 250;
    private static final int MAX_SIZE = 175;
    private static final int LINE_WIDTH = 10;

    private Font font;
    private Color color;

    public NumberObjectImage(String fontName, Color color) {
        this.font = Font.font(fontName, MAX_SIZE);
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private double getTextWidth(String str) {
        Text text = new Text(str);
        text.setFont(font);

        return text.getLayoutBounds().getWidth();
    }

    public Image getImage(int number) {
        Canvas canvas = new Canvas(SIZE, SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.save();
        gc.setEffect(new BoxBlur(2, 2, 1));
        gc.setFill(Color.WHITE);
        gc.fillOval(LINE_WIDTH, LINE_WIDTH, SIZE - 2 * LINE_WIDTH, SIZE - 2
                * LINE_WIDTH);
        gc.setEffect(new BoxBlur(2, 2, 1));
        gc.setStroke(color);
        gc.setLineWidth(10);
        gc.strokeOval(LINE_WIDTH, LINE_WIDTH, SIZE - 2 * LINE_WIDTH, SIZE - 2
                * LINE_WIDTH);

        gc.setEffect(null);
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        gc.setFont(font);
        gc.setFill(color);
        double textWidth = Math.min(getTextWidth(Integer.toString(number)),
                MAX_SIZE);
        gc.fillText(Integer.toString(number), (SIZE - textWidth) / 2,
                MAX_SIZE + LINE_WIDTH, MAX_SIZE);
        gc.restore();

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        return canvas.snapshot(sp, null);
    }
}
