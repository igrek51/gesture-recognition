package igrek.touchinterface.logic.engine;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import igrek.touchinterface.logic.gestures.FreemanHistogram;
import igrek.touchinterface.logic.gestures.GestureManager;
import igrek.touchinterface.logic.gestures.SingleGesture;
import igrek.touchinterface.logic.gestures.Track;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
import igrek.touchinterface.system.keyinput.InputHandler;
import igrek.touchinterface.system.keyinput.InputHandlerCancellable;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;
import igrek.touchinterface.settings.Config;


//TODO: rozpoznawanie złożonych gestów: analiza 2 w tył, analiza nierozpoznanych ostatnich gestów, minimalny współczynnik korelacji
//TODO: wykorzystanie z OpenCV: filtracja szumów, generowanie pełnego konturu, obliczanie łańcuchów freemana, korelacja histogramów
//TODO: inteligencja: usuwanie wzorców, które prowadzą do błędnego rozpoznawania
//TODO: uczenie: dodawanie wszystkich wzorców, usuwanie wzorców, które są rzadko podstawą do rozpoznania
//TODO: mechanizm usuwania złych wzorców
//TODO: klasa engine do zarządzania logiką na wyższym poziomie

public class Engine extends BaseEngine {
    public Engine(AppCompatActivity activity){
        super(activity);
    }

    public void init2(){
        app.lastTracks = new ArrayList<>();
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
                addNewTrack();
            }
        }
    }

    public void setAppMode(Types.AppMode mode) throws Exception {
        app.mode = mode;
    }

    public void addNewTrack() {
        Track filteredTrack = Track.filteredTrack(Track.getAllPixels(app.currentTrack));
        app.lastTracks.add(app.currentTrack);
        while (app.lastTracks.size() > 4) {
            app.lastTracks.remove(0);
        }
        app.currentHistogram = new FreemanHistogram(filteredTrack);
        app.currentSingleGesture = new SingleGesture(app.currentTrack.getStart(), app.currentHistogram);
        app.currentTrack = null;
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
        inputmanager.inputScreenShow("Liczba pojedynczych gestów składających się na złożony gest:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                try {
                    ComplexGesture complexGesture = new ComplexGesture();
                    complexGesture.add(app.currentSingleGesture);
                    gestureManager.saveSample(complexGesture, inputText);
                } catch (SoftErrorException e) {
                    Output.error(e);
                }
            }
        });
    }

    public void saveCurrentSample2() {
        if (app.currentHistogram == null || app.currentHistogram.histogram == null || app.currentSingleGesture == null) {
            Output.error("Brak histogramu do zapisania");
            return;
        }
        inputmanager.inputScreenShow("Znak odpowiadający gestowi:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                try {
                    ComplexGesture complexGesture = new ComplexGesture();
                    complexGesture.add(app.currentSingleGesture);
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
}
