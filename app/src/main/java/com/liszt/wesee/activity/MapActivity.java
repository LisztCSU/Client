package com.liszt.wesee.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.liszt.wesee.R;
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
import java.util.zip.Inflater;

import Cookies.PersistentCookieStore;
import adapter.NearbyListAdapter;
import bean.nearbyListBean;

public class MapActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap =null;
    private ListView listView = null;
    private Button refresh = null;
    private LocationClient mLocationClient;
    private boolean isFirstLocation;
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



        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);


        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,1);
        }else {
            mBaiduMap.setMyLocationEnabled(true);
            mLocationClient = new LocationClient(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            mLocationClient.setLocOption(option);
            isFirstLocation = true;
            MyLocationListener myLocationListener = new MyLocationListener();
            mLocationClient.registerLocationListener(myLocationListener);
            mLocationClient.start();
        }
        refresh = (Button) findViewById(R.id.bt_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mid = sharedPreferences.getString("mid","0");
                MyThread2 thread = new  MapActivity.MyThread2(uid,mid);
                thread.start();

            }
        });
        listView = (ListView) findViewById(R.id.user_nearby);
        adapter = new NearbyListAdapter(MapActivity.this, dataList,
                R.layout.nearby_list, from,
                new int[] {R.id.user_info,R.id.bt_invite});
        listView.setAdapter(adapter);

        }
    public void initDataList(List<Map<String,Object>> list,List<nearbyListBean> beanList){

        for(nearbyListBean bean : beanList){
            Map<String,Object> map = new HashMap<>();

            map.put(from[0],bean.getNickname()+"@"+bean.getUsername());
            map.put(from[1],bean.getUid2()+"@"+bean.getMid());

            list.add(map);
        }
        adapter.notifyDataSetChanged();


    }
    public void addMarkers(List<nearbyListBean> beanList){
        for(nearbyListBean bean:beanList) {
            LatLng latLng = new LatLng(bean.getLatitude(),bean.getLongitude());
            View view = getLayoutInflater().inflate(R.layout.marker, null);
            TextView textView = (TextView) view.findViewById(R.id.marker_nickname);
            textView.setText(bean.getNickname());

            mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromView(view)));
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();

    }
    @Override
    protected void onPause() {

        //在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时必须调用mMapView.onDestroy()
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }
            if (isFirstLocation) {
                MapStatusUpdate update = null;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.e("lyl", "update:" + latLng);

                update = MapStatusUpdateFactory.zoomTo(16f);
                mBaiduMap.animateMapStatus(update);

                update = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(update);

//            update = MapStatusUpdateFactory.zoomTo(16f);
//            baiduMap.animateMapStatus(update);
                isFirstLocation = false;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("lyl", "update:" + latLng1);
            mBaiduMap.setMyLocationData(locData);

            new MapActivity.MyThread(uid, location.getLongitude(),location.getLatitude()).start();

        }
    }
    class MyThread extends Thread {
        private String uid;
        private double longitude;
        private double latitude;
;

        public MyThread(String uid, double longitude, double latitude ) {
            this.uid = uid;
            this.longitude = longitude;
            this.latitude = latitude;

        }

        @Override
        public void run() {
            EasyHttp.get("location/add").params("uid",uid).params("longitude",longitude+"").params("latitude",latitude+"").execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(MapActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == -1) {
                            Toast.makeText(MapActivity.this, "未登录", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (code == 0) {

                            Toast.makeText(MapActivity.this, "更新位置失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

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
                   Toast.makeText(MapActivity.this, "请求失败", Toast.LENGTH_LONG).show();
               }

               @Override
               public void onSuccess(String result) {
                   try {
                       JSONObject obj = new JSONObject(result);
                       int code = obj.optInt("code");
                       if (code == 1) {
                           Toast.makeText(MapActivity.this, "刷新列表成功", Toast.LENGTH_LONG).show();
                           myBeanList.clear();
                           dataList.clear();
                           JSONArray dataObj = obj.getJSONArray("dataList");
                           if (dataObj != null) {
                               int size = dataObj.length();

                               Intent intent = getIntent();
                               String mid = intent.getStringExtra("mid");
                               for(int i = 0;i<size;i++){
                                   JSONObject json = (JSONObject) dataObj.getJSONObject(i);

                                   myBeanList.add(new nearbyListBean(json.getString("uid2"),
                                                                     json.getString("username"),
                                                                     json.getString("nickname"),
                                                                     Double.parseDouble(json.getString("longitude")),
                                                                     Double.parseDouble(json.getString("latitude")),
                                                                     mid));

                               }
                               addMarkers(myBeanList);
                                initDataList(dataList,myBeanList);
                           }


                       } else {
                           Toast.makeText(MapActivity.this, "获取列表失败", Toast.LENGTH_LONG).show();
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           });

       }


  }
    
}
