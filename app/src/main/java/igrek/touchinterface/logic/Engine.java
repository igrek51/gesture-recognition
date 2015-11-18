package igrek.touchinterface.logic;

import android.app.Activity;
import android.content.Intent;

import igrek.touchinterface.graphics.*;
import igrek.touchinterface.graphics.Buttons.*;
import igrek.touchinterface.managers.*;
import igrek.touchinterface.settings.*;
import igrek.touchinterface.system.Output;

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
    public Sensors sensors;
    public Preferences preferences;
    public InputManager inputmanager = null;

    public Engine(Activity activity) {
        this.activity = activity;
        Output.reset();
        app = App.reset(this);
        preferences = new Preferences(activity);
        graphics = new Graphics(activity, this);
        buttons = new Buttons();
        timer = new TimerManager(this, Config.timer_interval0);
        try {
            sensors = new Sensors(activity);
        } catch (Exception e) {
            sensors = null;
            Output.error(e);
        }
        //files = new Files(activity);
        Output.log("Utworzenie aplikacji.");
    }

    public void init() {
        //po inicjalizacji grafiki - po ustaleniu rozmiarów
        Output.log("Inicjalizacja (po starcie grafiki).");
        touchpanel = new TouchPanel(this, graphics.w, graphics.h);
        control = new Control(this);
        inputmanager = new InputManager(activity, graphics);
        //przyciski
        Buttons.Button b;
        b = buttons.add("Czyść konsolę", "clear", graphics.w / 2, 0, graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                Output.echos = "";
            }
        });
        b = buttons.add("Minimalizuj", "minimize", 0, b.y + b.h, graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                minimize();
            }
        });
        buttons.add("Zakończ", "exit", graphics.w / 2, b.y + b.h, graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                control.executeEvent(Types.ControlEvent.BACK);
            }
        });
        try {
            setAppMode(Types.AppMode.MENU);
        } catch (Exception e) {
            Output.error(e);
        }
        //odczekanie przed czyszczeniem konsoli
        Output.echoWait(4000);
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
        if (sensors != null) {
            sensors.unregister();
        }
    }

    public void resume() {
        if (sensors != null) {
            sensors.register();
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

        } catch (Exception e) {
            Output.error(e);
        }
    }

    @Override
    public void touch_down(float touch_x, float touch_y) {
        if (buttons.checkPressed(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touch_down(touch_x, touch_y);
        }
    }

    @Override
    public void touch_move(float touch_x, float touch_y) {
        if (touchpanel != null) {
            touchpanel.touch_move(touch_x, touch_y);
        }
    }

    @Override
    public void touch_up(float touch_x, float touch_y) {
        if (buttons.checkReleased(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touch_up(touch_x, touch_y);
        }
    }

    @Override
    public void resize_event() {
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
        buttons.hideAll();;
        if (app.mode == Types.AppMode.MENU) {
            buttons.setVisible("clear");
            buttons.setVisible("minimize");
            buttons.setVisible("exit");
        } else if (app.mode == Types.AppMode.COMPASS) {
            buttons.setVisible("back");
        }
    }
}
