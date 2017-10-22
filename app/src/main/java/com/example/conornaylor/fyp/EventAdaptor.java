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

/**
 * Created by conornaylor on 20/10/2017.
 */

public class EventAdaptor extends ArrayAdapter<String> {

    public EventAdaptor(Context context, String[] events) {
        super(context, R.layout.custom_event_row, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater  inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_event_row, parent, false);

        String singleEvent = getItem(position);
        TextView eventText = (TextView) customView.findViewById(R.id.eventName);
        ImageView eventImage = (ImageView) customView.findViewById(R.id.eventImage);

        eventText.setText(singleEvent);
        eventImage.setImageResource(R.drawable.boltmess);
        return customView;
    }
}
