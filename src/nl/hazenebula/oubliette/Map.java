package nl.hazenebula.oubliette;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Map implements Serializable {
    private Field[][] fields;
    private List<FieldObject> objects;
    private WallObject[][][] walls;

    public Map(int width, int height, Field fill) {
        fields = new Field[width][height];
        for (int x = 0; x < fields.length; ++x) {
            for (int y = 0; y < fields[x].length; ++y) {
                fields[x][y] = fill;
            }
        }

        objects = new LinkedList<>();

        walls = new WallObject[width + 1][height + 1][2];
    }

    public int getWidth() {
        return fields.length;
    }

    public int getHeight() {
        return fields[0].length;
    }

    public Field getField(int x, int y) {
        return fields[x][y];
    }

    public void setField(int x, int y, Field field) {
        fields[x][y] = field;
    }

    public void setFields(Field[][] fields) {
        this.fields = fields;
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

    public WallObject getWall(int x, int y, Direction dir) {
        return walls[x][y][dir.id()];
    }

    public void setWall(int x, int y, Direction dir, WallObject val) {
        walls[x][y][dir.id()] = val;
    }

    public void setWalls(WallObject[][][] walls) {
        this.walls = walls;
    }
}
