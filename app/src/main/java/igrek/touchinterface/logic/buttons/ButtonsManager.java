package igrek.touchinterface.logic.buttons;

import igrek.touchinterface.logic.Types;
import igrek.touchinterface.system.output.Output;

public class ButtonsManager extends BaseButtonsManager {
    public ButtonsManager(){
        super();
        initButtons();
    }

    public void initButtons(){
        int w = app.w;
        int h = app.h;
        //TODO: rozmiary buttonów w module grafiki lub realtywne wielkości
        add("Czyść konsolę", "clear", 0, 0, w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                Output.reset();
            }
        });
        add("Zakończ", "exit", lastXRight(), 0, w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.quit();
            }
        });
        add("Lokalizacja wzorców", "samplesPath", 0, lastYBottom(), w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.clickedPathPreferences();
            }
        });
        add("Zapisz wzorzec", "saveSample", lastXRight(), lastYTop(), w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.saveCurrentSample();
            }
        });
        add("Lista wzorców", "listSamples", 0, lastYBottom(), w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.listSamples();
            }
        });
        add("Rozpoznaj", "recognizeSample", lastXRight(), lastYTop(), w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.gestureManager.recognizeSample(engine.currentGesture);
            }
        });
        add("Usuń wzorzec", "deleteSample", 0, lastYBottom(), w / 2, 0, new ButtonActionListener() {
            public void clicked() throws Exception {
                engine.deleteSample();
            }
        });
    }

    @Override
    public boolean isButtonVisible(Button b){
        String bid = b.id;
        if (app.mode == Types.AppMode.MENU) {
            if(bid.equals("exit")) return true;
            if(bid.equals("clear")) return true;
            if(bid.equals("samplesPath")) return true;
            if(bid.equals("saveSample")) return true;
            if(bid.equals("listSamples")) return true;
            if(bid.equals("recognizeSample")) return true;
            if(bid.equals("deleteSample")) return true;
        }
        return false;
    }
}
