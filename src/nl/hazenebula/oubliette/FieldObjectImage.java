package nl.hazenebula.oubliette;

import javafx.scene.image.Image;

public class FieldObjectImage implements Comparable<FieldObjectImage> {
    private Image[][] images;
    private String name;

    public FieldObjectImage(int width, int height) {
        images = new Image[width][height];
        name = null;
    }

    public void setImage(int sizeX, int sizeY, Image image) {
        if (sizeX >= 1 && sizeX <= images.length
                && sizeY >= 0 && sizeY <= images[sizeX - 1].length) {
            images[sizeX - 1][sizeY - 1] = image;
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

    public int compareTo(FieldObjectImage other) {
        return name.compareTo(other.name);
    }
}
