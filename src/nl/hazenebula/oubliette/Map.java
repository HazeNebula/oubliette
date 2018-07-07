package nl.hazenebula.oubliette;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Map implements Serializable {
    private Field[][] fields;
    private List<FieldObject> objects;
    private Wall[][][] walls;

    public Map(int width, int height, Field fill) {
        fields = new Field[width][height];
        for (int x = 0; x < fields.length; ++x) {
            for (int y = 0; y < fields[x].length; ++y) {
                fields[x][y] = fill;
            }
        }

        objects = new LinkedList<>();

        walls = new Wall[width + 1][height + 1][2];
    }

    public void resize(int width, int height, Field fill) {
        Field[][] newFields = new Field[width][height];
        for (int x = 0; x < newFields.length; ++x) {
            for (int y = 0; y < newFields[x].length; ++y) {
                if (x < fields.length && y < fields[x].length) {
                    newFields[x][y] = fields[x][y];
                } else {
                    newFields[x][y] = fill;
                }
            }
        }
        fields = newFields;

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
        return fields.length;
    }

    public int getHeight() {
        return fields[0].length;
    }

    public Field getField(int x, int y) {
        return fields[x][y];
    }

    public Field[][] getFields() {
        return fields;
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

    public Wall getWall(int x, int y, Direction dir) {
        return walls[x][y][dir.id()];
    }

    public Wall[][][] getWalls() {
        return walls;
    }

    public void setWall(int x, int y, Direction dir, Wall val) {
        walls[x][y][dir.id()] = val;
    }

    public void setWalls(Wall[][][] walls) {
        this.walls = walls;
    }
}
