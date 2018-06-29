package nl.hazenebula.oubliette;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseDrawHandler implements EventHandler<MouseEvent> {
    private final EventHandler<MouseEvent> onDrawEventHandler;

    private boolean pressing = false;

    public MouseDrawHandler(EventHandler<MouseEvent> onDrawEventHandler) {
        this.onDrawEventHandler = onDrawEventHandler;
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
        }
    }

    public boolean isPressing() {
        return pressing;
    }
}
