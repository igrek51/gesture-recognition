package igrek.touchinterface.logic;

import android.app.Activity;
import android.content.Intent;

import igrek.touchinterface.gestures.FreemanHistogram;
import igrek.touchinterface.gestures.SingleGesture;
import igrek.touchinterface.gestures.Track;
import igrek.touchinterface.graphics.*;
import igrek.touchinterface.graphics.Buttons.*;
import igrek.touchinterface.managers.*;
import igrek.touchinterface.managers.files.Files;
import igrek.touchinterface.managers.files.Path;
import igrek.touchinterface.settings.*;
import igrek.touchinterface.system.Output;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

//TODO: rozpoznawanie złożonych gestów: analiza 2 w tył
//TODO: zapis do pliku
//TODO: wybór lokalizacji bazy danych
// /storage/extSdCard/Android/data/igrek.touchinterface/samples
//TODO: wykorzystanie z OpenCV: filtracja szumów, generowanie pełnego konturu, obliczanie łańcuchów freemana, korelacja histogramów
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
    Files files;
    public Preferences preferences;
    public InputManager inputmanager = null;
    private BaseLoaderCallback mLoaderCallback;

    public Track track1 = null;
    public List<Track> tracks;
    public FreemanHistogram fHistogram = null;
    public SingleGesture singleGesture = null;

    public Engine(Activity activity) {
        this.activity = activity;
        Output.reset();
        app = App.reset(this);
        preferences = new Preferences(activity);
        graphics = new Graphics(activity, this);
        buttons = new Buttons();
        timer = new TimerManager(this, Config.timer_interval0);
        activity.setContentView(graphics);
        files = new Files(activity);
        Output.log("Utworzenie aplikacji.");

        mLoaderCallback = new BaseLoaderCallback(this.activity) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    Output.info("OpenCV loaded successfully");
                } else {
                    super.onManagerConnected(status);
                }
            }
        };
        if (!OpenCVLoader.initDebug()) {
            Output.info("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, activity, mLoaderCallback);
        } else {
            Output.info("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    public void init() {
        //po inicjalizacji grafiki - po ustaleniu rozmiarów
        Output.log("Inicjalizacja (po starcie grafiki).");
        touchpanel = new TouchPanel(this, graphics.w, graphics.h);
        control = new Control(this);
        inputmanager = new InputManager(activity, graphics);
        //przyciski
        //TODO: rozmiary buttonów i ich widocznosc w module grafiki
        buttons.add("Czyść konsolę", "clear", 0, 0, graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                Output.reset();
            }
        });
        buttons.add("Zakończ", "exit", graphics.w / 2, 0, graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                control.executeEvent(Types.ControlEvent.BACK);
            }
        });
        buttons.add("Lokalizacja wzorców", "samplesPath", 0, buttons.lastYBottom(), graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                clickedPathPreferences();
            }
        });
        buttons.add("Zapisz wzorzec", "saveSample", 0, buttons.lastYTop(), graphics.w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                saveCurrentSample();
            }
        });
        preferencesLoad();
        tracks = new ArrayList<>();
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
    public void timerRun() {
        if (!running) return;
        if (!init) return;
        update();
        if (inputmanager != null && !inputmanager.visible) {
            graphics.invalidate();
        }
    }

    public void pause() {

    }

    /*static{
        System.loadLibrary("opencv_java3");
    }*/
    public void resume() {

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
            //TODO: repaint tylko w potrzebie a nie w każdej klatce
            graphics.refresh();
            //obsługa przycisków
            buttons.executeClicked();
            //zdejmowanie gestu po braku aktywności
            if (track1 != null) {
                if (System.currentTimeMillis() > app.gesture_edit_time + Config.Gestures.max_wait_time) {
                    addNewTrack();
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
            touchpanel.touchDown(touch_x, touch_y);
        }
    }

    @Override
    public void touchMove(float touch_x, float touch_y) {
        if (buttons.checkMoved(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touchMove(touch_x, touch_y);
        }
    }

    @Override
    public void touchUp(float touch_x, float touch_y) {
        if (buttons.checkReleased(touch_x, touch_y)) return;
        if (touchpanel != null) {
            touchpanel.touchUp(touch_x, touch_y);
        }
    }

    @Override
    public void resizeEvent() {
        app.w = graphics.w;
        app.h = graphics.h;
        if (!init) init();
        Output.log("Rozmiar ekranu zmieniony na: " + graphics.w + "px x " + graphics.h + "px");
    }

    public void keycodeBack() {
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

    public void keycodeMenu() {
        try {
            control.executeEvent(Types.ControlEvent.MENU);
        } catch (Exception e) {
            Output.error(e);
        }
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

    public void setAppMode(Types.AppMode mode) throws Exception {
        app.mode = mode;
        buttons.hideAll();
        if (app.mode == Types.AppMode.MENU) {
            buttons.setVisible("exit");
            buttons.setVisible("clear");
            buttons.setVisible("samplesPath");
            buttons.setVisible("saveSample");
        }
    }

    public void addNewTrack() {
        Track filteredTrack = Track.filteredTrack(Track.getAllPixels(track1));
        tracks.add(track1);
        while (tracks.size() > 4) {
            tracks.remove(0);
        }
        fHistogram = new FreemanHistogram(filteredTrack);
        singleGesture = new SingleGesture(track1.getStart(), fHistogram);
        track1 = null;
    }

    public void clickedPathPreferences() {
        inputmanager.inputScreenShow("Ścieżka do wzorców:", app.samplesPath, new InputManager.InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                app.samplesPath = inputText;
                preferencesSave();
            }
        });
    }

    public void preferencesSave() {
        //zapisanie do shared preferences
        preferences.setString("samplesPath", app.samplesPath);
        Output.info("Zapisano preferencje.");
    }

    public void preferencesLoad() {
        //wczytanie z shared preferences
        if (preferences.exists("samplesPath")) {
            app.samplesPath = preferences.getString("samplesPath");
            Output.info("Wczytano ścieżkę wzorców: " + app.samplesPath);
        } else {
            Output.info("Wczytano domyślną ścieżkę wzorców: " + app.samplesPath);
        }
    }

    public void saveCurrentSample() {
        if (fHistogram == null || fHistogram.histogram == null || singleGesture == null) {
            Output.error("Brak histogramu do zapisania");
            return;
        }
        inputmanager.inputScreenShow("Znak odpowiadający gestowi:", new InputManager.InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                saveCurrentSample2(inputText);
            }
        });
    }

    public void saveCurrentSample2(String character) {
        Path samplesPath2 = files.pathSD().append(app.samplesPath);
        //wyznaczanie nazwy pliku
        int sampleNumber = 1;
        while (files.exists(samplesPath2.append(character + "-" + sampleNumber).toString())) {
            sampleNumber++;
        }
        //serializacja wzorca i zapisanie histogramu
    }
}
