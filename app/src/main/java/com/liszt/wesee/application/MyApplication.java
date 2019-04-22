package com.liszt.wesee.application;

import android.app.Application;


import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.liszt.wesee.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cookie.CookieManger;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        EasyHttp.init(this);
        EasyHttp.getInstance().setBaseUrl(getString(R.string.weseeUrl))
                .debug("EasyHttp")
                .setRetryCount(3)
                .setConnectTimeout(3000)
                .setCookieStore(new CookieManger(this));
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).imageDownloader(
                new BaseImageDownloader(this, 60 * 1000, 60 * 1000)) // connectTimeout超时时间
                .build();
        ImageLoader.getInstance().init(config);
    }

}