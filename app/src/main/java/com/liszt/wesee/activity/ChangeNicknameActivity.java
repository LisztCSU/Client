package com.liszt.wesee.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
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


import Cookies.PersistentCookieStore;
import watchers.emptyWatcher;

public class ChangeNicknameActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    EditText nickname;
    Button change;
    String uid;
    String nickname_hint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        nickname = (EditText) findViewById(R.id.nickname);
        change = (Button) findViewById(R.id.bt_change);
        EditText[] editTextList = {nickname};
        Button[] buttonList = {change};
        nickname.addTextChangedListener(new emptyWatcher(editTextList,buttonList));
        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","");
        nickname_hint = sharedPreferences.getString("nickname","");
        SpannableString spannableString = new SpannableString(nickname_hint);
        nickname.setHint(spannableString);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeNicknameActivity.MyThread(uid,nickname.getText().toString()).start();
            }
        });
    }
    class MyThread extends Thread {

        private String uid;
        private String nickname;

        public MyThread(String uid, String nickname) {
            this.uid = uid;
            this.nickname = nickname;
        }

        @Override
        public void run() {
            EasyHttp.get("user/changeNickname").params("uid", uid).params("nickname", nickname).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(ChangeNicknameActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Intent intent = new Intent(ChangeNicknameActivity.this, AccountActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ChangeNicknameActivity.this, "未登录", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(ChangeNicknameActivity.this, LoginActivity.class);
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
