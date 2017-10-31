package com.example.conornaylor.fyp;


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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class EventFragment extends Fragment {

    private boolean isOrganiser;


    public EventFragment() {
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

        String[] events = {"Ed Sheeran", "Avicii", "BoltyWolty", "Messi"};
        ListView eventsList = (ListView) view.findViewById(R.id.eventList);
        ListAdapter myAdapter = new EventAdaptor(getActivity(), events);
        eventsList.setAdapter(myAdapter);

        eventsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        String event = String.valueOf((parent.getItemAtPosition(pos)));
                        Toast.makeText(getActivity(), event, Toast.LENGTH_LONG).show();
                    }
                }
        );

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new CreateEventFragment());
                ft.commit();
            }
        });
        if(isOrganiser) {
            fab.hide();
        }
    }
}
