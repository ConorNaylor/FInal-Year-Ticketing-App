package com.example.conornaylor.fyp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.event.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Map locMap;
    private LatLng latlng;
    private String message;
    private String lt = "lat";
    private String lg = "lng";
    private double d1, d2;
    private Button button1;
    private EditText searchview;
    private boolean allEvents = true;
    private Event e;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("event") != null) {
            e = Event.getEventByID(extras.getString("event"));
            allEvents = false;
        }

        setTitle("Location");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button1 = findViewById(R.id.button1);
        searchview = findViewById(R.id.searchView1);

        if(!allEvents){
            button1.setVisibility(View.INVISIBLE);
            searchview.setVisibility(View.INVISIBLE);
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchview.getText().toString());
            }
        });

    }

    private void focusOnEvent(Event e) {
        LatLng latLng = new LatLng(e.getLocation().lat, e.getLocation().lng);

        mMap.addMarker(new MarkerOptions().position(latLng).title(e.getTitle()));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(allEvents) {
            putEventsOnMap();
            setClickListeners();
        }else { focusOnEvent(e); }
    }

    private void setClickListeners() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event e = Event.getEventByTitle(marker.getTitle());
                Intent myIntent = new Intent(MapsActivity.this, MainActivity.class);
                myIntent.putExtra("event",e.getId());
                MapsActivity.this.startActivity(myIntent);
                finish();
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    public void putEventsOnMap() {
        for (Event e : Event.getEvents()) {

            message = e.getTitle();

            d1 = e.getLocation().lat;
            d2 = e.getLocation().lng;

            latlng = new LatLng(d1, d2);

            mMap.addMarker(new MarkerOptions().position(latlng).title(message));
        }
    }

    protected void search(String str) {

        Event e = Event.searchForEvent(str);

        if(e != null) {

            LatLng latLng = new LatLng(e.getLocation().lat, e.getLocation().lng);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(e.getTitle());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        }
    }

}
