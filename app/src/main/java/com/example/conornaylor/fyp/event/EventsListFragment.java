package com.example.conornaylor.fyp.event;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.activities.MapsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class EventsListFragment extends Fragment {

    public static final String MyPreferences = "preferences";
    public static final String EventPreferences = "eventPreferences";
    private SharedPreferences preferences;
    private String userIdString = "id";
    private String canMakeEventString = "canMakeEvent";
    private String userID;
    private boolean canMakeEvent;
    private String dateString;
    private String todayString;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private boolean isFABOpen = false;


    public EventsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Events");

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userID = preferences.getString(userIdString, null);
        canMakeEvent = preferences.getBoolean(canMakeEventString, false);

        Date date = new Date();
        todayString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date);

        ArrayList<Event> allEvents =  Event.getEvents();
        ArrayList<Event> events =  new ArrayList<>();
        for(Event e: allEvents){
            dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(e.getDate());
            if(e.getDate().after(date) || todayString.equals(dateString)){
                events.add(e);
            }
        }
        ListView eventsList = view.findViewById(R.id.eventList);
        ListAdapter myAdapter = new EventAdaptor(getActivity(), events);
        eventsList.setAdapter(myAdapter);
        ((BaseAdapter)myAdapter).notifyDataSetChanged();

        eventsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
//                        showProgress(true);
                        String event = String.valueOf((parent.getItemAtPosition(pos)));
                        Toast.makeText(getActivity(), Event.getEventByID(event).getTitle(), Toast.LENGTH_SHORT).show();
                        Fragment fragment = ViewEventFragment.newInstance((Event)parent.getItemAtPosition(pos));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("viewEvent");
                        ft.commit();
                    }
                }
        );

        FloatingActionButton fab0 = view.findViewById(R.id.fab0);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);
        fab3 = view.findViewById(R.id.fab3);

        fab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new CreateEventFragment()).addToBackStack("createEvent");
                ft.commit();
            }
        });
        if(!canMakeEvent) {
            fab2.hide();
        }

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), MapsActivity.class);
                getActivity().startActivity(myIntent);
            }
        });
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.fab_margin65));
        fab1.animate().translationX(-getResources().getDimension(R.dimen.fab_margin));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.fab_margin195));
        fab2.animate().translationX(-getResources().getDimension(R.dimen.fab_quadtriple));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.fab_margin130));
        fab3.animate().translationX(-getResources().getDimension(R.dimen.fab_margintriple));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab1.animate().translationX(0);
        fab2.animate().translationX(0);
        fab3.animate().translationX(0);
        fab3.animate().translationY(0);
    }
}
