package com.example.android.shelterforpets.User;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.example.android.shelterforpets.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.android.shelterforpets.Authentication.LogInActivity.mFirebaseAuth;

public class UserMainActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private static final int RC_EMAIL_VERIFY = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // Acquire a reference to the system Location Manager
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String loc;
    private double longitude;
    private double latitude;


    private ImageView takenPic;
    private TextView locationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);


        Button signout = (Button) findViewById(R.id.signout_button);
        ImageButton captureAnimal = (ImageButton) findViewById(R.id.capture_animal);
        takenPic = (ImageView) findViewById(R.id.taken_pic);
        locationTv = (TextView) findViewById(R.id.location_tv);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                loc = "Longitude: " + longitude + "\n"
                        + "Latitude: " + latitude;
                locationTv.setText(loc);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: 17-04-2017 check if location enabled
                Toast.makeText(this, "Check your Location and Network Settings", Toast.LENGTH_LONG).show();
            } else {
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                takenPic.setImageBitmap(imageBitmap);
            }


        }
    }
}
