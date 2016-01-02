package igrek.touchinterface.gestures;

import java.io.Serializable;

public class SingleGesture implements Serializable {
    private static final long serialVersionUID = 2L;

    private FreemanHistogram fhistogram;
    private Point start;
    private String character = null;
    private String filename = null;

    public SingleGesture(Point start, FreemanHistogram fhistogram){
        this.start = start;
        this.fhistogram = fhistogram;
    }

    public void setCharacter(String character){
        this.character = character;
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

    public String getCharacter(){
        return character;
    }

    public String getFilename(){
        return filename;
    }

    public float[] getHistogram(){
        return fhistogram.histogram;
    }

    public float getHistogram(int index){
        return fhistogram.histogram[index];
    }

    public Point getStart(){
        return start;
    }
}
