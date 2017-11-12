package com.example.conornaylor.fyp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewTicketsFragment extends Fragment {

    private TabHost tabHost;

    public ViewTicketsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_tickets, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabHost = getActivity().findViewById(R.id.tabhost1);

        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third tab");

        tab1.setIndicator("Tab1");
        tab1.setContent(R.id.Past);
        tabHost.addTab(tab1);


        // Inflate the layout for this fragment
        ArrayList<Ticket> myUpcomingTickets =  Ticket.getTickets();
        ArrayList<Ticket> myPastTickets = Ticket.getTickets();
        ListView upcomingEventsList = view.findViewById(R.id.upcoming_tickets);
        ListView pastEventsList = view.findViewById(R.id.past_tickets);
        ListAdapter myUpcomingAdapter = new TicketAdaptor(getActivity(), myUpcomingTickets);
        ListAdapter myPastAdapter = new TicketAdaptor(getActivity(), myPastTickets);
        upcomingEventsList.setAdapter(myUpcomingAdapter);
        pastEventsList.setAdapter(myPastAdapter);

    }
}
