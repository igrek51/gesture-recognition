package igrek.touchinterface.gestures;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import igrek.touchinterface.settings.Config;
import igrek.touchinterface.system.Output;

public class Track {
    public List<Point> points;

    public Track(){
        points = new ArrayList<>();
    }

    public int length(){
        return points.size();
    }

    public void addPoint(float x, float y){
        points.add(new Point(x, y));
    }

    public void addPoint(Point p){
        points.add(p);
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

    public void scale(float scale){
        scale(scale, scale);
    }

    public void scale(float scalex, float scaley){
        for (Point p : points) {
            p.x *= scalex;
            p.y *= scaley;
        }
    }

    public void list(){
        for(int i=0; i<points.size(); i++){
            Output.log("Punkt "+(i+1)+".:"+points.get(i).toString());
        }
    }

    public static Track filteredTrack(Track t){
        Track t2 = new Track();
        int avg_points = Config.Gestures.avg_points;
        int avg_points2;
        float x_sum, y_sum;
        for(int i=0; i<t.length() - (avg_points - 1); i++){
            x_sum = 0;
            y_sum = 0;
            avg_points2 = (i + avg_points > t.length()) ? (t.length() - i) : avg_points;
            for(int j=0; j<avg_points2; j++){
                x_sum += t.points.get(i+j).x;
                y_sum += t.points.get(i+j).y;
            }
            t2.addPoint(x_sum / avg_points2, y_sum / avg_points2);
        }
        return t2;
    }

    public static Track getAllPixels(Track t){
        Track t2 = new Track();
        int pxs;
        float p3x, p3y;
        for (int i = 0; i < t.points.size() - 1; i++) {
            Point p1 = t.points.get(i);
            Point p2 = t.points.get(i+1);
            pxs = (int)(Math.hypot(p2.x - p1.x, p2.y - p1.y) + 0.5); //zaokrąglenie w górę
            for(int j=0; j<pxs; j++){
                //interpolacja liniowa
                p3x =  p1.x + (p2.x - p1.x) * j / pxs;
                p3y =  p1.y + (p2.y - p1.y) * j / pxs;
                t2.addPoint(p3x, p3y);
            }
        }
        return t2;
    }
}
