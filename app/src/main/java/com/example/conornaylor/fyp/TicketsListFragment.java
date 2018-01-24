package com.example.conornaylor.fyp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;


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
    private getTicketsTask mAuthTask;
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

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
        userID = preferences.getString(userId, null);

        mAuthTask = new getTicketsTask();
        try {
            Boolean done = mAuthTask.execute().get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

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

        for(Ticket t: Ticket.getTickets()){
            if(t.getEvent().getDate().after(date)){
                myUpcomingTickets.add(t);
            }else{
                myPastTickets.add(t);
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
                        Fragment fragment = ViewTicketFragment.newInstance((Ticket)parent.getItemAtPosition(pos));
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
                        Fragment fragment = ViewTicketFragment.newInstance((Ticket)parent.getItemAtPosition(pos));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("viewTicket");
                        ft.commit();
                    }
                }
        );
    }

    public void makeTickets(JSONArray jArray){
        try {
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    obj = jArray.getJSONObject(i);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                Ticket tk = new Ticket(
                        obj.getString("id"),
                        obj.getString("seat"),
                        obj.getString("user"),
                        Event.getEventByID(obj.getString("event")));
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }


    public class getTicketsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void...params) {
            try {
                    String url = "http://192.168.0.59:8000/tickets/";
//                String url = "http://192.168.1.2:8000/tickets/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoInput(true);
                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Token " + token);

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    if (sb.toString() != null) {
                        System.out.println(sb.toString());
                        jArray = new JSONArray(sb.toString());
                        makeTickets(jArray);
                    }
                } else {
                    System.out.println(con.getResponseMessage());
                    return false;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) { super.onPostExecute(b); }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
