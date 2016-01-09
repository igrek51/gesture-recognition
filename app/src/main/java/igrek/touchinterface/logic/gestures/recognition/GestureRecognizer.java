package igrek.touchinterface.logic.gestures.recognition;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.InputGesture;
import igrek.touchinterface.logic.gestures.recognition.exceptions.EmptyListException;
import igrek.touchinterface.logic.gestures.recognition.exceptions.InsufficientGesturesException;
import igrek.touchinterface.logic.gestures.recognition.exceptions.NoGestureRecognized;
import igrek.touchinterface.logic.gestures.single.SingleGesture;
import igrek.touchinterface.logic.gestures.sample.ComplexGesture;
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

    /**
     * @param correlation współczynnik korelacji gestu
     * @param size złożoność gestu
     * @return zmodyfikowany współczynnik korelacji tak, aby uwzględniał złożoność gestu
     */
    private double getCorrectedCorrelation(double correlation, int size){
        return correlation;
    }

    private ComplexGesture getBestPossibility(List<GesturePossibility> possibilities) {
        //1. kryterium: korelacja wszystkich pojedynczych gestów >= correl_min - spełnione wcześniej
        //2.1 kryterium: największy wypadkowy współczynnik korelacji
        //2.2 kryterium: największa złożoność gestu
        //(2.1 i 2.2 mogą być jednocześnie)
        //decyduje sumaryczny współczynnik korelacji
        GesturePossibility best = null;
        double corrected_correl, max_corrected_correl = 0;
        for (GesturePossibility possibility : possibilities) {
            corrected_correl = getCorrectedCorrelation(possibility.getCorrelation(), possibility.size());
            if (best == null || corrected_correl  > max_corrected_correl) {
                best = possibility;
                max_corrected_correl = corrected_correl;
            }
        }
        if (best == null) return null;
        return best.getComplexGesture();
    }

    public ComplexGesture recognizeComplexGesture(List<InputGesture> unrecognizedGestures, boolean wait) throws EmptyListException, InsufficientGesturesException, NoGestureRecognized {
        if (unrecognizedGestures.isEmpty()) {
            throw new EmptyListException("Brak gestów do rozpoznania");
        }
        if (samples.isEmpty()) {
            throw new EmptyListException("Brak wzorców do porównania");
        }
        //drzewo potencjalnych gestów
        List<GesturePossibility> possibilities = new ArrayList<>();
        //dla każdego wzorca
        for (ComplexGesture complex : samples) {
            //zapamiętywanie wartości korelacji w  każdym kroku
            GesturePossibility track = new GesturePossibility(complex);
            //sprawdzaj kolejne poziomy, obliczając korelację
            for (int level = 0; level < complex.size(); level++) {
                //jeśli ciąg nierozpoznanych gestów jest za krótki, aby móc kontynuować dalsze porównanie
                if (level >= unrecognizedGestures.size()) {
                    //koniec rozpoznawania, brak wyniku, czekanie na większą liczbę gestów
                    if (wait) {
                        //Output.info("Czekam na kolejne gesty, aby ukończyć analizę:");
                        //track.list();
                        throw new InsufficientGesturesException();
                    } else { //brak czekania - odrzucenie gestu w przypadku braku dalszych gestów
                        break;
                    }
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
                    possibilities.add(track);
                }
            }
        }
        //żaden wzorzec nie pasował
        if (possibilities.isEmpty()) {
            //zaflagowanie najstarszego inputa jako zanalizowanych
            unrecognizedGestures.get(0).setAnalyzed(true);
            throw new NoGestureRecognized("Żaden gest nie został rozpoznany");
        }
        //wszystkie możliwe wzorce zostały w pełni sprawdzone i znajdują się w tree, nie trzeba czekać na dłuższe gesty
        Output.log("Możliwości (" + possibilities.size() + "):");
        for (GesturePossibility pos : possibilities) {
            pos.list();
        }
        ComplexGesture result = getBestPossibility(possibilities);
        if (result == null) {
            throw new NoGestureRecognized("getBestPossibility = null");
        }
        //zaflagowanie określonych inputów jako zanalizowanych
        for (int i = 0; i < result.size(); i++) {
            unrecognizedGestures.get(i).setAnalyzed(true);
        }
        Output.info("Rozpoznano złożony gest: " + result.getCharacter() + ", " + result.getName());
        return result;
    }
}
