package igrek.touchinterface.logic.gestures.single;

import java.io.Serializable;

import igrek.touchinterface.logic.gestures.single.FreemanHistogram;
import igrek.touchinterface.logic.gestures.single.Point;

public class SingleGesture implements Serializable {
    private static final long serialVersionUID = 2L;

    private FreemanHistogram fhistogram;
    private Point start;
    //TODO: parametryzowanie odporności na przesuwanie gestu
    //TODO: osobny sposób przechowywania kropki

    public SingleGesture(Point start, FreemanHistogram fhistogram) {
        this.start = start;
        this.fhistogram = fhistogram;
    }

    public float[] getHistogram() {
        if (fhistogram == null) return null;
        return fhistogram.histogram;
    }

    public float getHistogram(int index) {
        return fhistogram.histogram[index];
    }

    public Point getStart() {
        return start;
    }
}
