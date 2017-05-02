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

import com.salmaali.app.petspot.DatabaseObjects.Pet;
import com.salmaali.app.petspot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddPet extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 1;

    Uri selectedImageUri;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        Intent i = getIntent();
        userID = i.getStringExtra("userID");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        final DatabaseReference petsDatabase = mFirebaseDatabase.getReference().child("Pets").child(userID);
        final StorageReference petsStorage = storage.getReference().child("pets");

        ImageView petPic = (ImageView) findViewById(R.id.add_pet_image);
        final EditText petName = (EditText) findViewById(R.id.add_pet_name);
        final EditText petType = (EditText) findViewById(R.id.add_pet_type);
        final EditText petGender = (EditText) findViewById(R.id.add_pet_gender);
        final EditText petAge = (EditText) findViewById(R.id.add_pet_age);
        final EditText petBreed = (EditText) findViewById(R.id.add_pet_breed);
        ImageButton addPet = (ImageButton) findViewById(R.id.add_pet_button);

        petPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        addPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (petName.getText().toString().equals("") || petType.getText().toString().equals("")
                        || petGender.getText().toString().equals("") || petAge.getText().toString().equals("")
                        || petBreed.getText().toString().equals("")) {
                    Toast.makeText(AddPet.this, R.string.check_all_fields, Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedImageUri != null) {
                        final String uuid = UUID.randomUUID().toString();
                        StorageReference photoRef = petsStorage.child(uuid);

                        UploadTask uploadTask = photoRef.putFile(selectedImageUri);
                        final ProgressDialog progress = ProgressDialog.show(AddPet.this, "Adding Pet",
                                "Uploading Photo and Adding Pet", true);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddPet.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Pet pet = new Pet(downloadUrl.toString(), petName.getText().toString(),
                                        petType.getText().toString(), petBreed.getText().toString(),
                                        petAge.getText().toString(), petGender.getText().toString(),
                                        userID);
                                petsDatabase.child(uuid).setValue(pet);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
        }
    }
}
