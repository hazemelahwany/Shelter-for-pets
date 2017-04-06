package com.example.android.shelterforpets;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText resetPasswordEmail;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetPasswordEmail = (EditText) findViewById(R.id.forgot_password_email);
        resetPasswordButton = (Button) findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resetPasswordEmail.getText().toString().equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, R.string.check_email_entered_toast,
                            Toast.LENGTH_LONG).show();
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = resetPasswordEmail.getText().toString();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                R.string.confirm_email_sent_toast, Toast.LENGTH_LONG)
                                                .show();
                                        startActivity(new Intent(ForgotPasswordActivity.this,
                                                LogInActivity.class));
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                R.string.check_email_entered_toast, Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
