package igrek.touchinterface.logic.gestures.recognition;

import java.util.List;

import igrek.touchinterface.logic.gestures.InputGesture;
import igrek.touchinterface.logic.gestures.SingleGesture;
import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.output.Output;

public class GestureRecognizer {
    App app;
    List<ComplexGesture> samples;

    public GestureRecognizer(List<ComplexGesture> samples) {
        app = App.geti();
        this.samples = samples;
    }

    private ComplexGesture getBestTrack(GestureTree tree) {
        //1. kryterium: korelacja wszystkich pojedynczych gestów >= correl_min - spełnione wcześniej
        //2. kryterium: największa złożoność gestu
        //3. kryterium: największy wypadkowy współczynnik korelacji
        GesturePossibility best = null;
        int max_n = 0;
        double max_correl = 0;
        for (GesturePossibility track : tree.tracks()) {
            if (best == null || track.size() > max_n || (track.size() == max_n && track.getCorrelation() > max_correl)) {
                best = track;
                max_n = track.size();
                max_correl = track.getCorrelation();
            }
        }
        if (best == null) return null;
        return best.getComplexGesture();
    }

    public ComplexGesture recognizeComplexGesture(List<InputGesture> unrecognizedGestures) {
        if (unrecognizedGestures.isEmpty()){
            Output.error("Brak gestów do rozpoznania");
            return null;
        }
        if (samples.isEmpty()){
            Output.error("Brak wzorców do porównania");
            return null;
        }
        //TODO: synchronizacja z wątkiem grafiki
        //drzewo potencjalnych gestów
        GestureTree tree = new GestureTree();
        //dla każdego wzorca
        for (ComplexGesture complex : samples) {
            //zapamiętywanie wartości korelacji w  każdym kroku
            GesturePossibility track = new GesturePossibility(complex);
            //sprawdzaj kolejne poziomy, obliczając korelację
            for (int level = 0; level < complex.size(); level++) {
                //jeśli ciąg nierozpoznanych gestów jest za krótki, aby móc kontynuować dalsze porównanie
                if (level >= unrecognizedGestures.size()) {
                    //koniec rozpoznawania, brak wyniku, czekanie na większą liczbę gestów
                    Output.info("Czekam na kolejne gesty, aby ukończyć analizę:");
                    track.list();
                    return null;
                }
                SingleGesture unrecognized = unrecognizedGestures.get(level).getSingleGesture();
                SingleGesture singleSample = complex.get(level);
                double correl = Correlator.singleGesturesCorrelation(singleSample, unrecognized);
                //jeśli warunek minimalnej korelacji pojedynczych gestów nie jest spełniony
                if (correl < Config.Gestures.Correlation.single_gesture_min_correlation) {
                    break; //nie analizuj więcej tego gestu
                }
                //zapamiętanie wartości korelacji w danym kroku
                track.setCorrelation(level, correl);
                //jeśli była to ostatnia część gestu
                if (level == complex.size() - 1) {
                    //zachowanie gestu w drzewie potencjalnych rozwiązań
                    tree.tracks().add(track);
                }
            }
        }
        //żaden wzorzec nie pasował
        if (tree.isEmpty()) {
            //zaflagowanie najstarszego inputa jako zanalizowanych
            unrecognizedGestures.get(0).setAnalyzed(true);
            //TODO: ponowienie próby
            Output.error("Żaden gest nie został rozpoznany");
            return null;
        }
        //wszystkie możliwe wzorce zostały w pełni sprawdzone i znajdują się w tree, nie trzeba czekać na dłuższe gesty
        tree.list();
        ComplexGesture result = getBestTrack(tree);
        if (result == null) {
            Output.error("getBestTrack = null");
            return null;
        }
        //zaflagowanie określonych inputów jako zanalizowanych
        for(int i=0; i<result.size(); i++){
            unrecognizedGestures.get(i).setAnalyzed(true);
        }
        Output.info("Rozpoznano złożony gest: " + result.getCharacter() + ", " + result.getFilename());
        return result;
    }
}
