package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Field;
import nl.hazenebula.oubliette.Map;

public class RoomGenerator implements TerrainGenerator {
    private final int numberOfAttempts;
    private final int minWidth;
    private final int maxWidth;
    private final int minHeight;
    private final int maxHeight;
    private final Field floorTile;

    private int curX;
    private int curY;
    private int curWidth;
    private int curHeight;

    public RoomGenerator(int numberOfAttempts, int minWidth, int maxWidth,
                         int minHeight, int maxHeight, Field floorTile)
            throws IllegalArgumentException {
        if (maxWidth < minWidth) {
            throw new IllegalArgumentException("The minimum width of a room " +
                    "is larger than the maximum width.");
        }
        if (maxHeight < minHeight) {
            throw new IllegalArgumentException("The minimum height of a room " +
                    "is larger than the maximum height.");
        }

        this.numberOfAttempts = numberOfAttempts;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.floorTile = floorTile;
    }

    private void getRoom(int width, int height) {
        curWidth = Integer.MAX_VALUE;
        curHeight = Integer.MAX_VALUE;

        while (curWidth > width || curHeight > height) {
            curWidth = Util.randInt(minWidth, maxWidth + 1);
            curHeight = Util.randInt(minHeight, maxHeight + 1);
        }

        curX = Util.randInt(0, width - curWidth);
        curY = Util.randInt(0, height - curHeight);
    }

    private boolean[][] initializeGrid(int xoffset, int yoffset, int width,
                                       int height, Map map) {
        boolean[][] grid = new boolean[width][height];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                grid[x][y] = map.getField(x + xoffset, y + yoffset)
                        == floorTile;
            }
        }

        return grid;
    }

    private boolean roomFits(boolean[][] grid) {
        for (int x = curX; x < curX + curWidth; ++x) {
            for (int y = curY; y < curY + curHeight; ++y) {
                if (grid[x][y]) {
                    return false;
                }
            }
        }

        return true;
    }

    private void placeRoomOnGrid(boolean[][] grid) {
        for (int x = curX; x < curX + curWidth; ++x) {
            for (int y = curY; y < curY + curHeight; ++y) {
                grid[x][y] = true;
            }
        }
    }

    private void carveRooms(int xoffset, int yoffset, int width, int height,
                            boolean[][] grid, Map map) {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (grid[x][y]) {
                    map.setField(x + xoffset, y + yoffset, floorTile);
                }
            }
        }
    }

    public Map generate(int x, int y, int width, int height, Map map) throws
            IllegalArgumentException {
        if (maxWidth > width) {
            throw new IllegalArgumentException("Maximum width exceeds the " +
                    "width of the selected area.");
        }
        if (maxHeight > height) {
            throw new IllegalArgumentException("Maximum height exceeds the " +
                    "height of the selected area.");
        }

        boolean[][] grid = initializeGrid(x, y, width, height, map);

        for (int i = 0; i < numberOfAttempts; ++i) {
            getRoom(width, height);
            if (roomFits(grid)) {
                placeRoomOnGrid(grid);
            }
        }

        carveRooms(x, y, width, height, grid, map);

        return map;
    }
}
