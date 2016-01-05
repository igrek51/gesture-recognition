package igrek.touchinterface.logic.engine;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import igrek.touchinterface.logic.gestures.FreemanHistogram;
import igrek.touchinterface.logic.gestures.GestureManager;
import igrek.touchinterface.logic.gestures.SingleGesture;
import igrek.touchinterface.logic.gestures.Track;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
import igrek.touchinterface.system.keyinput.InputHandlerCancellable;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;
import igrek.touchinterface.settings.Config;

//TODO: inteligencja: usuwanie wzorców, które prowadzą do błędnego rozpoznawania
//TODO: uczenie: dodawanie wszystkich wzorców, usuwanie wzorców, które są rzadko podstawą do rozpoznania
//TODO: mechanizm usuwania złych wzorców
//TODO: tryb debugowania - przyciski, tryb szybkiego pisania - release

public class Engine extends BaseEngine {
    public Engine(AppCompatActivity activity) {
        super(activity);
    }

    public void init2() {
        app.lastTracks = new ArrayList<>();
        app.lastSingleGestures = new ArrayList<>();
        gestureManager = new GestureManager();
        try {
            setAppMode(Types.AppMode.MENU);
        } catch (Exception e) {
            Output.error(e);
        }
        //odczekanie przed czyszczeniem konsoli
        Output.echoWait(4000);
    }

    public void update2() throws Exception {
        //TODO: repaint tylko w potrzebie a nie w każdej klatce
        graphics.repaintRequest();
        //obsługa przycisków
        buttonsManager.executeClicked();
        //zdejmowanie gestu po braku aktywności
        if (app.currentTrack != null) {
            if (System.currentTimeMillis() > app.gesture_edit_time + Config.Gestures.max_wait_time) {
                addCurrentGestureToHistory();
            }
        }
    }

    public void setAppMode(Types.AppMode mode) throws Exception {
        app.mode = mode;
    }

    public void addCurrentGestureToHistory() {
        //jeśli nie ma rysowania
        if (app.currentTrack != null) {
            Track filteredTrack = Track.filteredTrack(Track.getAllPixels(app.currentTrack));
            FreemanHistogram currentHistogram = new FreemanHistogram(filteredTrack);
            SingleGesture currentSingleGesture = new SingleGesture(app.currentTrack.getStart(), currentHistogram);
            app.lastTracks.add(app.currentTrack);
            app.lastSingleGestures.add(currentSingleGesture);
            while (app.lastTracks.size() > 4) {
                app.lastTracks.remove(0);
                app.lastSingleGestures.remove(0);
            }
            app.currentTrack = null;
        }
    }

    public void clickedPathPreferences() {
        inputmanager.inputScreenShow("Ścieżka do wzorców:", app.samplesPath, new InputHandlerCancellable() {
            @Override
            public void onAccept(String input) {
                app.samplesPath = input;
                preferences.preferencesSave();
            }
        });
    }

    public void saveCurrentSample() {
        app.engine.addCurrentGestureToHistory();
        inputmanager.inputScreenShow("Liczba pojedynczych gestów:", Integer.class, new InputHandlerCancellable() {
            @Override
            public void onAccept(int input) {
                if (input <= 0) {
                    Output.error("Podana za mała iczba gestów");
                } else if (input > app.lastTracks.size()) {
                    Output.error("Podana za duża liczba gestów");
                } else {
                    saveCurrentSample2(input);
                }
            }
        });
    }

    public void saveCurrentSample2(final int gesture_size) {
        inputmanager.inputScreenShow("Znak odpowiadający gestowi:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                try {
                    ComplexGesture complexGesture = new ComplexGesture();
                    for (int i = gesture_size; i > 0; i--) {
                        complexGesture.add(app.lastSingleGestures.get(app.lastSingleGestures.size() - i));
                    }
                    gestureManager.saveSample(complexGesture, inputText);
                } catch (SoftErrorException e) {
                    Output.error(e);
                }
            }
        });
    }

    public void deleteSample() {
        inputmanager.inputScreenShow("Nazwa pliku wzorca:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                try {
                    gestureManager.deleteSample(inputText);
                } catch (SoftErrorException e) {
                    Output.error(e);
                }
            }
        });
    }

    public SingleGesture getLastSingleGesture() {
        addCurrentGestureToHistory();
        if (app.lastSingleGestures.isEmpty()) return null;
        return app.lastSingleGestures.get(app.lastSingleGestures.size() - 1);
    }
}
