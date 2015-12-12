package igrek.touchinterface.gestures;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.system.Output;

public class Track {
    public List<Point> points;

    public Track(){
        points = new ArrayList<>();
    }

    public void addPoint(float x, float y){
        points.add(new Point(x, y));
    }

    public void center(){
        if(points.size() > 0){
            Point firstPoint = points.get(0);
            for (int i = points.size()-1; i >= 0; i--) {
                points.get(i).x -= firstPoint.x;
                points.get(i).y -= firstPoint.y;
            }
        }
    }

    public void move(float x, float y){
        for (int i = points.size()-1; i >= 0; i--) {
            points.get(i).x += x;
            points.get(i).y += y;
        }
    }

    public void list(){
        for(int i=0; i<points.size(); i++){
            Output.log("Punkt "+(i+1)+".:"+points.get(i).toString());
        }
    }
}
