package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Field;
import nl.hazenebula.oubliette.Map;

public class CaveGenerator implements TerrainGenerator {
    public static final double ON_PROB = 0.45d;
    public static final int OFF_LIMIT = 3;
    public static final int ON_THRESHOLD = 4;
    public static final int NUMBER_OF_STEPS = 5;

    private final double onProb;
    private final int offLimit;
    private final int onThreshold;
    private final int numberOfSteps;
    private final Field openField;

    public CaveGenerator(double onProb, int offLimit, int onThreshold,
                         int numberOfSteps, Field openField) {
        this.onProb = onProb;
        this.offLimit = offLimit;
        this.onThreshold = onThreshold;
        this.numberOfSteps = numberOfSteps;
        this.openField = openField;
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
                    grid[x][y] = nNeighbours >= offLimit;
                } else {
                    grid[x][y] = nNeighbours > onThreshold;
                }
            }
        }

        return grid;
    }

    private Map carvePassages(Map map, boolean[][] grid) {
        for (int x = 0; x < grid.length; ++x) {
            for (int y = 0; y < grid[x].length; ++y) {
                if (grid[x][y]) {
                    map.setField(x, y, openField);
                }
            }
        }

        return map;
    }

    @Override
    public Map generate(Map map) {
        boolean[][] grid = initializeGrid(map.getWidth(), map.getHeight());

        for (int i = 0; i < numberOfSteps; ++i) {
            grid = doSimulationStep(grid);
        }

        return carvePassages(map, grid);
    }
}
