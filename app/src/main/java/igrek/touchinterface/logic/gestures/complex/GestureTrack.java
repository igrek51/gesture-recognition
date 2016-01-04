package igrek.touchinterface.logic.gestures.complex;

import java.util.ArrayList;
import java.util.List;

public class GestureTrack {
    private List<GestureTrackItem> items;
    private ComplexGesture complex;

    public GestureTrack(ComplexGesture complex){
        items = new ArrayList<>();
        this.complex = complex;
    }

    public List<GestureTrackItem> items(){
        return items;
    }

    public int size(){
        return items.size();
    }

    public boolean isCompleted(int analyzed){
        return analyzed >= items.size();
    }

    public double getCorrelation(){
        double product = 1;
        for(GestureTrackItem item : items){
            product *= item.getCorrelation();
        }
        return product;
    }

    public ComplexGesture getComplexGesture(){
        return complex;
    }
}
