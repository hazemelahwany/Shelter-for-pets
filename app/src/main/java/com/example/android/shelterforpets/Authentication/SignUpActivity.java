package com.example.android.shelterforpets.Authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.shelterforpets.R;
import com.example.android.shelterforpets.User.UserMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private static final int RC_EMAIL_VERIFY = 2;
    private EditText signupEmail;
    private EditText signupPassword;
    private EditText signupCinfirmPassword;
    private Button signupButton;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        signupEmail = (EditText) findViewById(R.id.signup_email);
        signupPassword = (EditText) findViewById(R.id.signup_password);
        signupCinfirmPassword = (EditText) findViewById(R.id.signup_confirm_password);
        signupButton = (Button) findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signupEmail.getText().toString().equals("") || signupPassword.getText()
                        .toString().equals("")|| signupCinfirmPassword.getText().toString()
                        .equals("")) {
                    Toast.makeText(SignUpActivity.this, R.string.check_all_fields,
                            Toast.LENGTH_LONG).show();
                } else {
                    if (signupPassword.getText().toString()
                            .equals(signupCinfirmPassword.getText().toString())) {

                        String email = signupEmail.getText().toString();
                        String password = signupPassword.getText().toString();

                        LogInActivity.mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                   @Override
                                   public void onComplete(@NonNull Task<AuthResult> task) {
                                       Log.d("auth_status", "createUserWithEmail:onComplete:"
                                               + task.isSuccessful());

                                       // If sign in fails, display a message to the user. If sign
                                       // in succeeds the auth state listener will be notified and
                                       // logic to handle the signed in user can be handled in the
                                       // listener.
                                       if (!task.isSuccessful()) {
                                           Toast.makeText(SignUpActivity.this,
                                                   R.string.auth_failed_toast, Toast.LENGTH_SHORT)
                                                   .show();
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
                    if (user.isEmailVerified()) {
                        startActivityForResult(new Intent(SignUpActivity.this, UserMainActivity.class),
                                RC_SIGNIN);
                    } else {
                        startActivityForResult(new Intent(SignUpActivity.this, LogInActivity.class),
                                RC_EMAIL_VERIFY);
                    }
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogInActivity.mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogInActivity.mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
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
            LogInActivity.mFirebaseAuth.getCurrentUser().sendEmailVerification()
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