package igrek.touchinterface.system.keyinput;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import igrek.touchinterface.R;
import igrek.touchinterface.graphics.Graphics;
import igrek.touchinterface.system.output.Output;

public class InputManager {
    private Graphics graphics;
    private Activity activity;
    private InputMethodManager imm;
    private boolean visible = false;
    private EditText editText;
    private TextView textViewLabel;
    private View layoutView;
    private Button button_ok;
    private Button button_cancel;
    private InputHandler inputHandler = null;
    private Class<?> value_type = null;

    public InputManager(Activity activity, Graphics graphics) {
        this.activity = activity;
        this.graphics = graphics;
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        //monitorowanie stanu ekranu - czy doklejona jest klawiatura ekranowa
        //graphics.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener());
        //inicjalizacja layoutu
        LayoutInflater inflater = activity.getLayoutInflater();
        layoutView = inflater.inflate(R.layout.keyboardinput, null);
        //akcja dla przycisku OK
        button_ok = (Button) layoutView.findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputFormAccept();
            }
        });
        button_cancel = (Button) layoutView.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                inputFormCancel();
            }
        });
        editText = (EditText) layoutView.findViewById(R.id.inputKeyboardText);
        textViewLabel = (TextView) layoutView.findViewById(R.id.label_text);
    }

    public void inputScreenShow(String label, String initial_value, Class<?> value_type, InputHandler inputHandler) {
        this.inputHandler = inputHandler;
        this.value_type = value_type;
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        textViewLabel.setText(label);
        activity.setContentView(layoutView);
        editText.setText(initial_value);
        editText.setSelection(initial_value.length());
        if(Integer.class.isAssignableFrom(value_type)) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        editText.requestFocus();
        //button cancel
        if(inputHandler instanceof InputHandlerCancellable){
            button_cancel.setVisibility(View.VISIBLE);
        }else{
            button_cancel.setVisibility(View.INVISIBLE);
        }
        imm.showSoftInput(editText, 0);
        visible = true;
    }

    public void inputScreenShow(String label, Object initial_value, InputHandler inputHandler) {
        //typ zawartości na podstawie podanej wartości początkowej
        inputScreenShow(label, initial_value.toString(), initial_value.getClass(), inputHandler);
    }

    public void inputScreenShow(String label, Class<?> value_type, InputHandler inputHandler){
        inputScreenShow(label, "", value_type, inputHandler); //domyślna wartość pusta
    }

    public void inputScreenShow(String label, InputHandler inputHandler){
        inputScreenShow(label, "", String.class, inputHandler); //domyślna wartość pusta, domyślny typ - String
    }

    public void inputScreenHide(){
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        Output.echoWait(0);
        activity.setContentView(graphics);
        visible = false;
    }

    private void inputFormAccept(){
        inputScreenHide();
        if(inputHandler!=null){
            //wywołanie zdarzenia
            if(Integer.class.isAssignableFrom(value_type)) {
                int value = 0;
                try {
                    value = Integer.parseInt(editText.getText().toString());
                }catch (NumberFormatException e){
                    Output.error("Nieprawidłowy format liczby całkowitej");
                }
                inputHandler.onAccept(value);
            }else {
                inputHandler.onAccept(editText.getText().toString());
            }
        }
    }

    public boolean isCancellable() {
        return inputHandler != null && inputHandler instanceof InputHandlerCancellable;
    }

    public boolean isVisible(){
        return visible;
    }

    private void inputFormCancel(){
        inputScreenHide();
        if(inputHandler!=null){
            if(inputHandler instanceof InputHandlerCancellable) {
                InputHandlerCancellable inputHandlerCancellable = (InputHandlerCancellable) inputHandler;
                inputHandlerCancellable.onCancel(); //wywołanie zdarzenia
            }
        }
    }
}
