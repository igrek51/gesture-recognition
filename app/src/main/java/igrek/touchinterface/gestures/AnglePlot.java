package igrek.touchinterface.gestures;

import java.util.List;

public class AnglePlot {
    public float[] angles = null;
    Point start = null;

    public AnglePlot(Track t) {
        List<Point> points = t.points;
        if (!points.isEmpty()) {
            start = new Point(points.get(0));
            calculateAngles(points);
        }
    }

    public int getLength() {
        if (angles != null) {
            return angles.length;
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
            angles = null;
        } else {
            angles = new float[points.size() - 1];
            float dx, dy;
            for (int i = 0; i < points.size() - 1; i++) {
                dx = points.get(i + 1).x - points.get(i).x;
                dy = points.get(i + 1).y - points.get(i).y;
                angles[i] = (float) Math.atan2(-dy, dx);
            }
        }
    }

    public void normalizeX(){

    }

}
