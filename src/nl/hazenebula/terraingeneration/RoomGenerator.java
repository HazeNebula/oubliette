package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Map;
import nl.hazenebula.oubliette.Tile;

public class RoomGenerator implements TerrainGenerator {
    public static final int NUMBER_OF_ATTEMPTS = 1000;
    public static final int MIN_WIDTH = 3;
    public static final int MAX_WIDTH = 7;
    public static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 7;
    public static final Tile FLOOR_TILE = Tile.WHITE;

    private final boolean snapToOddPos;
    private final int numberOfAttempts;
    private final int minWidth;
    private final int maxWidth;
    private final int minHeight;
    private final int maxHeight;
    private final Tile floorTile;

    private int curX;
    private int curY;
    private int curWidth;
    private int curHeight;

    public RoomGenerator(boolean snapToOddPos, int numberOfAttempts,
                         int minWidth, int maxWidth, int minHeight,
                         int maxHeight, Tile floorTile)
            throws IllegalArgumentException {
        if (maxWidth < minWidth) {
            throw new IllegalArgumentException("The minimum width of a room " +
                    "is larger than the maximum width.");
        } else if (maxHeight < minHeight) {
            throw new IllegalArgumentException("The minimum height of a room " +
                    "is larger than the maximum height.");
        } else if (snapToOddPos && minWidth % 2 == 0) {
            throw new IllegalArgumentException("The minimum width should be " +
                    "odd.");
        } else if (snapToOddPos && maxWidth % 2 == 0) {
            throw new IllegalArgumentException("The maximum width should be " +
                    "odd.");
        } else if (snapToOddPos && minHeight % 2 == 0) {
            throw new IllegalArgumentException("The minimum height should be " +
                    "odd.");
        } else if (snapToOddPos && maxHeight % 2 == 0) {
            throw new IllegalArgumentException("The maximum height should be " +
                    "odd.");
        }

        this.snapToOddPos = snapToOddPos;
        this.numberOfAttempts = numberOfAttempts;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.floorTile = floorTile;
    }

    private void getRoom(int width, int height) {
        if (snapToOddPos) {
            curWidth = Util.randInt(0, (maxWidth - minWidth) / 2 + 1) * 2
                    + minWidth;
            curHeight = Util.randInt(0, (maxHeight - minHeight) / 2 + 1) * 2
                    + minHeight;

            curX = Util.randInt(0, (width - curWidth) / 2 + 1) * 2;
            curY = Util.randInt(0, (height - curHeight) / 2 + 1) * 2;
        } else {
            curWidth = Integer.MAX_VALUE;
            curHeight = Integer.MAX_VALUE;

            while (curWidth > width || curHeight > height) {
                curWidth = Util.randInt(minWidth, maxWidth + 1);
                curHeight = Util.randInt(minHeight, maxHeight + 1);
            }

            curX = Util.randInt(0, width - curWidth);
            curY = Util.randInt(0, height - curHeight);
        }
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

        if (snapToOddPos) {
            width = width - (1 - width % 2);
            height = height - (1 - height % 2);
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
