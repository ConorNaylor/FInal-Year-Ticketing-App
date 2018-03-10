package com.example.conornaylor.fyp.ticket;

import com.example.conornaylor.fyp.event.Event;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    public String getUserId() {
        return userId;
    }

    public String toString(){
        return this.getSeat();
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

    private void addUniqueTickets(Ticket t) {
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

    public static ArrayList<Ticket> getOneTicketPerEvent(){
        ArrayList<Ticket> list = new ArrayList<>();
        for (Ticket t : getTickets()) {
            if(!list.contains(t)){
                list.add(t);
            }
        }
        return list;
    }

    public static ArrayList<ArrayList<Ticket>> getAllTicketsPerEvent() {
        Map<String, ArrayList<Ticket>> map = new HashMap<>();
        ArrayList<Ticket> allTickets = Ticket.getTickets();

        ArrayList<Ticket> tickets = getOneTicketPerEvent();
        for (int i = 0; i < tickets.size(); i++) {
            map.put(tickets.get(i).getEvent().getId(), new ArrayList<Ticket>());
        }
        for (int i = 0; i < allTickets.size(); i++) {
            Ticket t = allTickets.get(i);
            map.get(t.getEvent().getId()).add(t);
        }
        // Convert to list of lists
        return new ArrayList<>(map.values());
    }

    public static ArrayList<Ticket> getAllTicketsForEvent(Event e) {
        ArrayList<Ticket> allTickets = Ticket.getTickets();
        ArrayList<Ticket> list = new ArrayList<>();

        for(Ticket t: allTickets) {
            if(t.getEvent().getId().equals(e.getId())){
                list.add(t);
            }
        }
        return list;
    }
}
