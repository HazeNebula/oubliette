package nl.hazenebula.terraingeneration;

public enum MazeType {
    LAST("Last", new LastElementPicker<>()),
    RANDOM("Random", new RandomElementPicker<>());

    private String name;
    private ElementPicker<Point> ep;

    MazeType(String name, ElementPicker<Point> ep) {
        this.name = name;
        this.ep = ep;
    }

    @Override
    public String toString() {
        return name;
    }

    public ElementPicker<Point> getElementPicker() {
        return ep;
    }
}
