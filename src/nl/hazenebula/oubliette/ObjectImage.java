package nl.hazenebula.oubliette;

import javafx.scene.image.Image;

import java.io.File;

public class ObjectImage implements Comparable<ObjectImage> {
    private Image[][] images;
    private String name;

    public ObjectImage(int width, int height) {
        images = new Image[width][height];
        name = null;
    }

    public void setImage(int sizeX, int sizeY, File file) {
        if (sizeX >= 1 && sizeX <= images.length
                && sizeY >= 0 && sizeY <= images[sizeX - 1].length) {
            images[sizeX - 1][sizeY - 1] = new Image("file:" + file.toString());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage(int sizeX, int sizeY) {
        return images[sizeX - 1][sizeY - 1];
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return images.length;
    }

    public int getHeight() {
        return images[0].length;
    }

    public boolean completed() {
        for (Image[] imageArray : images) {
            for (Image image : imageArray) {
                if (image == null) {
                    return false;
                }
            }
        }

        return true;
    }

    public int compareTo(ObjectImage other) {
        return name.compareTo(other.name);
    }
}
