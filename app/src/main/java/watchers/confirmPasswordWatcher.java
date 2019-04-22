package watchers;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class confirmPasswordWatcher implements TextWatcher {
    private EditText confirmPassword;
    private EditText password;
    private Context context;

    public confirmPasswordWatcher(EditText confirmPassword,EditText password,Context context) {
        this.confirmPassword = confirmPassword;
        this.password = password;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(TextUtils.isEmpty(confirmPassword.getText())||TextUtils.isEmpty(password.getText())||confirmPassword.getText().toString().equals(password.getText().toString())){
            Toast.makeText(context, "密码不一致", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {


    }
}
