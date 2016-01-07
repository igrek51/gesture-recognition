package igrek.touchinterface.logic.gestures.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SamplesContainer implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ComplexGesture> samples;

    public SamplesContainer(){
        samples = new ArrayList<>();
    }

    public List<ComplexGesture> getSamples(){
        return samples;
    }

    public int size(){
        return samples.size();
    }

    public boolean nameExists(String name){
        return findByName(name) != null;
    }

    public ComplexGesture findByName(String name){
        for(ComplexGesture sample : samples){
            if(sample.getName().equals(name)) return sample;
        }
        return null;
    }

    public String getNextName(String character){
        int sampleNumber = 0;
        String name;
        do {
            sampleNumber++;
            name = character + "." + sampleNumber;
        } while (nameExists(name));
        return name;
    }
}
