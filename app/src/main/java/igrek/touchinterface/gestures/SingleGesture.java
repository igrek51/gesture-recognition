package igrek.touchinterface.gestures;

import java.io.Serializable;

public class SingleGesture implements Serializable {
    private static final long serialVersionUID = 1L;

    FreemanHistogram fhistogram;
    Point start;

    public SingleGesture(Point start, FreemanHistogram fhistogram){
        this.start = start;
        this.fhistogram = fhistogram;
    }
}
