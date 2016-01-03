package igrek.touchinterface.settings;

import android.view.WindowManager;

import org.opencv.imgproc.Imgproc;

public class Config {
    //STAŁE
    //  OUTPUT
    public static class Output {
        public static final String logTag = "log";
        public static final int echo_showtime = 1600; //[ms]
        public static final boolean show_exceptions_trace = true;
    }
    //  SCREEN
    public static final int fullscreen_flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
    public static final boolean fullscreen = true;
    public static final boolean hide_taskbar = true;
    public static final boolean keep_screen_on = true;
    //  TIMER
    public static final int timer_interval0 = 0;
    public static final int timer_fps0 = 10;
    //  BUTTONY
    public static class Buttons {
        public static final int fontsize = 24;
        public static final int height = 70;
        public static final int padding_h = 10;
        public static final int padding_v = 10;
    }
    //  CZCIONKI
    public static class Fonts {
        public static final int fontsize = 24;
        public static final int lineheight = 25;
    }
    //  KOLORY
    public static class Colors {
        public static final int background = 0x000000;
        public static final int echo_text = 0xa000f000;
        public static final int signature = 0x003000;
        public static class Buttons {
            public static final int background = 0x303030;
            public static final int background_clicked = 0x202020;
            public static final int outline = 0x606060;
            public static final int outline_clicked = 0x404040;
            public static final int text = 0xf0f0f0;
            public static final int alpha = 0xb0;
            public static final int alpha_clicked = 0xd0;
        }
    }
    //  GESTY
    public static class Gestures {
        public static final int max_wait_time = 500; //czas braku aktywności po jakim gest jest zdejmowany z ekranu [ms]
        public static final float plot_height = 0.4f;
        public static final int avg_points = 15; //liczba pikseli do uśrednienia przy filtrowaniu
        public static final int normalize_x_samples = 128;
        public static class FreemanChains {
            public static final int directions = 8;
        }
        public static class Correlation {
            //metoda porównywania histogramów:
            //CV_COMP_BHATTACHARYYA, CV_COMP_INTERSECT, CV_COMP_CHISQR, CV_COMP_CORREL
            public static final int histogram_compare_method = Imgproc.CV_COMP_CORREL;
            public static final float start_point_r1 = 0.35f; //maksymalna względna odległość punktów startu niewpływająca na współczynnik korelacji
            public static final float start_point_r2 = 0.85f; //minimalna względna odległość punktów startu powyżej której współczynnik korelacji = 0
        }
    }
    //  USTAWIENIA UŻYTKOWNIKA
    public static final String shared_preferences_name = "userpreferences";
}
