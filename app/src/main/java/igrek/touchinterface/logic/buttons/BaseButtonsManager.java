package igrek.touchinterface.logic.buttons;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.buttons.geometry.Geometry;
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

    public Button add(String text, ButtonsManager.ButtonId id, Geometry geo, int align, ButtonActionListener actionListener) {
        Button b = new Button(text, id, actionListener);
        b.setGeometry(geo, align);
        buttons.add(b);
        return b;
    }

    public Button add(String text, ButtonsManager.ButtonId id, Geometry geo, ButtonActionListener actionListener) {
        return add(text, id, geo, Types.Align.DEFAULT, actionListener);
    }

    public boolean isButtonVisible(Button b){
        return true;
    }

    public Button get(ButtonsManager.ButtonId id) throws SoftErrorException {
        for (Button b : buttons) {
            if (b.id == id) return b;
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

    public void resetAllButtonsGeometry(){
        for (Button b : buttons) {
            b.resetGeometry();
        }
    }


    public float lastLeft() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        return buttons.get(buttons.size() - 1).x;
    }

    public float lastLeftRelative() {
        return lastLeft() / app.w();
    }

    public float lastRight() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        Button last = buttons.get(buttons.size() - 1);
        return last.x + last.w;
    }

    public float lastRightRelative() {
        return lastRight() / app.w();
    }

    public float lastTop() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        return buttons.get(buttons.size() - 1).y;
    }

    public float lastTopRelative() {
        return lastTop() / app.h();
    }

    public float lastBottom() {
        if (buttons.size() == 0) {
            Output.error("Brak buttonów na liście.");
            return 0;
        }
        Button last = buttons.get(buttons.size() - 1);
        return last.y + last.h;
    }

    public float lastBottomRelative() {
        return lastBottom() / app.h();
    }
}
