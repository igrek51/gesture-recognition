package igrek.touchinterface.logic.gestures.single;

import java.io.Serializable;
import java.util.List;

import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.output.Output;

public class FreemanHistogram implements Serializable {
    private static final long serialVersionUID = 2L;

    public float[] histogram = null;

    public FreemanHistogram(Track t) {
        List<Point> points = t.getPoints();
        calculateHistogram(points);
    }

    private FreemanHistogram() { }

    public int size(){
        return histogram.length;
    }

    public float countSamples() {
        float sum = 0;
        for (int i = 0; i < Config.Gestures.FreemanChains.directions; i++) {
            sum += histogram[i];
        }
        return sum;
    }

    public float angle(Point p1, Point p2) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        return (float) Math.atan2(-dy, dx);
    }

    public boolean isAngleInRange(float angle, float range1, float range2) {
        //normalizacja zakresów do liczb: [0, 2*PI)
        float pi2 = (float) Math.PI * 2;
        while (range1 < 0) range1 += pi2;
        while (range2 < 0) range2 += pi2;
        while (range1 >= pi2) range1 -= pi2;
        while (range2 >= pi2) range2 -= pi2;
        //normalizacja sprawdzanego kąta
        while (angle < 0) angle += pi2;
        while (angle >= pi2) angle -= pi2;
        if (range1 <= range2) { //przedział nie przechodzi przez zero
            return range1 <= angle && angle <= range2;
        } else {
            //zakres przechodzi przez zero (range2 < range1)
            return angle >= range1 || angle <= range2;
        }
    }

    public int getAngleScopeNumber(float angle, int scopes) {
        float scope_step = (float) (Math.PI * 2 / scopes);
        float scope_offset = (float) (Math.PI / scopes);
        //sprawdzenie zakresów oprócz początkowego
        for (int i = 0; i < scopes; i++) {
            float left_side = -scope_offset + scope_step * i;
            if (isAngleInRange(angle, left_side, left_side + scope_step)) return i;
        }
        Output.error("Nie dopasowano kąta do żadnego zakresu.");
        return 0;
    }

    private void calculateHistogram(List<Point> points) {
        histogram = new float[Config.Gestures.FreemanChains.directions];
        for (int i = 0; i < Config.Gestures.FreemanChains.directions; i++) {
            histogram[i] = 0;
        }
        //osobne traktowanie kropki i zapisanie histogramu zerowego
        if(points.size() <= Config.Gestures.max_dot_pixels){
            //TODO: inne traktowanie kropki
            return;
        }
        for (int i = 0; i < points.size() - 1; i++) {
            float angle = angle(points.get(i), points.get(i + 1));
            int scopeNumber = getAngleScopeNumber(angle, Config.Gestures.FreemanChains.directions);
            histogram[scopeNumber]++;
        }
        if(Config.Gestures.FreemanChains.normalize_histogram){
            normalize();
        }
    }

    public void normalize() {
        float sum = countSamples();
        if (sum > 0) {
            for (int i = 0; i < Config.Gestures.FreemanChains.directions; i++) {
                histogram[i] /= sum;
            }
        }
    }

    public static float[] getNewNormalized(float[] fh){
        float sum = 0;
        for (int i = 0; i < Config.Gestures.FreemanChains.directions; i++) {
            sum += fh[i];
        }
        float[] nowy = new float[Config.Gestures.FreemanChains.directions];
        for (int i = 0; i < Config.Gestures.FreemanChains.directions; i++) {
            nowy[i] = fh[i] / sum;
        }
        return nowy;
    }
}
