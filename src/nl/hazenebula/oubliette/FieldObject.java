package nl.hazenebula.oubliette;

import javafx.scene.image.Image;

public class FieldObject {
    private final Image img;

    private final int width;
    private final int height;
    private final Direction dir;

    public FieldObject(Image img, int width, int height, Direction dir) {
        this.img = img;
        this.width = width;
        this.height = height;
        this.dir = dir;
    }

    public Image getImage() {
        return img;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Direction getDir() {
        return dir;
    }
}
