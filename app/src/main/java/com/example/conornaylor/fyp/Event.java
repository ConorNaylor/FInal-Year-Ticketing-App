package com.example.conornaylor.fyp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by conornaylor on 27/10/2017.
 */

public class Event {
    private String title;
    private String address;
    private String description;
    private String date;
    private LocationData location;
    private ArrayList<Event> events = new ArrayList<>();

    public Event(String title, String address, String description, String date, LocationData loc){
        this.title = title;
        this.address = address;
        this.description = description;
        this.date = date;
        this.location = loc;
        events.add(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {return date;}

    public void setDate(String date) {
        this.date = date;
    }

    public LocationData getLocation() {
        return location;
    }

    public void setLocation(LocationData location) {
        this.location = location;
    }

    public ArrayList<Event> getEvents(){
        return events;
    }

    public void removeEvent(Event event){
        events.remove(event);
    }

    public String toString(){
        return this.title + this.description + this.address + this.date;
    }
}
