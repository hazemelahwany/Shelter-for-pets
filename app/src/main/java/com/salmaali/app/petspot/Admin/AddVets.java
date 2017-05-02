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
import com.salmaali.app.petspot.DatabaseObjects.Vet;
import com.salmaali.app.petspot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.salmaali.app.petspot.Authentication.LogInActivity.mFirebaseAuth;

public class AddVets extends AppCompatActivity {

    private static final int RC_SIGNIN = 1;
    private EditText vetName;
    private EditText drName;
    private EditText vetAddress;
    private EditText vetTelephone;
    private EditText vetOpenFrom;
    private EditText vetOpenTo;
    private ImageButton addVet;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference vetDatabase;
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
        setContentView(R.layout.activity_add_vets);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        vetDatabase = mFirebaseDatabase.getReference().child("Vets");

        vetName = (EditText) findViewById(R.id.vet_name);
        drName = (EditText) findViewById(R.id.dr_name);
        vetAddress = (EditText) findViewById(R.id.vet_address);
        vetTelephone = (EditText) findViewById(R.id.vet_telephone);
        vetOpenFrom = (EditText) findViewById(R.id.vet_open_from);
        vetOpenTo = (EditText) findViewById(R.id.vet_open_to);
        addVet = (ImageButton) findViewById(R.id.add_vet_button);

        addVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vetName.getText().toString().equals("") || drName.getText().toString().equals("")
                        || vetAddress.getText().toString().equals("") || vetTelephone.getText().toString().equals("")
                        || vetOpenFrom.getText().toString().equals("") || vetOpenTo.getText().toString().equals("")) {
                    Toast.makeText(AddVets.this, R.string.check_all_fields, Toast.LENGTH_LONG).show();
                } else {
                    String vName = vetName.getText().toString();
                    String dName = drName.getText().toString();
                    String vAddress = vetAddress.getText().toString();
                    String vPhone = vetTelephone.getText().toString();
                    String vFrom = vetOpenFrom.getText().toString();
                    String vTo = vetOpenTo.getText().toString();

                    Vet vet = new Vet(vName, dName, vAddress, vPhone, vFrom, vTo);
                    vetDatabase.child(vName + vPhone).setValue(vet);
                }
            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    startActivityForResult(new Intent(AddVets.this, LogInActivity.class),
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
