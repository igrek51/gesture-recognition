package igrek.touchinterface.logic.gestures;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import igrek.touchinterface.logic.Types;
import igrek.touchinterface.logic.gestures.samples.SamplesCollector;
import igrek.touchinterface.logic.gestures.samples.ComplexGesture;
import igrek.touchinterface.logic.gestures.recognition.RecognizedGesture;
import igrek.touchinterface.logic.gestures.samples.SamplesContainer;
import igrek.touchinterface.logic.gestures.recognition.GestureRecognizer;
import igrek.touchinterface.logic.gestures.recognition.exceptions.EmptyListException;
import igrek.touchinterface.logic.gestures.recognition.exceptions.InsufficientGesturesException;
import igrek.touchinterface.logic.gestures.recognition.exceptions.NoGestureRecognized;
import igrek.touchinterface.logic.gestures.single.FreemanHistogram;
import igrek.touchinterface.logic.gestures.single.SingleGesture;
import igrek.touchinterface.logic.gestures.single.Track;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.files.Path;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;

public class GestureManager {
    App app;
    public SamplesContainer samplesContainer;
    public Track currentTrack = null; //lista punktów w trakcie rysowania
    private List<InputGesture> lastInputGestures; //historia wprowadzonych pojedynczych gestów
    public List<RecognizedGesture> recognized;
    public SamplesCollector samplesCollector;

    public GestureManager() {
        app = App.geti();
        lastInputGestures = new ArrayList<>();
        recognized = new ArrayList<>();
        samplesContainer = new SamplesContainer();
        loadSamples();
        samplesCollector = new SamplesCollector();
    }

    public Path getSamplesPath() {
        return app.engine.files.pathSD().append(app.samplesPath);
    }

