package com.example.conornaylor.fyp;

import java.io.Serializable;

/**
 * Created by conornaylor on 27/10/2017.
 */

public class LocationData implements Serializable{
    public double lat;
    public double lng;

    public LocationData(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

}
