package nl.hazenebula.oubliette;

import javafx.scene.paint.Color;

public enum Field {
    EMPTY(Grid.FRONT_COLOR, "Empty"),
    FILLED(Grid.BACK_COLOR, "Filled");

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
