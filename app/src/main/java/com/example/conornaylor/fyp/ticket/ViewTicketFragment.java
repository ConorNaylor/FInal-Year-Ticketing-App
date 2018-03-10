package com.example.conornaylor.fyp.ticket;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.conornaylor.fyp.activities.EnterActivity;
import com.example.conornaylor.fyp.event.ViewEventFragment;
import com.example.conornaylor.fyp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewTicketFragment extends Fragment {

    private Ticket ticket;
    private TextView nameText;
    private TextView dateText;
    private TextView priceText;
    private TextView locText;
    private ImageView ticketImage;
    private Button button;
    private CheckBox enteredBox;
    private FloatingActionButton fab;
    private boolean isTicketOwner = true;
    private String date;
    private String todayString;
    private ArrayList<Ticket> tickets;
    private Spinner spinner;
    private int iCurrentSelection;

    public ViewTicketFragment() {
        // Required empty public constructor
    }

    public static ViewTicketFragment newInstance(Ticket t) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("ticket", t);
        ViewTicketFragment fragment = new ViewTicketFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            ticket = (Ticket)bundle.getSerializable("ticket");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_ticket, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());

        tickets = Ticket.getAllTicketsForEvent(ticket.getEvent());
        for(int i = 0; i < tickets.size(); i++){
            if(tickets.get(i) == ticket){
            iCurrentSelection = i;
            }
        }

        getActivity().setTitle(ticket.getEvent().getTitle());

        Date today = new Date();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(ticket.getEvent().getDate());
        todayString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(today);

        fab = getActivity().findViewById(R.id.enterEventFAB);
        button = getActivity().findViewById(R.id.viewEventButton);
        nameText = getActivity().findViewById(R.id.viewEventName);
        dateText = getActivity().findViewById(R.id.viewTicketDate);
        priceText = getActivity().findViewById(R.id.viewTicketPrice);
        locText = getActivity().findViewById(R.id.viewEventLocation);
        ticketImage = getActivity().findViewById(R.id.imageViewTicket);
        enteredBox = getActivity().findViewById(R.id.enteredRadio);
        spinner = getActivity().findViewById(R.id.ticketSpinner);

        enteredBox.setChecked(ticket.isEntered());
        enteredBox.setClickable(false);


        if(!isTicketOwner || !date.equals(todayString) || ticket.isEntered() ) {
            fab.setVisibility(View.INVISIBLE);
        }

        nameText.setText(ticket.getEvent().getTitle());
        dateText.setText(date);
        locText.setText(ticket.getEvent().getAddress());
        if(ticket.getEvent().getPrice() <= 0){
            priceText.setText("Free");
        }else {
            priceText.setText("€" + ticket.getEvent().getPrice().toString());
        }

        Glide.with(getActivity())
                .load("http://18.218.18.192:8000"  + ticket.getEvent().getImageURL())
                .fitCenter()
                .into(ticketImage);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnterActivity.class);
                intent.putExtra("ticket_id", ticket.getId());
                startActivity(intent);
            }
        });

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = ViewEventFragment.newInstance(ticket.getEvent());
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("viewEventforTicket");;
                        ft.commit();
                    }
                }
        );

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, tickets);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(iCurrentSelection);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (iCurrentSelection != i){
                    Ticket t = (Ticket) adapterView.getItemAtPosition(i);
                    Fragment fragment = ViewTicketFragment.newInstance(t);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container, fragment).addToBackStack(t.getSeat());
                    ft.commit();
                }
                iCurrentSelection = i;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

}
