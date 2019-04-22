package com.liszt.wesee.activity;

import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;
import datahelper.AESencrypt;
import watchers.emptyWatcher;
public class LoginActivity extends AppCompatActivity  {

//    private static final int TYPE_MSG_ERROR = 0;
//    private static final int TYPE_MSG_SUCCESS =  1;
    TextView register;
    Button login;
    EditText account;
    EditText password;
    CheckBox clearText;
    CheckBox setVisible;
    String encoderules;
    SharedPreferences pref;
    private Handler myHandler = null;

    private WeakReference<Handler> mHanderRef = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        clearText = (CheckBox) findViewById(R.id.text_clear);
        setVisible=(CheckBox) findViewById(R.id.setVisible);
        register = (TextView) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);
        EditText editTextList[] = {account ,password};
        Button buttonList[] = {login};
        account.addTextChangedListener(new emptyWatcher(editTextList,buttonList));
        password.addTextChangedListener(new emptyWatcher(editTextList,buttonList));
        encoderules = getResources().getString(R.string.encoderules);
        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(TextUtils.isEmpty(account.getText())){
                    clearText.setChecked(false);
                }
                else {
                    clearText.setChecked(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(account.getText())){
                    clearText.setChecked(false);
                }
                else {
                    clearText.setChecked(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       clearText.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               account.setText("");
           }
       });
        setVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
                else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new MyThread(account.getText().toString(),AESencrypt.AESEncode(password.getText().toString(),encoderules)).start();

            }
        });

//        myHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == TYPE_MSG_ERROR) {
//                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
//                } else if (msg.what == TYPE_MSG_SUCCESS) {
//                    String result = (String) msg.obj;
//                    try {
//                        JSONObject obj =  new JSONObject(result);
//                        int code = obj.optInt("code");
//                        if (code == 1) {
//                            JSONObject dataObj = obj.optJSONObject("data");
//                            if (dataObj != null) {
//                                String id = dataObj.optString("id", "");
//                                String username = dataObj.optString("username","");
//                                pref = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
//                                SharedPreferences.Editor editor = pref.edit();
//                                editor.putString("uid",id);
//                                editor.putString("username",username);
//                                editor.putString("isAuth","1");
//                                editor.apply();
//                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                                startActivity(intent);
//                                finish();
//
//                            }
//
//                        } else {
//                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        };
//        mHanderRef = new WeakReference<>(myHandler);
    }

    class MyThread extends Thread {

        private String account;
        private String password;

        public MyThread(String account, String passsword) {
            this.account = account;
            this.password = passsword;
        }

        @Override
        public void run() {
            /*OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookiesManager())
                    .build();
            RequestBody requestBody = new FormBody.Builder()
                    .add("account", account)
                    .add("password", password)
                    .build();
            client.newCall(new Request.Builder().url(getResources().getString(R.string.weseeUrl)+"/user/login").post(requestBody).build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Handler handler = mHanderRef.get();
                    if (handler != null) {
                        handler.sendMessage(handler.obtainMessage(TYPE_MSG_ERROR));
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Handler handler = mHanderRef.get();
                    if (handler != null) {
                        Message message = handler.obtainMessage(TYPE_MSG_SUCCESS);
                        if (response.isSuccessful()) {
                            message.obj = response.body().string();
                            handler.sendMessage(message);
                        }
                    }
                }
            });*/
            EasyHttp.get("user/login").params("account", account).params("password", password).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj =  new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            JSONObject dataObj = obj.optJSONObject("data");
                            if (dataObj != null) {
                                String id = dataObj.optString("id", "");
                                String username = dataObj.optString("username","");
                                pref = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("uid",id);
                                editor.putString("username",username);
                                editor.putString("isAuth","1");
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
//        private class CookiesManager implements CookieJar {
//
//            private final PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
//
//            @Override
//            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                if (cookies != null && cookies.size() > 0) {
//                    for (Cookie item : cookies) {
//                        cookieStore.add(url, item);
//                    }
//                }
//            }
//
//            @Override
//            public List<Cookie> loadForRequest(HttpUrl url) {
//                List<Cookie> cookies = cookieStore.get(url);
//                return cookies;
//            }
//        }
   }


}
