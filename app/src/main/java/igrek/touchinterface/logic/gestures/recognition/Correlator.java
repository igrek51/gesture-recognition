package igrek.touchinterface.logic.gestures.recognition;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import igrek.touchinterface.logic.gestures.single.Point;
import igrek.touchinterface.logic.gestures.single.SingleGesture;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;

public class Correlator {
    //zmienna pomocnicza do obliczania średniej ważonej
    private static final double correlation_weight_sum = Config.Gestures.Correlation.correlation_hist_weight + Config.Gestures.Correlation.correlation_start_point_weight + Config.Gestures.Correlation.correlation_length_weight;

    public static double correlationHist(float[] histogram1, float[] histogram2) {
        if(histogram1.length != histogram2.length) return 0;
        Mat hist1 = new Mat(histogram1.length, 1, CvType.CV_32FC1);
        Mat hist2 = new Mat(histogram1.length, 1, CvType.CV_32FC1);
        for (int i = 0; i < histogram1.length; i++) {
            hist1.put(i, 0, histogram1[i]);
            hist2.put(i, 0, histogram2[i]);
        }
        return Imgproc.compareHist(hist1, hist2, Config.Gestures.Correlation.histogram_compare_method);
    }

    public static double correlationHist(SingleGesture sg1, SingleGesture sg2) {
        return correlationHist(sg1.getHistogram(), sg2.getHistogram());
    }

    public static double correlationStartPoint(Point p1, Point p2) {
        double d = p1.distanceTo(p2);
        double rd = d / App.geti().engine.graphics.getMinScreenSize();
        if (rd < Config.Gestures.Correlation.start_point_r1) return 1;
        if (rd > Config.Gestures.Correlation.start_point_r2) return 0;
        //interpolacja liniowa
        return (Config.Gestures.Correlation.start_point_r2 - rd) / (Config.Gestures.Correlation.start_point_r2 - Config.Gestures.Correlation.start_point_r1);
    }

    public static double correlationStartPoint(SingleGesture sg1, SingleGesture sg2) {
        return correlationStartPoint(sg1.getStart(), sg2.getStart());
    }

    public static double correlationLength(int length1, int length2){
        //jeśli oba gesty są kropkami
        if(length1 <= Config.Gestures.max_dot_pixels && length2 <= Config.Gestures.max_dot_pixels){
            return 1;
        }
        //korelacja uwzględniająca długość gestu
        int minl = length1 < length2 ? length1 : length2;
        int maxl = length1 < length2 ? length2 : length1;
        //różnica podzielona przez minimum
        double rd = ((double)(maxl - minl)) / minl;
        //martwa strefa
        if (rd < Config.Gestures.Correlation.correlation_length_rd1) return 1;
        if (rd > Config.Gestures.Correlation.correlation_length_rd2) return 0;
        //interpolacja liniowa
        return (Config.Gestures.Correlation.correlation_length_rd2 - rd) / (Config.Gestures.Correlation.correlation_length_rd2 - Config.Gestures.Correlation.correlation_length_rd1);
    }

    public static double correlationLength(SingleGesture sg1, SingleGesture sg2){
        return correlationLength(sg1.getLength(), sg2.getLength());
    }

    public static double singleGesturesCorrelation(SingleGesture sg1, SingleGesture sg2) {
        double correl_start_point = correlationStartPoint(sg1, sg2);
        double correl_hist = correlationHist(sg1, sg2);
        double correl_length = correlationLength(sg1, sg2);
        //średnia ważona poszczególnych czynników
        return (correl_hist * Config.Gestures.Correlation.correlation_hist_weight + correl_start_point * Config.Gestures.Correlation.correlation_start_point_weight + correl_length * Config.Gestures.Correlation.correlation_length_weight) / (correlation_weight_sum);
    }
}
