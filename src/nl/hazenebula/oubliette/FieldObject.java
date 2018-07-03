package nl.hazenebula.oubliette;

import javafx.scene.image.Image;

public class FieldObject {
    private Image img;
    private int x;
    private int y;
    private int width;
    private int height;
    private Direction dir;

    public FieldObject(Image img, int x, int y, int width, int height,
                       Direction dir) {
        this.img = img;
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.dir = dir;
    }

    public FieldObject(FieldObject other) {
        img = other.img;
        x = other.x;
        y = other.y;
        width = other.width;
        height = other.height;
        dir = other.dir;
    }

    public Image getImage() {
        return img;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public void setImage(Image img) {
        this.img = img;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public boolean inBounds(int x, int y) {
        return x >= getX() && x < getX() + getWidth()
                && y >= getY() && y < getY() + getHeight();
    }
}
