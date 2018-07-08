package nl.hazenebula.oubliette;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseDrawHandler implements EventHandler<MouseEvent> {
    private final EventHandler<MouseEvent> onClickEventHandler;
    private final EventHandler<MouseEvent> onReleaseEventHandler;
    private final EventHandler<MouseEvent> onDrawEventHandler;
    private final EventHandler<MouseEvent> onSlideEventHandler;

    private boolean isPressing;
    private boolean hasMoved;

    public MouseDrawHandler(EventHandler<MouseEvent> onClickEventHandler,
                            EventHandler<MouseEvent> onReleaseEventHandler,
                            EventHandler<MouseEvent> onDrawEventHandler,
                            EventHandler<MouseEvent> onSlideEventHandler) {
        this.onClickEventHandler = onClickEventHandler;
        this.onReleaseEventHandler = onReleaseEventHandler;
        this.onDrawEventHandler = onDrawEventHandler;
        this.onSlideEventHandler = onSlideEventHandler;

        isPressing = false;
        hasMoved = false;
    }

    @Override
    public void handle(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
            isPressing = true;
        } else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (hasMoved) {
                onReleaseEventHandler.handle(e);
            } else {
                onClickEventHandler.handle(e);
            }

            isPressing = false;
            hasMoved = false;
        } else if (e.getEventType() == MouseEvent.MOUSE_MOVED ||
                e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            hasMoved = true;
        }

        if (hasMoved && isPressing) {
            onDrawEventHandler.handle(e);
        } else if (hasMoved) {
            onSlideEventHandler.handle(e);
        }
    }

    public boolean isPressing() {
        return isPressing;
    }
}
