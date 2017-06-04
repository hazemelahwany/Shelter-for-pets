package com.salmaali.app.petspot.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.salmaali.app.petspot.Admin.AdminActivity;
import com.salmaali.app.petspot.R;
import com.salmaali.app.petspot.Shelter.ShelterMainActivity;
import com.salmaali.app.petspot.User.UserMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LogInActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;

    private EditText loginEmail;
    private EditText loginPassword;
    private ImageButton signin;
    private TextView forgotPassword;

    public static FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference adminsDatabase;
    private DatabaseReference sheltersDatabase;
    ProgressDialog progress;

    boolean adminFlag = false;
    boolean shelterFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        adminsDatabase = mFirebaseDatabase.getReference().child("admins");
        sheltersDatabase = mFirebaseDatabase.getReference().child("Shelters");

        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        signin = (ImageButton) findViewById(R.id.login_button);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);




        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    checkType(user);
                } else {
                    // User is signed out
                    progress.dismiss();

                }
                // ...
            }
        };

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginEmail.getText().toString().equals("") ||
                        loginPassword.getText().toString().equals("")) {
                    Toast.makeText(LogInActivity.this, R.string.authentication_validation_toast,
                            Toast.LENGTH_LONG).show();
                } else {
                    progress = ProgressDialog.show(LogInActivity.this, "Logging In",
                            "Please Wait", true);
                    String email = loginEmail.getText().toString();
                    String password = loginPassword.getText().toString();
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progress.dismiss();
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        if (!isOnline()) {

                                            Toast.makeText(LogInActivity.this, "Check your Internet Connectivity",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LogInActivity.this, R.string.auth_failed_toast,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                    });
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LogInActivity.this, ForgotPasswordActivity.class);
                LogInActivity.this.startActivity(i);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        progress = ProgressDialog.show(LogInActivity.this, "Logging In",
                "Please Wait", true);
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

    private void checkType(final FirebaseUser user) {
        adminsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot admin : dataSnapshot.getChildren()) {
                    if (admin.getKey().equals(user.getUid())) {
                        adminFlag = true;
                        progress.dismiss();
                        startActivityForResult(new Intent(LogInActivity.this, AdminActivity.class),
                                RC_SIGNIN);
                        break;
                    }
                }
                if (!adminFlag) {
                    sheltersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot shelter : dataSnapshot.getChildren()) {
                                if (shelter.getKey().equals(user.getUid())) {
                                    shelterFlag = true;
                                    progress.dismiss();
                                    startActivityForResult(new Intent(LogInActivity.this, ShelterMainActivity.class),
                                        RC_SIGNIN);
                                    break;
                                }
                            }
                            if (!shelterFlag) {
                                    progress.dismiss();
                                    startActivityForResult(new Intent(LogInActivity.this, UserMainActivity.class),
                                            RC_SIGNIN);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) LogInActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}