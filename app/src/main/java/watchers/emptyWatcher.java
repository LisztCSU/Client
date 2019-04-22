package watchers;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class emptyWatcher implements TextWatcher {
    private EditText EditList[];
    private Button  ButtonList[];

    public emptyWatcher( EditText editList[],Button[] buttonlist) {
        EditList = editList;
        ButtonList = buttonlist;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean empty = false;
        for(int i = 0;i < EditList.length;i++){
            empty = empty || TextUtils.isEmpty(EditList[i].getText());
        }
        if(empty){
            for (int i = 0;i < ButtonList.length;i++)
                ButtonList[i].setEnabled(false);
        }
        else {
            for (int i = 0;i < ButtonList.length;i++)
                ButtonList[i].setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {


    }
}