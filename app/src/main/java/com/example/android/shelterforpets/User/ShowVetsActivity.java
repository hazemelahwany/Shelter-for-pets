package com.example.android.shelterforpets.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.shelterforpets.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowVetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vets);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference vetsDatabase = mFirebaseDatabase.getReference().child("Vets");

        final ListView vetsListView = (ListView) findViewById(R.id.vets_list);
        final ArrayList<String> vetsList = new ArrayList<>();
        final ShowVetsAdapter vetsAdapter = new ShowVetsAdapter(this, R.layout.vets_list_item, vetsList);

        vetsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot vet : dataSnapshot.getChildren()) {
                    String s = vet.child("vetName").getValue(String.class) + "\nDr. " +
                            vet.child("drName").getValue(String.class) + "%%"
                            + vet.child("vetAddress").getValue(String.class) + "%%"
                            + vet.child("telephoneNumber").getValue(String.class) + "%%"
                            + vet.child("vetOpenFrom").getValue(String.class) + " - "
                            + vet.child("vetOpenTo").getValue(String.class);
                    vetsList.add(s);
                }
                vetsListView.setAdapter(vetsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
