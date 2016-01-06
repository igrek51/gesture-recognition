package igrek.touchinterface.logic.gestures;

public class InputGesture {
    private Track track;
    private SingleGesture singleGesture;
    boolean analyzed = false;

    public InputGesture(Track track, SingleGesture singleGesture){
        this.track = track;
        this.singleGesture = singleGesture;
    }

    public Track getTrack() {
        return track;
    }

    public SingleGesture getSingleGesture() {
        return singleGesture;
    }

    public boolean isAnalyzed() {
        return analyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }
}
