package igrek.touchinterface.logic.gestures.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.SingleGesture;

public class ComplexGesture implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;

    public List<SingleGesture> sgs;
    private String character = null;
    private String filename = null;

    public ComplexGesture(){
        sgs = new ArrayList<>();
    }

    public int compareTo(Object another) {
        ComplexGesture g2 = (ComplexGesture) another;
        return this.filename.compareTo(g2.filename);
    }

    public void add(SingleGesture sg){
        sgs.add(sg);
    }

    public int size(){
        return sgs.size();
    }

    public SingleGesture get(int i){
        if(i<0 || i>=sgs.size()) return null;
        return sgs.get(i);
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
}
