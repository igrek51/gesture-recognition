package igrek.touchinterface.logic.engine;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import igrek.touchinterface.logic.gestures.FreemanHistogram;
import igrek.touchinterface.logic.gestures.GestureManager;
import igrek.touchinterface.logic.gestures.SingleGesture;
import igrek.touchinterface.logic.gestures.Track;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.system.keyinput.InputHandlerCancellable;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;
import igrek.touchinterface.settings.Config;

public class Engine extends BaseEngine {
    public Engine(AppCompatActivity activity){
        super(activity);
    }

    public void init2(){
        lastTracks = new ArrayList<>();
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
        graphics.refresh();
        //obsługa przycisków
        buttonsManager.executeClicked();
        //zdejmowanie gestu po braku aktywności
        if (currentTrack != null) {
            if (System.currentTimeMillis() > app.gesture_edit_time + Config.Gestures.max_wait_time) {
                addNewTrack();
            }
        }
    }

    public void setAppMode(Types.AppMode mode) throws Exception {
        app.mode = mode;
    }

    public void addNewTrack() {
        Track filteredTrack = Track.filteredTrack(Track.getAllPixels(currentTrack));
        lastTracks.add(currentTrack);
        while (lastTracks.size() > 4) {
            lastTracks.remove(0);
        }
        currentHistogram = new FreemanHistogram(filteredTrack);
        currentGesture = new SingleGesture(currentTrack.getStart(), currentHistogram);
        currentTrack = null;
    }

    public void clickedPathPreferences() {
        inputmanager.inputScreenShow("Ścieżka do wzorców:", app.samplesPath, new InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                app.samplesPath = inputText;
                preferences.preferencesSave();
            }
        });
    }

    public void saveCurrentSample() {
        if (currentHistogram == null || currentHistogram.histogram == null || currentGesture == null) {
            Output.error("Brak histogramu do zapisania");
            return;
        }
        inputmanager.inputScreenShow("Znak odpowiadający gestowi:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String inputText) {
                try {
                    gestureManager.saveSample(currentGesture, inputText);
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
