package nl.hazenebula.terraingeneration;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point other = (Point)o;
            return this.x == other.x && this.y == other.y;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 31 * x + 31 * y;
    }

    @Override
    public String toString() {
        return x + "\t" + y;
    }
}
