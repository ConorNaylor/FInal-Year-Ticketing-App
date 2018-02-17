package com.example.conornaylor.fyp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by conornaylor on 17/01/2018.
 */

public class Attendee {

    private String name;
    private String id;
    private String ticketId;
    private Date pruchaseDate;
    private static ArrayList<Attendee> attendees = new ArrayList<>();


    public Attendee(String name, String id, String TicketId, Date purchaseDate){
        this.name = name;
        this.id = id;
        this.ticketId = TicketId;
        this.pruchaseDate = purchaseDate;
        addUniqueAttendees(this);
    }

    public String getName() { return name; }

    public Date getPruchaseDate() { return pruchaseDate; }

    public String getTicketId() { return ticketId; }

    public String getUserId() { return this.id; }

    public void addUniqueAttendees(Attendee t) {
        if(attendees.isEmpty()){
            attendees.add(t);
        }else{
            for(Attendee at: attendees) {
                if (at.getTicketId().equals(t.getTicketId())) {
                    return;
                }
            }
            attendees.add(t);
        }
    }
}
