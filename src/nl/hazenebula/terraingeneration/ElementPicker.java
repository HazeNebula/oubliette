package nl.hazenebula.terraingeneration;

import java.util.List;

public abstract class ElementPicker<T> {
    protected List<T> list;
    protected int index;

    public ElementPicker() {
        this.index = 0;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void add(T elem) {
        list.add(elem);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public abstract T choose();

    public void removeLastChoice() {
        list.remove(index);
    }
}
