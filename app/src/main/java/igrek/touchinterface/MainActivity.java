package igrek.touchinterface;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import igrek.touchinterface.logic.engine.Engine;
import igrek.touchinterface.system.output.Output;

public class MainActivity extends AppCompatActivity {
    private Engine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        engine = new Engine(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Output.log("orientation changed: landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Output.log("orientation changed: portrait");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        engine.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        engine.resume();
    }

    @Override
    protected void onDestroy() {
        engine.quit();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return engine.optionsSelect(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            engine.keycodeBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            engine.keycodeMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /*
        //przechwycenie klawisza menu
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        */
        return super.onKeyDown(keyCode, event);
    }
}


