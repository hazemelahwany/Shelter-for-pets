package com.example.android.shelterforpets.Admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.shelterforpets.DatabaseObjects.Vet;
import com.example.android.shelterforpets.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddVets extends AppCompatActivity {

    private EditText vetName;
    private EditText drName;
    private EditText vetAddress;
    private EditText vetTelephone;
    private EditText vetOpenFrom;
    private EditText vetOpenTo;
    private Button addVet;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference vetDatabase;

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
        addVet = (Button) findViewById(R.id.add_vet_button);

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

    }
}
