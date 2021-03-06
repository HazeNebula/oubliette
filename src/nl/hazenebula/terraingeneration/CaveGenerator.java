package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Map;
import nl.hazenebula.oubliette.Tile;

public class CaveGenerator implements TerrainGenerator {
    public static final double ON_PROB = 0.45d;
    public static final int OFF_THRESHOLD = 3;
    public static final int ON_THRESHOLD = 4;
    public static final int NUMBER_OF_STEPS = 5;
    public static final Tile BACK_TILE = Tile.BLUE;
    public static final Tile FLOOR_TILE = Tile.WHITE;

    private final double onProb;
    private final int offThreshold;
    private final int onThreshold;
    private final int numberOfSteps;
    private final Tile backColor;
    private final Tile floor;

    public CaveGenerator(double onProb, int offThreshold, int onThreshold,
                         int numberOfSteps, Tile backTile, Tile floorTile) {
        this.onProb = onProb;
        this.offThreshold = offThreshold;
        this.onThreshold = onThreshold;
        this.numberOfSteps = numberOfSteps;
        this.backColor = backTile;
        this.floor = floorTile;
    }

    private boolean[][] initializeGrid(int width, int height) {
        boolean[][] grid = new boolean[width][height];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (Util.randDouble() < onProb) {
                    grid[x][y] = true;
                } else {
                    grid[x][y] = false;
                }
            }
        }

        return grid;
    }

    private int countAliveNeighbours(boolean[][] grid, int xPos, int yPos) {
        int nNeighbours = 0;

        for (int x = xPos - 1; x <= xPos + 1; ++x) {
            for (int y = yPos - 1; y <= yPos + 1; ++y) {
                if (x >= 0 && x < grid.length && y >= 0 && y < grid[x].length) {
                    if (x != xPos || y != yPos) {
                        if (grid[x][y]) {
                            nNeighbours++;
                        }
                    }
                }
            }
        }

        return nNeighbours;
    }

    private boolean[][] doSimulationStep(boolean[][] oldGrid) {
        boolean[][] grid = new boolean[oldGrid.length][oldGrid[0].length];

        for (int x = 0; x < grid.length; ++x) {
            for (int y = 0; y < grid[x].length; ++y) {
                int nNeighbours = countAliveNeighbours(oldGrid, x, y);
                if (oldGrid[x][y]) {
                    grid[x][y] = nNeighbours >= offThreshold;
                } else {
                    grid[x][y] = nNeighbours > onThreshold;
                }
            }
        }

        return grid;
    }

    private void carvePassages(int xoffset, int yoffset, boolean[][] grid,
                               Map map) {
        for (int x = 0; x < grid.length; ++x) {
            for (int y = 0; y < grid[x].length; ++y) {
                Tile tile = (grid[x][y]) ? floor : backColor;
                map.setField(x + xoffset, y + yoffset, tile);
            }
        }
    }

    @Override
    public Map generate(int x, int y, int width, int height, Map map) {
        boolean[][] grid = initializeGrid(width, height);

        for (int i = 0; i < numberOfSteps; ++i) {
            grid = doSimulationStep(grid);
        }

        carvePassages(x, y, grid, map);

        return map;
    }
}
