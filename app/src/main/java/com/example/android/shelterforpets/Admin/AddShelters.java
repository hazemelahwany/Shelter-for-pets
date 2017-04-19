package com.example.android.shelterforpets.Admin;

import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.shelterforpets.DatabaseObjects.Shelter;
import com.example.android.shelterforpets.R;
import com.example.android.shelterforpets.TrackGPS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddShelters extends AppCompatActivity implements OnMapReadyCallback {

    private EditText shelterName;
    private Button addShelter;
    private TrackGPS gps;
    private double longitude;
    private double latitude;
    SupportMapFragment mapFragment;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference sheltersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shelters);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        sheltersDatabase = mFirebaseDatabase.getReference().child("Shelters");

        shelterName = (EditText) findViewById(R.id.add_shelter_name);
        addShelter = (Button) findViewById(R.id.add_shelter_button);

        gps = new TrackGPS(AddShelters.this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            mapFragment.getMapAsync(AddShelters.this);

        } else {
            gps.showSettingsAlert();
        }

        addShelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shelterName.getText().toString().equals("")) {
                    Toast.makeText(AddShelters.this, "Enter Shelter Name", Toast.LENGTH_LONG).show();
                } else {
                    Shelter shelter = new Shelter(shelterName.getText().toString(),
                            "" + longitude + "," + latitude);
                    sheltersDatabase.child(shelter.getShelterName()+(int)latitude + (int) longitude)
                            .setValue(shelter);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng currentLoc = new LatLng(latitude, longitude);
        Marker address = googleMap.addMarker(new MarkerOptions().position(currentLoc)
                .title("Your Address"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));

        address.setDraggable(true);
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng markerLocation = marker.getPosition();
                latitude = markerLocation.latitude;
                longitude = markerLocation.longitude;
                Toast.makeText(AddShelters.this, markerLocation.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }
}
