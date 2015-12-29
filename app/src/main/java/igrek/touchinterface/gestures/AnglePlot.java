package igrek.touchinterface.gestures;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.settings.Config;

public class AnglePlot {
    public List<Float> angles = null;
    Point start = null;

    public AnglePlot(Track t) {
        angles = new ArrayList<>();
        List<Point> points = t.points;
        if (!points.isEmpty()) {
            start = new Point(points.get(0));
            calculateAngles(points);
        }
    }

    public int getLength() {
        if (angles != null) {
            return angles.size();
        }
        return 0;
    }

    public float interpolateY(float y1, float y2, int index, int all) {
        float dy = y2 - y1;
        if (dy > Math.PI) { //skok z -180 do +180
            //zmiana mniejszego kata na zbyt duży
            y1 += 2 * Math.PI;
            //interpolacja kąta (bez skoku)
            float interpolated = y1 + (y2 - y1) * (index + 1) / all;
            //przesunięcie z powrotem do zakresu -pi, pi
            if (interpolated >= Math.PI) interpolated -= 2 * Math.PI;
            return interpolated;
        } else if (dy < -Math.PI) { //skok z +180 do -180
            //zmiana mniejszego kata na zbyt duży
            y2 += 2 * Math.PI;
            //interpolacja kąta (bez skoku)
            float interpolated = y1 + (y2 - y1) * (index + 1) / all;
            //przesunięcie z powrotem do zakresu -pi, pi
            if (interpolated >= Math.PI) interpolated -= 2 * Math.PI;
            return interpolated;
        } else {
            //interpolacja liniowa
            return y1 + (y2 - y1) * (index + 1) / all;
        }
    }

    private void calculateAngles(List<Point> points) {
        if (points.size() <= 1) {
            //TODO: kropka
        } else {
            angles = new ArrayList<>();
            float dx, dy;
            for (int i = 0; i < points.size() - 1; i++) {
                dx = points.get(i + 1).x - points.get(i).x;
                dy = points.get(i + 1).y - points.get(i).y;
                angles.add(new Float(Math.atan2(-dy, dx)));
            }
        }
    }

    private float interpolateSample(float y1, float y2, float pos) {
        //interpolacja liniowa
        return y1 + (y2 - y1) * pos;
    }

    public void normalizeX() {
        List<Float> angles2 = new ArrayList<>();
        if(angles.size() > 0) {
            float y1, y2, pos;
            int posi;
            for (int i = 0; i < Config.Gestures.normalize_x_samples; i++) {
                pos = angles.size() * i / Config.Gestures.normalize_x_samples;
                posi = (int) pos; //zakres: [0; angles.size()-1]
                pos -= posi; //reszta - położenie między punktami
                y1 = angles.get(posi);
                y2 = (posi + 1 >= angles.size()) ? angles.get(posi) : angles.get(posi + 1);
                angles2.add(interpolateSample(y1, y2, pos));
            }
        }
        angles = angles2;
    }

}
