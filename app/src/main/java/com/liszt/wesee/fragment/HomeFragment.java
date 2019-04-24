package com.liszt.wesee.fragment;



import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.liszt.wesee.R;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MovieListAdapter;
import bean.movieListBean;


public class HomeFragment extends Fragment{
    // 缓存Fragment view
    private View rootView;
    private ListView listview;
    private Context mContext;
    private List<movieListBean> myBeanList= new ArrayList<>();
    private MovieListAdapter adapter;
    private static final String from[] ={"img","title","info","mate","wantaccount"};
    List<Map<String, Object>> dataList = new ArrayList<>();
    private static HomeFragment homeFragment;
    public HomeFragment(){}
    public static HomeFragment getNewInstance(){
        if (homeFragment ==null){
            homeFragment =new HomeFragment();
        }
        return homeFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
//        if (Build.VERSION.SDK_INT >= 11) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
//        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listview = getView().findViewById(R.id.list_movie);
        new MyThread().start();
        adapter = new MovieListAdapter(mContext, dataList,
                R.layout.movie_list, from,
                new int[] { R.id.img_movie, R.id.movie_title, R.id.movie_info,R.id.bt_mate,R.id.wantaccount});

//        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
//
//            @Override
//            public boolean setViewValue(View view, Object data,
//                                        String textRepresentation) {
//                if (view instanceof ImageView && data instanceof Bitmap) {
//                    ImageView iv = (ImageView) view;
//                    iv.setImageBitmap((Bitmap) data);
//                    return true;
//                }
//                return false;
//            }
//        });



        listview.setAdapter(adapter);

    }
    public List<Map<String,Object>> initDataList(List<movieListBean> beanList){
        List<Map<String,Object>> list = new ArrayList<>();
        for(movieListBean bean : beanList){
            Map<String,Object> map = new HashMap<>();
            map.put(from[0],bean.getImgUrl());
            map.put(from[1],bean.getTitle());
            map.put(from[2],bean.getStar()+"\n豆瓣:"+bean.getScore()+"\n时长:"+bean.getDuration()+"\n"+bean.getRegion()+"\n导演:"+bean.getDirector()+"\n演员:"+bean.getActors());
            map.put(from[3],bean.getId());
            map.put(from[4],bean.getWantaccount()+"人想看");
            list.add(map);
        }
        adapter.notifyDataSetChanged();

        return list;

    }
//    public Bitmap returnBitMap( final String url) {
//        bitmap = null;
//        myFileUrl = null;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    myFileUrl = new URL(url);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
//                            .openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//                    InputStream is = conn.getInputStream();
//                    bitmap = BitmapFactory.decodeStream(is);
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//                });
//
//
//        return bitmap;
//    }
    class MyThread extends Thread {



        @Override
        public void run() {
            EasyHttp.get("movie/getMovieList").execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(mContext, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            JSONArray dataObj = obj.getJSONArray("dataList");
                            if (dataObj != null) {
                                int size = dataObj.length();


                                for(int i = 0;i<size;i++){
                                JSONObject json = (JSONObject) dataObj.getJSONObject(i);
                                  myBeanList.add(new movieListBean(
                                          json.getString("id"),
                                          json.getString("title"),
                                          json.getString("score"),
                                          json.getString("star"),
                                          json.getString("duration"),
                                          json.getString("votecount"),
                                          json.getString("region"),
                                          json.getString("director"),
                                          json.getString("actors"),
                                          json.getString("imgUrl"),
                                          json.getString("wantcount"))) ;
                                }

                                dataList = initDataList(myBeanList);
                            }


                        } else {
                            Toast.makeText(mContext, "获取列表失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
