package igrek.touchinterface.logic.touchscreen;

import igrek.touchinterface.logic.engine.Engine;
import igrek.touchinterface.logic.gestures.Track;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.system.output.Output;

public class TouchPanel {
    float w, h; //rozmiary ekranu dotykowego
    float start_x, start_y; //punkt początkowy smyrania
    int dpi;
    public static final float INCH = 2.54f; //1 cal [cm]
    App app;
    Engine engine;

    public TouchPanel() {
        app = App.geti();
        engine = app.engine;
        w = app.engine.graphics.w;
        h = app.engine.graphics.w;
        dpi = engine.activity.getResources().getDisplayMetrics().densityDpi;
        Output.log("DPI urządzenia: " + dpi);
    }

    public float pixelsToCm(float pixels) {
        return pixels / dpi * INCH;
    }

    public float cmToPixels(float cm) {
        return cm / INCH * dpi;
    }

    public boolean touchDown(float touch_x, float touch_y) {
        start_x = touch_x;
        start_y = touch_y;
        if(engine.currentTrack != null) {
            engine.addNewTrack();
        }
        app.gesture_edit_time = System.currentTimeMillis();
        engine.currentTrack = new Track();
        engine.currentTrack.addPoint(touch_x, touch_y);
        return true;
    }

    public boolean touchMove(float touch_x, float touch_y) {
        if(engine.currentTrack != null) {
            engine.currentTrack.addPoint(touch_x, touch_y);
        }
        app.gesture_edit_time = System.currentTimeMillis();
        return false;
    }

    public boolean touchUp(float touch_x, float touch_y) {
        return false;
    }
}
