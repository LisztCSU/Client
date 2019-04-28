package adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.liszt.wesee.R;
import com.liszt.wesee.activity.MapActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieListAdapter extends SimpleAdapter {
    private Context mcontext = null;
    private SharedPreferences sharedPreferences;
    private String uid;
    private String mid;

    public MovieListAdapter(Context context,
                            List<? extends Map<String, ?>> data, int resource,
                            String[] from, int[] to) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mcontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final View view = super.getView(position, convertView, parent);
        final ImageView image = (ImageView) view.findViewById(R.id.img_movie);
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
        Button mate = (Button) view.findViewById(R.id.bt_mate);
        mate.setText("约看");
        sharedPreferences = mcontext.getSharedPreferences("Cookies_Prefs",mcontext.MODE_PRIVATE);
        if (sharedPreferences.getString("uid", "0").equals("0")) {
            mate.setEnabled(false);
        }
        mate.setEnabled(true);
        uid = sharedPreferences.getString("uid", "0");
        mid = map.get("mate").toString();
        mate.setTag(mid);
        mate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyThread(uid,v.getTag().toString()).start();
                sharedPreferences.edit().putString("mid",v.getTag().toString()).apply();

            }
        });


        String urlString = map.get("img").toString();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_load)
                .showImageOnFail(R.mipmap.failure)
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间，可能会出现闪动
                .bitmapConfig(Bitmap.Config.RGB_565)//图片编码类型
                .build();

        ImageLoader.getInstance().displayImage(urlString, image, options);

        return view;
    }

    class MyThread extends Thread {
        private String uid;
        private String mid;

        public MyThread(String uid, String mid) {
            this.uid = uid;
            this.mid = mid;
        }

        @Override
        public void run() {
            EasyHttp.get("mate/wantTosee").params("uid", uid).params("mid", mid).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(mcontext, "操作失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Intent intent = new Intent(mcontext, MapActivity.class);
                            //sharedPreferences.edit().putString("mid",mid).apply();
                            mcontext.startActivity(intent);
                        } else if (code == 0) {
                            Toast.makeText(mcontext, "操作失败", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mcontext, "未登录", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }
}