package nl.hazenebula.oubliette;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseDrawHandler implements EventHandler<MouseEvent> {
    private final EventHandler<MouseEvent> onDrawEventHandler;
    private final EventHandler<MouseEvent> onSlideEventHandler;

    private boolean pressing = false;

    public MouseDrawHandler(EventHandler<MouseEvent> onDrawEventHandler, EventHandler<MouseEvent> onSlideEventHandler) {
        this.onDrawEventHandler = onDrawEventHandler;
        this.onSlideEventHandler = onSlideEventHandler;
    }

    @Override
    public void handle(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
            pressing = true;
        } else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
            pressing = false;
        }

        if (pressing) {
            onDrawEventHandler.handle(e);
        } else {
            onSlideEventHandler.handle(e);
        }
    }

    public boolean isPressing() {
        return pressing;
    }
}
