package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Field;
import nl.hazenebula.oubliette.Map;

public class Fill implements TerrainGenerator {
    public static final Field FILL_COLOR = Field.BLUE;

    private Field color;

    public Fill(Field color) {
        this.color = color;
    }

    public Map generate(int xoffset, int yoffset, int width, int height,
                        Map map) throws IllegalArgumentException {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; ++y) {
                map.setField(x + xoffset, y + yoffset, color);
            }
        }

        return map;
    }
}
