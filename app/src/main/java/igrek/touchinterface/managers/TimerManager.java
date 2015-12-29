package igrek.touchinterface.managers;

import java.util.Timer;
import java.util.TimerTask;

import igrek.touchinterface.logic.Engine;
import igrek.touchinterface.settings.Config;

public class TimerManager {
    //metoda run do zaimplementowania
    public interface MasterOfTime {
        void timerRun();
    }

    private MasterOfTime master_of_time = null;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private int interval; //ms

    public TimerManager(MasterOfTime master_of_time, int interval) {
        this.master_of_time = master_of_time;
        if (interval == 0) interval = 1000 / Config.timer_fps0; //odczytanie z fpsów
        this.interval = interval;
        start();
    }

    public TimerManager(MasterOfTime master_of_time) {
        this(master_of_time, Config.timer_interval0);
    }

    public void setInterval(int interval) {
        if (interval <= 0) interval = 1;
        this.interval = interval;
        reset();
    }

    public void setFPS(int fps) {
        setInterval(1000 / fps);
    }

    public void start() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Engine gameengine = (Engine) master_of_time;
                //Only the original thread that created a view hierarchy can touch its views.
                gameengine.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        master_of_time.timerRun();
                    }
                });
            }
        };
        timer.schedule(timerTask, interval, interval);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void reset() {
        stop();
        start();
    }
}
