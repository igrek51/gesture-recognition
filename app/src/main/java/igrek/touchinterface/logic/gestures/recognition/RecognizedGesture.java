package igrek.touchinterface.logic.gestures.recognition;

import java.util.List;

import igrek.touchinterface.logic.gestures.sample.ComplexGesture;
import igrek.touchinterface.logic.gestures.single.SingleGesture;

public class RecognizedGesture {
    private List<SingleGesture> inputSingleGestures;
    private ComplexGesture sample;
    private String character;

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
}
