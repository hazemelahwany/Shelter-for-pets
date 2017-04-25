package com.example.android.shelterforpets.Shelter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.shelterforpets.Authentication.LogInActivity;
import com.example.android.shelterforpets.DatabaseObjects.LostPet;
import com.example.android.shelterforpets.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReceivedRequests extends AppCompatActivity {

    private LostPetsImageAdapter adapter;
    private ArrayList<String> lostPets;
    private ArrayList<String> lostPetsLocations;
    private String userID;

    private DatabaseReference lostPetsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_requests);

        Intent i = getIntent();
        userID = i.getStringExtra("user");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        lostPetsDatabase = mFirebaseDatabase.getReference().child("Shelters").child(userID).child("Requests");

        lostPets = new ArrayList<>();
        lostPetsLocations = new ArrayList<>();
        adapter = new LostPetsImageAdapter(this, R.layout.requests_list_item, lostPets);

        final GridView lostPetsList = (GridView) findViewById(R.id.requests_list);

        lostPetsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot request : dataSnapshot.getChildren()) {
                    lostPets.add(request.child("downloadURL").getValue(String.class));
                    lostPetsLocations.add((request.child("location").getValue(String.class)));
                }
                lostPetsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lostPetsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                double lng = Double.parseDouble(lostPetsLocations.get(i).split(",")[0]);
                double lat = Double.parseDouble(lostPetsLocations.get(i).split(",")[1]);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String data = String.format("geo:%s,%s", lat, lng);
//                if (zoomLevel != null) {
//                    data = String.format("%s?z=%s", data, zoomLevel);
//                }
                intent.setData(Uri.parse(data));
                startActivity(intent);
            }
        });

    }
}
