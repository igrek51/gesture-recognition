package igrek.touchinterface.logic.buttons;

import android.graphics.Paint;
import android.graphics.Rect;

import igrek.touchinterface.logic.Types;
import igrek.touchinterface.settings.Config;

public class Button {
    public float x, y;
    public float w, h;
    public String text, id;
    public int clicked = 0; //0 - nie wciśnięty, 1 - przyciśnięty (nie puszczony), 2 - wciśnięty i puszczony (kliknięty)
    public ButtonActionListener actionListener;

    public Button(String text, String id, float x, float y, float w, float h, ButtonActionListener actionListener) {
        this.text = text;
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.actionListener = actionListener;
    }

    public Button setPos(float x, float y, float w, float h, int align) {
        //rozmiar
        this.w = w;
        if (h == 0) h = Config.Buttons.height; //domyślna wysokość
        this.h = h;
        if ((align & 0xf00) != 0) { //coś jest na pozycji adjust
            Paint paint2 = new Paint();
            paint2.setTextSize(Config.Buttons.fontsize);
            Rect textBounds = new Rect();
            paint2.getTextBounds(text, 0, text.length(), textBounds);
            if (Types.isFlagSet(align, Types.Align.HADJUST)) { //automatyczne dobranie szerokości
                this.w = textBounds.width() + 2 * Config.Buttons.padding_h;
            }
            if (Types.isFlagSet(align, Types.Align.VADJUST)) { //automatyczne dobranie wysokości
                this.h = textBounds.height() + 2 * Config.Buttons.padding_v;
            }
        }
        //pozycja
        //domyślne wartości
        if ((align & 0x0f) == 0) align |= Types.Align.LEFT;
        if ((align & 0xf0) == 0) align |= Types.Align.TOP;
        if (Types.isFlagSet(align, Types.Align.LEFT)) {
            this.x = x;
        } else if (Types.isFlagSet(align, Types.Align.HCENTER)) {
            this.x = x - this.w / 2;
        } else { //right
            this.x = x - this.w;
        }
        if (Types.isFlagSet(align, Types.Align.TOP)) {
            this.y = y;
        } else if (Types.isFlagSet(align, Types.Align.VCENTER)) {
            this.y = y - this.h / 2;
        } else { //bottom
            this.y = y - this.h;
        }
        return this;
    }

    public boolean isInRect(float touch_x, float touch_y) {
        return touch_x >= x && touch_x <= x + w && touch_y >= y && touch_y <= y + h;
    }
}