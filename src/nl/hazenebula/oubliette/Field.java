package nl.hazenebula.oubliette;

import javafx.scene.paint.Color;

import java.io.Serializable;

public enum Field implements Serializable {
    OPEN(Color.rgb(255, 255, 255), "Open"),
    STONE(Color.rgb(51, 153, 204), "Stone");

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
}
