package igrek.touchinterface.logic.gestures.samples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import igrek.touchinterface.logic.gestures.single.SingleGesture;
import igrek.touchinterface.system.output.Output;

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
    private Date creationTime;

    public ComplexGesture(){
        singleGestures = new ArrayList<>();
        creationTime = new Date();
    }

    public int compareTo(Object another) {
        ComplexGesture g2 = (ComplexGesture) another;
        return this.name.compareTo(g2.name);
    }

    public void add(SingleGesture sg){
        singleGestures.add(sg);
    }

    public void addAll(List<SingleGesture> singleGestures){
        for(SingleGesture sg : singleGestures){
            add(sg);
        }
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
        Output.log("Dobre rozpoznanie gestu ("+name+"): "+goodRecognitions+" - "+badRecognitions+" = "+getBalanceRecognitions());
    }

    public int getGoodRecognitions() {
        return goodRecognitions;
    }

    public void addBadRecognition(){
        goodRecognitions--;
        badRecognitions++;
        Output.log("Niepoprawne rozpoznanie gestu ("+name+"): "+goodRecognitions+" - "+badRecognitions+" = "+getBalanceRecognitions());
    }

    public int getBadRecognitions() {
        return badRecognitions;
    }

    public int getBalanceRecognitions(){
        return goodRecognitions - badRecognitions;
    }

    public Date getCreationTime(){
        return creationTime;
    }
}
