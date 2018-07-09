package nl.hazenebula.oubliette;

import javafx.scene.paint.Color;

import java.io.Serializable;

public enum Tile implements Serializable {
    WHITE(Color.WHITE, "White"),
    BLUE(Color.rgb(51, 153, 204), "Blue"),
    RED(Color.rgb(183, 65, 14), "Red"),
    GREEN(Color.rgb(169, 186, 157), "Green"),
    GRAY(Color.GRAY, "Gray"),
    BLACK(Color.BLACK, "Black");

    private Color color;
    private String str;

    Tile(Color color, String str) {
        this.color = color;
        this.str = str;
    }

    public Color color() {
        return color;
    }

    @Override
    public String toString() {
        return str;
    }

    public static Tile fromString(String str) {
        for (Tile tile : Tile.values()) {
            if (tile.toString().equals(str)) {
                return tile;
            }
        }

        return null;
    }
}
