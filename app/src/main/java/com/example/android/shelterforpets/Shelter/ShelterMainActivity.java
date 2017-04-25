package com.example.android.shelterforpets.Shelter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.shelterforpets.Authentication.LogInActivity;
import com.example.android.shelterforpets.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.android.shelterforpets.Authentication.LogInActivity.mFirebaseAuth;

public class ShelterMainActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener mAuthStateListener;
    String userID;

    private static final int RC_SIGNIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_main);

        // TODO: 25-04-2017 lsa hyt3mal
        Button viewEvents = (Button) findViewById(R.id.shelter_view_events);

        Button viewRequests = (Button) findViewById(R.id.shelter_view_request);
        viewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShelterMainActivity.this, ReceivedRequests.class);
                i.putExtra("user", userID);
                startActivity(i);
            }
        });

        Button signOut = (Button) findViewById(R.id.shelter_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInActivity.mFirebaseAuth.signOut();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {

                    userID = user.getUid();
                    // User is signed in
                    Toast.makeText(ShelterMainActivity.this, "User signed in", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                    startActivityForResult(new Intent(ShelterMainActivity.this, LogInActivity.class),
                            RC_SIGNIN);
                }
            }
        };
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
        }
    }
}
