package nl.hazenebula.terraingeneration;

public class RandomElementPicker<T> extends ElementPicker<T> {
    @Override
    public T choose() {
        index = Util.randInt(0, list.size());

        return list.get(index);
    }
}
