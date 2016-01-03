package igrek.touchinterface.logic.touchscreen;

public interface ITouchScreenController {
    void touchDown(float touch_x, float touch_y);

    void touchMove(float touch_x, float touch_y);

    void touchUp(float touch_x, float touch_y);

    void resizeEvent();
}
