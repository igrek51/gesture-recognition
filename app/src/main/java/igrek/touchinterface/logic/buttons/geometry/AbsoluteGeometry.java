package igrek.touchinterface.logic.buttons.geometry;

public class AbsoluteGeometry implements Geometry {
    protected float x, y, w, h;

    public AbsoluteGeometry(float x, float y, float w, float h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public AbsoluteGeometry(float w, float h){
        this(0, 0, w, h);
    }

    public AbsoluteGeometry(){
        this(0, 0, 0, 0);
    }

    @Override
    public float getW() {
        return w;
    }

    @Override
    public float getH() {
        return h;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
