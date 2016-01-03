package igrek.touchinterface.logic.buttons.geometry;

import igrek.touchinterface.settings.App;

public class RelativeGeometry implements Geometry {
    protected float x, y, w, h;

    public RelativeGeometry(float x, float y, float w, float h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public RelativeGeometry(float w, float h){
        this(0, 0, w, h);
    }

    public RelativeGeometry(){
        this(0, 0, 0, 0);
    }

    @Override
    public float getW() {
        return w * App.geti().w();
    }

    @Override
    public float getH() {
        return h * App.geti().h();
    }

    @Override
    public float getX() {
        return x * App.geti().w();
    }

    @Override
    public float getY() {
        return y * App.geti().h();
    }
}
