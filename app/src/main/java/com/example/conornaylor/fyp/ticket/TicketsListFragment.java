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

import java.util.ArrayList;
import java.util.Date;


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

        // Inflate the layout for this fragment
        ArrayList<Ticket> myUpcomingTickets = new ArrayList<>();
        ArrayList<Ticket> myPastTickets = new ArrayList<>();
        ArrayList<String> myUpcomingTicketsforAdding = new ArrayList<>();
        ArrayList<String> myPastTicketsforAdding = new ArrayList<>();

        for (Ticket t : Ticket.getTickets()) {
            if (t.getEvent().getDate().after(date)) {
                if(myUpcomingTickets.isEmpty()){
                    myUpcomingTickets.add(t);
                }else {
                    for(int i = 0; i < myUpcomingTicketsforAdding.size(); i++) {
                        System.out.println(myUpcomingTickets.size() + " is the size of the upcoming events array");
                        System.out.println(myUpcomingTickets.get(i).getEvent().getId());
                        if (!myUpcomingTicketsforAdding.get(i).equals(t.getEvent().getId())) {
                            System.out.println(myUpcomingTickets.get(i).getEvent().getId().equals(t.getEvent().getId()) +" blah blah blah blah blah Different Event upcoming");
//                            myUpcomingTickets.add(t);
                            myUpcomingTicketsforAdding.add(t.getEvent().getId());
                        } else System.out.println(myUpcomingTickets.get(i).getEvent().getId().equals(t.getEvent().getId()) + "  blah blah blah blah blah  Same Event upcoming ");
                    }
                }
            } else {
                if(myPastTickets.isEmpty()){
                    myPastTickets.add(t);
                } else {
                    for (int i = 0; i < myPastTicketsforAdding.size(); i++) {
                        System.out.println(myPastTickets.size() + " is the size of the past events array");
                        System.out.println(myPastTickets.get(i).getEvent().getId());
                        if (!myPastTicketsforAdding.get(i).equals(t.getEvent().getId())) {
                            System.out.println(" blah blah blah blah blah Different Event past");
//                            myPastTickets.add(t);
                            myPastTicketsforAdding.add(t.getEvent().getId());
                        } else System.out.println(" blah blah blah blah blah Same Event past");
                    }
                }
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
//                        showProgress(true);
//                        String ticket = String.valueOf((parent.getItemAtPosition(pos)));
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
//                        showProgress(true);
//                        String ticket = String.valueOf((parent.getItemAtPosition(pos)));
                        Fragment fragment = ViewTicketFragment.newInstance((Ticket) parent.getItemAtPosition(pos));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("viewTicket");
                        ft.commit();
                    }
                }
        );
    }

}
