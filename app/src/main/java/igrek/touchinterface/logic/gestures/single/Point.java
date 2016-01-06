package igrek.touchinterface.logic.gestures.single;

import java.io.Serializable;

public class Point implements Serializable {
    private static final long serialVersionUID = 2L;

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

    public float distanceTo(Point p2) {
        return (float)Math.hypot(p2.x - this.x, p2.y - this.y);
    }
}
