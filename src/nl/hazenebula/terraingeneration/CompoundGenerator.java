package nl.hazenebula.terraingeneration;

import nl.hazenebula.oubliette.Map;
import nl.hazenebula.oubliette.Tile;

import java.util.*;

public class CompoundGenerator implements TerrainGenerator {
    // room generation defaults
    public static final int NUMBER_OF_ATTEMPTS = 100;
    public static final int MIN_WIDTH = 3;
    public static final int MAX_WIDTH = 7;
    public static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 7;

    // maze generation defaults
    public static final MazeType MAZE_TYPE = MazeType.LAST;

    // compound generation defaults
    public static final double CONNECTION_PROB = 0.02d;
    public static final Tile FLOOR_TILE = Tile.WHITE;

    // room generation fields
    private final int numberOfAttempts;
    private final int minWidth;
    private final int maxWidth;
    private final int minHeight;
    private final int maxHeight;

    // maze generation fields
    private final ElementPicker<Point> ep;

    // compound generation fields
    private final double connectionProb;
    private final Tile floorTile;

    // variable fields
    private boolean[][] prohibitedToDraw;
    private int width;
    private int height;
    private int curX;
    private int curY;
    private int curWidth;
    private int curHeight;
    private List<Set<Point>> regions;

    public CompoundGenerator(int numberOfAttempts, int minWidth, int maxWidth,
                             int minHeight, int maxHeight,
                             ElementPicker<Point> ep, double connectionProb,
                             Tile floorTile) {
        if (minWidth % 2 == 0) {
            throw new IllegalArgumentException("The minimum width should be " +
                    "odd.");
        } else if (maxWidth % 2 == 0) {
            throw new IllegalArgumentException("The maximum width should be " +
                    "odd.");
        } else if (minHeight % 2 == 0) {
            throw new IllegalArgumentException("The minimum height should be " +
                    "odd.");
        } else if (maxHeight % 2 == 0) {
            throw new IllegalArgumentException("The maximum height should be " +
                    "odd.");
        }

        this.numberOfAttempts = numberOfAttempts;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        this.ep = ep;

        this.connectionProb = connectionProb;
        this.floorTile = floorTile;

        this.regions = new LinkedList<>();
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

    private void getRoom(int width, int height) {
        curWidth = Util.randInt(0, (maxWidth - minWidth) / 2 + 1) * 2
                + minWidth;
        curHeight = Util.randInt(0, (maxHeight - minHeight) / 2 + 1) * 2
                + minHeight;

        curX = Util.randInt(0, (width - curWidth) / 2 + 1) * 2;
        curY = Util.randInt(0, (height - curHeight) / 2 + 1) * 2;
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

    private void addRoomRegion() {
        Set<Point> region = new HashSet<>();
        for (int x = curX; x < curX + curWidth; ++x) {
            for (int y = curY; y < curY + curHeight; ++y) {
                region.add(new Point(x, y));
            }
        }

        regions.add(region);
    }

    private void generateRooms(boolean[][] grid) {
        for (int i = 0; i < numberOfAttempts; ++i) {
            getRoom(width, height);
            if (roomFits(grid)) {
                addRoomRegion();
                placeRoomOnGrid(grid);
            }
        }
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

    private void expandCell(boolean[][] grid, Point cell, Set<Point> region) {
        grid[cell.x][cell.y] = true;
        region.add(cell);
    }

    private Point addPath(boolean[][] grid, Point cell, Direction dir,
                          Set<Point> region) {
        Point wall = new Point(cell.x + dir.dx(), cell.y + dir.dy());
        grid[wall.x][wall.y] = true;
        region.add(wall);

        Point newCell = new Point(cell.x + 2 * dir.dx(), cell.y + 2 * dir.dy());
        grid[newCell.x][newCell.y] = true;
        region.add(newCell);

        return newCell;
    }

    private Point performStep(boolean[][] grid, Point cell, Set<Point> region) {
        expandCell(grid, cell, region);

        List<Direction> dirs = getUnvisitedDirections(grid, cell);
        if (dirs.isEmpty()) {
            return null;
        }
        Direction dir = dirs.get(Util.randInt(0, dirs.size()));

        return addPath(grid, cell, dir, region);
    }

    private void generateMazes(boolean[][] grid) {
        List<Point> expandableCells = getExpandableCells(grid);
        while (!expandableCells.isEmpty()) {
            List<Point> cells = new ArrayList<>();
            cells.add(expandableCells.remove(Util.randInt(0,
                    expandableCells.size())));
            Set<Point> region = new HashSet<>();

            ep.setList(cells);
            while (!cells.isEmpty()) {
                Point cell = ep.choose();
                Point newCell = performStep(grid, cell, region);

                if (newCell == null) {
                    ep.removeLastChoice();
                } else {
                    ep.add(newCell);
                }
            }

            regions.add(region);
            expandableCells = getExpandableCells(grid);
        }
    }

    private List<Point> getNeighbors(boolean[][] grid, Point cell) {
        List<Point> neighbors = new ArrayList<>(Direction.values().length);
        for (Direction dir : Direction.values()) {
            Point neighbor = new Point(cell.x + dir.dx(), cell.y + dir.dy());
            if (neighbor.x >= 0 && neighbor.x < width && neighbor.y >= 0
                    && neighbor.y < height) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    private boolean connectsTwoRegions(Point cell, boolean[][] grid,
                                       Set<Point> region) {
        boolean connectsTwoRegions = false;
        for (Direction dir : Direction.values()) {
            Point c1 = new Point(cell.x + dir.dx(), cell.y + dir.dy());
            Point c2 = new Point(cell.x + dir.opposite().dx(), cell.y
                    + dir.opposite().dy());

            if (c1.x >= 0 && c1.x < width && c2.x >= 0 && c2.x < width &&
                    c1.y >= 0 && c1.y < height && c2.y >= 0 && c2.y < height) {
                connectsTwoRegions |= grid[c1.x][c1.y] & grid[c2.x][c2.y]
                        & region.contains(c1) && !region.contains(c2);
            }
        }

        return connectsTwoRegions;
    }

    private boolean connectsTwoRegions(Point cell, boolean[][] grid,
                                       Set<Point> r1, Set<Point> r2) {
        boolean connectsRegions = false;
        for (Direction dir : Direction.values()) {
            Point c1 = new Point(cell.x + dir.dx(), cell.y + dir.dy());
            Point c2 = new Point(cell.x + dir.opposite().dx(), cell.y
                    + dir.opposite().dy());

            if (c1.x >= 0 && c1.x < width && c2.x >= 0 && c2.x < width &&
                    c1.y >= 0 && c1.y < height && c2.y >= 0 && c2.y < height) {
                connectsRegions |= grid[c1.x][c1.y] && grid[c2.x][c2.y]
                        && r1.contains(c1) && r2.contains(c2);
            }
        }

        return connectsRegions;
    }

    private List<Point> getConnectors(boolean[][] grid, Set<Point> region) {
        List<Point> connectors = new ArrayList<>();
        for (Point cell : region) {
            for (Point neighbor : getNeighbors(grid, cell)) {
                if (connectsTwoRegions(neighbor, grid, region)) {
                    connectors.add(neighbor);
                }
            }
        }

        return connectors;
    }

    private List<Point> getConnectors(boolean[][] grid, Set<Point> r1,
                                      Set<Point> r2) {
        List<Point> connectors = new ArrayList<>();
        for (Point cell : r1) {
            for (Point neighbor : getNeighbors(grid, cell)) {
                if (connectsTwoRegions(neighbor, grid, r1, r2)) {
                    connectors.add(neighbor);
                }
            }
        }

        return connectors;
    }

    private Set<Point> getOtherRegion(boolean[][] grid, Set<Point> mainRegion,
                                      Point connector) {
        for (Set<Point> region : regions) {
            if (connectsTwoRegions(connector, grid, mainRegion, region)) {
                return region;
            }
        }

        return null;
    }

    private void connectRegions(boolean[][] grid) {
        Set<Point> mainRegion = regions.remove(0);

        while (!regions.isEmpty()) {
            List<Point> mainConnectors = getConnectors(grid, mainRegion);
            Point firstConnector = mainConnectors.get(Util.randInt(0,
                    mainConnectors.size()));

            // get the other region
            Set<Point> otherRegion = getOtherRegion(grid, mainRegion,
                    firstConnector);

            // get all other connectors connecting the region
            List<Point> connectors = getConnectors(grid, mainRegion,
                    otherRegion);

            // open the connection
            grid[firstConnector.x][firstConnector.y] = true;
            mainRegion.add(firstConnector);

            // for all other connections: open with prob connectionProb
            for (Point connector : connectors) {
                if (Util.randDouble() <= connectionProb) {
                    grid[connector.x][connector.y] = true;
                    mainRegion.add(connector);
                }
            }

            // unify the regions
            mainRegion.addAll(otherRegion);
            regions.remove(otherRegion);
        }
    }

    private boolean isDeadEnd(boolean[][] grid, int x, int y) {
        int nWalls = 0;

        for (Direction dir : Direction.values()) {
            Point cell = new Point(x + dir.dx(), y + dir.dy());

            if (cell.x < 0 || cell.x >= width || cell.y < 0 || cell.y >= height
                    || !grid[cell.x][cell.y]) {
                nWalls++;
            }
        }

        return nWalls >= 3;
    }

    private void sparsify(boolean[][] grid) {
        boolean removedDeadEnds = true;
        while (removedDeadEnds) {
            removedDeadEnds = false;

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (grid[x][y] && isDeadEnd(grid, x, y)) {
                        grid[x][y] = false;
                        removedDeadEnds = true;
                    }
                }
            }
        }
    }

    private void carveCompound(int xoffset, int yoffset, boolean[][] grid,
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
                        Map map)
            throws IllegalArgumentException {
        if (maxWidth > width) {
            throw new IllegalArgumentException("Maximum width exceeds the " +
                    "width of the selected area.");
        }
        if (maxHeight > height) {
            throw new IllegalArgumentException("Maximum height exceeds the " +
                    "height of the selected area.");
        }

        width = selectionWidth - (1 - selectionWidth % 2);
        height = selectionHeight - (1 - selectionHeight % 2);

        boolean[][] grid = initializeGrid(x, y, map);
        generateRooms(grid);
        generateMazes(grid);
        connectRegions(grid);
        sparsify(grid);

        carveCompound(x, y, grid, map);

        return map;
    }
}
