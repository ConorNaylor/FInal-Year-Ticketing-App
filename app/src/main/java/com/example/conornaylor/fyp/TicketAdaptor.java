package com.example.conornaylor.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by conornaylor on 11/11/2017.
 */

public class TicketAdaptor extends ArrayAdapter<Ticket> {

    private TextView ticketName;
    private TextView ticketDate;
    private TextView ticketPrice;
    private TextView ticketSeat;
    private ImageView eventImage;
    private Ticket t;

    public TicketAdaptor(Context context, ArrayList<Ticket> tickets) {
        super(context, R.layout.custom_ticket_row, tickets);
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

        ticketName.setText(t.getEvent().getTitle());
        ticketDate.setText(t.getEvent().getDate());
        ticketPrice.setText(t.getUserId());
        ticketSeat.setText(t.getSeat());
        eventImage.setImageResource(R.drawable.boltmess);
        return customView;
    }
}

