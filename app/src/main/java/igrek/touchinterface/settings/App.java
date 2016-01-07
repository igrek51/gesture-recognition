package igrek.touchinterface.settings;

import igrek.touchinterface.logic.engine.Engine;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.system.output.Output;

public class App {
    private App() {
        instance = this; //dla pewności
    }

    private static App instance = null;

    public static App geti() {
        if (instance == null) {
            Output.error("Utworzona instancja App bez resetowania i przekazania engine");
            instance = new App();
        }
        return instance;
    }

    public static App reset(Engine engine) {
        instance = new App();
        instance.engine = engine;
        return instance;
    }

    public Engine engine;

    //  ALIASY
    //  Rozmiar ekranu
    public float w() {
        return engine.graphics.w;
    }

    public float h() {
        return engine.graphics.h;
    }

    //  ZMIENNE APLIKACJI
    public Types.AppMode mode = Types.AppMode.MENU;
    public long gesture_edit_time = 0;
    public String samplesPath = "Android/data/igrek.touchinterface/samples";
}
