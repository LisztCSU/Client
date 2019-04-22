package adapter;
import java.util.List;

import com.liszt.wesee.R;
import com.liszt.wesee.activity.ChangeMobileActivity;


import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.accountListBean;



public class AccountListAdapter extends ArrayAdapter {
    private final int ImageId;

    public AccountListAdapter(Context context, int endImage, List<accountListBean> obj){
        super(context,endImage,obj);
        ImageId = endImage;//这个是传入我们自己定义的界面

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        accountListBean bean = (accountListBean) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(ImageId,parent,false);//这个是实例化一个我们自己写的界面Item
        LinearLayout linearLayout = view.findViewById(R.id.ll_view);
        ImageView endImage = view.findViewById(R.id.icon_end);
        TextView headText = view.findViewById(R.id.list_item_title);
        TextView endText=    view.findViewById(R.id.list_item_info);
        endImage.setImageResource(bean.getImageID());
        headText.setText(bean.getTitle());
        endText.setText(bean.getInfo());
        final ComponentName[] componentNames = (bean.getComponentName());
        linearLayout.setOnClickListener(new View.OnClickListener() {//检查哪一项被点击了
            @Override
            public void onClick(View view) {
                  if(position != 0) {
                      Intent intent = new Intent();
                      intent.setComponent(componentNames[position]);
                      getContext().startActivity(intent);
                  }



               }

        });
        return view;
    }
}
