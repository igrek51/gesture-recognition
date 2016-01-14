package igrek.touchinterface.logic.engine;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.GestureManager;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.gestures.recognition.RecognizedGesture;
import igrek.touchinterface.logic.gestures.samples.ComplexGesture;
import igrek.touchinterface.logic.gestures.single.SingleGesture;
import igrek.touchinterface.system.keyinput.InputHandlerCancellable;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;
import igrek.touchinterface.settings.Config;

//TODO: statystyki: procent dobrze rozpoznanych, ilość odrzuceń na podstawie punktu startu, długości, współczynnika korelacji złożonego gestu, korelacja dla odrzucanych i dla nieodrzucanych, modyfikacja współczynników na podstawie statystyki - inteligencja

public class Engine extends BaseEngine {

    public Engine(AppCompatActivity activity) {
        super(activity);
    }

    public void init2() {
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
        graphics.repaintRequest();
        //obsługa przycisków
        buttonsManager.executeClicked();
        //zdejmowanie gestu po braku aktywności
        if (gestureManager.currentTrack != null) {
            if (System.currentTimeMillis() > app.gesture_edit_time + Config.Gestures.max_input_wait_time) {
                gestureManager.addCurrentGestureToHistory();
                if (app.mode == Types.AppMode.WRITING) {
                    gestureManager.inputAndTryToRecognize(false);
                }
            }
        }
    }

    public void setAppMode(Types.AppMode mode) throws Exception {
        app.mode = mode;
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
        gestureManager.addCurrentGestureToHistory();
        gestureManager.resetInputs();
        inputmanager.inputScreenShow("Znak odpowiadający gestowi:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String input) {
                if (input.length() == 0) {
                    Output.error("Nie podano znaku.");
                    return;
                }
                saveCurrentSample2(input);
            }
        });
    }

    public void saveCurrentSample2(final String gesture_character) {
        inputmanager.inputScreenShow("Liczba pojedynczych gestów:", Integer.class, new InputHandlerCancellable() {
            @Override
            public void onAccept(int input) {
                try {
                    if (input <= 0) {
                        Output.errorThrow("Podano za małą iczbę gestów");
                    }
                    if (input > gestureManager.getLastInputsCount()) {
                        Output.errorThrow("Podano za dużą liczbę gestów");
                    }
                    ComplexGesture complexGesture = new ComplexGesture();
                    for (int i = input - 1; i >= 0; i--) {
                        complexGesture.add(gestureManager.getLastInputGesture(i).getSingleGesture());
                    }
                    gestureManager.addSample(complexGesture, gesture_character);
                } catch (SoftErrorException e) {
                    Output.error(e);
                }
            }
        });
    }

    public void addUnrecognizedGesture() {
        gestureManager.addCurrentGestureToHistory();
        gestureManager.resetInputs();
        inputmanager.inputScreenShow("Znak odpowiadający nierozpoznanemu gestowi:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String input) {
                if (input.length() == 0) {
                    Output.error("Nie podano znaku.");
                    return;
                }
                addUnrecognizedGesture2(input);
            }
        });
    }

    public void addUnrecognizedGesture2(final String gesture_character) {
        inputmanager.inputScreenShow("Liczba pojedynczych gestów:", Integer.class, new InputHandlerCancellable() {
            @Override
            public void onAccept(int input) {
                try {
                    if (input <= 0) {
                        Output.errorThrow("Podano za małą iczbę gestów");
                    }
                    if (input > gestureManager.getLastInputsCount()) {
                        Output.errorThrow("Podano za dużą liczbę gestów");
                    }
                    ComplexGesture complexGesture = new ComplexGesture();
                    for (int i = input - 1; i >= 0; i--) {
                        complexGesture.add(gestureManager.getLastInputGesture(i).getSingleGesture());
                    }
                    gestureManager.addSample(complexGesture, gesture_character);
                    //dopisanie nowego znaku
                    List<SingleGesture> singleGestures = new ArrayList<>();
                    for (int i = input - 1; i >= 0; i--) {
                        singleGestures.add(gestureManager.getLastInputGesture(i).getSingleGesture());
                    }
                    gestureManager.recognized.add(new RecognizedGesture(singleGestures, complexGesture));
                } catch (SoftErrorException e) {
                    Output.error(e);
                }
            }
        });
    }

    public void addCorrectedGesture() {
        gestureManager.addCurrentGestureToHistory();
        gestureManager.resetInputs();
        inputmanager.inputScreenShow("Znak odpowiadający poprawianemu gestowi:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String input) {
                if (input.length() == 0) {
                    Output.error("Nie podano znaku.");
                    return;
                }
                addCorrectedGesture2(input);
            }
        });
    }

    public void addCorrectedGesture2(final String gesture_character) {
        inputmanager.inputScreenShow("Liczba pojedynczych gestów:", Integer.class, new InputHandlerCancellable() {
            @Override
            public void onAccept(int input) {
                try {
                    if (input <= 0) {
                        Output.errorThrow("Podano za małą iczbę gestów");
                    }
                    if (input > gestureManager.getLastInputsCount()) {
                        Output.errorThrow("Podano za dużą liczbę gestów");
                    }
                    //usunięcie pozostałych gestów w przypadku dłuższego niż 1
                    Output.log("addCorrectedGesture2: "+input);
                    for (int i = 0; i < input; i++) {
                        Output.log("addCorrectedGesture2: "+i);
                        gestureManager.backspaceGesture();
                    }
                    ComplexGesture complexGesture = new ComplexGesture();
                    for (int i = input - 1; i >= 0; i--) {
                        complexGesture.add(gestureManager.getLastInputGesture(i).getSingleGesture());
                    }
                    gestureManager.addSample(complexGesture, gesture_character);
                    //dopisanie nowego znaku
                    List<SingleGesture> singleGestures = new ArrayList<>();
                    for (int i = input - 1; i >= 0; i--) {
                        singleGestures.add(gestureManager.getLastInputGesture(i).getSingleGesture());
                    }
                    gestureManager.recognized.add(new RecognizedGesture(singleGestures, complexGesture));
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
                    gestureManager.removeSample(inputText);
                } catch (SoftErrorException e) {
                    Output.error(e);
                }
            }
        });
    }

}
