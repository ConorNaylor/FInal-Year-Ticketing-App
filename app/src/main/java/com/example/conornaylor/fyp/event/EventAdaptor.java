package com.example.conornaylor.fyp.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.conornaylor.fyp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by conornaylor on 20/10/2017.
 */

public class EventAdaptor extends ArrayAdapter<Event> {

    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private String userId = "id";
    private String userID;
    private TextView eventName;
    private TextView eventAdd;
    private TextView eventDate;
    private ImageView eventImage;
    private TextView eventPrice;
    private Event e;
    private Context context;

    public EventAdaptor(Context context, ArrayList<Event> events) {
        super(context, R.layout.custom_event_row, events);

        this.context = context;

        preferences = context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userID = preferences.getString(userId, null);
        token = preferences.getString(tk, null);
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
        eventPrice = customView.findViewById(R.id.customeventPrice);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(e.getDate());

        eventName.setText(e.getTitle());
        eventAdd.setText(e.getAddress());
        eventDate.setText(date);

        if(e.getPrice() <= 0){
            eventPrice.setText("Free");
        }else {
            eventPrice.setText("€" + e.getPrice().toString());
        }

        Glide.with(context)
                .load("http://18.218.18.192:8000"  + e.getImageURL())
                .centerCrop()
                .into(eventImage);


        return customView;
    }
}
