package igrek.touchinterface.logic.buttons;

import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.buttons.geometry.RelativeGeometry;
import igrek.touchinterface.system.output.Output;

public class ButtonsManager extends BaseButtonsManager {
    public ButtonsManager(){
        super();
        initButtons();
    }

    public enum ButtonId {
        CLEAR_CONSOLE, EXIT,
        SAMPLES_PATH, LIST_SAMPLES, SAVE_SAMPLE, DELETE_SAMPLE,
        RECOGNIZE_SAMPLE, RECOGNIZE_RESET,
        MENU, WRITING,
        BACKSPACE
    }

    public void initButtons(){
        Button clear_console_b = add("Czyść konsolę", ButtonId.CLEAR_CONSOLE, new RelativeGeometry(0, 0, 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                Output.reset();
            }
        });
        add("Zakończ", ButtonId.EXIT, new RelativeGeometry(last().rightRelative(), 0, 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.quit();
            }
        });
        add("Szybkie Pisanie", ButtonId.WRITING, new RelativeGeometry(0, last().bottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.setAppMode(Types.AppMode.WRITING);
            }
        });
        add("Manager gestów", ButtonId.MENU, new RelativeGeometry(0, clear_console_b.bottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.setAppMode(Types.AppMode.MENU);
            }
        });
        add("Zapisz wzorzec", ButtonId.SAVE_SAMPLE, new RelativeGeometry(last().rightRelative(), last().topRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.saveCurrentSample();
            }
        });
        add("Lista wzorców", ButtonId.LIST_SAMPLES, new RelativeGeometry(0, last().bottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.listSamples();
            }
        });
        add("Rozpoznaj", ButtonId.RECOGNIZE_SAMPLE, new RelativeGeometry(last().rightRelative(), last().topRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.inputAndTryToRecognize(true);
            }
        });
        add("Usuń wzorzec", ButtonId.DELETE_SAMPLE, new RelativeGeometry(0, last().bottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.deleteSample();
            }
        });
        Button recognize_reset = add("Reset input", ButtonId.RECOGNIZE_RESET, new RelativeGeometry(last().rightRelative(), last().topRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.resetInputs();
            }
        });
        add("Lokalizacja wzorców", ButtonId.SAMPLES_PATH, new RelativeGeometry(0, last().bottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.clickedPathPreferences();
            }
        });
        add("Backspace", ButtonId.MENU, new RelativeGeometry(0, recognize_reset.topRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.backspaceGesture();
            }
        });

    }

    @Override
    public boolean isButtonVisible(Button b){
        ButtonId bid = b.id;
        if (app.mode == Types.AppMode.MENU) {
            if(bid == ButtonId.EXIT) return true;
            if(bid == ButtonId.CLEAR_CONSOLE) return true;
            if(bid == ButtonId.SAMPLES_PATH) return true;
            if(bid == ButtonId.SAVE_SAMPLE) return true;
            if(bid == ButtonId.LIST_SAMPLES) return true;
            if(bid == ButtonId.RECOGNIZE_SAMPLE) return true;
            if(bid == ButtonId.DELETE_SAMPLE) return true;
            if(bid == ButtonId.RECOGNIZE_RESET) return true;
            if(bid == ButtonId.WRITING) return true;
        }else if (app.mode == Types.AppMode.WRITING) {
            if(bid == ButtonId.EXIT) return true;
            if(bid == ButtonId.CLEAR_CONSOLE) return true;
            if(bid == ButtonId.SAVE_SAMPLE) return true;
            if(bid == ButtonId.RECOGNIZE_RESET) return true;
            if(bid == ButtonId.MENU) return true;
            if(bid == ButtonId.BACKSPACE) return true;
        }
        return false;
    }
}
