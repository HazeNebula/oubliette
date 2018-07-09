package nl.hazenebula.terraingeneration;

public class LastElementPicker<T> extends ElementPicker<T> {
    @Override
    public T choose() {
        index = list.size() - 1;

        return list.get(index);
    }
}
