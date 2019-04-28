package com.liszt.wesee.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.cookie.PersistentCookieStore;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import CustomizedControl.SlideButton;

import static android.graphics.Color.parseColor;

public class SettingActivity extends AppCompatActivity {

    private static  final int MAX_COUNT=50;
    private SeekBar far;
    private SeekBar max;
    private TextView text1;
    private TextView text2;
    private SlideButton button;
    private Button save;
    String recommendedAccept = "1";
    String uid;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        far = (SeekBar) findViewById(R.id.sb_far);
        max = (SeekBar) findViewById(R.id.sb_max);
        far.setMax(MAX_COUNT);
        max.setMax(MAX_COUNT);
        text1 = (TextView) findViewById(R.id.text_far);
        text2 =(TextView) findViewById(R.id.text_max);
        button = (SlideButton) findViewById(R.id.bt_accept);
        save = (Button) findViewById(R.id.bt_save);
        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","0");
        button.setSmallCircleModel(parseColor("#cccccc"), parseColor("#00000000"), parseColor("#FF4040"), parseColor("#cccccc"));

        far.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text1.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text2.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });
        button.setOnCheckedListener(new SlideButton.SlideButtonOnCheckedListener() {
            @Override
            public void onCheckedChangeListener(boolean isChecked) {
                  if(isChecked){
                      recommendedAccept = "1";
                  }
                  else {
                      recommendedAccept = "0";
                  }
            }

        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 new SettingActivity.MyThread2(uid,recommendedAccept,text2.getText().toString(),text1.getText().toString()).start();
            }
        });
        new SettingActivity.MyThread(uid).start();
    }


    class MyThread extends Thread {

        private String uid;


        public MyThread(String uid) {
            this.uid = uid;
        }

        @Override
        public void run() {
            EasyHttp.get("setting/getSetting").params("uid", uid).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(SettingActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            JSONObject dataObj = obj.optJSONObject("data");
                            if (dataObj != null) {

                               int far1 = Integer.parseInt( dataObj.optString("far", ""));
                                int max1 = Integer.parseInt( dataObj.optString("max", ""));
                               String recommendedAccept = dataObj.optString("recommendedAccept", "");
                               text1.setText(far1+"");
                               text2.setText(max1+"");
                               far.setProgress(far1);
                               max.setProgress(max1);

                               button.setChecked(strToboolean(recommendedAccept));

                            }

                        } else if (code == -1) {
                            Toast.makeText(SettingActivity.this, "未登录或用户不一致", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SettingActivity.this, "不存在该用户", Toast.LENGTH_LONG).show();
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
        private String recommendedAccept;
        private String max;
        private String far;



        public MyThread2(String uid,String recommendedAccept,String max,String far) {

            this.uid = uid;
            this.recommendedAccept = recommendedAccept;
            this.max=max;
            this.far=far;
        }

        @Override
        public void run() {
            EasyHttp.get("setting/changeSetting").params("uid", uid).params("recommendedAccept",recommendedAccept).params("max",max).params("far",far).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(SettingActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {

                                Toast.makeText(SettingActivity.this, "保存成功", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(SettingActivity.this,SettingActivity.class);
                            startActivity(intent);
                            finish();


                        } else if (code == -1) {
                            Toast.makeText(SettingActivity.this, "未登录或用户不一致", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SettingActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
    private boolean strToboolean (String str){
        if(str.equals("0")){
            return false;
        }
        else {
            return true;
        }
    }
}
