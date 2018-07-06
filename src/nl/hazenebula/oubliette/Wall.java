package nl.hazenebula.oubliette;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Wall implements Serializable {
    private transient Image img;
    private int x;
    private int y;
    private int width;
    private Direction dir;

    public Wall(Image img, int width, Direction dir) {
        this.img = img;
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.dir = dir;
    }

    public Wall(Wall other) {
        img = other.img;
        x = 0;
        y = 0;
        width = other.width;
        dir = other.dir;
    }

    public Wall(Wall other, int x, int y) {
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

    private void readObject(ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        ois.defaultReadObject();
        img = SwingFXUtils.toFXImage(ImageIO.read(ois), null);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", oos);
    }
}
