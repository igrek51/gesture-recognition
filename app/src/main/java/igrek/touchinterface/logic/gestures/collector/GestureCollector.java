package igrek.touchinterface.logic.gestures.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import igrek.touchinterface.logic.gestures.sample.ComplexGesture;
import igrek.touchinterface.logic.gestures.sample.SamplesContainer;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;

public class GestureCollector {
    App app;
    public SamplesContainer samplesContainer;
    private List<ComplexGesture> samples;

    public GestureCollector() {
        app = App.geti();
        samplesContainer = app.engine.gestureManager.samplesContainer;
    }

    public List<ComplexGesture> getSamplesByCharacter(String character) {
        List<ComplexGesture> samples = samplesContainer.getSamples();
        List<ComplexGesture> newSamples = new ArrayList<>();
        for (ComplexGesture sample : samples) {
            if (sample.getCharacter().equals(character)) {
                newSamples.add(sample);
            }
        }
        return newSamples;
    }

    public int getSamplesCountByCharacter(String character) {
        List<ComplexGesture> samples = samplesContainer.getSamples();
        int count = 0;
        for (ComplexGesture sample : samples) {
            if (sample.getCharacter().equals(character)) {
                count++;
            }
        }
        return count;
    }

    public void limitSamplesByCharacter(String character) {
        if(getSamplesCountByCharacter(character) > Config.Gestures.max_samples_count){
            List<ComplexGesture> samplesCharacter = getSamplesByCharacter(character);
            Collections.sort(samplesCharacter, new GestureOptimizerComparator());
            List<ComplexGesture> samples = samplesContainer.getSamples();
            while(samplesCharacter.size() > Config.Gestures.max_samples_count){
                //usuwanie po jednym ostatnim wzorcu
                samples.remove(samplesCharacter.get(samplesCharacter.size()-1));
            }
            //lista wzorców powinna zostać zapisana
        }
    }

    public void limitSamples() {
        List<ComplexGesture> samples = samplesContainer.getSamples();
        for(ComplexGesture sample : samples){
            limitSamplesByCharacter(sample.getCharacter());
        }
        //lista wzorców powinna zostać zapisana
    }

    public void analyzeRecognized(){
        //TODO: analiza wpisanych znaków - po czasie i zwiększenie licznika dobrych rozpoznań i zapisanie faktu zanalizowania
    }
}
