package igrek.touchinterface.logic.gestures.complex;

import igrek.touchinterface.logic.gestures.SingleGesture;

public class GestureTrackItem {
    private SingleGesture singleGesture;
    private Double correlation;

    public GestureTrackItem(SingleGesture singleGesture, Double correlation){
        this.singleGesture = singleGesture;
        this.correlation = correlation;
    }

    public void setCorrelation(Double correlation){
        this.correlation = correlation;
    }

    public double getCorrelation(){
        if(correlation == null) return 0;
        return correlation;
    }

    public SingleGesture getSingleGesture(){
        return singleGesture;
    }
}
