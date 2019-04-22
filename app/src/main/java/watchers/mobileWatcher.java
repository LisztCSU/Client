package watchers;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class mobileWatcher implements TextWatcher {
    private EditText EditId;
    private Button  ButtionId;

    public mobileWatcher(EditText id,Button buttonId) {
        EditId = id;
        ButtionId = buttonId;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(Pattern.matches("^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$",EditId.getText().toString())){
            ButtionId.setEnabled(true);
        }
        else {
            ButtionId.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {


    }
}