package igrek.touchinterface.logic.gestures;

import java.io.Serializable;

public class SingleGesture implements Serializable {
    private static final long serialVersionUID = 2L;

    private FreemanHistogram fhistogram;
    private Point start;
    //TODO: parametryzowanie odporności na przesuwanie gestu

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
