package igrek.touchinterface.graphics;

import android.content.Context;
import igrek.touchinterface.logic.Engine;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.Output;

public class Graphics extends CanvasView {
    private App app;

    public Graphics(Context context, Engine engine) {
        super(context, engine);
        app = App.geti();
    }

    @Override
    public void repaint() {
        drawBackground();
        setFontSize(Config.Fonts.fontsize);
        drawSignature();
        if(app.mode == Types.AppMode.MENU){

        }
        drawButtons();
        drawEcho();
    }

    void drawBackground() {
        setColor(Config.Colors.background);
        clearScreen();
    }

    public void drawSignature() {
        setColor(Config.Colors.signature);
        setFont(Types.Font.FONT_MONOSPACE);
        drawText("Igrek", w, h, Types.Align.BOTTOM_RIGHT);
    }

    public void drawEcho() {
        setColor(Config.Colors.text);
        setFont();
        Output.echoTryClear();
        drawTextMultiline(Output.echos, 0, 80 + 3 * Config.Buttons.height + 3 * Config.Fonts.lineheight, Config.Fonts.lineheight);
    }

    public void drawButtons() {
        for (Buttons.Button b : engine.buttons.buttons) {
            drawButton(b);
        }
    }

    public void drawButton(Buttons.Button b) {
        if (!b.visible) return;
        setColor(Config.Colors.buttons_background, Config.Colors.buttons_alpha);
        fillRect(b.x, b.y, b.x + b.w, b.y + b.h);
        setColor(Config.Colors.buttons_outline, Config.Colors.buttons_alpha);
        outlineRect(b.x, b.y, b.x + b.w, b.y + b.h, 2);
        setColor(Config.Colors.buttons_text, Config.Colors.buttons_alpha);
        setFontSize(Config.Buttons.fontsize);
        setFont();
        drawText(b.text, b.x + b.w / 2, b.y + b.h / 2, Types.Align.CENTER);
        setFontSize(Config.Fonts.fontsize);
    }
}
