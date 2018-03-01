package com.example.conornaylor.fyp.event;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.conornaylor.fyp.ticket.CreateTicketFragment;
import com.example.conornaylor.fyp.utilities.EventStatsFragment;
import com.example.conornaylor.fyp.utilities.NFCDisplayActivity;
import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.ticket.Ticket;
import com.example.conornaylor.fyp.ticket.ViewTicketFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewEventFragment extends Fragment {

    private Event event;
    private EditText nameText;
    private EditText descText;
    private EditText dateText;
    private EditText priceText;
    private EditText numTicksText;
    private EditText locText;
    private Button button;
    private Button authButton;
    private ImageView eventImageView;
    private boolean show = false;
    private FloatingActionButton fab;
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String userIdString = "id";
    private String userID;
    private String date;
    private Ticket ticket;


    public ViewEventFragment() {
    }

    public static ViewEventFragment newInstance(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", e);
        ViewEventFragment fragment = new ViewEventFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            event = (Event)bundle.getSerializable("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userID = preferences.getString(userIdString, null);

        getActivity().setTitle(event.getTitle());

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(event.getDate());

//        show = false;
        authButton = getActivity().findViewById(R.id.auth_button);
        fab = getActivity().findViewById(R.id.fab2);
        button = getActivity().findViewById(R.id.viewStatsButton);
        nameText = getActivity().findViewById(R.id.viewEventName);
        descText = getActivity().findViewById(R.id.viewEventDescription);
        dateText = getActivity().findViewById(R.id.viewEventDate);
        priceText = getActivity().findViewById(R.id.viewEventPrice);
        numTicksText = getActivity().findViewById(R.id.viewnumTickets);
        locText = getActivity().findViewById(R.id.viewEventLocation);
        eventImageView = getActivity().findViewById(R.id.viewEventImage);

        Picasso.with(getActivity()).load("http://18.218.18.192:8000"  + event.getImageURL()).into(eventImageView);

        if (!event.getUserId().equals(userID)) {
            fab.setVisibility(View.INVISIBLE);
            fab.setEnabled(false);
            numTicksText.setVisibility(View.INVISIBLE);
            button.setText("Buy Ticket");
            if(hasTicket()) {
                authButton.setText("View Ticket(s)");
            }else{
                authButton.setVisibility(View.INVISIBLE);
                authButton.setEnabled(false);
            }
        }

        nameText.setEnabled(show);
        descText.setEnabled(show);
        dateText.setEnabled(show);
        locText.setEnabled(show);
        priceText.setEnabled(show);
        numTicksText.setEnabled(show);

        nameText.setText(event.getTitle());
        descText.setText(event.getDescription());
        dateText.setText(date);
        locText.setText(event.getAddress());
        if (event.getPrice() <= 0) {
            priceText.setText("Free");
        } else {
            priceText.setText("€" + event.getPrice().toString());
        }
        numTicksText.setText(event.getNumTicks() + "");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show) {
                    show = false;
                    fab.setBackgroundColor(Color.GREEN);
                    updateEvent();
                } else {
                    nameText.requestFocus();
                    show = true;
                    fab.setBackgroundColor(Color.BLACK);
                }
                nameText.setEnabled(show);
                descText.setEnabled(show);
                dateText.setEnabled(show);
                locText.setEnabled(show);
                priceText.setEnabled(show);
                numTicksText.setEnabled(show);
            }
        });

        if (event.getUserId().equals(userID)) {
            authButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(getActivity(), NFCDisplayActivity.class);
                            getActivity().startActivity(myIntent);
                        }
                    }
            );
        }else {
            authButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment fragment = ViewTicketFragment.newInstance(ticket);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.container, fragment).addToBackStack("createTicket");
                            ft.commit();
                        }
                    }
            );
        }

        if (!event.getUserId().equals(userID)) {
            button.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment fragment = CreateTicketFragment.newInstance(event);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.container, fragment).addToBackStack("createTicket");
                            ft.commit();
                        }
                    }
            );
        }else button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = EventStatsFragment.newInstance(event);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("createTicket");
                        ft.commit();
                    }
                }
        );

    }

    private void updateEvent() {
        if(!event.getTitle().equals(nameText.getText().toString()) || !event.getDescription().equals(descText.getText().toString()) || !new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(event.getDate()).equals(dateText.getText().toString()) || !event.getAddress().equals(locText.getText().toString()) || !event.getPrice().equals(Double.parseDouble(priceText.getText().toString().substring(1))) || event.getNumTicks() != Integer.parseInt(numTicksText.getText().toString())){
            event.setTitle(nameText.getText().toString());
            event.setDescription(descText.getText().toString());
            event.setAddress(locText.getText().toString());
            event.setNumTicks(Integer.parseInt(numTicksText.getText().toString()));
            event.setPrice(Double.parseDouble(priceText.getText().toString().substring(1)));
        }

    }

    private boolean hasTicket(){
        for(Ticket t: Ticket.getTickets()){
            if(t.getEvent().getId().equals(event.getId())){
                ticket = t;
                return true;
            }
        }
        return false;
    }
}
