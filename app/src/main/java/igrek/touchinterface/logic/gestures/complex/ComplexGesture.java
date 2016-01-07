package igrek.touchinterface.logic.gestures.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import igrek.touchinterface.logic.gestures.single.SingleGesture;

/**
 * gest złożony z wielu pojedynczych gestów - wzorzec
 */
public class ComplexGesture implements Serializable, Comparable {
    private static final long serialVersionUID = 2L;

    private List<SingleGesture> singleGestures;
    private String character = null;
    private String name = null;
    private int goodRecognitions = 0;
    private int badRecognitions = 0;

    /**
     * czas dodania wzorca
     */
    private Date creation_time;

    public ComplexGesture(){
        singleGestures = new ArrayList<>();
        creation_time = new Date();
    }

    public int compareTo(Object another) {
        ComplexGesture g2 = (ComplexGesture) another;
        return this.name.compareTo(g2.name);
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

    public void setName(String name){
        this.name = name;
    }

    public String getCharacter(){
        return character;
    }

    public String getName(){
        return name;
    }

    public void addGoodRecognition(){
        goodRecognitions++;
    }

    public int getGoodRecognitions() {
        return goodRecognitions;
    }

    public void addBadRecognition(){
        badRecognitions++;
    }

    public int getBadRecognitions() {
        return badRecognitions;
    }

    public int getBalanceRecognitions(){
        return goodRecognitions - badRecognitions;
    }
}
