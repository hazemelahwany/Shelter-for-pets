package com.salmaali.app.petspot.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.salmaali.app.petspot.Adapters.EventAdapter;
import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowEventsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu_item) {
            LogInActivity.mFirebaseAuth.signOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_events, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference eventsDatabase = mFirebaseDatabase.getReference().child("Events");

        final ListView events = (ListView) rootView.findViewById(R.id.events_list);
        final ArrayList<String> eventsList = new ArrayList<>();
        final EventAdapter eventAdapter = new EventAdapter(getActivity(), R.layout.events_list_item, eventsList);

        eventsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot event : dataSnapshot.getChildren()) {
                    String s = event.child("eventName").getValue(String.class) + "%%"
                            + event.child("eventDescription").getValue(String.class) + "%%"
                            + event.child("eventDate").getValue(String.class) + "%%"
                            + event.child("eventTime").getValue(String.class) + "%%"
                            + event.child("eventPhoto").getValue(String.class);
                    eventsList.add(s);
                }
                events.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }

}
