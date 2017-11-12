package com.example.conornaylor.fyp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by conornaylor on 10/11/2017.
 */

public class Ticket implements Serializable{

    private String ticketId;
    private String seat;
    private String userId;
    private Event event;
    private static ArrayList<Ticket> tickets = new ArrayList<>();

    public Ticket(String ticketId, String seat, Double price, String userId, Event event){
        this.ticketId =ticketId;
        this.seat = seat;
        this.userId = userId;
        this.event = event;
        tickets.add(this);
    }

    public static ArrayList<Ticket> getTickets(){
        return tickets;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
