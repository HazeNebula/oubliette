package nl.hazenebula.oubliette;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FieldObjectImage {
    private BufferedImage[][] images;
    private String name;

    public FieldObjectImage(int width, int height) {
        images = new BufferedImage[width][height];
        name = null;
    }

    public void setImage(int sizeX, int sizeY, File file) throws IOException {
        if (sizeX >= 1 && sizeX <= images.length
                && sizeY >= 0 && sizeY <= images[sizeX - 1].length) {
            images[sizeX - 1][sizeY - 1] = ImageIO.read(file);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public BufferedImage getImage(int sizeX, int sizeY) {
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
        for (BufferedImage[] imageArray : images) {
            for (BufferedImage image : imageArray) {
                if (image == null) {
                    return false;
                }
            }
        }

        return true;
    }
}
