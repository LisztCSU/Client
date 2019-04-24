package com.liszt.wesee.activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import Cookies.PersistentCookieStore;
import adapter.AccountListAdapter;
import bean.accountListBean;


public class AccountActivity extends AppCompatActivity {

    private List<accountListBean> myBeanList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String uid;
    String username;
    String nickname;
    String mobile;
    Button bt;
    ListView listView;
    AccountListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        sharedPreferences = getSharedPreferences("Cookies_Prefs", MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "0");

//        if(uid.equals("0")) {
//            Log.v("nmsl:", uid);
//        }
//        else{Log.v("youdongxi:",uid);}
        new AccountActivity.MyThread(uid).start();
        listView = (ListView) super.findViewById(R.id.accout_list);
        adapter = new AccountListAdapter(AccountActivity.this, R.layout.account_list, myBeanList);
        listView.setAdapter(adapter);
        bt = (Button) findViewById(R.id.login_cancel);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AccountActivity.MyThread2(uid).start();
            }
        });

    }

    void init(String username, String nickname, String mobile, ComponentName[] componentNames) {
        accountListBean bean1 = new accountListBean("用户名", username, R.mipmap.icon_right, componentNames);
        myBeanList.add(bean1);
        accountListBean bean2 = new accountListBean("昵称", nickname, R.mipmap.icon_right, componentNames);
        myBeanList.add(bean2);
        accountListBean bean3 = new accountListBean("手机号", mobile, R.mipmap.icon_right, componentNames);
        myBeanList.add(bean3);
        accountListBean bean4 = new accountListBean("修改密码", "", R.mipmap.icon_right, componentNames);
        myBeanList.add(bean4);
        adapter.notifyDataSetChanged();
    }

    class MyThread extends Thread {

        private String uid;


        public MyThread(String uid) {
            this.uid = uid;
        }

        @Override
        public void run() {
            EasyHttp.get("user/getUser").params("uid", uid).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(AccountActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            JSONObject dataObj = obj.optJSONObject("data");
                            if (dataObj != null) {

                                mobile = dataObj.optString("mobile", "");
                                nickname = dataObj.optString("nickname", "");
                                username = dataObj.optString("username", "");
                                SharedPreferences.Editor eidtor = sharedPreferences.edit();
                                eidtor.putString("nickname", nickname);
                                eidtor.putString("mobile", mobile);
                                eidtor.apply();
                                if (mobile.equals("")) {
                                    ComponentName[] componentNames = {new ComponentName(AccountActivity.this, AccountActivity.class),
                                            new ComponentName(AccountActivity.this, ChangeNicknameActivity.class),
                                            new ComponentName(AccountActivity.this, BindMobileActivity.class),
                                            new ComponentName(AccountActivity.this, ChangePasswordActivity.class)};
                                    init(username, nickname, "未绑定", componentNames);
                                } else {
                                    ComponentName[] componentNames = {new ComponentName(AccountActivity.this, AccountActivity.class),
                                            new ComponentName(AccountActivity.this, ChangeNicknameActivity.class),
                                            new ComponentName(AccountActivity.this, ChangeMobileActivity.class),
                                            new ComponentName(AccountActivity.this, ChangePasswordActivity.class)};
                                    init(username, nickname, mobile, componentNames);
                                }
                            }

                        } else if (code == -1) {
                            Toast.makeText(AccountActivity.this, "未登录或用户不一致", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(AccountActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AccountActivity.this, "不存在该用户", Toast.LENGTH_LONG).show();
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

        public MyThread2(String uid) {
            this.uid = uid;
        }

        @Override
        public void run() {
            EasyHttp.get("user/loginCancel").params("uid", uid).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(AccountActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (code == -1) {
                            Toast.makeText(AccountActivity.this, "未登录或用户不一致", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AccountActivity.this, "不存在该用户", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
