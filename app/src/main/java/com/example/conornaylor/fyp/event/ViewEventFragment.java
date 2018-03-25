package com.example.conornaylor.fyp.event;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.activities.MapsActivity;
import com.example.conornaylor.fyp.ticket.CreateTicketFragment;
import com.example.conornaylor.fyp.ticket.Ticket;
import com.example.conornaylor.fyp.ticket.ViewTicketFragment;
import com.example.conornaylor.fyp.utilities.NFCDisplayActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewEventFragment extends Fragment {

    private Event event;
    private EditText nameText;
    private TextView descText;
    private EditText dateText;
    private EditText priceText;
    private EditText numTicksText;
    private TextView locText;
    private Button button;
    private Button authButton;
    private ImageView eventImageView;
    private ImageView mapImage;
    private boolean show = false;
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String userIdString = "id";
    private String userID;
    private String dateString;
    private String todayString;
    private Ticket ticket;
    private Date date;


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

//        show = false;
        authButton = getActivity().findViewById(R.id.auth_button);
        button = getActivity().findViewById(R.id.viewStatsButton);
        nameText = getActivity().findViewById(R.id.viewEventName);
        descText = getActivity().findViewById(R.id.viewEventDescription);
        dateText = getActivity().findViewById(R.id.viewEventDate);
        priceText = getActivity().findViewById(R.id.viewEventPrice);
        numTicksText = getActivity().findViewById(R.id.viewnumTickets);
        locText = getActivity().findViewById(R.id.viewEventLocation);
        eventImageView = getActivity().findViewById(R.id.viewEventImage);
        mapImage = getActivity().findViewById(R.id.mapImage);

        Glide.with(getActivity())
                .load("http://18.218.18.192:8000"  + event.getImageURL())
                .centerCrop()
                .into(eventImageView);


        date = new Date();
        todayString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date);
        dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(event.getDate());

        if (!event.getUserId().equals(userID)) {
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
        priceText.setEnabled(false);
        numTicksText.setEnabled(show);

        nameText.setText(event.getTitle());
        descText.setText(event.getDescription());
        dateText.setText(dateString);
        locText.setText(event.getAddress());
        if (event.getPrice() <= 0) {
            priceText.setText("Free");
        } else {
            priceText.setText("€" + event.getPrice().toString());
        }
        numTicksText.setText(event.getNumTicks() + "");

        {

            authButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (event.getUserId().equals(userID)) {
                                if (dateString.equals(todayString)) {
                                    Intent myIntent = new Intent(getActivity(), NFCDisplayActivity.class);
                                    getActivity().startActivity(myIntent);
                                } else{
                                    Toast.makeText(getActivity(), "Only available on day of event.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Fragment fragment = ViewTicketFragment.newInstance(ticket);
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.container, fragment).addToBackStack("viewTicket").addToBackStack(ticket.getSeat());
                                ft.commit();
                            }
                        }
                    }
            );
        }

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (event.getUserId().equals(userID)) {
                            Fragment fragment = EventStatsFragment.newInstance(event);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.container, fragment).addToBackStack("stats");
                            ft.commit();
                        } else {
                            if(date.before(event.getDate()) || dateString.equals(todayString)){
                                Fragment fragment = CreateTicketFragment.newInstance(event);
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.container, fragment).addToBackStack("createTicket");
                                ft.commit();
                            }else
                                Toast.makeText(getActivity(), "Only available before event.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        mapImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(getActivity(), MapsActivity.class).putExtra("event", event.getId());
                        getActivity().startActivity(myIntent);
                    }
                });

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

