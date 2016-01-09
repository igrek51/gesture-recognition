package igrek.touchinterface.logic.gestures.recognition;

import java.util.List;

import igrek.touchinterface.logic.gestures.samples.ComplexGesture;
import igrek.touchinterface.logic.gestures.single.SingleGesture;

public class RecognizedGesture {
    private List<SingleGesture> inputSingleGestures;
    private ComplexGesture sample;
    private String character;
    private boolean addedAutomatically = false;
    private ComplexGesture addedAutomaticallySample = null;

    public RecognizedGesture(List<SingleGesture> inputSingleGestures, ComplexGesture sample) {
        this.inputSingleGestures = inputSingleGestures;
        this.sample = sample;
        this.character = sample.getCharacter();
    }

    public String getCharacter() {
        return character;
    }

    public ComplexGesture getSample() {
        return sample;
    }

    public List<SingleGesture> getInputSingleGestures() {
        return inputSingleGestures;
    }

    public boolean isAddedAutomatically() {
        return addedAutomatically;
    }

    public void setAddedAutomatically(boolean addedAutomaticly) {
        this.addedAutomatically = addedAutomaticly;
    }

    public void setAddedAutomaticallySample(ComplexGesture addedAutomaticallySample){
        this.addedAutomaticallySample = addedAutomaticallySample;
    }

    public ComplexGesture getAddedAutomaticallySample() {
        return addedAutomaticallySample;
    }
}
