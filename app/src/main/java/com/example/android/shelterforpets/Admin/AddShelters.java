package com.example.android.shelterforpets.Admin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.shelterforpets.Authentication.LogInActivity;
import com.example.android.shelterforpets.DatabaseObjects.Shelter;
import com.example.android.shelterforpets.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddShelters extends AppCompatActivity {

    private EditText shelterName;
    private EditText shelterEmail;
    private EditText shelterPassword;
    private EditText shelterConfirmPassword;
    private double longitude;
    private double latitude;

    private DatabaseReference sheltersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shelters);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        sheltersDatabase = mFirebaseDatabase.getReference().child("Shelters");

        Bundle b = getIntent().getExtras();
        longitude = b.getDouble("long");
        latitude = b.getDouble("lat");
        Toast.makeText(this, "" + latitude + "\n" + longitude, Toast.LENGTH_SHORT).show();


        shelterName = (EditText) findViewById(R.id.add_shelter_name);
        shelterEmail = (EditText) findViewById(R.id.add_shelter_email);
        shelterPassword = (EditText) findViewById(R.id.add_shelter_password);
        shelterConfirmPassword = (EditText) findViewById(R.id.add_shelter_confirm_password);
        Button addShelter = (Button) findViewById(R.id.add_shelter_button);

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
    }

}
