package com.salmaali.app.petspot.User;


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

public class PetAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> adapter;

    public PetAdapter(Context c, int layoutResourceId, ArrayList<String> adapter) {
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

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.pets_list_item, parent, false);
        }
        String s = getItem(position);
        String[] sArray = s.split("%%");
        ImageView petPhoto = (ImageView) convertView.findViewById(R.id.pet_image_view);
        TextView petName = (TextView) convertView.findViewById(R.id.pet_name);
        TextView petType = (TextView) convertView.findViewById(R.id.pet_type);
        TextView petBreed = (TextView) convertView.findViewById(R.id.pet_breed);

        petName.setText(sArray[0]);
        petType.setText(sArray[1]);
        petBreed.setText(sArray[2]);

        if (!sArray[3].equals("null")) {
            Glide.with(petPhoto.getContext())
                    .load(sArray[3])
                    .into(petPhoto);
        }
        return convertView;
    }
}
