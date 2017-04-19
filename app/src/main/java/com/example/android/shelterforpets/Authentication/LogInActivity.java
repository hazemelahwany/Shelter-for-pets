package com.example.android.shelterforpets.Authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shelterforpets.Admin.AdminActivity;
import com.example.android.shelterforpets.R;
import com.example.android.shelterforpets.User.UserMainActivity;
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
    private Button signin;
    private TextView forgotPassword;
    private TextView signup;

    public static FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference adminsDatabase;
    private DatabaseReference sheltersDatabase;

    boolean adminFlag = false;
    boolean shelterFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        adminsDatabase = mFirebaseDatabase.getReference().child("admins");
        sheltersDatabase = mFirebaseDatabase.getReference().child("shelters");

        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        signin = (Button) findViewById(R.id.login_button);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        signup = (TextView) findViewById(R.id.register);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    checkType(user);
//                    if () {
//                        startActivityForResult(new Intent(LogInActivity.this, AdminActivity.class),
//                                RC_SIGNIN);
//
//                    } else if (checkIfShelter(user)) {
//
//
//                    } else {
//
//                        if (user.isEmailVerified()) {
//
//                            startActivityForResult(new Intent(LogInActivity.this, UserMainActivity.class),
//                                    RC_SIGNIN);
//
//                        } else {
//                            user.sendEmailVerification()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Toast.makeText(LogInActivity.this,
//                                                    R.string.verify_email_toast, Toast.LENGTH_SHORT)
//                                                    .show();
//                                        }
//                                    });
//                        }
//                    }
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
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
                    String email = loginEmail.getText().toString();
                    String password = loginPassword.getText().toString();
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("auth_status", "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w("auth_status", "signInWithEmail:failed", task.getException());
                                        Toast.makeText(LogInActivity.this, R.string.auth_failed_toast,
                                                Toast.LENGTH_SHORT).show();
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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LogInActivity.this, SignUpActivity.class);
                LogInActivity.this.startActivity(i);
            }
        });
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

    private void checkType(final FirebaseUser user) {
        adminsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot admin : dataSnapshot.getChildren()) {
                    if (admin.getKey().equals(user.getUid())) {
                        adminFlag = true;
                        startActivityForResult(new Intent(LogInActivity.this, AdminActivity.class),
                                RC_SIGNIN);
                        break;
                    }
                }
                if (!adminFlag) {
                    if (user.isEmailVerified()) {

                        startActivityForResult(new Intent(LogInActivity.this, UserMainActivity.class),
                                RC_SIGNIN);

                    } else {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(LogInActivity.this,
                                                R.string.verify_email_toast, Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private boolean checkIfShelter(FirebaseUser user) {
        return false;
    }

}
