package com.example.conornaylor.fyp.activities;

import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.event.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button1 = findViewById(R.id.button1);
        searchview = findViewById(R.id.searchView1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        putEventsOnMap();
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

    protected void search(List<Address> addresses) {

        Address address = addresses.get(0);
        double home_long = address.getLongitude();
        double home_lat = address.getLatitude();
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        locationTv.setText("Latitude:" + address.getLatitude() + ", Longitude:"
//                + address.getLongitude());


    }

}
