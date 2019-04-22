package com.liszt.wesee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import Cookies.PersistentCookieStore;
import datahelper.AESencrypt;
import watchers.confirmPasswordWatcher;
import watchers.emptyWatcher;

public class ChangePasswordActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText oldPassword;
    EditText newPassword;
    EditText confirmPassword;
    Button change;
    String uid;
    String encoderules;
    private Handler myHandler = null;
    private WeakReference<Handler> mHanderRef= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPassword = (EditText) findViewById(R.id.password_old);
        newPassword = (EditText) findViewById(R.id.password_new);
        confirmPassword = (EditText) findViewById(R.id.password_confirm);
        change = (Button) findViewById(R.id.bt_change);
        EditText editTextList[] = {oldPassword, newPassword, confirmPassword};
        Button buttonList[] = {change};
        encoderules = getResources().getString(R.string.encoderules);
        oldPassword.addTextChangedListener(new emptyWatcher(editTextList, buttonList));
        newPassword.addTextChangedListener(new emptyWatcher(editTextList, buttonList));
        confirmPassword.addTextChangedListener(new emptyWatcher(editTextList, buttonList));
        confirmPassword.addTextChangedListener(new confirmPasswordWatcher(confirmPassword, newPassword, this));
        sharedPreferences = getSharedPreferences("Cookies_Prefs", MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "0");
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangePasswordActivity.MyThread(uid, AESencrypt.AESEncode(oldPassword.getText().toString(), encoderules),
                        AESencrypt.AESEncode(newPassword.getText().toString(), encoderules),
                        AESencrypt.AESEncode(confirmPassword.getText().toString(), encoderules)).start();

            }
        });
    }

    class MyThread extends Thread {

        private String uid;
        private String oldPassword;
        private String newPassword;
        private String confirmPassword;
;

        public MyThread(String uid, String oldPassword, String newPassword, String confirmPassword) {
            this.uid = uid;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;

        }

        @Override
        public void run() {
            EasyHttp.get("user/changePassword").params("uid", uid).params("oldPassword", oldPassword).params("newPassword", newPassword).params("confirmPassword", confirmPassword).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(ChangePasswordActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Intent intent = new Intent(ChangePasswordActivity.this, AccountActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (code == 0) {
                            Toast.makeText(ChangePasswordActivity.this, "修改失败", Toast.LENGTH_LONG).show();
                        } else if (code == 2) {
                            Toast.makeText(ChangePasswordActivity.this, "原密码错误", Toast.LENGTH_LONG).show();
                        } else if (code == 3) {
                            Toast.makeText(ChangePasswordActivity.this, "密码不一致", Toast.LENGTH_LONG).show();
                        } else {
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        }

}
