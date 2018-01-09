package com.example.conornaylor.fyp;


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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewTicketFragment extends Fragment {

    private Ticket ticket;
    private TextView nameText;
    private TextView dateText;
    private TextView seatText;
    private TextView priceText;
    private TextView locText;
    private Button button;
    private FloatingActionButton fab;
    private boolean isTicketOwner = true;

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

        getActivity().setTitle(ticket.getEvent().getTitle());
//        show = false;
        fab = getActivity().findViewById(R.id.enterEventFAB);
        button = getActivity().findViewById(R.id.viewEventButton);
        nameText = getActivity().findViewById(R.id.viewEventName);
        dateText = getActivity().findViewById(R.id.viewTicketDate);
        priceText = getActivity().findViewById(R.id.viewTicketPrice);
        seatText = getActivity().findViewById(R.id.viewTicketSeat);
        locText = getActivity().findViewById(R.id.viewEventLocation);

        if(!isTicketOwner) {
            fab.setVisibility(View.INVISIBLE);
        }

        nameText.setText(ticket.getEvent().getTitle());
        dateText.setText(ticket.getEvent().getDate().toString());
        seatText.setText(ticket.getSeat());
        locText.setText(ticket.getEvent().getAddress());
        priceText.setText("Free");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new EnterFragment());
                ft.commit();
            }
        });

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = ViewEventFragment.newInstance(ticket.getEvent());
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.commit();
                    }
                }
        );
    }

}
