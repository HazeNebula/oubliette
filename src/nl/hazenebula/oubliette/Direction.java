package nl.hazenebula.oubliette;

public enum Direction {
    NORTH(0, 0),
    EAST(90, 1),
    SOUTH(180, 2),
    WEST(270, 3);

    private final double angle;
    private final int id;

    Direction(double angle, int id) {
        this.angle = angle;
        this.id = id;
    }

    public double angle() {
        return angle;
    }

    public int id() {
        return id;
    }

    public Direction prev() {
        switch (this) {
            case NORTH:
                return WEST;
            case EAST:
                return NORTH;
            case SOUTH:
                return EAST;
            case WEST:
                return SOUTH;
            default:
                return null;
        }
    }

    public Direction next() {
        switch (this) {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            default:
                return null;
        }
    }
}
