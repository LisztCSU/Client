package com.liszt.wesee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
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
import watchers.emptyWatcher;
import watchers.mobileWatcher;

public class BindMobileActivity extends AppCompatActivity {

    private  final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000);
    String uid;
    SharedPreferences sharedPreferences;
    EditText mobile;
    EditText vcode;
    Button sendVcode;
    Button bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_mobile);
        mobile = (EditText) findViewById(R.id.mobile);
        vcode = (EditText) findViewById(R.id.vcode);
        sendVcode = (Button) findViewById(R.id.bt_sendVcode);
        bind = (Button) findViewById(R.id.bt_bind);
        EditText editTextList[] ={mobile,vcode};
        Button    buttonList[] = {bind};
        mobile.addTextChangedListener(new mobileWatcher(mobile,sendVcode));
        vcode.addTextChangedListener(new emptyWatcher(editTextList,buttonList));

        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","0");
        sendVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BindMobileActivity.MyThread(uid, mobile.getText().toString()).start();
            }
        });
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BindMobileActivity.MyThread2(uid,vcode.getText().toString()).start();
            }
        });


    }
    class MyThread extends Thread {

        private String uid;
        private  String mobile;


        public MyThread(String uid, String mobile) {
            this.uid = uid;
            this.mobile = mobile;
        }

        @Override
        public void run() {
            EasyHttp.get("sms/sendVcode_bind").params("uid",uid).params("mobile",mobile).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(BindMobileActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            myCountDownTimer.start();
                        } else if (code == 2) {
                            Toast.makeText(BindMobileActivity.this, "该手机号已注册", Toast.LENGTH_LONG).show();
                        } else if(code == 0) {
                            Toast.makeText(BindMobileActivity.this, "错误的手机号", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(BindMobileActivity.this, "未登录", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(BindMobileActivity.this, LoginActivity.class);
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
    class MyThread2 extends Thread {

        private String uid;
        private String vcode;


        public MyThread2(String uid, String vcode) {
            this.uid = uid;
            this.vcode = vcode;

        }

        @Override
        public void run() {
            EasyHttp.get("user/bindMobile").params("uid", uid).params("vcode", vcode).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(BindMobileActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Intent intent = new Intent(BindMobileActivity.this, AccountActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (code == 2) {
                            Toast.makeText(BindMobileActivity.this, "绑定失败", Toast.LENGTH_LONG).show();
                        } else if (code == 0) {
                            Toast.makeText(BindMobileActivity.this, "错误的验证码", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(BindMobileActivity.this, "未登录", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            sendVcode.setClickable(false);
            sendVcode.setText(l / 1000 + "秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            sendVcode.setText("重新获取");
            //设置可点击
            sendVcode.setClickable(true);
        }
    }
}
