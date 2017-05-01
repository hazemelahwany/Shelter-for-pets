package com.example.android.shelterforpets.User;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.shelterforpets.R;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> adapter;

    public UserAdapter(Context c, int layoutResourceId, ArrayList<String> adapter) {
        super(c, layoutResourceId , adapter);
        mContext = c;
        this.adapter = new ArrayList<>();
        this.adapter = adapter;
    }

    @Override
    public int getCount() {
        return adapter.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return adapter.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.users_list_item, parent, false);
        }
        ImageView userPic = (ImageView) convertView.findViewById(R.id.user_list_item_pic);
        TextView userName = (TextView) convertView.findViewById(R.id.user_list_item_name);

        String s = getItem(position);
        String[] sArray = s.split("%%");

        userName.setText(sArray[0]);

        if (!sArray[1].equals("null")) {
            Glide.with(userPic.getContext())
                    .load(sArray[1])
                    .into(userPic);
        }
        return convertView;
    }
}
