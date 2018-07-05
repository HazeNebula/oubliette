package nl.hazenebula.oubliette;

import javafx.scene.image.Image;

import java.io.File;

public class WallObjectImage implements Comparable<WallObjectImage> {
    private Image[] images;
    private String name;

    public WallObjectImage(int width) {
        images = new Image[width];
        name = null;
    }

    public void setImage(int sizeX, File file) {
        if (sizeX >= 1 && sizeX <= images.length) {
            images[sizeX - 1] = new Image("file:" + file.toString());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage(int sizeX) {
        return images[sizeX - 1];
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return images.length;
    }

    public boolean completed() {
        for (Image image : images) {
            if (image == null) {
                return false;
            }
        }

        return true;
    }

    public int compareTo(WallObjectImage other) {
        return name.compareTo(other.name);
    }
}
