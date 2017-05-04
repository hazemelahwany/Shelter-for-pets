package com.salmaali.app.petspot.Adapters;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.salmaali.app.petspot.R;

import java.util.ArrayList;

public class LostPetsImageAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> adapter;


    public LostPetsImageAdapter(Context c, int layoutResourceId, ArrayList<String> adapter) {
        super(c, layoutResourceId , adapter);
        mContext = c;
        this.adapter = new ArrayList<>();
        this.adapter = adapter;
    }

    void setGridData(ArrayList<String> adapter) {
        this.adapter = adapter;
        notifyDataSetChanged();
    }

    public int getCount() {
        return adapter.size();
    }

    public String getItem(int position) {
        return adapter.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.requests_list_item, parent, false);
        }
        imageView = (ImageView) convertView.findViewById(R.id.lost_pet_image_view);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);

        String url = getItem(position);

        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);

        return convertView;
    }
}
