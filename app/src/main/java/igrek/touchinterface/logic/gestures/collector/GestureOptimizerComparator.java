package igrek.touchinterface.logic.gestures.collector;

import java.util.Comparator;

import igrek.touchinterface.logic.gestures.sample.ComplexGesture;

public class GestureOptimizerComparator implements Comparator<ComplexGesture> {
    @Override
    public int compare(ComplexGesture a, ComplexGesture b) {
        int diff = b.getBalanceRecognitions() - a.getBalanceRecognitions();
        if(diff == 0){
            return b.getCreationTime().compareTo(a.getCreationTime());
        }else {
            return diff;
        }
    }
}
