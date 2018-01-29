package com.example.conornaylor.fyp;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import java.util.ArrayList;


public class EventsListFragment extends Fragment {

    public static final String MyPreferences = "preferences";
    public static final String EventPreferences = "eventPreferences";
    private SharedPreferences preferences;
    private String userIdString = "id";
    private String canMakeEventString = "canMakeEvent";
    private String userID;
    private boolean canMakeEvent;


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

        ArrayList<Event> events =  Event.getEvents();
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

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new CreateEventFragment()).addToBackStack("createEvent");
                ft.commit();
            }
        });
        System.out.println("Can make events :" + canMakeEvent);
        if(!canMakeEvent) {
            fab.hide();
        }
    }
}
