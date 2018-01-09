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
    private Date date;
    private LocationData location;
    private int numTicks;
    private String userId;
    private static ArrayList<Event> events = new ArrayList<>();

    public Event(String id, String title, String address, String description, Date date, int numTicks, String userId, LocationData loc){
        this.id = id;
        this.title = title;
        this.address = address;
        this.description = description;
        this.date = date;
        this.location = loc;
        this.numTicks = numTicks;
        this.userId = userId;
        this.addToUniqueEvents(this);
    }

    public static Event getEventByID(String id){
        for(Event e: events){
            if(e.getId().equals(id)){
                return e;
            }
        }
        return null;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

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

    public void removeEvent(Event event){
        events.remove(event);
    }

    public String toString(){
        return this.id;
    }

    public void addToUniqueEvents(Event e) {
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
}
