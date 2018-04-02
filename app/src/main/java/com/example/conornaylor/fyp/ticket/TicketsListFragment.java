package com.example.conornaylor.fyp.ticket;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import com.example.conornaylor.fyp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicketsListFragment extends Fragment {

    private TabHost tabHost;
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;
    private String tk = "token";
    private String userId = "id";
    private String token;
    private String userID;
    private JSONObject obj;
    private JSONArray jArray;
    private ListAdapter myUpcomingAdapter;
    private ListAdapter myPastAdapter;
    private String todayString;
    private String dateString;

    public TicketsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_tickets, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Tickets");

        tabHost = getActivity().findViewById(R.id.tabhost1);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("Upcoming");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Past");

        tab1.setIndicator("Upcoming");
        tab1.setContent(R.id.Upcoming);

        tab2.setIndicator("Past");
        tab2.setContent(R.id.Past);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        Date date = new Date();
        todayString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date);

        // Inflate the layout for this fragment
        ArrayList<Ticket> myUpcomingTickets = new ArrayList<>();
        ArrayList<Ticket> myPastTickets = new ArrayList<>();
        ArrayList<Ticket> uniqueTicketForEvents = Ticket.getOneTicketPerEvent();
        ArrayList<ArrayList<Ticket>> splitTickets = Ticket.mapTicketsForEachEvent();

        for (Ticket t : uniqueTicketForEvents) {
            dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(t.getEvent().getDate());
            if (t.getEvent().getDate().after(date) || todayString.equals(dateString)) {
                myUpcomingTickets.add(t);
            } else {
                myPastTickets.add(t);
            }
        }

        for (ArrayList i : splitTickets) {
            for (Object t : i) {
                Ticket tick = (Ticket) t;
                System.out.println(tick.getEvent().getId());
            }
        }

        // Sort tickets by date.
        ListView upcomingEventsList = view.findViewById(R.id.upcoming_tickets);
        ListView pastEventsList = view.findViewById(R.id.past_tickets);
        myUpcomingAdapter = new TicketAdaptor(getActivity(), myUpcomingTickets);
        myPastAdapter = new TicketAdaptor(getActivity(), myPastTickets);
        upcomingEventsList.setAdapter(myUpcomingAdapter);
        pastEventsList.setAdapter(myPastAdapter);

        upcomingEventsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        Fragment fragment = ViewTicketFragment.newInstance((Ticket) parent.getItemAtPosition(pos));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("viewTicket");
                        ft.commit();
                    }
                }
        );

        pastEventsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        Fragment fragment = ViewTicketFragment.newInstance((Ticket) parent.getItemAtPosition(pos));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("viewTicket");
                        ft.commit();
                    }
                }
        );
    }
}
