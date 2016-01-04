package igrek.touchinterface.logic.gestures.complex;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.system.output.Output;

public class GestureTree {
    private List<GestureTrack> tracks;
    public int analyzed = 0;

    public GestureTree() {
        tracks = new ArrayList<>();
    }

    public List<GestureTrack> tracks() {
        return tracks;
    }

    public int size() {
        return tracks.size();
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    public boolean isCompleted() {
        if (tracks.isEmpty()) return false;
        for (GestureTrack track : tracks) {
            if (!track.isCompleted(analyzed)) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        tracks.clear();
        analyzed = 0;
    }

    public void listCandidates() {
        Output.info("Potencjalne gesty (" + size() + "), poziom analizy=" + analyzed + ":");
        for (GestureTrack track : tracks) {
            ComplexGesture complex = track.getComplexGesture();
            Output.info("" + complex.getCharacter() + ", " + complex.getFilename() + ", size=" + complex.size() + " : ");
            for (GestureTrackItem item : track.items()) {
                Output.info(" x " + item.getCorrelation());
            }
            Output.info(" = " + track.getCorrelation());
        }
    }
}
