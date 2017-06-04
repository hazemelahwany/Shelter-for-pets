package com.salmaali.app.petspot.User;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.Authentication.StarterActivity;
import com.salmaali.app.petspot.DatabaseObjects.LostPet;
import com.salmaali.app.petspot.R;
import com.salmaali.app.petspot.TrackGPS;
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

public class UserMainActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private static final int RC_EMAIL_VERIFY = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int MY_REQUEST_CODE = 4;

    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private StorageReference lostPetsRef;
    private DatabaseReference sheltersDatabase;


    private double longitude;
    private double latitude;
    private Location currentLocation;
    private float minimumDistance;
    private String shelterID;
    protected static FirebaseAuth firebaseAuth;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu_item) {
            FirebaseAuth.getInstance().signOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        sheltersDatabase = mFirebaseDatabase.getReference().child("Shelters");
        lostPetsRef = storage.getReference().child("lost_pets");

        ImageButton captureAnimal = (ImageButton) findViewById(R.id.capture_animal);
        ImageButton showEvents = (ImageButton) findViewById(R.id.user_show_events);
        ImageButton showVets = (ImageButton) findViewById(R.id.user_show_vets);
        ImageButton showUsers = (ImageButton) findViewById(R.id.user_show_users);
        ImageButton profile = (ImageButton) findViewById(R.id.user_profile);
        ImageButton showRequest = (ImageButton) findViewById(R.id.user_show_requests);

        showEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMainActivity.this, ShowEventsActivity.class));
            }
        });

        showVets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMainActivity.this, ShowVetsActivity.class));
            }
        });

        showUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserMainActivity.this, ShowUsersActivity.class);
                i.putExtra("userID", firebaseAuth.getCurrentUser().getUid());
                startActivity(i);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserMainActivity.this, UserProfileActivity.class);
                i.putExtra("userID", firebaseAuth.getCurrentUser().getUid());
                startActivity(i);
            }
        });

        showRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMainActivity.this, UserShowRequests.class));
            }
        });

        TrackGPS gps = new TrackGPS(UserMainActivity.this);
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


        captureAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out
                    if (!StarterActivity.fb) {
                        startActivityForResult(new Intent(UserMainActivity.this, LogInActivity.class),
                                RC_SIGNIN);
                    } else {
                        if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
                            LoginManager.getInstance().logOut();
                        }
                        startActivityForResult(new Intent(UserMainActivity.this, StarterActivity.class),
                                RC_SIGNIN);
                    }
                }
            }
        };

    }

    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_REQUEST_CODE);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
            else {
                // Your app will not have this permission. Turn off all functions
                Toast.makeText(UserMainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();

            }
        }
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
        } else if (requestCode == RC_EMAIL_VERIFY && resultCode == RESULT_OK) {
            firebaseAuth.getCurrentUser().sendEmailVerification()
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
                    Toast.makeText(UserMainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    LostPet lostPet = new LostPet(downloadUrl.toString(), "" + longitude + "," + latitude);
                    sheltersDatabase.child(shelterID).child("Requests").child(uuid).setValue(lostPet);
                    Toast.makeText(UserMainActivity.this, "Sent to nearest shelter", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
