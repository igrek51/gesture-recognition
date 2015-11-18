package igrek.touchinterface.logic;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import igrek.touchinterface.graphics.*;
import igrek.touchinterface.managers.*;
import igrek.touchinterface.settings.*;
import igrek.touchinterface.system.Output;

public class Engine implements TimerManager.MasterOfTime, CanvasView.TouchPanel {
    boolean init = false;
    boolean running = true;
    public Activity activity;
    App app;
    TimerManager timer;
    Random random;
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
        random = new Random();
        try {
            sensors = new Sensors(activity);
        } catch (Exception e) {
            sensors = null;
            Output.error(e);
        }
        //files = new Files(activity);
        Output.log("Utworzenie aplikacji.");
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

    public void init() {
        //po inicjalizacji grafiki - po ustaleniu rozmiarów
        Output.log("Inicjalizacja (po starcie grafiki).");
        touchpanel = new TouchPanel(this, graphics.w, graphics.h);
        control = new Control(this);
        inputmanager = new InputManager(activity, graphics);
        //przyciski
        Buttons.Button b1;
        b1 = buttons.add("Znajdź mnie, Iro!", "login", 0, 0, graphics.w, 80);
        buttons.add("Ustawienia", "preferences", 0, b1.y + b1.h, graphics.w / 2, 0);
        b1 = buttons.add("Wyloguj", "logout", graphics.w / 2, b1.y + b1.h, graphics.w / 2, 0);
        buttons.add("Kompas", "compass", 0, b1.y + b1.h, graphics.w / 2, 0);
        b1 = buttons.add("Czyść konsolę", "clear", graphics.w / 2, b1.y + b1.h, graphics.w / 2, 0);
        buttons.add("Minimalizuj", "minimize", 0, b1.y + b1.h, graphics.w / 2, 0);
        b1 = buttons.add("Zakończ", "exit", graphics.w / 2, b1.y + b1.h, graphics.w / 2, 0);
        buttons.add("Powrót", "back", 0, graphics.h, graphics.w, 0, Types.Align.BOTTOM);
        preferencesLoad();
        try {
            setAppMode(Types.AppMode.MENU);
        } catch (Exception e) {
            Output.error(e);
        }
        //odczekanie przed czyszczeniem konsoli
        Output.echoWait(4000);
        init = true;
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
            Output.log("Anulowanie próby ponownego zamknięcia aplikacji.");
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
            if (buttons.isClicked()) {
                buttonsExecute(buttons.clickedId());
            }

        } catch (Exception e) {
            Output.error(e);
        }
    }

    public void buttonsExecute(String bid) throws Exception {
        if (bid.equals("exit")) {
            control.executeEvent(Types.ControlEvent.BACK);
        } else if (bid.equals("clear")) {
            Output.echos = "";
        } else if (bid.equals("preferences")) {
            inputmanager.inputScreenShow("Login:", new InputManager.InputHandler() {
                @Override
                public void onInput(String inputText) {
                    app.login = inputText;
                    inputmanager.inputScreenShow("Hasło:", new InputManager.InputHandler() {
                        @Override
                        public void onInput(String inputText) {
                            app.pass = inputText;
                            preferencesSave();
                        }
                    });
                }
            });
        } else if (bid.equals("compass")) {
            setAppMode(Types.AppMode.COMPASS);
        } else if (bid.equals("back")) {
            setAppMode(Types.AppMode.MENU);
        } else if (bid.equals("minimize")) {
            minimize();
        } else {
            Output.errorthrow("Nie obsłużono zdarzenia dla przycisku: " + bid);
        }
    }

    @Override
    public void touch_down(float touch_x, float touch_y) {
        if (buttons.checkClickedNoAction(touch_x, touch_y)) return;
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
        if (buttons.checkClicked(touch_x, touch_y)) return;
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

    public void preferencesSave() {
        //zapisanie do shared preferences
        preferences.setBoolean("login_set", true);
        preferences.setString("login_login", app.login);
        preferences.setString("login_pass", app.pass);
        Output.info("Zapisano preferencje logowania.");
    }

    public void preferencesLoad() {
        //wczytanie z shared preferences
        if (preferences.exists("login_set")) {
            app.login = preferences.getString("login_login");
            app.pass = preferences.getString("login_pass");
            Output.info("Wczytano preferencje logowania.");
        } else {
            app.login = "";
            app.pass = "";
        }
    }

    public void setAppMode(Types.AppMode mode) throws Exception {
        app.mode = mode;
        buttons.disableAllButtons();
        if (app.mode == Types.AppMode.MENU) {
            buttons.setActive("login", true);
            buttons.setActive("preferences", true);
            buttons.setActive("logout", true);
            buttons.setActive("compass", true);
            buttons.setActive("clear", true);
            buttons.setActive("minimize", true);
            buttons.setActive("exit", true);
            if (sensors != null) {
                sensors.unregister();
            }
        } else if (app.mode == Types.AppMode.COMPASS) {
            buttons.setActive("back", true);
            if (sensors != null) {
                sensors.register();
            }
        }
    }
}
