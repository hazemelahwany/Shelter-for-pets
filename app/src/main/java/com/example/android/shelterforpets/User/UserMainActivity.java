package com.example.android.shelterforpets.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shelterforpets.Authentication.LogInActivity;
import com.example.android.shelterforpets.DatabaseObjects.LostPet;
import com.example.android.shelterforpets.R;
import com.example.android.shelterforpets.TrackGPS;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.example.android.shelterforpets.Authentication.LogInActivity.mFirebaseAuth;

public class UserMainActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private static final int RC_EMAIL_VERIFY = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private StorageReference lostPetsRef;
    private DatabaseReference sheltersDatabase;


    private double longitude;
    private double latitude;
    private TrackGPS gps;
    private Location currentLocation;
    private float minimumDistance;
    private String shelterID;


    private ImageView takenPic;
    private TextView locationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        sheltersDatabase = mFirebaseDatabase.getReference().child("Shelters");
        lostPetsRef = storage.getReference().child("lost_pets");

        Button signout = (Button) findViewById(R.id.signout_button);
        ImageButton captureAnimal = (ImageButton) findViewById(R.id.capture_animal);
        takenPic = (ImageView) findViewById(R.id.taken_pic);
        locationTv = (TextView) findViewById(R.id.location_tv);

        gps = new TrackGPS(UserMainActivity.this);
        currentLocation = new Location("");
        minimumDistance = 0;

        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);
        } else {
            gps.showSettingsAlert();
        }

        sheltersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot shelter : dataSnapshot.getChildren()) {
                        String location = shelter.child("shelterAddress").getValue(String.class);
                        double lng = Double.parseDouble(location.split(",")[0]);
                        double lat = Double.parseDouble(location.split(",")[1]);
                        Location shelterLocation = new Location("");
                        shelterLocation.setLatitude(lat);
                        shelterLocation.setLongitude(lng);
                        float currentDistance = shelterLocation.distanceTo(currentLocation);
                        if (minimumDistance == 0) {
                            minimumDistance = currentDistance;
                            shelterID = shelter.getKey();
                        } else {
                            if (currentDistance < minimumDistance) {
                                minimumDistance = currentDistance;
                                shelterID = shelter.getKey();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
            }
        });

        captureAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if (user.isEmailVerified()) {
                        Toast.makeText(UserMainActivity.this, "User signed in", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        startActivityForResult(new Intent(UserMainActivity.this, LogInActivity.class),
                                RC_EMAIL_VERIFY);
                    }
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                    startActivityForResult(new Intent(UserMainActivity.this, LogInActivity.class),
                            RC_SIGNIN);
                }
            }
        };

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGNIN) {
            if (resultCode == RESULT_OK) {
                Log.v("signin", "signed in");
            } else if (resultCode == RESULT_CANCELED) {
                Log.v("signin", "signed in cancelled");
                finish();
            }
        } else if (requestCode == RC_EMAIL_VERIFY && resultCode == RESULT_OK) {
            mFirebaseAuth.getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UserMainActivity.this,
                                    R.string.verify_email_toast, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataArray = baos.toByteArray();

            final String uuid = UUID.randomUUID().toString();
            StorageReference photoRef = lostPetsRef.child(uuid);
            UploadTask uploadTask = photoRef.putBytes(dataArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    LostPet lostPet = new LostPet(downloadUrl.toString(), "" + longitude + "," + latitude);
                    sheltersDatabase.child(shelterID).child("Requests").child(uuid).setValue(lostPet);
                }
            });
            takenPic.setImageBitmap(imageBitmap);
            String loc = "Longitude: " + longitude + "\n"
                    + "Latitude: " + latitude;
            locationTv.setText(loc);



        }
    }
}
