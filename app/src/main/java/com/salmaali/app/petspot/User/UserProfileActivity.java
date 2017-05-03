package com.salmaali.app.petspot.User;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent i = getIntent();
        userID = i.getStringExtra("userID");

        FirebaseUser user = LogInActivity.mFirebaseAuth.getCurrentUser();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userDatabase = mFirebaseDatabase.getReference().child("Users").child(userID);
        DatabaseReference petsDatabase = mFirebaseDatabase.getReference().child("Pets").child(userID);

        final TextView user_name = (TextView) findViewById(R.id.user_profile_name);
        final TextView userPetsExistance = (TextView) findViewById(R.id.pets_existance);
        final ListView petsListView = (ListView) findViewById(R.id.user_profile_pets_list);
        final ImageView user_picture = (ImageView) findViewById(R.id.user_profile_picture);
        ImageButton editProfile = (ImageButton) findViewById(R.id.user_profile_edit_button);
        ImageButton addPet = (ImageButton) findViewById(R.id.user_profile_add_pet);
        final TextView volunteerStatus = (TextView) findViewById(R.id.user_profile_volunteering_status);

        if (!userID.equals(user.getUid())) {
            editProfile.setVisibility(View.GONE);
            addPet.setVisibility(View.GONE);
        }

        final ArrayList<String> petsList = new ArrayList<>();
        final PetAdapter adapter = new PetAdapter(this, R.layout.pets_list_item, petsList);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("firstName").getValue(String.class) + " "
                        + dataSnapshot.child("lastName").getValue(String.class);
                user_name.setText(name);
                boolean vStatus = dataSnapshot.child("volunteer").getValue(Boolean.class);
                if (vStatus) {
                    volunteerStatus.setText(R.string.volunteer_true);
                } else {
                    volunteerStatus.setText(R.string.volunteer_false);
                }
                if (!dataSnapshot.child("photoUrl").getValue(String.class).equals("null")) {
                    Glide.with(getApplicationContext())
                            .load(dataSnapshot.child("photoUrl").getValue(String.class))
                            .into(user_picture);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        petsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    userPetsExistance.setVisibility(View.GONE);
                    petsListView.setVisibility(View.VISIBLE);
                    for (DataSnapshot pet : dataSnapshot.getChildren()) {
                        String s = pet.child("petName").getValue(String.class) + "%%"
                                + pet.child("petType").getValue(String.class) + "%%"
                                + pet.child("petBreed").getValue(String.class) + "%%"
                                + pet.child("photoUrl").getValue(String.class) + "%%"
                                + pet.getKey();

                        petsList.add(s);
                    }
                    petsListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        petsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pet = adapter.getItem(i).split("%%")[4];
                Intent intent = new Intent(UserProfileActivity.this, PetDetailsActivity.class);
                intent.putExtra("pet_id", pet);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        addPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, AddPet.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }
}
