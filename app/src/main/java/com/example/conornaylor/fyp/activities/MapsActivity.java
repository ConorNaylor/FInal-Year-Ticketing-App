package com.example.conornaylor.fyp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.event.Event;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
    private ArrayList<Event> events;


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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.43333, -7.95), 6.5f));
        }else {
            focusOnEvent(e);
        }
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
        Date date = new Date();
        String todayString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date);
        events = new ArrayList<>();
        for (Event e : Event.getEvents()) {
            String dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(e.getDate());
            if (e.getDate().after(date) || todayString.equals(dateString)) {
                message = e.getTitle();

                d1 = e.getLocation().lat;
                d2 = e.getLocation().lng;

                latlng = new LatLng(d1, d2);

                mMap.addMarker(new MarkerOptions().position(latlng).title(message));

                events.add(e);
            }
        }
    }

    protected void search(String str) {

        ArrayList<Marker> markers = new ArrayList<>();
        ArrayList<Event> searchResults = Event.searchForEvent(str);
        boolean noResults = false;


        if(events == null || searchResults == null || events.isEmpty() || searchResults.isEmpty()) {
            noResults = true;
        } else {
            for (Event ev : events) {
                if (searchResults.contains(ev)) {
                    LatLng latLng = new LatLng(ev.getLocation().lat, ev.getLocation().lng);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(ev.getTitle()));
                    marker.showInfoWindow();

                    markers.add(marker);
                }
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker m : markers) {
                builder.include(m.getPosition());
            }
            if(markers.isEmpty()) {
                noResults = true;
            }else {
                LatLngBounds bounds = builder.build();
                int padding = ((500 * 10) / 100);
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }
        }
        if(noResults) {
            Toast.makeText(this, "No search results.", Toast.LENGTH_SHORT).show();
        }
    }

}
