package igrek.touchinterface.system.timer;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

import igrek.touchinterface.settings.Config;

public class TimerManager {
    private ITimerRunner timerRunner = null;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private Activity activity;
    private int interval; //ms

    public TimerManager(Activity activity, ITimerRunner timerRunner, int interval) {
        this.activity = activity;
        this.timerRunner = timerRunner;
        if (interval == 0) interval = 1000 / Config.timer_fps0; //odczytanie z fpsów
        this.interval = interval;
        start();
    }

    public TimerManager(Activity activity, ITimerRunner timerRunner) {
        this(activity, timerRunner, Config.timer_interval0);
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerRunner.timerRun();
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
