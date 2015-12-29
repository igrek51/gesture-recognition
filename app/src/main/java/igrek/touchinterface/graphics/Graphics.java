package igrek.touchinterface.graphics;

import android.content.Context;
import android.graphics.Paint;

import igrek.touchinterface.gestures.Point;
import igrek.touchinterface.gestures.Track;
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
        if (!init) return;
        setFontSize(Config.Fonts.fontsize);
        drawSignature();
        if (app.mode == Types.AppMode.MENU) {
            drawGesturePlot();
            drawMiniTracks();
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
        //TODO: widoczność przycisków i rozmiary w module grafiki
        if (!b.visible) return;
        if (b.clicked > 0) {
            setColor(Config.Colors.Buttons.background_clicked, Config.Colors.Buttons.alpha_clicked);
        } else {
            setColor(Config.Colors.Buttons.background, Config.Colors.Buttons.alpha);
        }
        fillRect(b.x, b.y, b.x + b.w, b.y + b.h);
        if (b.clicked > 0) {
            setColor(Config.Colors.Buttons.outline_clicked, Config.Colors.Buttons.alpha_clicked);
        } else {
            setColor(Config.Colors.Buttons.outline, Config.Colors.Buttons.alpha);
        }
        outlineRect(b.x, b.y, b.x + b.w, b.y + b.h, 2);
        setColor(Config.Colors.Buttons.text, Config.Colors.Buttons.alpha);
        setFontSize(Config.Buttons.fontsize);
        setFont();
        drawText(b.text, b.x + b.w / 2, b.y + b.h / 2, Types.Align.CENTER);
        setFontSize(Config.Fonts.fontsize);
    }

    public void drawTrack() {
        if (engine.gest1 != null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            setColor(0xffffff);
            Point last = null;
            for (Point p : engine.gest1.points) {
                if (last != null) {
                    drawLine(last.x, last.y, p.x, p.y);
                }
                last = p;
            }
            paint.setStrokeWidth(0);
            setColor(0x00a000);
            drawText("Liczba punktów: " + engine.gest1.points.size(), 0, h, Types.Align.BOTTOM_LEFT);
        }
    }

    public void drawTrack(Track track, float scale, float dx, float dy) {
        paint.setStrokeWidth(1);
        Point last = null;
        for (Point p : track.points) {
            if (last != null) {
                drawLine(last.x * scale + dx, last.y * scale + dy, p.x * scale + dx, p.y * scale + dy);
            }
            last = p;
        }
        paint.setStrokeWidth(0);
    }

    public void drawMiniTracks() {
        setColor(0xa0a0a0);
        for (int i = 1; i <= 4; i++) {
            if (engine.gestures.size() - i >= 0) {
                Track track = engine.gestures.get(engine.gestures.size() - i);
                drawTrack(track, 0.25f, w - i * w / 4, h * 3 / 4);
            }
        }
    }

    public void drawGesturePlot() {
        if (engine.plot1 != null && engine.gest1 == null) {
            if (engine.plot1.angles != null && engine.plot1.angles.size() > 1) {
                float hr2 = h * Config.Gestures.plot_height / 2;
                //układ współrzędnych
                setColor(0x0000a0);
                drawLine(0, h / 2, w, h / 2); //X
                drawLine(1, h / 2 - hr2, 1, h / 2 + hr2); //Y
                //pomocnicze linie
                setColor(0x000060);
                drawLine(0, h / 2 - hr2, w, h / 2 - hr2);
                drawLine(0, h / 2 + hr2, w, h / 2 + hr2);
                //wykres
                float x_scale = Config.Gestures.plot_x_scale;
                if (engine.plot1.getLength() * x_scale > w) {
                    x_scale = (float) w / engine.plot1.getLength();
                }
                setColor(0x00a0a0);
                for (int i = 1; i < engine.plot1.getLength(); i++) {
                    drawLine(x_scale * (i - 1), h / 2 - (float) (engine.plot1.angles.get(i - 1) * hr2 / Math.PI), x_scale * i, h / 2 - (float) (engine.plot1.angles.get(i) * hr2 / Math.PI));
                }
            }
        }
    }
}
