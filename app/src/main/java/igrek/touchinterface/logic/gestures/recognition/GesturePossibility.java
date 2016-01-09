package igrek.touchinterface.logic.gestures.recognition;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.samples.ComplexGesture;
import igrek.touchinterface.system.output.Output;

public class GesturePossibility {
    private List<Double> correlations;
    private ComplexGesture sample;

    public GesturePossibility(ComplexGesture sample) {
        correlations = new ArrayList<>();
        this.sample = sample;
        for (int i = 0; i < sample.size(); i++) {
            correlations.add(new Double(0));
        }
    }

    public int size() {
        return sample.size();
    }

    public double getCorrelation() {
        if(correlations.isEmpty()) return 0;
        //średnia arytmetyczna z pojedynczych gestów
        double sum = 0;
        for (Double correlation : correlations) {
            sum += correlation;
        }
        return sum / correlations.size();
    }

    public Double getCorrelation(int index) {
        if (index < 0 || index >= correlations.size()) return null;
        return correlations.get(index);
    }

    public void setCorrelation(int index, double value) {
        if (index < 0 || index >= correlations.size()) return;
        correlations.set(index, value);
    }

    public ComplexGesture getComplexGesture() {
        return sample;
    }

    public void list() {
        StringBuilder sb = new StringBuilder();
        sb.append(sample.getCharacter() + ", " + sample.getName() + ", size=" + sample.size() + " : ");
        for (int i = 0; i < sample.size(); i++) {
            sb.append((i == 0 ? "" : " , ") + getCorrelation(i));
        }
        if (sample.size() > 1) {
            sb.append(" = " + getCorrelation());
        }
        Output.log(sb.toString());
    }
}
