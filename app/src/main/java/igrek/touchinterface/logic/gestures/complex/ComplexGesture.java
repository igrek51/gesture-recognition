package igrek.touchinterface.logic.gestures.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.SingleGesture;

public class ComplexGesture implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;

    private List<SingleGesture> singleGestures;
    private String character = null;
    private String filename = null;

    public ComplexGesture(){
        singleGestures = new ArrayList<>();
    }

    public int compareTo(Object another) {
        ComplexGesture g2 = (ComplexGesture) another;
        return this.filename.compareTo(g2.filename);
    }

    public void add(SingleGesture sg){
        singleGestures.add(sg);
    }

    /**
     * @return złożoność gestu
     */
    public int size(){
        return singleGestures.size();
    }

    public SingleGesture get(int i){
        if(i<0 || i>= singleGestures.size()) return null;
        return singleGestures.get(i);
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
