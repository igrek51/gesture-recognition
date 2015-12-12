package igrek.touchinterface.settings;

import android.view.WindowManager;

public class Config {
    //STAŁE
    //  OUTPUT
    public static class Output {
        public static final String logTag = "AppLog";
        public static final int echo_spaces = 43;
        public static final int echo_showtime = 1800; //[ms]
        public static final int echo_line_max = 40; //maksymalna liczba znaków w 1 wierszu
        public static final boolean show_exceptions_trace = false;
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
        public static final int fontsize = 12;
        public static final int height = 35;
        public static final int padding_h = 10;
        public static final int padding_v = 5;
    }
    //  CZCIONKI
    public static class Fonts {
        public static final int fontsize = 12;
        public static final int lineheight = 13;
    }
    //  KOLORY
    public static class Colors {
        public static final int background = 0x000000;
        public static final int text = 0x00f000;
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
        public static final int stats_success = 0x00c000;
        public static final int stats_error = 0xc00000;
    }
    //  PANEL DOTYKOWY
    public static class Touch {
        //  Sterowanie względne
        //maksymalna odległość między początkiem a końcem w przypadku zatwierdzania (przycisk OK) [cm]
        public static final float max_assert_distance = 0.7f;
        //maksymalna dopuszczalna odchyłka kąta od osi układu współrzędnych (z jednej strony) [stopnie]
        public static final float max_angle_bias = 38f;
        //minimalna odległość między punktem początkowym a końcowym [cm]
        public static final float min_relative_distance = 1.0f;
        //  Sterowanie bezwzględne
        //promień środkowego koła (sterowanie bezwzględne) jako część szerokości ekranu
        public static final float center_radius = 0.167f;
    }
    //  USTAWIENIA UŻYTKOWNIKA
    public static final String shared_preferences_name = "userpreferences";
    //  KLAWIATURA EKRANOWA
    public static final int keyboard_min_height = 100; //px
    //  KOMPAS
    public static final float compass_scale = 0.7f;
}
