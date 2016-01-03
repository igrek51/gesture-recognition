package igrek.touchinterface.settings.preferences;

import android.app.Activity;

import igrek.touchinterface.settings.App;
import igrek.touchinterface.system.output.Output;

public class Preferences extends BasePreferences {
    App app;

    public Preferences(Activity activity){
        super(activity);
        app = App.geti();
    }

    public void preferencesSave() {
        //zapisanie do shared preferences
        setString("samplesPath", app.samplesPath);
        Output.info("Zapisano preferencje.");
    }

    public void preferencesLoad() {
        //wczytanie z shared preferences
        if (exists("samplesPath")) {
            app.samplesPath = getString("samplesPath");
            Output.info("Wczytano ścieżkę wzorców: " + app.samplesPath);
        } else {
            Output.info("Wczytano domyślną ścieżkę wzorców: " + app.samplesPath);
        }
    }
}
