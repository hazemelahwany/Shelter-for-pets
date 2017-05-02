package com.salmaali.app.petspot.Admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.DatabaseObjects.Shelter;
import com.salmaali.app.petspot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.salmaali.app.petspot.Authentication.LogInActivity.mFirebaseAuth;

public class AddShelters extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private EditText shelterName;
    private EditText shelterEmail;
    private EditText shelterPassword;
    private EditText shelterConfirmPassword;
    private double longitude;
    private double latitude;

    private DatabaseReference sheltersDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

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
        setContentView(R.layout.activity_add_shelters);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        sheltersDatabase = mFirebaseDatabase.getReference().child("Shelters");

        Bundle b = getIntent().getExtras();
        longitude = b.getDouble("long");
        latitude = b.getDouble("lat");



        shelterName = (EditText) findViewById(R.id.add_shelter_name);
        shelterEmail = (EditText) findViewById(R.id.add_shelter_email);
        shelterPassword = (EditText) findViewById(R.id.add_shelter_password);
        shelterConfirmPassword = (EditText) findViewById(R.id.add_shelter_confirm_password);
        ImageButton addShelter = (ImageButton) findViewById(R.id.add_shelter_button);

        addShelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shelterName.getText().toString().equals("") || shelterEmail.getText().toString().equals("")
                        || shelterPassword.getText().toString().equals("") || shelterConfirmPassword.getText().toString().equals("")) {
                    Toast.makeText(AddShelters.this, R.string.check_all_fields, Toast.LENGTH_LONG).show();
                } else if (!shelterPassword.getText().toString().equals(shelterConfirmPassword.getText().toString())) {
                    Toast.makeText(AddShelters.this, R.string.check_passwords, Toast.LENGTH_LONG).show();
                } else {
                    LogInActivity.mFirebaseAuth.createUserWithEmailAndPassword(
                            shelterEmail.getText().toString(), shelterPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        FirebaseUser user = task.getResult().getUser();
                                        Shelter shelter = new Shelter(shelterName.getText().toString(),
                                                "" + longitude + "," + latitude);
                                        sheltersDatabase.child(user.getUid()).setValue(shelter);
                                    }
                                }
                            });
                }
            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    startActivityForResult(new Intent(AddShelters.this, LogInActivity.class),
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
