package nl.hazenebula.oubliette;

import javafx.scene.paint.Color;

import java.io.Serializable;

public enum Field implements Serializable {
    WHITE(Color.WHITE, "White"),
    BLUE(Color.rgb(51, 153, 204), "Blue"),
    RED(Color.rgb(183, 65, 14), "Red"),
    GREEN(Color.rgb(169, 186, 157), "Green"),
    GRAY(Color.GRAY, "Gray"),
    BLACK(Color.BLACK, "Black");

    private Color color;
    private String str;

    Field(Color color, String str) {
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

    public static Field fromString(String str) {
        for (Field field : Field.values()) {
            if (field.toString().equals(str)) {
                return field;
            }
        }

        return null;
    }
}
