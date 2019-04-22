package com.liszt.wesee.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.liszt.wesee.activity.LoginActivity;
import com.liszt.wesee.R;


public class MineLoginFragment extends Fragment{
    // 缓存Fragment view
    private View rootView;
    Button bt_login;
    private static MineLoginFragment mineLoginFragment;
    public MineLoginFragment(){}
    public static MineLoginFragment getNewInstance(){
        if (mineLoginFragment ==null){
            mineLoginFragment =new MineLoginFragment();
        }
        return mineLoginFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mine_login, container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        bt_login =rootView.findViewById(R.id.bt_login);
        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // The size for this PublisherAdView is defined in the XML layout as AdSize.FLUID. It could
        // also be set here by calling publisherAdView.setAdSizes(AdSize.FLUID).
        //
        // An ad with fluid size will automatically stretch or shrink to fit the height of its
        // content, which can help layout designers cut down on excess whitespace.



        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                //getActivity().onBackPressed();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
