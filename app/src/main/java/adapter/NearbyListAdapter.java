package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.SimpleAdapter;


import com.liszt.wesee.R;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyListAdapter extends SimpleAdapter {
    private Context mcontext = null;
    private SharedPreferences sharedPreferences;


    public NearbyListAdapter(Context context,
                            List<? extends Map<String, ?>> data, int resource,
                            String[] from, int[] to) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mcontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
         View view =super.getView(position, convertView, parent);
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
        Button invite = (Button) view.findViewById(R.id.bt_invite);
        invite.setText("邀请");
        sharedPreferences = mcontext.getSharedPreferences("Cookies_Prefs",mcontext.MODE_PRIVATE);
        if (sharedPreferences.getString("uid", "0").equals("0")) {
           invite.setEnabled(false);
        }
        invite.setEnabled(true);
        String  uid = sharedPreferences.getString("uid", "0");
        String arr[] = map.get("invite").toString().split("@");
        String uid2 = arr[0];
        String   mid = arr[1];
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        return view;
    }
}
