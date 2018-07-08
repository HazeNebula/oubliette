package nl.hazenebula.oubliette;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Selection {
    private BooleanProperty isSelecting;
    private IntegerProperty x;
    private IntegerProperty y;
    private IntegerProperty width;
    private IntegerProperty height;

    public Selection() {
        isSelecting = new SimpleBooleanProperty(false);
        x = new SimpleIntegerProperty(0);
        y = new SimpleIntegerProperty(0);
        width = new SimpleIntegerProperty(0);
        height = new SimpleIntegerProperty(0);
    }

    public void setSelecting(boolean selecting) {
        isSelecting.setValue(selecting);
    }

    public void setX(int x) {
        this.x.setValue(x);
    }

    public void setY(int y) {
        this.y.setValue(y);
    }

    public void setWidth(int width) {
        this.width.setValue(width);
    }

    public void setHeight(int height) {
        this.height.setValue(height);
    }

    public boolean isSelecting() {
        return isSelecting.get();
    }

    public int getX() {
        return x.get();
    }

    public int getY() {
        return y.get();
    }

    public int getWidth() {
        return width.get();
    }

    public int getHeight() {
        return height.get();
    }

    public BooleanProperty isSelectingProperty() {
        return isSelecting;
    }

    public IntegerProperty xProperty() {
        return x;
    }

    public IntegerProperty yProperty() {
        return y;
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public IntegerProperty heightProperty() {
        return height;
    }
}
