package com.liszt.wesee.activity;



import android.content.SharedPreferences;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.widget.RadioGroup;
import android.view.View;
import android.widget.TextView;

import com.liszt.wesee.fragment.MineFragment;
import com.liszt.wesee.fragment.MineLoginFragment;
import com.liszt.wesee.fragment.HomeFragment;
import com.liszt.wesee.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    private RadioGroup radioGroup;
    private FragmentTransaction fragmentTransaction;
    Fragment homeFragment,mineFragment,mineLoginFragment;
    public static final int VIEW_HOME_INDEX = 0;
    public static final int VIEW_MINE_INDEX = 1;
    private int temp_position_index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
//        String str=sharedPreferences.getString("isAuth","0");
//        if(str.equals("0")) {
//            Log.v("nmsl:", str);
//        }
//        else{Log.v("youdongxi:",str);}
        initView();
        findViewById(R.id.id_bt_home).setOnClickListener(this);
        findViewById(R.id.id_bt_mine).setOnClickListener(this);
    }
    private void initView() {
       radioGroup = (RadioGroup) findViewById(R.id.id_navcontent);
        homeFragment = HomeFragment.getNewInstance();
        mineFragment = MineFragment.getNewInstance();
        mineLoginFragment = MineLoginFragment.getNewInstance();
        //显示
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.id_fragment_content, homeFragment);
        fragmentTransaction.commit();


    }
    public void switchView(View view) {
        findViewById(R.id.home_img).setBackgroundResource(R.mipmap.ic_home);
        findViewById(R.id.min_img).setBackgroundResource(R.mipmap.ic_mine);
        ((TextView)findViewById(R.id.home_tv)).setTextColor(Color.parseColor("#707070"));
        ((TextView)findViewById(R.id.min_tv)).setTextColor(Color.parseColor("#707070"));
        switch (view.getId()) {
            case R.id.id_bt_home:
                if (temp_position_index != VIEW_HOME_INDEX) {
                    //显示
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.id_fragment_content, homeFragment);
                    fragmentTransaction.commit();
                }
                temp_position_index = VIEW_HOME_INDEX;
                findViewById(R.id.home_img).setBackgroundResource(R.mipmap.ic_home_on);
                ((TextView)findViewById(R.id.home_tv)).setTextColor(Color.parseColor("#00a628"));
                break;
            case R.id.id_bt_mine:
                if (temp_position_index != VIEW_MINE_INDEX) {
                    //显示
                    if (sharedPreferences.getString("isAuth","0").equals("1")) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.id_fragment_content, mineFragment);
                        fragmentTransaction.commit();

                    }
                    else {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.id_fragment_content, mineLoginFragment);
                        fragmentTransaction.commit();

                    }
                }
                temp_position_index = VIEW_MINE_INDEX;
                findViewById(R.id.min_img).setBackgroundResource(R.mipmap.ic_mine_on);
                ((TextView)findViewById(R.id.min_tv)).setTextColor(Color.parseColor("#00a628"));
                break;

        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent intent = getIntent();
//        startActivityAfterLogin(intent);
//    }

//
//    public void startActivityAfterLogin(Intent intent) {
//        if (intent.getExtras() != null && getIntent().getExtras().getString("isAuth") != null) {
//            String isAuth = intent.getStringExtra("isAuth");
//            //未登录（这里用自己的登录逻辑去判断是否未登录）
//            if ("1".equals(isAuth)) {
//                Toast.makeText(MainActivity.this, "哈哈哈", Toast.LENGTH_LONG).show();
//            }
//            else{
//                ComponentName componentName = new ComponentName(this, LoginActivity.class);
//                Intent intent2 = new Intent();
//                intent2.putExtra("classNameMain", intent.getComponent().getClassName());
//                intent2.setComponent(componentName);
//                intent2.setAction("maingotologin");
//                super.startActivity(intent2);
//                super.startActivity(intent);
//            }
//        }
//        else {
//            ComponentName componentName = new ComponentName(this, LoginActivity.class);
//            Intent intent2 = new Intent();
//            intent2.putExtra("classNameMain", intent.getComponent().getClassName());
//            intent2.setComponent(componentName);
//            intent2.setAction("maingotologin");
//            super.startActivity(intent2);
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switchView(v);
    }
}



