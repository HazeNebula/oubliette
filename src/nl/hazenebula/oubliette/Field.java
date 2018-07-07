package nl.hazenebula.oubliette;

import javafx.scene.paint.Color;

import java.io.Serializable;

public enum Field implements Serializable {
    WHITE(Color.rgb(255, 255, 255), "White"),
    BLUE(Color.rgb(51, 153, 204), "Blue"),
    GRAY(Color.rgb(211, 211, 211), "Gray"),
    BLACK(Color.rgb(0, 0, 0), "Black");

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
