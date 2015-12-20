package igrek.touchinterface.gestures;

public class Point {
    public float x;
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p2) {
        this(p2.x, p2.y);
    }

    @Override
    public String toString() {
        return "x = " + x + ", y = " + y;
    }
}
