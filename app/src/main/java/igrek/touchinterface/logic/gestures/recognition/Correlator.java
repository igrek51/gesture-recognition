package igrek.touchinterface.logic.gestures.recognition;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import igrek.touchinterface.logic.gestures.single.Point;
import igrek.touchinterface.logic.gestures.single.SingleGesture;
import igrek.touchinterface.settings.App;
import igrek.touchinterface.settings.Config;

public class Correlator {

    public static double correlationHist(SingleGesture sg1, SingleGesture sg2) {
        Mat hist1 = new Mat(Config.Gestures.FreemanChains.directions, 1, CvType.CV_32FC1);
        Mat hist2 = new Mat(Config.Gestures.FreemanChains.directions, 1, CvType.CV_32FC1);
        for (int i = 0; i < Config.Gestures.FreemanChains.directions; i++) {
            hist1.put(i, 0, sg1.getHistogram(i));
            hist2.put(i, 0, sg2.getHistogram(i));
        }
        return Imgproc.compareHist(hist1, hist2, Config.Gestures.Correlation.histogram_compare_method);
    }

    public static double correlationStartPoint(Point p1, Point p2) {
        double d = p1.distanceTo(p2);
        double rd = d / App.geti().engine.graphics.getMinScreenSize();
        if (rd < Config.Gestures.Correlation.start_point_r1) return 1;
        if (rd > Config.Gestures.Correlation.start_point_r2) return 0;
        //interpolacja liniowa
        return (Config.Gestures.Correlation.start_point_r2 - rd) / (Config.Gestures.Correlation.start_point_r2 - Config.Gestures.Correlation.start_point_r1);
    }
    
    //TODO: korelacja uwzględniająca rozmiar i skalowanie

    public static double singleGesturesCorrelation(SingleGesture sg1, SingleGesture sg2) {
        double correl_start_point = correlationStartPoint(sg1.getStart(), sg2.getStart());
        if(correl_start_point <= 0) return 0;
        return correl_start_point * correlationHist(sg1, sg2);
    }
}