    public void loadSamples() {
        String samplesPath = getSamplesPath().toString();
        if (!app.engine.files.exists(samplesPath) || !app.engine.files.isFile(samplesPath)) {
            Output.info("Brak pliku z wzorcami: " + samplesPath);
            return;
        }
        try {
            FileInputStream fileIn = new FileInputStream(samplesPath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            samplesContainer = (SamplesContainer) in.readObject();
            in.close();
            fileIn.close();
            sortSamples();
        } catch (Exception e) {
            Output.error("Błąd przy deserializacji z pliku: " + samplesPath);
            Output.error(e);
        }
        Output.info("Załadowano wzorce: " + samplesContainer.size());
    }

    public void saveSamples() throws SoftErrorException {
        samplesCollector.limitSamples();
        Output.log("Zapisywanie wzorców do pliku....");
        sortSamples();
        String samplesPath = getSamplesPath().toString();
        try {
            FileOutputStream fileOut = new FileOutputStream(samplesPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(samplesContainer);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            Output.error(e);
            Output.errorThrow("Błąd przy serializacji do pliku: " + samplesPath);
        }
    }

    public void sortSamples() {
        Collections.sort(samplesContainer.getSamples());
    }

    public void listSamples() {
        Output.info("Lista wzorców: " + samplesContainer.getSamples().size());
        for (ComplexGesture sample : samplesContainer.getSamples()) {
            Output.info("Znak: " + sample.getCharacter() + ", Nazwa: " + sample.getName() + ", Złożoność: " + sample.size());
        }
        app.engine.stats.listCharacterSamplesCount();
    }

    public void removeSample(ComplexGesture g) throws SoftErrorException {
        //usunięcie z listy
        if (!samplesContainer.getSamples().remove(g)) {
            Output.errorThrow("Nie znaleziono wzorca do usunięcia na liście.");
        }
        Output.info("Usunięto wzorzec: " + g.getName());
    }

    public void removeSample(String name) throws SoftErrorException {
        ComplexGesture sample = samplesContainer.findByName(name);
        if (sample == null) {
            Output.errorThrow("Nie znaleziono wzorca o nazwie: " + name);
        }
        removeSample(sample);
    }

    public void clearSamples(){
        samplesContainer.getSamples().clear();
        Output.info("Usunięto wszystkie wzorce.");
    }

    public void addSample(ComplexGesture gesture, String character) throws SoftErrorException {
        //wyznaczanie nazwy
        String name = samplesContainer.getNextName(character);
        //serializacja wzorca i zapisanie histogramu
        gesture.setCharacter(character);
        gesture.setName(name);
        samplesContainer.getSamples().add(gesture);
        Output.info("Dodano wzorzec znaku (" + character + "): " + name);
    }


    public InputGesture getLastInputGesture(int endIndex) {
        if (lastInputGestures.isEmpty()) return null;
        if (endIndex < 0 || endIndex >= lastInputGestures.size()) return null;
        return lastInputGestures.get(lastInputGestures.size() - 1 - endIndex);
    }

    public InputGesture getLastInputGesture() {
        return getLastInputGesture(0);
    }

    public int getLastInputsCount() {
        return lastInputGestures.size();
    }

    public void addCurrentGestureToHistory() {
        if (currentTrack != null) { //jeśli jest w trakcie rysowania
            Track filteredTrack = Track.filteredTrack(Track.getAllPixels(currentTrack));
            FreemanHistogram currentHistogram = new FreemanHistogram(filteredTrack);
            SingleGesture currentSingleGesture = new SingleGesture(currentTrack.getStart(), currentHistogram);
            lastInputGestures.add(new InputGesture(currentTrack, currentSingleGesture));
            while (lastInputGestures.size() > Config.Gestures.max_input_gestures_history) {
                lastInputGestures.remove(0);
            }
            currentTrack = null;
        }
    }

    public void addPointToCurrentTrack(float x, float y) {
        if (currentTrack == null) currentTrack = new Track();
        currentTrack.addPoint(x, y);
        app.gesture_edit_time = System.currentTimeMillis();
    }


    public List<InputGesture> getLastUnrecognizedInputs() {
        List<InputGesture> unrecognizedGestures = new ArrayList<>();
        for (InputGesture inputGesture : lastInputGestures) {
            if (!inputGesture.isAnalyzed()) {
                unrecognizedGestures.add(inputGesture);
            }
        }
        return unrecognizedGestures;
    }


    public void inputAndTryToRecognize(boolean wait) {
        addCurrentGestureToHistory();
        boolean repeat;
        do {
            List<InputGesture> unrecognized = getLastUnrecognizedInputs();
            if (unrecognized.isEmpty()) {
                return;
            }
            GestureRecognizer recognizer = new GestureRecognizer(samplesContainer.getSamples());
            try {
                ComplexGesture result = recognizer.recognizeComplexGesture(unrecognized, wait);
                //gest rozpoznany
                onCorrectRecognition(result, unrecognized, recognizer);
                repeat = true;
            } catch (InsufficientGesturesException e) {
                repeat = false;
            } catch (EmptyListException e) {
                repeat = false;
            } catch (NoGestureRecognized e) {
                Output.error(e.getMessage());
                if (wait) {
                    repeat = true;
                } else {
                    //nierozpoznany - okno dialogowe z napisaniem co to było + opcjonalnie dodanie gestu do bazy
                    app.engine.addUnrecognizedGesture();
                    repeat = false;
                }
            }
        } while (repeat);
    }

    public void onCorrectRecognition(ComplexGesture result, List<InputGesture> unrecognized, GestureRecognizer recognizer) {
        List<SingleGesture> singleGestures = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            singleGestures.add(unrecognized.get(i).getSingleGesture());
        }
        RecognizedGesture recognizedGesture = new RecognizedGesture(singleGestures, result);
        //zwiększenie liczby poprawnych rozpoznań dla gestu
        result.addGoodRecognition();
        app.engine.stats.addGoodRecognition();
        //jeśli gest został rozpoznany słabo
        if(recognizer.getBestCorrelation() <= Config.Gestures.Collector.complex_gesture_max_correlation_to_collect){
            //dodanie gestu do bazy jako nowy wzorzec
            try {
                ComplexGesture newSample = new ComplexGesture();
                newSample.addAll(singleGestures);
                addSample(newSample, result.getCharacter());
                //zapisanie faktu, że został automatycznie dodany
                recognizedGesture.setAddedAutomatically(true);
                recognizedGesture.setAddedAutomaticallySample(newSample);
                Output.log("Dodano automatycznie wzorzec: " + newSample.getName());
            } catch (SoftErrorException e) {
                Output.error(e);
            }
        }
        recognized.add(recognizedGesture);
    }

    public void resetInputs() {
        addCurrentGestureToHistory();
        //wszystkie inputy jako analyzed
        for (InputGesture input : lastInputGestures) {
            input.setAnalyzed(true);
        }
        Output.log("Input zresetowany.");
    }

    public void backspaceGesture() {
        if (recognized.isEmpty()) return;
        RecognizedGesture removed = recognized.get(recognized.size() - 1);
        recognized.remove(recognized.size() - 1);
        //jeśli gest został dodany automatycznie
        if(removed.isAddedAutomatically()){
            //natychamiastowe usunięcie nowo zapisanego gestu
            try{
                ComplexGesture addedAutomatically = removed.getAddedAutomaticallySample();
                Output.info("Usuwanie wzorca dodanego automatycznie: "+addedAutomatically.getName());
                removeSample(addedAutomatically);
            }catch(SoftErrorException e){
                Output.error(e);
            }
        }
        //obsłużenie usunięcia gestu - zapisanie złego rozpoznania
        removed.getSample().addBadRecognition();
        app.engine.stats.addBadRecognition();
        //usuwanie wzorca ze złym bilansem
        if(removed.getSample().getBalanceRecognitions() <= Config.Gestures.Collector.max_ballance_to_remove) {
            try{
                Output.info("Usuwanie wzorca z powodu niskiego bilansu rozpoznań: "+removed.getCharacter());
                removeSample(removed.getSample());
            }catch(SoftErrorException e){
                Output.error(e);
            }
        }
    }

    public void newGestureDrawing(float touch_x, float touch_y) {
        if (app.mode == Types.AppMode.WRITING) {
            inputAndTryToRecognize(true);
        }
        addCurrentGestureToHistory();
        addPointToCurrentTrack(touch_x, touch_y);
    }

    public void correctSample(){
        //poprawienie ostatnio rozpoznanego wzorca
        app.engine.addCorrectedGesture();
    }

    public String getRecognizedString(){
        StringBuilder sb = new StringBuilder();
        for(RecognizedGesture recognizedOne : recognized){
            sb.append(recognizedOne.getCharacter());
        }
        return sb.toString();
    }
}
