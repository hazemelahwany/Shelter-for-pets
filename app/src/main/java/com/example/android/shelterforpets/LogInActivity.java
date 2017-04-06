package com.example.android.shelterforpets;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button signin;
    private TextView forgotPassword;
    private TextView signup;

    public static FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

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
                    if (user.isEmailVerified()) {
                        // TODO: 05-04-2017 go to profile activity
                        // TODO: 06-04-2017 remove after creating activity
                        Toast.makeText(LogInActivity.this, "Signed in Successfully ",
                                Toast.LENGTH_SHORT).show();
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
}

