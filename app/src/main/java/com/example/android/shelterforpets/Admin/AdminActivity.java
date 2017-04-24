package com.example.android.shelterforpets.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.shelterforpets.R;


public class AdminActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button addAdmins = (Button) findViewById(R.id.add_admin);
        Button addShelters = (Button) findViewById(R.id.add_shelter);
        Button vets = (Button) findViewById(R.id.admin_vets);
        Button events = (Button) findViewById(R.id.admin_events);

        addAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddAdmins.class));
            }
        });

        addShelters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, SelectShelterLocation.class));
            }
        });

        vets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddVets.class));
            }
        });

        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddEvents.class));
            }
        });
    }


}
