package igrek.touchinterface.graphics;

import android.content.Context;
import android.graphics.Paint;

import igrek.touchinterface.logic.buttons.Button;
import igrek.touchinterface.logic.engine.Engine;
import igrek.touchinterface.logic.gestures.Point;
import igrek.touchinterface.logic.gestures.Track;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.touchscreen.ITouchScreenController;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.output.Output;


public class Graphics extends CanvasView {
    private App app;
    private Engine engine;
    private boolean repaintRequested = true;
    public boolean init = false;

    public Graphics(Context context, ITouchScreenController touchScreenController) {
        super(context, touchScreenController);
        app = App.geti();
        engine = app.engine;
    }

    public void repaintRequest(){
        repaintRequested = true;
    }

    @Override
    public void repaint() {
        if(!repaintRequested) return;
        repaintRequested = false;
        drawBackground();
        if (!init) return;
        setFontSize(Config.Fonts.fontsize);
        drawSignature();
        drawAppMode(app.mode);
        drawButtons();
        drawEcho();
    }

    private void drawAppMode(Types.AppMode mode){
        if (mode == Types.AppMode.MENU) {
            drawGestureHistogram();
            drawMiniTracks();
            drawTrack();
        }
    }

    private void drawBackground() {
        setColor(Config.Colors.background);
        clearScreen();
    }

    private void drawSignature() {
        setColor(Config.Colors.signature);
        setFont(Types.Font.FONT_MONOSPACE);
        drawText("Igrek", w, h, Types.Align.BOTTOM_RIGHT);
    }

    private void drawEcho() {
        setColor(Config.Colors.echo_text);
        setFont();
        Output.echoClear1AfterDelay();
        String splitecho = splitTextMultiline(Output.echos, w);
        drawTextMultiline(splitecho, 0, h, Config.Fonts.lineheight, Types.Align.BOTTOM);
    }

    private void drawButtons() {
        for (Button b : engine.buttonsManager.buttons) {
            drawButton(b);
        }
    }

    private void drawButton(Button b) {
        if (!engine.buttonsManager.isButtonVisible(b)) return;
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

    private void drawTrack() {
        if (engine.currentTrack != null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            setColor(0xffffff);
            Point last = null;
            for (Point p : engine.currentTrack.getPoints()) {
                if (last != null) {
                    drawLine(last.x, last.y, p.x, p.y);
                }
                last = p;
            }
            paint.setStrokeWidth(0);
            setColor(0x00a000);
            drawText("Liczba punktów: " + engine.currentTrack.getPoints().size(), 0, h, Types.Align.BOTTOM_LEFT);
        }
    }

    private void drawTrack(Track track, float scale, float dx, float dy) {
        paint.setStrokeWidth(1);
        Point last = null;
        for (Point p : track.getPoints()) {
            if (last != null) {
                drawLine(last.x * scale + dx, last.y * scale + dy, p.x * scale + dx, p.y * scale + dy);
            }
            last = p;
        }
        paint.setStrokeWidth(0);
    }

    private void drawMiniTracks() {
        setColor(0xa0a0a0);
        for (int i = 1; i <= 4; i++) {
            if (engine.lastTracks.size() - i >= 0) {
                Track track = engine.lastTracks.get(engine.lastTracks.size() - i);
                drawTrack(track, 0.25f, w - i * w / 4, h * 3 / 4);
            }
        }
    }

    private void drawGestureHistogram() {
        if (engine.currentHistogram != null && engine.currentTrack == null) {
            if (engine.currentHistogram.histogram != null) {
                float hr2 = h * Config.Gestures.plot_height / 2;
                //układ współrzędnych
                setColor(0x0000a0);
                drawLine(0, h / 2 + hr2, w, h / 2 + hr2); //X
                drawLine(1, h / 2 - hr2, 1, h / 2 + hr2); //Y
                //wykres
                setColor(0x00a0a0);
                for (int i = 0; i < engine.currentHistogram.size(); i++) {
                    float left = w * i / engine.currentHistogram.size() + 1;
                    float right = w * (i+1) / engine.currentHistogram.size() - 1;
                    float top = h / 2 + hr2 - engine.currentHistogram.histogram[i] * h * Config.Gestures.plot_height;
                    fillRect(left, top, right, h / 2 + hr2);
                }
            }
        }
    }
}
