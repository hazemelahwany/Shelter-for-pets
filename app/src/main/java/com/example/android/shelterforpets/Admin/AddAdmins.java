package com.example.android.shelterforpets.Admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.shelterforpets.Authentication.LogInActivity;
import com.example.android.shelterforpets.DatabaseObjects.Admin;
import com.example.android.shelterforpets.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.android.shelterforpets.Authentication.LogInActivity.mFirebaseAuth;

public class AddAdmins extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private ImageButton addAdmin;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference adminsDatabase;
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
        setContentView(R.layout.activity_add_admins);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        adminsDatabase = mFirebaseDatabase.getReference().child("admins");

        firstName = (EditText) findViewById(R.id.add_admin_first_name);
        lastName = (EditText) findViewById(R.id.add_admin_last_name);
        email = (EditText) findViewById(R.id.add_admin_email);
        password = (EditText) findViewById(R.id.add_admin_password);
        confirmPassword = (EditText) findViewById(R.id.add_admin_confirm_password);
        addAdmin = (ImageButton) findViewById(R.id.add_admin_button);

        addAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("")
                        || email.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(AddAdmins.this, R.string.check_all_fields, Toast.LENGTH_LONG).show();
                } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    Toast.makeText(AddAdmins.this, R.string.check_passwords, Toast.LENGTH_LONG).show();
                } else {
                    String emailString = email.getText().toString();
                    String passwordString = password.getText().toString();
                    final String firstNameString = firstName.getText().toString();
                    final String lastNameString = lastName.getText().toString();
                    LogInActivity.mFirebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = task.getResult().getUser();
                                        Admin admin = new Admin(firstNameString, lastNameString);
                                        adminsDatabase.child(user.getUid()).setValue(admin);
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
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                    startActivityForResult(new Intent(AddAdmins.this, LogInActivity.class),
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
