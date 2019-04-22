package com.liszt.wesee.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;


import android.support.v7.app.AlertDialog;
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
import watchers.mobileWatcher;

public class ChangeMobileActivity extends AppCompatActivity {

    final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000);
    SharedPreferences sharedPreferences;
    EditText mobile;
    EditText vcode;
    Button sendVcode;
    Button  untied;
    Button change;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile);
        mobile = (EditText) findViewById(R.id.mobile);
        vcode = (EditText) findViewById(R.id.vcode);
        sendVcode = (Button) findViewById(R.id.bt_sendVcode);
        untied =(Button) findViewById(R.id.bt_untied);
        change =(Button) findViewById(R.id.bt_change);
        EditText editTextList[] ={mobile,vcode};
        Button    buttonList[] = {untied,change};
        mobile.addTextChangedListener(new mobileWatcher(mobile,sendVcode));
        mobile.addTextChangedListener(new emptyWatcher(editTextList,buttonList));
        vcode.addTextChangedListener(new emptyWatcher(editTextList,buttonList));

        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
         uid = sharedPreferences.getString("uid","0");
         String mobie_hint = sharedPreferences.getString("mobile","");
        SpannableString spannableString = new SpannableString(mobie_hint);
         mobile.setHint(spannableString);
        sendVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeMobileActivity.MyThread(uid, mobile.getText().toString()).start();
            }
        });
        untied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeMobileActivity.MyThread3(uid,vcode.getText().toString()).start();
            }
        });

    }
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ChangeMobileActivity.this);
        normalDialog.setIcon(R.drawable.ic_launcher_background);
        normalDialog.setTitle("确定解绑？");
        normalDialog.setMessage("解绑手机号将不能用手机号登录以及找回密码");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ChangeMobileActivity.MyThread2(uid,vcode.getText().toString()).start(); //...To-do
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
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
            EasyHttp.get("sms/sendVcode_confirm").params("uid",uid).params("mobile",mobile).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(ChangeMobileActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            myCountDownTimer.start();
                        } else if (code == 2) {
                            Toast.makeText(ChangeMobileActivity.this, "与绑定手机号不一致", Toast.LENGTH_LONG).show();
                        } else if(code == 0) {
                            Toast.makeText(ChangeMobileActivity.this, "错误的手机号", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(ChangeMobileActivity.this, "未登录", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }}
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
        public void run(){
            EasyHttp.get("user/untiedMobile").params("uid",uid).params("vcode",vcode).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(ChangeMobileActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Intent intent = new Intent(ChangeMobileActivity.this, AccountActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (code == 2) {
                            Toast.makeText(ChangeMobileActivity.this, "解绑失败", Toast.LENGTH_LONG).show();
                        } else if (code == 0) {
                            Toast.makeText(ChangeMobileActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ChangeMobileActivity.this, "未登录", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(ChangeMobileActivity.this, LoginActivity.class);
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

    class MyThread3 extends Thread {

        private String uid;
        private String vcode;
        public MyThread3(String uid, String vcode) {
            this.uid = uid;
            this.vcode = vcode;
        }

        @Override
        public void run() {
            EasyHttp.get("user/confirmVcode").params("uid",uid).params("vcode",vcode).execute(new SimpleCallBack<String>() {

            @Override
            public void onError(ApiException e) {
                Toast.makeText(ChangeMobileActivity.this, "请求失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    int code = obj.optInt("code");
                    if (code == 1) {
                        Intent intent = new Intent(ChangeMobileActivity.this,BindMobileActivity.class);
                        startActivity(intent);
                    }
                    else if(code == 0) {
                        Toast.makeText(ChangeMobileActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(ChangeMobileActivity.this, "未登录", Toast.LENGTH_LONG).show();
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


