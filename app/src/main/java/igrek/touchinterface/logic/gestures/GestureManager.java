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
import igrek.touchinterface.logic.gestures.collector.GestureCollector;
import igrek.touchinterface.logic.gestures.sample.ComplexGesture;
import igrek.touchinterface.logic.gestures.recognition.RecognizedGesture;
import igrek.touchinterface.logic.gestures.sample.SamplesContainer;
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

//TODO: aktualizacja liczb poprawnych i niepoprawnych rozpoznań

public class GestureManager {
    App app;
    public SamplesContainer samplesContainer;
    public Track currentTrack = null; //lista punktów w trakcie rysowania
    private List<InputGesture> lastInputGestures; //historia wprowadzonych pojedynczych gestów
    public List<RecognizedGesture> recognized;
    public GestureCollector gestureCollector;

    public GestureManager() {
        app = App.geti();
        lastInputGestures = new ArrayList<>();
        recognized = new ArrayList<>();
        samplesContainer = new SamplesContainer();
        gestureCollector = new GestureCollector();
        loadSamples();
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
    }

    public void deleteSample(ComplexGesture g) throws SoftErrorException {
        //usunięcie z listy
        if (!samplesContainer.getSamples().remove(g)) {
            Output.errorThrow("Nie znaleziono wzorca do usunięcia na liście.");
        }
        //zapis nowych wzorców
        saveSamples();
        Output.info("Usunięto wzorzec: " + g.getName());
    }

    public void deleteSample(String name) throws SoftErrorException {
        ComplexGesture sample = samplesContainer.findByName(name);
        if (sample == null) {
            Output.errorThrow("Nie znaleziono wzorca o nazwie: " + name);
        }
        deleteSample(sample);
    }

    public void saveSample(ComplexGesture gesture, String character) throws SoftErrorException {
        //wyznaczanie nazwy
        String name = samplesContainer.getNextName(character);
        //serializacja wzorca i zapisanie histogramu
        gesture.setCharacter(character);
        gesture.setName(name);
        samplesContainer.getSamples().add(gesture);
        sortSamples();
        saveSamples();
        Output.info("Zapisano wzorzec znaku (" + character + "): " + name);
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
                List<SingleGesture> singleGestures = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    singleGestures.add(unrecognized.get(i).getSingleGesture());
                }
                recognized.add(new RecognizedGesture(singleGestures, result));
                repeat = true;
            } catch (InsufficientGesturesException e) {
                repeat = false;
            } catch (EmptyListException e) {
                repeat = false;
            } catch (NoGestureRecognized e) {
                repeat = true;
                Output.error(e.getMessage());
                if (!wait) {
                    //nierozpoznany - okno dialogowe z napisaniem co to było + opcjonalnie dodanie gestu do bazy
                    app.engine.addUnrecognizedGesture();
                    repeat = false;
                }
            }
        } while (repeat);
    }

    public void resetInputs() {
        //wysztkie inputy jako analyzed
        for (InputGesture input : lastInputGestures) {
            input.setAnalyzed(true);
        }
        Output.info("Input zresetowany.");
    }

    public void backspaceGesture() {
        if (recognized.isEmpty()) return;
        RecognizedGesture removed = recognized.get(recognized.size() - 1);
        recognized.remove(recognized.size() - 1);
        //TODO: obsłużenie usunięcia gestu - prawdopodobnie zły gest
        //TODO: jeśli nie był usunięty - dobry gest - analiza po czasie
    }

    public void newGestureDrawing(float touch_x, float touch_y) {
        if (app.mode == Types.AppMode.WRITING) {
            inputAndTryToRecognize(true);
        }
        addCurrentGestureToHistory();
        addPointToCurrentTrack(touch_x, touch_y);
    }
}
