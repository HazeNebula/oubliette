package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Map;

public interface TerrainGenerator {
    Map generate(int x, int y, int width, int height, Map map) throws
            IllegalArgumentException;
}
