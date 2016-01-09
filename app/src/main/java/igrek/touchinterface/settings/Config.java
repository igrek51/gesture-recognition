package igrek.touchinterface.settings;

import android.view.WindowManager;

import org.opencv.imgproc.Imgproc;

public class Config {
    //STAŁE
    //  OUTPUT
    public static class Output {
        public static final String logTag = "ylog";
        public static final int echo_showtime = 1800; //[ms]
        public static final boolean show_exceptions_trace = true;
    }
    //  SCREEN
    public static class Screen {
        public static final int fullscreen_flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        public static final boolean fullscreen = true;
        public static final boolean hide_taskbar = true;
        public static final boolean keep_screen_on = true;
        public static final boolean paint_antialias = true;
        public static final boolean paint_subpixel_text = true;
    }
    //  TIMER
    public static final int timer_interval0 = 0;
    public static final int timer_fps0 = 10;
    //  BUTTONY
    public static class Buttons {
        public static final int fontsize = 24;
        public static final int height = 60;
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
        public static final float histogram_plot_height = 0.4f; //relatywna wysokość wykresu histogramu na ekranie
        public static final int max_input_wait_time = 800; //czas braku aktywności po jakim gest jest zdejmowany z ekranu [ms]
        public static final int filter_avg_points = 15; //liczba pikseli do uśrednienia przy filtrowaniu
        public static final int max_input_gestures_history = 8; //maksymalna liczba wpisanych pojedynczych gestów trzymana w historii
        public static final int max_dot_pixels = 15; //maksymalna liczba pikseli ścieżki, któa zostaje uznana za kropkę
        public static class FreemanChains {
            public static final int directions = 8; //liczbia kierunków łańcuchów freemana
        }
        public static class Correlation {
            //metoda porównywania histogramów:
            //CV_COMP_BHATTACHARYYA, CV_COMP_INTERSECT, CV_COMP_CHISQR, CV_COMP_CORREL
            public static final int histogram_compare_method = Imgproc.CV_COMP_CORREL; //Współczynnik korelacji liniowej Pearsona
            public static final float start_point_r1 = 0.2f; //maksymalna względna odległość punktów startu niewpływająca na współczynnik korelacji
            public static final float start_point_r2 = 1.5f; //minimalna względna odległość punktów startu powyżej której współczynnik korelacji = 0
            public static final double correlation_length_rd1 = 0.5; //maksymalna względna różnica długości gestów niewpływająca na współczynnik korelacji
            public static final double correlation_length_rd2 = 2.5; //minimalna względna różnica długości gestów powyżej której współczynnik korelacji = 0
            //Wagi przy liczeniu średniej ważonej korelacji
            public static final double correlation_hist_weight = 1;
            public static final double correlation_start_point_weight = 0.25;
            public static final double correlation_length_weight = 0.25;
            public static final double single_gesture_min_correlation = 0.85; //minimalny współczynnik korelacji dla pojedynczego gestu
        }
        public static class Collector {
            public static final int max_samples_count = 10; //maksymalna liczba wzorców dla jednego znaku
            public static final double complex_gesture_max_correlation_to_collect = 0.95; //maksymalny współczynnik korelacji dla rozpoznanego złożonego gestu, który zostaje dodany do bazy jako nowy wzorzec
            public static final int max_ballance_to_remove = -3; //bilans rozpoznań, przy którym wzorzec zostaje usuwany
        }
    }
    //  USTAWIENIA UŻYTKOWNIKA
    public static final String shared_preferences_name = "userpreferences";
}
