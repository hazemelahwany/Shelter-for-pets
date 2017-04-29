package com.example.android.shelterforpets.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.shelterforpets.Fragments.ShowEventsFragment;
import com.example.android.shelterforpets.R;

public class ShowEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        if (savedInstanceState == null) {
            ShowEventsFragment showEventsFragment = new ShowEventsFragment();
            getFragmentManager().beginTransaction().add(R.id.eventsContainer, showEventsFragment).commit();
        }
    }
}
