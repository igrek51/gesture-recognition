package igrek.touchinterface.graphics;

import android.content.Context;
import android.graphics.Paint;

import igrek.touchinterface.gestures.Point;
import igrek.touchinterface.logic.Engine;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.Output;

public class Graphics extends CanvasView {
    private App app;
    public boolean init = false;

    public Graphics(Context context, Engine engine) {
        super(context, engine);
        app = App.geti();
    }

    @Override
    public void repaint() {
        drawBackground();
        if(!init) return;
        setFontSize(Config.Fonts.fontsize);
        drawSignature();
        if (app.mode == Types.AppMode.MENU) {
            drawTrack();
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
        if (b.clicked > 0) {
            setColor(Config.Colors.Buttons.background_clicked, Config.Colors.Buttons.alpha_clicked);
        } else {
            setColor(Config.Colors.Buttons.background, Config.Colors.Buttons.alpha);
        }
        fillRect(b.x, b.y, b.x + b.w, b.y + b.h);
        if (b.clicked > 0) {
            setColor(Config.Colors.Buttons.outline_clicked, Config.Colors.Buttons.alpha_clicked);
        }else{
            setColor(Config.Colors.Buttons.outline, Config.Colors.Buttons.alpha);
        }
        outlineRect(b.x, b.y, b.x + b.w, b.y + b.h, 2);
        setColor(Config.Colors.Buttons.text, Config.Colors.Buttons.alpha);
        setFontSize(Config.Buttons.fontsize);
        setFont();
        drawText(b.text, b.x + b.w / 2, b.y + b.h / 2, Types.Align.CENTER);
        setFontSize(Config.Fonts.fontsize);
    }

    public void drawTrack(){
        if (engine.gest1 != null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            setColor(0xffffff);
            Point last = null;
            for(Point p : engine.gest1.points){
                if(last != null) {
                    drawLine(last.x, last.y, p.x, p.y);
                }
                last = p;
            }
            paint.setStrokeWidth(0);
            setColor(0x00a000);
            drawText("Liczba punktów: "+engine.gest1.points.size(), 0, h, Types.Align.BOTTOM_LEFT);
        }
    }
}
