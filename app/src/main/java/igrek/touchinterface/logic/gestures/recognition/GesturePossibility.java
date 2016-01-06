package igrek.touchinterface.logic.gestures.recognition;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
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

    public boolean isCompleted(int analyzed) {
        return analyzed >= sample.size();
    }

    public double getCorrelation() {
        double product = 1;
        for (Double correlation : correlations) {
            product *= correlation;
        }
        return product;
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
        Output.info("" + sample.getCharacter() + ", " + sample.getFilename() + ", size = " + sample.size() + " : ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sample.size(); i++) {
            sb.append((i == 0 ? "" : " x ") + getCorrelation(i));
        }
        if (sample.size() > 1) {
            sb.append(" = " + getCorrelation());
        }
        Output.info(sb.toString());
    }
}