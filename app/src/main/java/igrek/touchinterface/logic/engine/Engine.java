package igrek.touchinterface.logic.engine;

import android.support.v7.app.AppCompatActivity;

import igrek.touchinterface.logic.gestures.GestureManager;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
import igrek.touchinterface.system.keyinput.InputHandlerCancellable;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;
import igrek.touchinterface.settings.Config;

//TODO: inteligencja: usuwanie wzorców, które prowadzą do błędnego rozpoznawania
//TODO: uczenie: dodawanie wszystkich wzorców, usuwanie wzorców, które są rzadko podstawą do rozpoznania
//TODO: mechanizm usuwania złych wzorców
//TODO: zapisywanie liczby dobrze rozpoznanych wzorców i źle rozpoznanych wzorców
//TODO: tryb debugowania - przyciski, tryb szybkiego pisania - release

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
        //TODO: repaint tylko w potrzebie a nie w każdej klatce
        graphics.repaintRequest();
        //obsługa przycisków
        buttonsManager.executeClicked();
        //zdejmowanie gestu po braku aktywności
        if (gestureManager.currentTrack != null) {
            if (System.currentTimeMillis() > app.gesture_edit_time + Config.Gestures.max_wait_time) {
                gestureManager.addCurrentGestureToHistory();
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
        inputmanager.inputScreenShow("Znak odpowiadający gestowi:", new InputHandlerCancellable() {
            @Override
            public void onAccept(String input) {
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
                    for (int i = input-1; i >= 0; i--) {
                        complexGesture.add(gestureManager.getLastInputGesture(i).getSingleGesture());
                    }
                    gestureManager.saveSample(complexGesture, gesture_character);
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
