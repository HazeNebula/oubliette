package nl.hazenebula.oubliette;

import javafx.scene.image.Image;

public class WallImage implements Comparable<WallImage> {
    private Image[] images;
    private String name;

    public WallImage(int width) {
        images = new Image[width];
        name = null;
    }

    public void setImage(int sizeX, Image image) {
        if (sizeX >= 1 && sizeX <= images.length) {
            images[sizeX - 1] = image;
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

    public int compareTo(WallImage other) {
        return name.compareTo(other.name);
    }
}
