package com.liszt.wesee.activity;
import com.liszt.wesee.R;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import adapter.NearbyListAdapter;
import bean.nearbyListBean;


public class TestActivity extends AppCompatActivity {
    private ListView listView = null;
    private Button refresh = null;
    private NearbyListAdapter adapter;
    private static final String from[] ={"info","invite"};
    private List<nearbyListBean> myBeanList = new ArrayList<>();
    List<Map<String, Object>> dataList = new ArrayList<>();

    SharedPreferences sharedPreferences;
    String uid;
    String mid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","0");
        Intent intent = getIntent();
        mid = intent.getStringExtra("mid");
        setContentView(R.layout.activity_test);
        refresh = (Button) findViewById(R.id.bt_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestActivity.MyThread2(uid,mid).start();

//                for(nearbyListBean bean:myBeanList){
//                    LatLng latLng = new LatLng(bean.getLatitude(),bean.getLongitude());
//                    mBaiduMap.addOverlay(new MarkerOptions().position(latLng));
//                    mBaiduMap.addOverlay(new TextOptions().text(bean.getNickname()).fontSize(12).position(latLng));
//                }
            }
        });

        listView = (ListView) findViewById(R.id.user_nearby);

        adapter = new NearbyListAdapter(TestActivity.this, dataList,
                R.layout.nearby_list, from,
                new int[] {R.id.user_info,R.id.bt_invite});
        listView.setAdapter(adapter);

    }

    public void initDataList( List<Map<String, Object>> dataList,List<nearbyListBean> beanList){
        dataList.clear();
        for(nearbyListBean bean : beanList){
            Map<String,Object> map = new HashMap<>();
            String str = bean.getNickname();
            map.put(from[0],bean.getNickname()+"@"+bean.getUsername());
            map.put(from[1],bean.getUid2()+"@"+bean.getMid());

           dataList.add(map);
        }
        adapter.notifyDataSetChanged();



    }





    class MyThread2 extends Thread{
        private  String uid;
        private  String mid;
        public MyThread2(String uid,String mid){
            this.uid = uid;
            this.mid = mid;
        }
        @Override
        public  void run(){
            EasyHttp.get("nearby/getNearbyList").params("uid",uid).params("mid",mid).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(TestActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Toast.makeText(TestActivity.this, "刷新列表成功", Toast.LENGTH_LONG).show();
                            JSONArray dataObj = obj.getJSONArray("dataList");
                            if (dataObj != null) {
                                int size = dataObj.length();

                                Intent intent = getIntent();
                                String mid = "26266893";
                                for(int i = 0;i<size;i++){
                                    JSONObject json = (JSONObject) dataObj.getJSONObject(i);
                                    myBeanList.add(new nearbyListBean(json.getString("uid2"),
                                            json.getString("username"),
                                            json.getString("nickname"),
                                            Double.parseDouble(json.getString("longitude")),
                                            Double.parseDouble(json.getString("latitude")),
                                            mid));

                                }
                                initDataList(dataList,myBeanList);

                            }


                        } else {
                            Toast.makeText(TestActivity.this, "获取列表失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }


    }

}
