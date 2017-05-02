package com.salmaali.app.petspot.Adapters;


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
import com.salmaali.app.petspot.R;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> adapter;

    public EventAdapter(Context c, int layoutResourceId, ArrayList<String> adapter) {
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
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.events_list_item, parent, false);
        }
        ImageView viewEventPhoto = (ImageView) convertView.findViewById(R.id.view_event_photo);
        TextView viewEventName = (TextView) convertView.findViewById(R.id.view_event_name);
        TextView viewEventDescription = (TextView) convertView.findViewById(R.id.view_event_description);
        TextView viewEventDate = (TextView) convertView.findViewById(R.id.view_event_date);
        TextView viewEventTime = (TextView) convertView.findViewById(R.id.view_event_time);

        String s = getItem(position);
        String[] sArray = s.split("%%");

        viewEventName.setText(sArray[0]);
        viewEventDescription.setText(sArray[1]);
        viewEventDate.setText(sArray[2]);
        viewEventTime.setText(sArray[3]);

        if (!sArray[4].equals("null")) {
            Glide.with(viewEventPhoto.getContext())
                    .load(sArray[4])
                    .into(viewEventPhoto);
        }

        return convertView;
    }
}
