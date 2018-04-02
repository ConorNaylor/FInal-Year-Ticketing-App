package com.example.conornaylor.fyp.event;

import com.example.conornaylor.fyp.utilities.LocationData;

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
    private Date date;
    private LocationData location;
    private int numTicks;
    private String userId;
    private static ArrayList<Event> events = new ArrayList<>();
    private double price;
    private String imageURL;

    public Event(String id, String title, String address, String description, Date date, int numTicks, String userId, LocationData loc, Double price, String imageURL){
        this.id = id;
        this.title = title;
        this.address = address;
        this.description = description;
        this.date = date;
        this.location = loc;
        this.numTicks = numTicks;
        this.userId = userId;
        this.addToUniqueEvents(this);
        this.price = price;
        this.imageURL = imageURL;
    }

    public static Event getEventByID(String id){
        for(Event e: events){
            if(e.getId().equals(id)){
                return e;
            }
        }
        return null;
    }

    public String getImageURL() { return imageURL; }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public Double getPrice(){ return price; }

    public void setPrice(Double price){ this.price = price; }

    public String getUserId() { return userId; }

    public int getNumTicks() { return numTicks; }

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

    public String getDescription() {
        return description;
    }

    public Date getDate() {return date;}

    public void setDate(Date date) {
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

    public String toString(){
        return this.id;
    }

    public static void deleteEvents(){ events.clear(); }

    private void addToUniqueEvents(Event e) {
        if(events.isEmpty()){
            events.add(e);
        }else{
            for(Event ev: events) {
                if (ev.getId().equals(e.getId())) {
                    return;
                }
            }
            events.add(e);
        }
    }

    public static ArrayList<Event> searchForEvent(String in) {
        ArrayList<Event> events = new ArrayList<>();
        for(Event e: getEvents()){
            if(e.getTitle().toLowerCase().equals(in.toLowerCase()) || e.getTitle().toLowerCase().contains(in.toLowerCase())){
                events.add(e);
            }
        }
        return events;
    }

    public static Event getEventByTitle(String title) {
        for(Event e: getEvents()){
            if(e.getTitle().equals(title)){
                return e;
            }
        }
        return null;
    }
}
