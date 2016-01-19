package igrek.touchinterface.logic.stats;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.samples.ComplexGesture;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.output.Output;

public class Statistics {
    public enum Recognition {
        GOOD, BAD, UNKNOWNGESTURE, ADDED_AUTOMATICALLY
    }

    private int samples_counter;

    private List<Recognition> recognitions;

    public Statistics() {
        recognitions = new ArrayList<>();
        samples_counter = 0;
    }

    public void addRecognition(Recognition r) {
        recognitions.add(r);
        samples_counter++;
        while (recognitions.size() > Config.Stats.max_stat_samples) {
            recognitions.remove(0);
        }
        //getRecognitionStats();
    }

    public void addGoodRecognition(){
        addRecognition(Recognition.GOOD);
    }

    public void addBadRecognition() {
        //usunięcie ostatniego dobrego rozpoznania
        for (int i = 0; i < recognitions.size(); i++) {
            Recognition one = recognitions.get(recognitions.size() - 1 - i);
            if (one.equals(Recognition.GOOD)) {
                recognitions.remove(one);
                break;
            }
        }
        addRecognition(Recognition.BAD);
    }

    public int countAll() {
        return recognitions.size();
    }

    public int countRecognitions(Recognition r) {
        int sum = 0;
        for (Recognition one : recognitions) {
            if (one.equals(r)) {
                sum++;
            }
        }
        return sum;
    }

    public void getRecognitionStats() {
        StringBuilder sb = new StringBuilder();
        Output.log("STATS: Liczba wzorców: " + App.geti().engine.gestureManager.samplesContainer.size() + "\n");
        int all = countAll();
        Output.log("STATS: Wszystkie staty: " + all + ", Counter: " + samples_counter + "\n");
        if (all > 0) {
            int goods = countRecognitions(Recognition.GOOD);
            int bads = countRecognitions(Recognition.BAD);
            Output.log("STATS: Dobre: " + goods + "\n");
            Output.log("STATS:   Zle: " + bads + "\n");
            Output.log("STATS: skuteczność %: " + ((float)(goods * 100)) / (goods + bads) + "\n");
        }
    }

    public void listCharacterSamplesCount(){
        //wygenerowanie listy znaków:
        List<String> characters = new ArrayList<>();
        for (ComplexGesture sample : App.geti().engine.gestureManager.samplesContainer.getSamples()) {
            if (!characters.contains(sample.getCharacter())) {
                characters.add(sample.getCharacter());
            }
        }
        for (String character : characters) {
            Output.log("STATS: Znak: " + character + "\t"+App.geti().engine.gestureManager.samplesCollector.getSamplesCountByCharacter(character));
        }
    }
}
