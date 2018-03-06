package com.example.conornaylor.fyp.ticket;

import com.example.conornaylor.fyp.event.Event;

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
    private boolean entered;
    private static ArrayList<Ticket> tickets = new ArrayList<>();

    public Ticket(String ticketId, String seat, String userId, Event event, boolean entered){
        this.ticketId = ticketId;
        this.seat = seat;
        this.userId = userId;
        this.event = event;
        this.entered = entered;
        addUniqueTickets(this);
    }

    public boolean isEntered() {
        return entered;
    }

    public static ArrayList<Ticket> getTickets(){
        return tickets;
    }

    public static void deleteTickets(){ tickets.clear(); }

    public String getId() {
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

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        final Ticket tick = (Ticket ) obj;
        return tick.getEvent().getId() == this.getEvent().getId();
    }

    public void addUniqueTickets(Ticket t) {
        if(tickets.isEmpty()){
            tickets.add(t);
        }else{
            for(Ticket tk: tickets) {
                if (tk.getId().equals(t.getId())) {
                    return;
                }
            }
            tickets.add(t);
        }
    }
}
