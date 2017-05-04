package com.salmaali.app.petspot.User;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salmaali.app.petspot.Adapters.LostPetsImageAdapter;
import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.R;

import java.util.ArrayList;

public class UserShowRequests extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private LostPetsImageAdapter adapter;
    private ArrayList<String> lostPets;
    private ArrayList<String> lostPetsLocations;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference lostPetsDatabase;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu_item) {
            firebaseAuth.signOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_requests);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        lostPetsDatabase = mFirebaseDatabase.getReference().child("Shelters");

        lostPets = new ArrayList<>();
        lostPetsLocations = new ArrayList<>();
        adapter = new LostPetsImageAdapter(this, R.layout.requests_list_item, lostPets);

        final ListView lostPetsList = (ListView) findViewById(R.id.requests_list);

        final ArrayList<String> sheltersWithRequests = new ArrayList<>();
        lostPetsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shelter : dataSnapshot.getChildren()) {
                    if (shelter.hasChild("Requests")) {
                        for (DataSnapshot request : shelter.child("Requests").getChildren()) {
                            lostPets.add(request.child("downloadURL").getValue(String.class));
                            lostPetsLocations.add((request.child("location").getValue(String.class)));
                        }
                        lostPetsList.setAdapter(adapter);
                    }
                }
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

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    startActivityForResult(new Intent(UserShowRequests.this, LogInActivity.class),
                            RC_SIGNIN);
                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGNIN) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {

                finish();
            }
        }
    }
}
