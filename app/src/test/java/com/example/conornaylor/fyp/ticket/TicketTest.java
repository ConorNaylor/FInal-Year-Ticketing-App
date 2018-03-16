package com.example.conornaylor.fyp.ticket;

import com.example.conornaylor.fyp.event.Event;
import com.example.conornaylor.fyp.utilities.LocationData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by conornaylor on 14/03/2018.
 */
public class TicketTest {

    Date date = new Date();
    LocationData loc = new LocationData(3,2);
    Event e = new Event("1", "Example","Example Venue","Example Desc", date, 5, "1",loc, 0.0, "http://ec2-18-218-18-192.us-east-2.compute.amazonaws.com:8000/media/eventphotos/cbdf9606-551.png");
    Ticket t = new Ticket("abc","1A","1",e,false);
    Ticket t2 = new Ticket("abd","2A","1",e,false);
    ArrayList<Ticket> ticksArray = new ArrayList<>();

    @Test
    public void getAllTicketsForEvent_isCorrect() {
        ticksArray.add(t);
        ticksArray.add(t2);
        assertEquals(Ticket.getAllTicketsForEvent(e), ticksArray);
    }

    @Test
    public void getOneTicketPerEvent_isCorrect() {
        assertEquals(Ticket.getOneTicketPerEvent().contains(t), true);
    }

    @Test
    public void getTickets_isCorrect() {
        ticksArray.add(t);
        ticksArray.add(t2);
        assertEquals(Ticket.getTickets(), ticksArray);
    }



}