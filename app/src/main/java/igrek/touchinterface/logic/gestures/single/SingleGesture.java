package igrek.touchinterface.logic.gestures.single;

import java.io.Serializable;

public class SingleGesture implements Serializable {
    private static final long serialVersionUID = 2L;

    private FreemanHistogram fhistogram;
    private Point start;

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

    public int getLength(){
        if (fhistogram == null) return 0;
        return fhistogram.pixels;
    }

    public Point getStart() {
        return start;
    }
}
