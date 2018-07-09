package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Map;
import nl.hazenebula.oubliette.Tile;

import java.util.ArrayList;
import java.util.List;

public class MazeGenerator implements TerrainGenerator {
    public static final MazeType MAZE_TYPE = MazeType.LAST;
    public static final Tile FLOOR_TILE = Tile.WHITE;

    private final ElementPicker<Point> ep;
    private final Tile floorTile;

    private int width;
    private int height;
    private boolean[][] prohibitedToDraw;

    public MazeGenerator(ElementPicker<Point> ep, Tile floorTile) {
        this.ep = ep;
        this.floorTile = floorTile;
    }

    private boolean[][] initializeGrid(int xoffset, int yoffset, Map map) {
        boolean[][] grid = new boolean[width][height];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (map.getField(x + xoffset, y + yoffset) == floorTile) {
                    for (int dx = -1; dx <= 1; ++dx) {
                        for (int dy = -1; dy <= 1; ++dy) {
                            int newX = x + dx;
                            int newY = y + dy;

                            if (newX >= 0 && newX < width && newY >= 0
                                    && newY < height) {
                                grid[newX][newY] = true;
                            }
                        }
                    }
                }
            }
        }

        prohibitedToDraw = new boolean[width][height];
        for (int x = 0; x < width; ++x) {
            System.arraycopy(grid[x], 0, prohibitedToDraw[x], 0, height);
        }

        return grid;
    }

    private List<Direction> getUnvisitedDirections(boolean[][] grid,
                                                   Point cell) {
        List<Direction> dirs = new ArrayList<>(Direction.values().length);

        for (Direction dir : Direction.values()) {
            Point newCell = new Point(cell.x + 2 * dir.dx(),
                    cell.y + 2 * dir.dy());
            if (newCell.x >= 0 && newCell.x < width && newCell.y >= 0
                    && newCell.y < height && !grid[newCell.x][newCell.y]) {
                dirs.add(dir);
            }
        }

        return dirs;
    }

    private List<Point> getExpandableCells(boolean[][] grid) {
        List<Point> cells = new ArrayList<>();

        for (int x = 0; x < width; x += 2) {
            for (int y = 0; y < height; y += 2) {
                Point cell = new Point(x, y);
                if (!grid[cell.x][cell.y]) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    private void expandCell(boolean[][] grid, Point cell) {
        grid[cell.x][cell.y] = true;
    }

    private Point addPath(boolean[][] grid, Point cell, Direction dir) {
        grid[cell.x + dir.dx()][cell.y + dir.dy()] = true;
        grid[cell.x + 2 * dir.dx()][cell.y + 2 * dir.dy()] = true;

        return new Point(cell.x + 2 * dir.dx(), cell.y + 2 * dir.dy());
    }

    private Point performStep(boolean[][] grid, Point cell) {
        expandCell(grid, cell);

        List<Direction> dirs = getUnvisitedDirections(grid, cell);
        if (dirs.isEmpty()) {
            return null;
        }
        Direction dir = dirs.get(Util.randInt(0, dirs.size()));

        return addPath(grid, cell, dir);
    }

    private void carveMaze(int xoffset, int yoffset, boolean[][] grid,
                           Map map) {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (grid[x][y] && !prohibitedToDraw[x][y]) {
                    map.setField(x + xoffset, y + yoffset, floorTile);
                }
            }
        }
    }

    @Override
    public Map generate(int x, int y, int selectionWidth, int selectionHeight,
                        Map map) throws IllegalArgumentException {
        width = selectionWidth - (1 - selectionWidth % 2);
        height = selectionHeight - (1 - selectionHeight % 2);

        boolean[][] grid = initializeGrid(x, y, map);

        List<Point> expandableCells = getExpandableCells(grid);
        while (!expandableCells.isEmpty()) {
            List<Point> cells = new ArrayList<>();
            cells.add(expandableCells.remove(Util.randInt(0,
                    expandableCells.size())));

            ep.setList(cells);
            while (!cells.isEmpty()) {
                Point cell = ep.choose();
                Point newCell = performStep(grid, cell);

                if (newCell == null) {
                    ep.removeLastChoice();
                } else {
                    ep.add(newCell);
                }
            }

            expandableCells = getExpandableCells(grid);
        }

        carveMaze(x, y, grid, map);

        return map;
    }
}
