package com.example.conornaylor.fyp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by conornaylor on 27/10/2017.
 */

public class Event implements Serializable{
    private String id;
    private String title;
    private String address;
    private String description;
    private String date;
    private LocationData location;
    private Double price;
    private int numTicks;
    private static ArrayList<Event> events = new ArrayList<>();

    public Event(String id, String title, String address, String description, String date, Double price, int numTicks, LocationData loc){
        this.id = id;
        this.title = title;
        this.address = address;
        this.description = description;
        this.date = date;
        this.location = loc;
        this.price = price;
        this.numTicks = numTicks;
        events.add(this);
    }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public int getNumTicks() { return numTicks; }

    public void setNumTicks(int numTicks) { this.numTicks = numTicks; }

    public String getId(){
        return this.id;
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

    public static ArrayList<Event> getEvents(){
        return events;
    }

    public void removeEvent(Event event){
        events.remove(event);
    }

    public String toString(){
        return this.title;
    }
}
