package com.example.conornaylor.fyp;

import android.content.Context;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by conornaylor on 20/10/2017.
 */

public class EventAdaptor extends ArrayAdapter<Event> {

    private TextView eventName;
    private TextView eventAdd;
    private TextView eventDate;
    private ImageView eventImage;
    private Event e;

    public EventAdaptor(Context context, ArrayList<Event> events) {
        super(context, R.layout.custom_event_row, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater  inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_event_row, parent, false);

        e = getItem(position);
        eventName = customView.findViewById(R.id.customeventName);
        eventAdd = customView.findViewById(R.id.customeventAddress);
        eventDate = customView.findViewById(R.id.customeventDate);
        eventImage = customView.findViewById(R.id.customeventImage);

        eventName.setText(e.getTitle());
        eventAdd.setText(e.getAddress());
        eventDate.setText(e.getDate());
        eventImage.setImageResource(R.drawable.boltmess);
        return customView;
    }
}
