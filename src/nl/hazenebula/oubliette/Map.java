package nl.hazenebula.oubliette;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Map implements Serializable {
    private Tile[][] tiles;
    private List<FieldObject> objects;
    private Wall[][][] walls;

    public Map(int width, int height, Tile fill) {
        tiles = new Tile[width][height];
        for (int x = 0; x < tiles.length; ++x) {
            for (int y = 0; y < tiles[x].length; ++y) {
                tiles[x][y] = fill;
            }
        }

        objects = new LinkedList<>();

        walls = new Wall[width + 1][height + 1][2];
    }

    public void resize(int width, int height, Tile fill) {
        Tile[][] newTiles = new Tile[width][height];
        for (int x = 0; x < newTiles.length; ++x) {
            for (int y = 0; y < newTiles[x].length; ++y) {
                if (x < tiles.length && y < tiles[x].length) {
                    newTiles[x][y] = tiles[x][y];
                } else {
                    newTiles[x][y] = fill;
                }
            }
        }
        tiles = newTiles;

        List<FieldObject> newObjects = new LinkedList<>();
        for (FieldObject obj : objects) {
            if (obj.getX() >= 0 && obj.getX() + obj.getWidth() <= width
                    && obj.getY() >= 0 && obj.getY() <= height) {
                newObjects.add(obj);
            }
        }
        objects = newObjects;

        Wall[][][] newWalls = new Wall[width][height][2];
        for (int x = 0; x < Math.min(walls.length, width); ++x) {
            for (int y = 0; y < Math.min(walls.length, height); ++y) {
                newWalls[x][y][Direction.NORTH.id()] = walls[x][y]
                        [Direction.NORTH.id()];
                newWalls[x][y][Direction.EAST.id()] = walls[x][y]
                        [Direction.EAST.id()];
            }
        }
        walls = newWalls;
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public Tile getField(int x, int y) {
        return tiles[x][y];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setField(int x, int y, Tile tile) {
        tiles[x][y] = tile;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public List<FieldObject> getObjects() {
        return objects;
    }

    public void setObjects(List<FieldObject> objects) {
        this.objects = objects;
    }

    public int getWallWidth() {
        return walls.length;
    }

    public int getWallHeight() {
        return walls[0].length;
    }

    public Wall getWall(int x, int y, Direction dir) {
        return walls[x][y][dir.id()];
    }

    public Wall[][][] getWalls() {
        return walls;
    }

    public void setWall(int x, int y, Direction dir, Wall wall) {
        walls[x][y][dir.id()] = wall;
    }

    public void setWalls(Wall[][][] walls) {
        this.walls = walls;
    }
}
