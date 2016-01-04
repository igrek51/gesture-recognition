package igrek.touchinterface.logic.gestures;

import java.io.Serializable;

public class SingleGesture implements Serializable {
    private static final long serialVersionUID = 2L;

    private FreemanHistogram fhistogram;
    private Point start;

    public SingleGesture(Point start, FreemanHistogram fhistogram){
        this.start = start;
        this.fhistogram = fhistogram;
    }

    public float[] getHistogram(){
        return fhistogram.histogram;
    }

    public float getHistogram(int index){
        return fhistogram.histogram[index];
    }

    public Point getStart(){
        return start;
    }
}
