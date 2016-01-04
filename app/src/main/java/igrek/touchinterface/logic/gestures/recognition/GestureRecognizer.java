package igrek.touchinterface.logic.gestures.recognition;

import java.util.List;

import igrek.touchinterface.logic.gestures.SingleGesture;
import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
import igrek.touchinterface.logic.gestures.complex.GestureTrack;
import igrek.touchinterface.logic.gestures.complex.GestureTrackItem;
import igrek.touchinterface.logic.gestures.complex.GestureTree;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;

public class GestureRecognizer {
    App app;
    GestureTree tree;

    public GestureRecognizer(){
        app = App.geti();
        tree = new GestureTree();
    }

    /*
    public void recognizeSingleGesture(SingleGesture unknownGesture, List<ComplexGesture> samples) throws SoftErrorException {
        if (unknownGesture == null || unknownGesture.getHistogram() == null) {
            Output.errorThrow("Brak gestu do rozpoznania");
        }
        if (samples.isEmpty()) {
            Output.errorThrow("Brak wzorców");
        }
        double correl;
        double correl_max = 0, correl_hist = 0, correl_start_point = 0;
        ComplexGesture g_max = null;
        //obliczenie wsp. korelacji z każdym wzorcem
        for (ComplexGesture g : samples) {
            SingleGesture sg = g.get(0);
            correl = Correlator.singleGesturesCorrelation(sg, unknownGesture);
            if (correl > correl_max || g_max == null) {
                correl_max = correl;
                correl_hist = Correlator.correlationHist(sg, unknownGesture);
                correl_start_point = Correlator.correlationStartPoint(sg.getStart(), unknownGesture.getStart());
                g_max = g;
            }
            //Output.info("Wzorzec: "+sg.getFilename()+", korelacja: "+correl);
        }
        if (g_max == null) {
            Output.errorThrow("Brak najlepszego dopasowania do wzorca");
        }
        Output.info("Najlepszy wzorzec: " + g_max.getCharacter() + ", " + g_max.getFilename() + ", korelacja: " + correl_max);
        Output.info("(c_hist=" + correl_hist + ", c_sp=" + correl_start_point + ")");
    }
    */

    public void createInitialGestureTree(SingleGesture unknownGesture, List<ComplexGesture> samples) {
        tree.reset();
        //dla każdego wzorca
        for (ComplexGesture complex : samples) {
            SingleGesture single = complex.get(0);
            if (single != null) {
                double correl = Correlator.singleGesturesCorrelation(single, unknownGesture);
                if (correl >= Config.Gestures.Correlation.single_gesture_min_correlation) {
                    //pierwszy gest spełnia warunek, dodanie do drzewa potencjalnych gestów
                    GestureTrack gestureTrack = new GestureTrack(complex);
                    gestureTrack.items().add(new GestureTrackItem(single, correl));
                    //wypełnienie pozostałymi gestami jeszcze nie sprawdzonymi
                    for (int i = 1; i < complex.size(); i++) {
                        gestureTrack.items().add(new GestureTrackItem(complex.get(i), null));
                    }
                    tree.tracks().add(gestureTrack);
                }
            }
        }
        tree.analyzed = 1;
    }

    public void checkNextStepGestureTree(SingleGesture unknownGesture) {
        //dla gestów jeszcze nie zakończonych
        for (GestureTrack track : tree.tracks()) {
            if (track.size() > tree.analyzed) {
                //sprawdzenie kolejnego gestu
                SingleGesture single = track.items().get(tree.analyzed).getSingleGesture();
                double correl = Correlator.singleGesturesCorrelation(single, unknownGesture);
                //jeśli został dobrze rozpoznany, to zostaje
                if (correl >= Config.Gestures.Correlation.single_gesture_min_correlation) {
                    track.items().get(tree.analyzed).setCorrelation(correl);
                }else{
                    //usuwanie potencjalnych gestów nie spełniających warunku
                    track.items().clear();
                    tree.tracks().remove(track);
                }
            }
        }
        tree.analyzed++;
    }

    public ComplexGesture getBestTrack() {
        //1. kryterium: korelacja wszystkich pojedynczych gestów >= correl_min
        //2. kryterium: największa złożoność gestu
        //3. kryterium: największy wypadkowy współczynnik korelacji
        GestureTrack best = null;
        int max_n = 0;
        double max_correl = 0;
        for (GestureTrack track : tree.tracks()) {
            if (best == null || track.size() > max_n || (track.size() == max_n && track.getCorrelation() > max_correl)) {
                best = track;
                max_n = track.size();
                max_correl = track.getCorrelation();
            }
        }
        if (best == null) return null;
        return best.getComplexGesture();
    }

    public void reset(){
        tree.reset();
    }

    public ComplexGesture recognizeComplexGesture(){
        if(tree.isEmpty()){
            Output.error("Żaden gest nie został rozpoznany");
            return null;
        }
        if(!tree.isCompleted()){
            Output.error("Nie można zakończyć rozpoznania, rozważanie dłuższych ciągów");
            return null;
        }
        ComplexGesture result = getBestTrack();
        if(result == null){
            Output.error("getBestTrack = null");
            return null;
        }
        Output.log("Rozpoznano złożony gest: "+result.getCharacter() + ", "+result.getFilename());
        return result;
    }

    public ComplexGesture inputAndRecognize(SingleGesture unknownGesture, List<ComplexGesture> samples){
        if(tree.analyzed == 0){
            Output.info("Tworzę nowe drzewo.");
            createInitialGestureTree(unknownGesture, samples);
        }else{
            Output.info("Sprawdzam kolejny poziom gestu.");
            checkNextStepGestureTree(unknownGesture);
        }
        tree.listCandidates();
        ComplexGesture result = recognizeComplexGesture();
        if(result != null){
            tree.reset();
        }
        return result;
    }
}
