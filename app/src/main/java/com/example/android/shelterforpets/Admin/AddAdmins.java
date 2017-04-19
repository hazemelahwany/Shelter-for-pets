package com.example.android.shelterforpets.Admin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.shelterforpets.Authentication.LogInActivity;
import com.example.android.shelterforpets.DatabaseObjects.Admin;
import com.example.android.shelterforpets.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAdmins extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Spinner adminType;
    private Button addAdmin;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference adminsDatabase;

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
        adminType = (Spinner) findViewById(R.id.add_admin_type_spinner);
        addAdmin = (Button) findViewById(R.id.add_admin_button);

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
                    final boolean userType;
                    if (adminType.getSelectedItem().toString().equalsIgnoreCase("normal"))
                        userType = false;
                    else
                        userType = true;
                    LogInActivity.mFirebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = task.getResult().getUser();
                                        Admin admin = new Admin(firstNameString, lastNameString, userType);
                                        adminsDatabase.child(user.getUid()).setValue(admin);
                                    }
                                }
                            });
                }

            }
        });
    }
}
