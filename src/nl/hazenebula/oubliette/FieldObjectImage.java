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
    }

    public void setImage(int x, int y, String path) throws IOException {
        if (x >= 0 && x < images.length && y >= 0 && y < images[x].length) {
            images[x][y] = ImageIO.read(new File(path));
        }
    }

    public BufferedImage getImage(int x, int y) {
        return images[x][y];
    }

    public String getName() {
        return name;
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
