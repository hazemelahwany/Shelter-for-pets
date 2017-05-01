package com.example.android.shelterforpets.User;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.shelterforpets.Authentication.LogInActivity;
import com.example.android.shelterforpets.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PetDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        final String userId = getIntent().getStringExtra("userID");
        final String petID = getIntent().getStringExtra("pet_id");

        FirebaseUser user = LogInActivity.mFirebaseAuth.getCurrentUser();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference petsDatabase = mFirebaseDatabase.getReference().child("Pets").child(userId).child(petID);

        final ImageView petImage = (ImageView) findViewById(R.id.pet_details_image);
        final TextView petName = (TextView) findViewById(R.id.pet_details_name);
        final TextView petType = (TextView) findViewById(R.id.pet_details_type);
        final TextView petBreed = (TextView) findViewById(R.id.pet_details_breed);
        final TextView petGender = (TextView) findViewById(R.id.pet_details_gender);
        final TextView petAge = (TextView) findViewById(R.id.pet_details_age);
        ImageButton editPet = (ImageButton) findViewById(R.id.pet_details_edit_pet);

        if (!userId.equals(user.getUid())) {
            editPet.setVisibility(View.GONE);
        } else {
            petsDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    petName.setText(dataSnapshot.child("petName").getValue(String.class));
                    petType.setText(dataSnapshot.child("petType").getValue(String.class));
                    petBreed.setText(dataSnapshot.child("petBreed").getValue(String.class));
                    petGender.setText(dataSnapshot.child("petGender").getValue(String.class));
                    petAge.setText(dataSnapshot.child("petAge").getValue(String.class) + " Years old");

                    Glide.with(getApplicationContext())
                            .load(dataSnapshot.child("photoUrl").getValue(String.class))
                            .into(petImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            editPet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PetDetailsActivity.this, EditPet.class);
                    intent.putExtra("userID", userId);
                    intent.putExtra("petID", petID);
                    startActivity(intent);
                }
            });
        }
    }
}
