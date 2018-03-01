package com.example.conornaylor.fyp.ticket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.conornaylor.fyp.event.Event;
import com.example.conornaylor.fyp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by conornaylor on 11/11/2017.
 */

public class TicketAdaptor extends ArrayAdapter<Ticket> {

    private TextView ticketName;
    private TextView ticketDate;
    private TextView ticketPrice;
    private TextView ticketSeat;
    private ImageView eventImage;
    private TextView ticketQty;
    private Ticket t;
    private String date;
    private Context context;

    public TicketAdaptor(Context context, ArrayList<Ticket> tickets) {
        super(context, R.layout.custom_ticket_row, tickets);

        this.context = context;
    }

    public int getQuantity(Event event){
        int count = 0;
        for(Ticket tick: Ticket.getTickets()){
            if(tick.getEvent().getId().equals(event.getId())){
                count++;
            }
        }
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_ticket_row, parent, false);

        t = getItem(position);

        ticketName = customView.findViewById(R.id.customTicketName);
        ticketDate = customView.findViewById(R.id.customTicketDate);
        ticketPrice = customView.findViewById(R.id.customTicketPrice);
        ticketSeat = customView.findViewById(R.id.customTicketSeat);
        eventImage = customView.findViewById(R.id.customeventImage);
        ticketQty = customView.findViewById(R.id.customTicketQuantity);

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(t.getEvent().getDate());

        ticketQty.setText("Qty: "+getQuantity(t.getEvent()));
        ticketName.setText(t.getEvent().getTitle());
        ticketDate.setText(date);
        ticketSeat.setText(t.getSeat());
        Picasso.with(context).load("http://18.218.18.192:8000"  + t.getEvent().getImageURL()).into(eventImage);
        if(t.getEvent().getPrice() <= 0){
            ticketPrice.setText("Free");
        }else {
            ticketPrice.setText("€" + t.getEvent().getPrice().toString());
        }
        return customView;
    }
}

