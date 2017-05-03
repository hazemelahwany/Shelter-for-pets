package com.salmaali.app.petspot.Admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.salmaali.app.petspot.User.ShowUsersActivity;

import static com.salmaali.app.petspot.Authentication.LogInActivity.mFirebaseAuth;


public class AdminActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGNIN = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu_item) {
            LogInActivity.mFirebaseAuth.signOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ImageButton addAdmins = (ImageButton) findViewById(R.id.add_admin);
        ImageButton addShelters = (ImageButton) findViewById(R.id.add_shelter);
        ImageButton vets = (ImageButton) findViewById(R.id.admin_vets);
        ImageButton events = (ImageButton) findViewById(R.id.admin_events);
        ImageButton showUsers = (ImageButton) findViewById(R.id.admin_show_users);

        addAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddAdmins.class));
            }
        });

        addShelters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, SelectShelterLocation.class));
            }
        });

        vets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddVets.class));
            }
        });

        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddEvents.class));
            }
        });

        showUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminActivity.this, ShowUsersActivity.class);
                i.putExtra("userID", mFirebaseAuth.getCurrentUser().getUid());
                startActivity(i);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    startActivityForResult(new Intent(AdminActivity.this, LogInActivity.class),
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
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
}
