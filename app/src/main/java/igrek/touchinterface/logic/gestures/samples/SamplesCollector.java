package igrek.touchinterface.logic.gestures.samples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.output.Output;

public class SamplesCollector {
    App app;

    private List<ComplexGesture> getSamples() {
        return app.engine.gestureManager.samplesContainer.getSamples();
    }

    public SamplesCollector() {
        app = App.geti();
    }

    public List<ComplexGesture> getSamplesByCharacter(String character) {
        List<ComplexGesture> samples = getSamples();
        List<ComplexGesture> newSamples = new ArrayList<>();
        for (ComplexGesture sample : samples) {
            if (sample.getCharacter().equals(character)) {
                newSamples.add(sample);
            }
        }
        return newSamples;
    }

    public int getSamplesCountByCharacter(String character) {
        List<ComplexGesture> samples = getSamples();
        int count = 0;
        for (ComplexGesture sample : samples) {
            if (sample.getCharacter().equals(character)) {
                count++;
            }
        }
        return count;
    }

    public void limitSamplesByCharacter(String character) {
        int samplesCount = getSamplesCountByCharacter(character);
        if (samplesCount > Config.Gestures.Collector.max_samples_count) {
            Output.log("Optymalizacja liczby wzorców dla znaku: " + character + ", liczba wzorców: " + samplesCount);
            List<ComplexGesture> samplesCharacter = getSamplesByCharacter(character);
            Collections.sort(samplesCharacter, new SamplesOptimizerComparator());
            List<ComplexGesture> samples = getSamples();
            while (samplesCharacter.size() > Config.Gestures.Collector.max_samples_count) {
                //usuwanie po jednym ostatnim wzorcu
                ComplexGesture toRemove = samplesCharacter.get(samplesCharacter.size() - 1);
                samples.remove(toRemove);
                samplesCharacter.remove(toRemove);
                Output.log("Usunięto wzorzec: " + toRemove.getName());
            }
        }
    }

    public void limitSamples() {
        List<ComplexGesture> samples = getSamples();
        //wygenerowanie listy znaków:
        List<String> characters = new ArrayList<>();
        for (ComplexGesture sample : samples) {
            if (!characters.contains(sample.getCharacter())) {
                characters.add(sample.getCharacter());
            }
        }
        Output.log("Optymalizacja wzorców: " + samples.size() + " (znaki: " + characters.size() + ")...");
        //optymalizacja według za dużej liczby wzorców - zostawienie tych najlepszych
        for (String character : characters) {
            limitSamplesByCharacter(character);
        }
    }
}
