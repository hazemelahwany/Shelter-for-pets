package com.salmaali.app.petspot.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.salmaali.app.petspot.DatabaseObjects.Pet;
import com.salmaali.app.petspot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditPet extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 1;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        final String userID = getIntent().getStringExtra("userID");
        final String petID = getIntent().getStringExtra("petID");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        final DatabaseReference petsDatabase = mFirebaseDatabase.getReference().child("Pets").child(userID).child(petID);
        final StorageReference petPics = storage.getReference().child("pets");

        final ImageView petImage = (ImageView) findViewById(R.id.edit_pet_pic);
        final EditText petName = (EditText) findViewById(R.id.edit_pet_name);
        final EditText petType = (EditText) findViewById(R.id.edit_pet_type);
        final EditText petBreed = (EditText) findViewById(R.id.edit_pet_breed);
        final EditText petGender = (EditText) findViewById(R.id.edit_pet_gender);
        final EditText petAge = (EditText) findViewById(R.id.edit_pet_age);
        ImageButton editPet = (ImageButton) findViewById(R.id.edit_pet_save_button);

        petsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petName.setText(dataSnapshot.child("petName").getValue(String.class));
                petType.setText(dataSnapshot.child("petType").getValue(String.class));
                petBreed.setText(dataSnapshot.child("petBreed").getValue(String.class));
                petGender.setText(dataSnapshot.child("petGender").getValue(String.class));
                petAge.setText(dataSnapshot.child("petAge").getValue(String.class));

                Glide.with(getApplicationContext())
                        .load(dataSnapshot.child("photoUrl").getValue(String.class))
                        .into(petImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        petImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        editPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (petName.getText().toString().equals("") || petType.getText().toString().equals("")
                        || petBreed.getText().toString().equals("") || petGender.getText().toString().equals("")
                        || petAge.getText().toString().equals("")){
                    Toast.makeText(EditPet.this, R.string.check_all_fields, Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedImageUri != null) {
                        StorageReference photoRef = petPics.child(petID);

                        UploadTask uploadTask = photoRef.putFile(selectedImageUri);
                        final ProgressDialog progress = ProgressDialog.show(EditPet.this, "Updating Pet",
                                "Updating", true);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditPet.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Pet pet = new Pet(downloadUrl.toString() ,petName.getText().toString(),
                                        petType.getText().toString(), petBreed.getText().toString(),
                                        petAge.getText().toString(), petGender.getText().toString(), userID);
                                petsDatabase.setValue(pet);
                                progress.dismiss();
                            }
                        });
                    } else {
                        Pet pet = new Pet("null", petName.getText().toString(),
                                petType.getText().toString(), petBreed.getText().toString(),
                                petAge.getText().toString(), petGender.getText().toString(),
                                userID);
                        petsDatabase.child(petName.getText().toString() + userID).setValue(pet);
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
        }
    }
}
