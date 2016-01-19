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
        public static int fontsize = 24;
        public static int height = 60;
        public static int padding_h = 10;
        public static int padding_v = 10;
    }
    //  CZCIONKI
    public static class Fonts {
        public static int fontsize = 24;
        public static int lineheight = 25;
    }
    //  KOLORY
    public static class Colors {
        public static int background = 0x000000;
        public static int echo_text = 0xff008000;
        public static int signature = 0x003000;
        public static int track = 0xffffff;
        public static int minitracks = 0x909090;
        public static final int histogram_axis = 0x000000;
        public static final int histogram_axis_aux = 0xa0a0a0;
        public static final int histogram_bin = 0x00a0a0;
        public static final int histogram_index = 0x505050;
        public static final int recognized_text = 0xa08000;
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
        public static final int filter_avg_points = 20; //liczba pikseli do uśrednienia przy filtrowaniu
        public static final int max_input_gestures_history = 8; //maksymalna liczba wpisanych pojedynczych gestów trzymana w historii
        public static final int max_dot_pixels = 20; //maksymalna liczba pikseli ścieżki, któa zostaje uznana za kropkę
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
            public static final double correlation_hist_weight = 0.668;
            public static final double correlation_start_point_weight = 0.166;
            public static final double correlation_length_weight = 0.166;
            public static final double single_gesture_min_correlation = 0.85; //minimalny współczynnik korelacji dla pojedynczego gestu
        }
        public static class Collector {
            public static final int max_samples_count = 35; //maksymalna liczba wzorców dla jednego znaku
            public static final double complex_gesture_max_correlation_to_collect = 0.96; //maksymalny współczynnik korelacji dla rozpoznanego złożonego gestu, który zostaje dodany do bazy jako nowy wzorzec
            public static final int max_ballance_to_remove = -3; //bilans rozpoznań, przy którym wzorzec zostaje usuwany
        }
    }
    //  Statystyki
    public static class Stats {
        public static final int max_stat_samples = 50;
    }
    //  USTAWIENIA UŻYTKOWNIKA
    public static final String shared_preferences_name = "userpreferences";
    //Zestaw ustawień: 0 - samsung galaxy grand prime (DEFAULT), 1 - emulator Android
    public static final int config_set = 0;
    //nadpisanie parametrów dla innego zestawu
    public Config() {
        if (config_set == 1) {
            Fonts.fontsize = 13;
            Fonts.lineheight = 14;
            Buttons.fontsize = 13;
            Buttons.height = 24;
            Buttons.padding_h = 10;
            Buttons.padding_v = 5;
            Colors.background = 0xffffff;
            Colors.echo_text = 0xa000a000;
            Colors.signature = 0xfefefe;
            Colors.track = 0x000000;
            Colors.minitracks = 0x707070;
        }
    }
}
