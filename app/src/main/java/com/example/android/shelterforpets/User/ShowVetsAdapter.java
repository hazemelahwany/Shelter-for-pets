package com.example.android.shelterforpets.User;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.shelterforpets.R;

import java.util.ArrayList;

public class ShowVetsAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> adapter;

    public ShowVetsAdapter(Context c, int layoutResourceId, ArrayList<String> adapter) {
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
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.vets_list_item, parent, false);
        }
        TextView vetName = (TextView) convertView.findViewById(R.id.show_vet_name_tv);
        TextView vetAddress = (TextView) convertView.findViewById(R.id.show_vet_address_tv);
        TextView vetPhone = (TextView) convertView.findViewById(R.id.show_vet_number_tv);
        TextView vetTime = (TextView) convertView.findViewById(R.id.show_vet_time_tv);

        String s = getItem(position);
        String[] sArray = s.split("%%");

        vetName.setText(sArray[0]);
        vetAddress.setText(sArray[1]);
        vetPhone.setText(sArray[2]);
        vetTime.setText(sArray[3]);

        return convertView;
    }
}
