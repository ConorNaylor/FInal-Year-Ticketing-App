package com.example.conornaylor.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Ticket t;
    private String date;
    private Context context;

    public TicketAdaptor(Context context, ArrayList<Ticket> tickets) {
        super(context, R.layout.custom_ticket_row, tickets);

        this.context = context;
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

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(t.getEvent().getDate());

        ticketName.setText(t.getEvent().getTitle());
        ticketDate.setText(date);
        ticketSeat.setText(t.getSeat());
        Picasso.with(context).load("http://192.168.0.59:8000"  + t.getEvent().getImageURL()).into(eventImage);
        if(t.getEvent().getPrice() <= 0){
            ticketPrice.setText("Free");
        }else {
            ticketPrice.setText("€" + t.getEvent().getPrice().toString());
        }
        return customView;
    }
}

