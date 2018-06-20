package com.hazenebula.oubliette;

import javafx.scene.paint.Color;

public enum Field {
    BLANK(Grid.FRONT_COLOR), OPEN(Grid.BACK_COLOR);

    private Color color;

    Field(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
