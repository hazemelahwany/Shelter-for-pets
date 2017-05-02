package com.salmaali.app.petspot.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.salmaali.app.petspot.Fragments.ShowEventsFragment;
import com.salmaali.app.petspot.R;

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
