package com.salmaali.app.petspot.Authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.salmaali.app.petspot.DatabaseObjects.User;
import com.salmaali.app.petspot.R;
import com.salmaali.app.petspot.User.UserMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private static final int RC_EMAIL_VERIFY = 2;
    private EditText signUpFirstName;
    private EditText signUpLastName;
    private EditText signupEmail;
    private EditText signupPassword;
    private EditText signupCinfirmPassword;
    private ImageButton signupButton;
    private CheckBox volunteer;
    FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabase = mFirebaseDatabase.getReference().child("Users");

        signUpFirstName = (EditText) findViewById(R.id.signup_first_name);
        signUpLastName = (EditText) findViewById(R.id.signup_last_name);
        signupEmail = (EditText) findViewById(R.id.signup_email);
        signupPassword = (EditText) findViewById(R.id.signup_password);
        signupCinfirmPassword = (EditText) findViewById(R.id.signup_confirm_password);
        signupButton = (ImageButton) findViewById(R.id.signup_button);
        volunteer = (CheckBox) findViewById(R.id.signup_volunteer_checkBox);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signupEmail.getText().toString().equals("") || signupPassword.getText()
                        .toString().equals("")|| signupCinfirmPassword.getText().toString()
                        .equals("") || signUpFirstName.getText().toString().equals("")
                        || signUpLastName.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, R.string.check_all_fields,
                            Toast.LENGTH_LONG).show();
                } else {
                    if (signupPassword.getText().toString()
                            .equals(signupCinfirmPassword.getText().toString())) {

                        String email = signupEmail.getText().toString();
                        String password = signupPassword.getText().toString();

                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                   @Override
                                   public void onComplete(@NonNull Task<AuthResult> task) {

                                       if (task.isSuccessful()) {
                                           FirebaseUser user = task.getResult().getUser();
                                           if (volunteer.isChecked()) {
                                               User u = new User(signUpFirstName.getText().toString(),
                                                       signUpLastName.getText().toString(), "null", true);
                                               usersDatabase.child(user.getUid()).setValue(u);
                                           } else {
                                               User u = new User(signUpFirstName.getText().toString(),
                                                       signUpLastName.getText().toString(), "null", false);
                                               usersDatabase.child(user.getUid()).setValue(u);
                                           }
                                       }

                                       // If sign in fails, display a message to the user. If sign
                                       // in succeeds the auth state listener will be notified and
                                       // logic to handle the signed in user can be handled in the
                                       // listener.
                                       if (!task.isSuccessful()) {
                                           if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                               Toast.makeText(SignUpActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                           } else {
                                               Toast.makeText(SignUpActivity.this,
                                                       "Error Occurred Please Try Again", Toast.LENGTH_SHORT)
                                                       .show();
                                           }
                                       }
                                   }
                               });

                    } else {
                        Toast.makeText(SignUpActivity.this, R.string.check_passwords,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                        startActivityForResult(new Intent(SignUpActivity.this, UserMainActivity.class),
                                RC_SIGNIN);
                } else {
                    // User is signed out

                }
            }
        };
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
                            Toast.makeText(SignUpActivity.this,
                                    R.string.verify_email_toast, Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
        }
    }
}
