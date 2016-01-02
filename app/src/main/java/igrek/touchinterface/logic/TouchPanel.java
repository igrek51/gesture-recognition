package igrek.touchinterface.logic;

import igrek.touchinterface.gestures.Track;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.system.Output;

public class TouchPanel {
    int w, h; //rozmiary ekranu dotykowego
    float start_x, start_y; //punkt początkowy smyrania
    int dpi;
    public static final float INCH = 2.54f; //1 cal [cm]
    App app;
    Engine engine;

    public TouchPanel(Engine engine, int w, int h) {
        this.engine = engine;
        this.w = w;
        this.h = h;
        app = App.geti();
        dpi = engine.activity.getResources().getDisplayMetrics().densityDpi;
        Output.log("DPI urządzenia: " + dpi);
    }

    public float pixelsToCm(float pixels) {
        return pixels / dpi * INCH;
    }

    public float cmToPixels(float cm) {
        return cm / INCH * dpi;
    }

    public float partialW(float partOfWidth){
        return w * partOfWidth;
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
        float rtx = touch_x / w;
        float rty = touch_y / h;
//        control_realtive(touch_x, touch_y);
//        return true;
        return false;
    }
}
