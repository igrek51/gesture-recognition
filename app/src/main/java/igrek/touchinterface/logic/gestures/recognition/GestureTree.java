package igrek.touchinterface.logic.gestures.recognition;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.logic.gestures.complex.ComplexGesture;
import igrek.touchinterface.logic.gestures.recognition.GesturePossibility;
import igrek.touchinterface.system.output.Output;

public class GestureTree {
    private List<GesturePossibility> tracks;

    public GestureTree() {
        tracks = new ArrayList<>();
    }

    public List<GesturePossibility> tracks() {
        return tracks;
    }

    public int size() {
        return tracks.size();
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    public void list() {
        Output.info("Potencjalne gesty (" + size() + "):");
        for (GesturePossibility track : tracks) {
            track.list();
        }
    }
}
