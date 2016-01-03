package igrek.touchinterface.logic.buttons;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.engine.Engine;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;

public abstract class BaseButtonsManager {
    public List<Button> buttons;
    protected App app;
    protected Engine engine;

    public BaseButtonsManager(){
        app = App.geti();
        engine = app.engine;
        buttons = new ArrayList<>();
    }

    public Button add(String text, String id, float x, float y, float w, float h, int align, ButtonActionListener actionListener) {
        Button b = new Button(text, id, x, y, w, h, actionListener);
        b.setPos(x, y, w, h, align);
        buttons.add(b);
        return b;
    }

    public Button add(String text, String id, float x, float y, float w, float h, ButtonActionListener actionListener) {
        return add(text, id, x, y, w, h, Types.Align.DEFAULT, actionListener);
    }

    public boolean isButtonVisible(Button b){
        return true;
    }

    public Button find(String id) throws SoftErrorException {
        for (Button b : buttons) {
            if (b.id.equals(id)) return b;
        }
        Output.errorThrow("Nie znaleziono przycisku o nazwie: " + id);
        return null;
    }

    public boolean checkPressed(float touch_x, float touch_y) {
        for (Button b : buttons) {
            if (isButtonVisible(b)) {
                if (b.isInRect(touch_x, touch_y)) {
                    b.clicked = 1;
                    return true; //przechwycenie
                }
            }
        }
        return false;
    }

    public boolean checkReleased(float touch_x, float touch_y) {
        for (Button b : buttons) {
            if (b.clicked == 1) { //jeśli był wciśnięty
                if (b.isInRect(touch_x, touch_y)) {
                    b.clicked = 2;
                    return true; //przechwycenie
                } else {
                    b.clicked = 0; //reset stanu
                }
            }
        }
        return false;
    }

    public boolean checkMoved(float touch_x, float touch_y) {
        for (Button b : buttons) {
            if (isButtonVisible(b)) {
                if (b.isInRect(touch_x, touch_y)) {
                    return true; //przechwycenie
                }
            }
        }
        return false;
    }

    public void executeClicked() throws Exception {
        for (Button b : buttons) {
            if (b.clicked == 2) { //został kliknięty
                b.clicked = 0; //zresetowanie udanego kliknięcia
                b.actionListener.clicked(); //wykonanie akcji
            }
        }
    }

    public float lastYTop() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        return buttons.get(buttons.size() - 1).y;
    }

    public float lastYBottom() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        Button last = buttons.get(buttons.size() - 1);
        return last.y + last.h;
    }

    public float lastXLeft() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        return buttons.get(buttons.size() - 1).x;
    }

    public float lastXRight() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        Button last = buttons.get(buttons.size() - 1);
        return last.x + last.w;
    }
}
