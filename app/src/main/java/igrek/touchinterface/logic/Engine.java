package igrek.touchinterface.logic;

import android.app.Activity;
import android.content.Intent;

import igrek.touchinterface.gestures.AnglePlot;
import igrek.touchinterface.gestures.Track;
import igrek.touchinterface.graphics.*;
import igrek.touchinterface.graphics.Buttons.*;
import igrek.touchinterface.managers.*;
import igrek.touchinterface.settings.*;
import igrek.touchinterface.system.Output;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

//TODO: zapisywanie wykresów kątów gestów
//TODO: rozpoznawanie pojedynczych gestów: korelacja sąsiadów
//TODO: korelacja wzajemna (cross-correlation) - znalezienie przesunięcia o maksymalnej korelacji
//TODO: rozpoznawanie złożonych gestów: analiza 2 w tył
//TODO: a może jednak łancuchy Freemana
public class Engine implements TimerManager.MasterOfTime, CanvasView.TouchPanel {
    boolean init = false;
    boolean running = true;
    public Activity activity;
    App app;
    TimerManager timer;
    public Graphics graphics;
    public Buttons buttons;
    TouchPanel touchpanel = null;
    Control control = null;
    public Preferences preferences;
    public InputManager inputmanager = null;

    public Track gest1 = null;
    public List<Track> gestures;
    public AnglePlot plot1 = null;

    private BaseLoaderCallback mLoaderCallback;

    public Engine(Activity activity) {
        this.activity = activity;
        Output.reset();
        app = App.reset(this);
        preferences = new Preferences(activity);
        graphics = new Graphics(activity, this);
        buttons = new Buttons();
        timer = new TimerManager(this, Config.timer_interval0);
        activity.setContentView(graphics);
        //files = new Files(activity);
        Output.log("Utworzenie aplikacji.");

        mLoaderCallback = new BaseLoaderCallback(this.activity) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Output.log("OpenCV loaded successfully");
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };
    }

    public void init() {
        //po inicjalizacji grafiki - po ustaleniu rozmiarów
        Output.log("Inicjalizacja (po starcie grafiki).");
        touchpanel = new TouchPanel(this, graphics.w, graphics.h);
        control = new Control(this);
        inputmanager = new InputManager(activity, graphics);
        //przyciski
        //TODO: rozmiary buttonów i ich widocznosc w module grafiki
        buttons.add("Minimalizuj", "minimize", 0, 0, graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                minimize();
            }
        });
        buttons.add("Zakończ", "exit", graphics.w / 2, 0, graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                control.executeEvent(Types.ControlEvent.BACK);
            }
        });
        buttons.add("Czyść konsolę", "clear", 0, buttons.lastYBottom(), graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                Output.reset();
            }
        });
        gestures = new ArrayList<>();
        try {
            setAppMode(Types.AppMode.MENU);
        } catch (Exception e) {
            Output.error(e);
        }
        //odczekanie przed czyszczeniem konsoli
        Output.echoWait(4000);
        graphics.init = true;
        init = true;
    }

    @Override
    public void timer_run() {
        if (!running) return;
        if (!init) return;
        update();
        if (inputmanager != null && !inputmanager.visible) {
            graphics.invalidate();
        }
    }

    public void pause() {

    }

    /*
    static{
        System.loadLibrary("opencv_java3");
    }
    */

    public void resume() {
        if (!OpenCVLoader.initDebug()) {
            Output.log("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, activity, mLoaderCallback);
        } else {
            Output.log("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void quit() {
        if (!running) { //próba ponownego zamknięcia
            Output.log("Zamykanie aplikacji (2) - anulowanie");
            return;
        }
        running = false;
        timer.stop();
        Output.log("Zamykanie aplikacji");
        activity.finish();
    }

    public void update() {
        try {
            //obsługa przycisków
            buttons.executeClicked();
            //zdejmowanie gestu po braku aktywności
            if (gest1 != null) {
                if (System.currentTimeMillis() > app.gesture_edit_time + Config.Gestures.max_wait_time) {
                    addNewTrack();
                    gest1 = null;
                }
            }
        } catch (Exception e) {
            Output.error(e);
        }
    }

    @Override
    public void touchDown(float touch_x, float touch_y) {
        if (buttons.checkPressed(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touch_down(touch_x, touch_y);
        }
    }

    @Override
    public void touchMove(float touch_x, float touch_y) {
        if (buttons.checkMoved(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touch_move(touch_x, touch_y);
        }
    }

    @Override
    public void touchUp(float touch_x, float touch_y) {
        if (buttons.checkReleased(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touch_up(touch_x, touch_y);
        }
    }

    @Override
    public void resizeEvent() {
        app.w = graphics.w;
        app.h = graphics.h;
        if (!init) init();
        Output.log("Rozmiar ekranu zmieniony na: " + graphics.w + "px x " + graphics.h + "px");
    }

    public void keycode_back() {
        if (inputmanager.visible) {
            inputmanager.inputScreenHide();
            return;
        }
        try {
            control.executeEvent(Types.ControlEvent.BACK);
        } catch (Exception e) {
            Output.error(e);
        }
    }

    public void keycode_menu() {
        try {
            control.executeEvent(Types.ControlEvent.MENU);
        } catch (Exception e) {
            Output.error(e);
        }
    }

    public boolean options_select(int id) {
        return false;
    }

    public void minimize() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(startMain);
    }

    public void setAppMode(Types.AppMode mode) throws Exception {
        app.mode = mode;
        buttons.hideAll();
        if (app.mode == Types.AppMode.MENU) {
            buttons.setVisible("minimize");
            buttons.setVisible("exit");
            buttons.setVisible("clear");
        } else if (app.mode == Types.AppMode.COMPASS) {
            buttons.setVisible("back");
        }
    }

    public void addNewTrack() {
        gest1 = Track.filteredTrack(Track.getAllPixels(gest1));
        gestures.add(gest1);
        while(gestures.size()>4){
            gestures.remove(0);
        }
        plot1 = new AnglePlot(gest1);
        plot1.normalizeX();
    }
}
