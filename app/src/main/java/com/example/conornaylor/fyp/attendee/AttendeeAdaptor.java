package com.example.conornaylor.fyp.attendee;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.conornaylor.fyp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by conornaylor on 17/01/2018.
 */

public class AttendeeAdaptor extends ArrayAdapter<Attendee> {

    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private TextView usernameIDView;
    private TextView purchaseDateView;
    private CheckBox enteredBox;
    private Attendee a;
    public static String clickedUserID;

    public AttendeeAdaptor(Context context, ArrayList<Attendee> attendees){
        super(context, R.layout.custom_attendee_row, attendees);

        preferences = context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_attendee_row, parent, false);

        a = getItem(position);
        usernameIDView  = customView.findViewById(R.id.customUsernameID);
        purchaseDateView = customView.findViewById(R.id.customPurchaseDate);
        enteredBox = customView.findViewById(R.id.enteredBox);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(a.getPruchaseDate());

        enteredBox.setChecked(a.getEntered());
        enteredBox.setClickable(false);
        usernameIDView.setText(a.getName() + " - " + a.getUserId());
        purchaseDateView.setText(date);

        return customView;
    }
}
