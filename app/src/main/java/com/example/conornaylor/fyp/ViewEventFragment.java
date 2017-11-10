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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


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
    private boolean show = false;
    private FloatingActionButton fab;
    private boolean isEventOwner = false;


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
        show = false;
        fab = getActivity().findViewById(R.id.fab2);
        button = getActivity().findViewById(R.id.viewStatsButton);
        nameText = getActivity().findViewById(R.id.viewEventName);
        descText = getActivity().findViewById(R.id.viewEventDescription);
        dateText = getActivity().findViewById(R.id.viewEventDate);
        priceText = getActivity().findViewById(R.id.viewEventPrice);
        numTicksText = getActivity().findViewById(R.id.viewnumTickets);
        locText = getActivity().findViewById(R.id.viewEventLocation);

        if(!isEventOwner) {
            fab.setVisibility(View.INVISIBLE);
            numTicksText.setVisibility(View.INVISIBLE);
            button.setText("Buy Ticket");
        }

        nameText.setEnabled(show);
        descText.setEnabled(show);
        dateText.setEnabled(show);
        locText.setEnabled(show);
        priceText.setEnabled(show);
        numTicksText.setEnabled(show);

        nameText.setText(event.getTitle());
        descText.setText(event.getDescription());
        dateText.setText(event.getDate());
        locText.setText(event.getAddress());
//        priceText.setText(event.getPrice().toString());
        numTicksText.setText(( event.getNumTicks()) + "");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show == true) {
                    show = false;
                    fab.setBackgroundColor(Color.GREEN);
                }else { show = true; }
                nameText.setEnabled(show);
                descText.setEnabled(show);
                dateText.setEnabled(show);
                locText.setEnabled(show);
                priceText.setEnabled(show);
                numTicksText.setEnabled(show);
            }
        });

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = CreateTicketFragment.newInstance(event);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.commit();
                    }
                }
        );
    }
}
