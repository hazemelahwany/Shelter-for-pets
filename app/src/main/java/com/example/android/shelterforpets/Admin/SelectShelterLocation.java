package com.example.android.shelterforpets.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.shelterforpets.R;
import com.example.android.shelterforpets.TrackGPS;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectShelterLocation extends AppCompatActivity implements OnMapReadyCallback {

    private double longitude;
    private double latitude;
    SupportMapFragment mapFragment;
    private MarkerOptions markerOptions;
    private LatLng currentLoc;
    private Marker address;

    private static final LatLngBounds BOUNDS_EGYPT = new LatLngBounds(
            new LatLng(24.09082, 25.51965 ), new LatLng(31.5084, 34.89005));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shelter_location);

        Button nextToAddShelter = (Button) findViewById(R.id.next_add_shelter);

        TrackGPS gps = new TrackGPS(SelectShelterLocation.this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            currentLoc = new LatLng(latitude, longitude);
            markerOptions = new MarkerOptions().position(currentLoc);
            mapFragment.getMapAsync(SelectShelterLocation.this);

        } else {
            gps.showSettingsAlert();
        }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                if (address != null) {
                    address.remove();
                }
                currentLoc = new LatLng(latitude, longitude);
                markerOptions = new MarkerOptions().position(currentLoc);
                mapFragment.getMapAsync(SelectShelterLocation.this);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e("Error", "An error occurred: " + status);
            }
        });

        nextToAddShelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectShelterLocation.this, AddShelters.class);
                Bundle b = new Bundle();
                b.putDouble("long", longitude);
                b.putDouble("lat", latitude);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        address = googleMap.addMarker(markerOptions.title("Your Address"));
        googleMap.setLatLngBoundsForCameraTarget(BOUNDS_EGYPT);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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
                Toast.makeText(SelectShelterLocation.this, markerLocation.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }
}