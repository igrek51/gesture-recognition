package igrek.touchinterface.gestures;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.managers.files.Path;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.Output;
import igrek.touchinterface.system.SoftErrorException;

public class GestureManager {
    public List<SingleGesture> samples;
    App app;

    public GestureManager() {
        app = App.geti();
        loadSamples();
    }

    public Path getSamplesPath() {
        return app.engine.files.pathSD().append(app.samplesPath);
    }

    public void loadSamples() {
        samples = new ArrayList<>();
        Path samplesPath2 = getSamplesPath();
        List<String> samplesFiles = app.engine.files.listDir(samplesPath2);
        for (String sampleFile : samplesFiles) {
            String filename = samplesPath2.append(sampleFile).toString();
            SingleGesture gesture;
            try {
                FileInputStream fileIn = new FileInputStream(filename);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                gesture = (SingleGesture) in.readObject();
                in.close();
                fileIn.close();
                samples.add(gesture);
            } catch (IOException e) {
                Output.error("Błąd przy deserializacji z pliku: " + filename);
                Output.error(e);
            } catch (ClassNotFoundException c) {
                Output.error("SingleGesture class not found");
            }
        }
        Output.info("Załadowano wzorce: " + samples.size());
    }

    public void listSamples() {
        Output.info("Lista wzorców: " + samples.size());
        for (SingleGesture sample : samples) {
            Output.info("Znak: " + sample.getCharacter() + ", Plik: " + sample.getFilename());
        }
    }

    public void deleteSample(SingleGesture g) throws SoftErrorException {
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

    public void saveSample(SingleGesture gesture, String character) throws SoftErrorException {
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
            Output.info("Zapisano wzorzec znaku (" + character + ") do pliku: " + filename);
        } catch (IOException e) {
            Output.errorThrow("Błąd przy serializacji do pliku: " + filename);
            e.printStackTrace();
        }
    }

    public double singleGesturesCcorrelation(SingleGesture sg1, SingleGesture sg2) {
        Mat hist1 = new Mat(Config.Gestures.FreemanChains.directions, 1, CvType.CV_32FC1);
        Mat hist2 = new Mat(Config.Gestures.FreemanChains.directions, 1, CvType.CV_32FC1);
        for (int i = 0; i < Config.Gestures.FreemanChains.directions; i++) {
            hist1.put(i, 0, sg1.getHistogram(i));
            hist2.put(i, 0, sg2.getHistogram(i));
        }
        return Imgproc.compareHist(hist1, hist2, Config.Gestures.histogram_compare_method);
    }

    public void recognizeSample(SingleGesture gesture) {
        if (gesture == null) {
            Output.error("Brak gestu do rozpoznania");
            return;
        }
        if (samples.isEmpty()) {
            Output.error("Brak wzorców");
            return;
        }
        double correl_max = 0, correl;
        SingleGesture g_max = null;
        //obliczenie wsp. korelacji z każdym wzorcem
        for (SingleGesture sg : samples) {
            correl = singleGesturesCcorrelation(sg, gesture);
            if (correl > correl_max) {
                correl_max = correl;
                g_max = sg;
            }
            Output.info("Wzorzec: "+sg.getFilename()+", korelacja: "+correl);
        }
        if(g_max == null) {
            Output.error("Brak najlepszego dopasowania do wzorca");
            return;
        }
        Output.info("Najlepszy wzorzec: "+g_max.getCharacter()+" ("+g_max.getFilename()+", korelacja: "+correl_max+")");
    }
}
