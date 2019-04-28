package com.liszt.wesee.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.liszt.wesee.activity.AccountActivity;
import com.liszt.wesee.R;
import com.liszt.wesee.activity.SettingActivity;


public class MineFragment extends Fragment{
    // 缓存Fragment view
    SharedPreferences sharedPreferences;
    ListView list_mine;
    ArrayAdapter adapter;

    private View rootView;
    private static MineFragment mineFragment;
    public MineFragment(){}
    public static MineFragment getNewInstance(){
        if (mineFragment ==null){
            mineFragment =new MineFragment();
        }
        return mineFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    } public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("Cookies_Prefs",getActivity().MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        String strArr[] = new String[]{"账号管理                                                   "+username, "我的订单", "设置"};
        list_mine =  getView(). findViewById(R.id.list_mine);
         adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, strArr);
        list_mine.setAdapter(adapter);
        list_mine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id == 0){
                        Intent intent = new Intent(getActivity(),AccountActivity.class);
                        startActivity(intent);
                    }
                    else if (id ==2){
                    Intent intent = new Intent(getActivity(),SettingActivity.class);
                    startActivity(intent);
                }

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
