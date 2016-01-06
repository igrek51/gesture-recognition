package igrek.touchinterface.logic.gestures;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
import igrek.touchinterface.logic.gestures.complex.RecognizedGesture;
import igrek.touchinterface.logic.gestures.recognition.GestureRecognizer;
import igrek.touchinterface.logic.gestures.single.FreemanHistogram;
import igrek.touchinterface.logic.gestures.single.SingleGesture;
import igrek.touchinterface.logic.gestures.single.Track;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.files.Path;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.system.output.Output;
import igrek.touchinterface.system.output.SoftErrorException;

//TODO: przegląd wzorców, punktów startu, usuwanie, manager
//TODO: serializacja całej listy wzorców wraz z liczbami poprawnych i niepoprawnych rozpoznań

public class GestureManager {
    App app;
    public List<ComplexGesture> samples;
    public Track currentTrack = null; //lista punktów w trakcie rysowania
    private List<InputGesture> lastInputGestures; //historia wprowadzonych pojedynczych gestów
    public List<RecognizedGesture> recognized;

    public GestureManager() {
        app = App.geti();
        lastInputGestures = new ArrayList<>();
        recognized = new ArrayList<>();
        loadSamples();
        sortSamples();
    }

    public Path getSamplesPath() {
        return app.engine.files.pathSD().append(app.samplesPath);
    }

    public ComplexGesture loadSample(String filename) throws Exception {
        FileInputStream fileIn = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        ComplexGesture gesture = (ComplexGesture) in.readObject();
        in.close();
        fileIn.close();
        return gesture;
    }

    public void loadSamples() {
        samples = new ArrayList<>();
        Path samplesPath2 = getSamplesPath();
        List<String> samplesFiles = app.engine.files.listDir(samplesPath2);
        for (String sampleFile : samplesFiles) {
            String filename = samplesPath2.append(sampleFile).toString();
            try {
                ComplexGesture gesture = loadSample(filename);
                samples.add(gesture);
            } catch (Exception e) {
                Output.error("Błąd przy deserializacji z pliku: " + filename);
                Output.error(e);
            }
        }
        Output.info("Załadowano wzorce: " + samples.size());
    }

    public void sortSamples() {
        Collections.sort(samples);
    }

    public void listSamples() {
        Output.info("Lista wzorców: " + samples.size());
        for (ComplexGesture sample : samples) {
            Output.info("Znak: " + sample.getCharacter() + ", Plik: " + sample.getFilename() + ", Złożoność: " + sample.size());
        }
    }

    public void deleteSample(ComplexGesture g) throws SoftErrorException {
        //usunięcie z plików
        if (!app.engine.files.delete(getSamplesPath().append(g.getFilename()))) {
            Output.errorThrow("Błąd podczas usuwania wzorca: " + g.getFilename());
        }
        //usunięcie z listy
        if (!samples.remove(g)) {
            Output.errorThrow("Nie znaleziono wzorca do usunięcia na liście.");
        }
        Output.info("Usunięto wzorzec.");
    }

    public void deleteSample(String filename) throws SoftErrorException {
        for (ComplexGesture sample : samples) {
            if (sample.getFilename().equals(filename)) {
                deleteSample(sample);
                return;
            }
        }
        Output.errorThrow("Nie znaleziono wzorca: " + filename);
    }

    public void saveSample(ComplexGesture gesture, String character) throws SoftErrorException {
        Path samplesPath2 = getSamplesPath();
        //wyznaczanie nazwy pliku
        int sampleNumber = 0;
        String filename;
        do {
            sampleNumber++;
            filename = samplesPath2.append(character + "-" + sampleNumber).toString();
        } while (app.engine.files.exists(filename));
        //serializacja wzorca i zapisanie histogramu
        gesture.setCharacter(character);
        gesture.setFilename(character + "-" + sampleNumber);
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gesture);
            out.close();
            fileOut.close();
            samples.add(gesture);
            sortSamples();
            Output.info("Zapisano wzorzec znaku (" + character + ") do pliku: " + filename);
        } catch (IOException e) {
            Output.errorThrow("Błąd przy serializacji do pliku: " + filename);
            e.printStackTrace();
        }
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
        ComplexGesture result;
        do {
            List<InputGesture> unrecognized = getLastUnrecognizedInputs();
            if (unrecognized.isEmpty()) {
                return;
            }
            GestureRecognizer recognizer = new GestureRecognizer(samples);
            result = recognizer.recognizeComplexGesture(unrecognized, wait);
            if (result != null) {
                List<SingleGesture> singleGestures = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    singleGestures.add(unrecognized.get(i).getSingleGesture());
                }
                recognized.add(new RecognizedGesture(singleGestures, result));
            }
        }while(result != null);
        //TODO: kontynuacja w przypadku nie rozpoznania żadnego gestu
    }

    public void resetInputs() {
        //wysztkie inputy jako analyzed
        for (InputGesture input : lastInputGestures) {
            input.setAnalyzed(true);
        }
        Output.info("Input zresetowany.");
    }


}
