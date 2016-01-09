package igrek.touchinterface.logic.gestures.samples;

import java.util.Comparator;

import igrek.touchinterface.logic.gestures.samples.ComplexGesture;

public class SamplesOptimizerComparator implements Comparator<ComplexGesture> {
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
