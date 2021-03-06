package igrek.touchinterface.logic.engine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import igrek.touchinterface.logic.buttons.ButtonsManager;
import igrek.touchinterface.logic.gestures.GestureManager;
import igrek.touchinterface.graphics.*;
import igrek.touchinterface.logic.touchscreen.ITouchScreenController;
import igrek.touchinterface.logic.touchscreen.TouchPanel;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.settings.preferences.Preferences;
import igrek.touchinterface.system.files.Files;
import igrek.touchinterface.system.keyinput.InputManager;
import igrek.touchinterface.system.output.SoftErrorException;
import igrek.touchinterface.system.timer.ITimerRunner;
import igrek.touchinterface.system.timer.TimerManager;
import igrek.touchinterface.settings.*;
import igrek.touchinterface.system.output.Output;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public abstract class BaseEngine implements ITimerRunner, ITouchScreenController {
    boolean init = false;
    boolean running = true;
    public Activity activity;
    public Engine engine;
    App app;
    TimerManager timer;
    public Graphics graphics;
    public ButtonsManager buttonsManager;
    TouchPanel touchpanel = null;
    public Files files;
    public Preferences preferences;
    public InputManager inputmanager = null;
    public GestureManager gestureManager = null;

    public BaseEngine(AppCompatActivity activity) {
        this.activity = activity;
        engine = (Engine) this;
        new Config();
        //schowanie paska tytułu
        if (Config.Screen.hide_taskbar) {
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().hide();
            }
        }
        //fullscreen
        if (Config.Screen.fullscreen) {
            activity.getWindow().setFlags(Config.Screen.fullscreen_flag, Config.Screen.fullscreen_flag);
        }
        if (Config.Screen.keep_screen_on) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        Output.reset();
        app = App.reset(engine);
        preferences = new Preferences(activity);
        graphics = new Graphics(activity, this);
        timer = new TimerManager(activity, this, Config.timer_interval0);
        activity.setContentView(graphics);
        files = new Files(activity);
        Output.log("Utworzenie aplikacji.");
        initOpenCV();
    }

    public void init() {
        //po inicjalizacji grafiki - po ustaleniu rozmiarów
        Output.log("Inicjalizacja (po starcie grafiki).");
        touchpanel = new TouchPanel();
        inputmanager = new InputManager(activity, graphics);
        buttonsManager = new ButtonsManager();
        preferences.preferencesLoad();
        engine.init2();
        graphics.init = true;
        init = true;
    }

    public void initOpenCV() {
        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.activity) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    Output.info("OpenCV załadowane");
                } else {
                    super.onManagerConnected(status);
                }
            }
        };
        if (!OpenCVLoader.initDebug()) {
            Output.info("Brak wewnętrznej biblioteki OpenCV. Inicjalizacja poprzez OpenCV Manager");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, activity, mLoaderCallback);
        } else {
            Output.info("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void timerRun() {
        if (!running) return;
        if (!init) return;
        update();
        if (inputmanager != null && !inputmanager.isVisible()) {
            Output.echoClear1AfterDelay();
            graphics.invalidate();
        }
    }

    public void pause() {

    }

    public void resume() {

    }

    public void quit() {
        if (!running) { //próba ponownego zamknięcia
            Output.log("Zamykanie aplikacji (2) - anulowanie");
            return;
        }
        try {
            gestureManager.saveSamples();
        }catch(SoftErrorException e){
            Output.error(e);
        }
        Output.info("Zamykanie...");
        running = false;
        timer.stop();
        Output.log("Zamykanie aplikacji");
        activity.finish();
    }

    public void update() {
        try {
            engine.update2();
        } catch (Exception e) {
            Output.error(e);
        }
    }

    @Override
    public void touchDown(float touch_x, float touch_y) {
        if (buttonsManager.checkPressed(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touchDown(touch_x, touch_y);
        }
    }

    @Override
    public void touchMove(float touch_x, float touch_y) {
        if (buttonsManager.checkMoved(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touchMove(touch_x, touch_y);
        }
    }

    @Override
    public void touchUp(float touch_x, float touch_y) {
        if (buttonsManager.checkReleased(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touchUp(touch_x, touch_y);
        }
    }

    @Override
    public void resizeEvent() {
        if (!init) init();
        buttonsManager.resetAllButtonsGeometry();
        Output.log("Rozmiar ekranu zmieniony na: " + graphics.w + "px x " + graphics.h + "px");
    }

    public void keycodeBack() {
        if (inputmanager.isVisible()) {
            inputmanager.inputScreenHide();
            return;
        }
        if (app.mode == Types.AppMode.MENU) {
            quit();
        }
    }

    public void keycodeMenu() {

    }

    public boolean optionsSelect(int id) {
        return false;
    }

    public void minimize() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(startMain);
    }
}
