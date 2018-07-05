package nl.hazenebula.oubliette;

import javafx.scene.image.Image;

public class WallObject {
    private Image img;
    private int x;
    private int y;
    private int width;
    private Direction dir;

    public WallObject(Image img, int width, Direction dir) {
        this.img = img;
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.dir = dir;
    }

    public WallObject(WallObject other) {
        img = other.img;
        x = 0;
        y = 0;
        width = other.width;
        dir = other.dir;
    }

    public WallObject(WallObject other, int x, int y) {
        img = other.img;
        this.x = x;
        this.y = y;
        width = other.width;
        dir = other.dir;
    }

    public Image getImage() {
        return img;
    }

    public int getWidth() {
        return width;
    }

    public Direction getDir() {
        return dir;
    }

    public void setImage(Image img) {
        this.img = img;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
