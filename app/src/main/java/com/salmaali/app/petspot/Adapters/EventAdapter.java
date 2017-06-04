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

/**
 * The EventAdapter class is used to match events list in UI and Java code.
 * @author Hazem El Ahwany
 * @since 2017-04-15
 */

public class EventAdapter extends ArrayAdapter<String> {

    /**
     * Context to store the activity or fragment using this adapter.
     */
    private Context mContext;
    /**
     * ArrayList used to put data in the ListView.
     */
    private ArrayList<String> adapter;

    /**
     * Constructor.
     * @param c                 the activity or fragment that access this adapter.
     * @param layoutResourceId  the row item id source to match with layout file.
     * @param adapter           the ArrayList that contatins the data to fill the ListView.
     */
    public EventAdapter(Context c, int layoutResourceId, ArrayList<String> adapter) {
        super(c, layoutResourceId , adapter);
        mContext = c;
        this.adapter = new ArrayList<>();
        this.adapter = adapter;
    }

    /**
     * Get ListView rows number.
     * @return rows number
     */
    @Override
    public int getCount() {
        return adapter.size();
    }

    /**
     * get value in a specific row.
     * @param position  the amount should be row number
     * @return          the value in row
     */
    @Nullable
    @Override
    public String getItem(int position) {
        return adapter.get(position);
    }

    /**
     * The main method that fills data in each row.
     * @param position      this should be row number we are filling right now
     * @param convertView   this should be the View that contains the listItem
     * @param parent        this the parent view
     * @return              should return the view with proper data
     */
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
        } else {
            Glide.clear(viewEventPhoto);
            viewEventPhoto.setImageResource(R.drawable.app_logo);
    }

        return convertView;
    }
}
