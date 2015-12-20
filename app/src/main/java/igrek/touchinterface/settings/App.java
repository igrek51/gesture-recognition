package igrek.touchinterface.settings;

import igrek.touchinterface.logic.Engine;
import igrek.touchinterface.logic.Types;
import igrek.touchinterface.system.Output;

public class App {
    //singleton
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

    //  ZMIENNE APLIKACJI
    public Types.AppMode mode = Types.AppMode.MENU;
    public long gesture_edit_time = 0;
    //rozmiar ekranu
    public int w = 0;
    public int h = 0;
}
