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
        SAMPLES_PATH, SAVE_SAMPLE, LIST_SAMPLES, RECOGNIZE_SAMPLE, DELETE_SAMPLE
    }

    public void initButtons(){
        //TODO: id buttonów nie jako string
        add("Czyść konsolę", ButtonId.CLEAR_CONSOLE, new RelativeGeometry(0, 0, 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                Output.reset();
            }
        });
        add("Zakończ", ButtonId.EXIT, new RelativeGeometry(lastRightRelative(), 0, 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.quit();
            }
        });
        add("Lokalizacja wzorców", ButtonId.SAMPLES_PATH, new RelativeGeometry(0, lastBottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.clickedPathPreferences();
            }
        });
        add("Zapisz wzorzec", ButtonId.SAVE_SAMPLE, new RelativeGeometry(lastRightRelative(), lastTopRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.saveCurrentSample();
            }
        });
        add("Lista wzorców", ButtonId.LIST_SAMPLES, new RelativeGeometry(0, lastBottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.listSamples();
            }
        });
        add("Rozpoznaj", ButtonId.RECOGNIZE_SAMPLE, new RelativeGeometry(lastRightRelative(), lastTopRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.inputSingleGestureAndRecognize();
            }
        });
        add("Usuń wzorzec", ButtonId.DELETE_SAMPLE, new RelativeGeometry(0, lastBottomRelative(), 0.5f, 0), new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.deleteSample();
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
        }
        return false;
    }
}
