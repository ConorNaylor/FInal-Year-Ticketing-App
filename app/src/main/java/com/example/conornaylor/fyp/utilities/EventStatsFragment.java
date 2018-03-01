package com.example.conornaylor.fyp.utilities;


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
import android.widget.TextView;
import android.widget.Toast;

import com.example.conornaylor.fyp.event.Event;
import com.example.conornaylor.fyp.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventStatsFragment extends Fragment {

    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private EventStatsFragment.getStats mAuthTask = null;
    private Event event;
    private TextView numTickets;
    private TextView revenue;
    private Double rev;
    private int numberTicks;
    private String input;
    private Gson gson;

    public EventStatsFragment() {
        // Required empty public constructor
    }

    public static EventStatsFragment newInstance(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", e);
        EventStatsFragment fragment = new EventStatsFragment();
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
        return inflater.inflate(R.layout.fragment_event_stats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());

        getActivity().setTitle(event.getTitle());

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);

        mAuthTask = new EventStatsFragment.getStats();
        mAuthTask.execute();

        numTickets = getActivity().findViewById(R.id.num_sold);
        revenue = getActivity().findViewById(R.id.revenue);

        numTickets.setSelected(false);
        revenue.setSelected(false);

//        numTickets.setEnabled(false);
//        revenue.setEnabled(false);


        numTickets.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if( numberTicks < 1 ){
                            Toast.makeText(getActivity(), "No tickets sold.", Toast.LENGTH_SHORT).show();
                        }else {
                            Fragment fragment = AttendeeListFragment.newInstance(event);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.container, fragment).addToBackStack("users");
                            ft.commit();
                        }
                    }
                }
        );

//        GraphView graph = getActivity().findViewById(R.id.graphView);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(1, 4),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3)
//        });
//        graph.addSeries(series);
    }


    public void parseInput(String in) {
        if (!in.isEmpty()) {
            try {
                gson = new Gson();
                JSONObject obj = new JSONObject(in);
                numberTicks = Integer.valueOf(obj.getString("count"));
                rev = numberTicks * event.getPrice();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            numTickets.setText(Integer.toString(numberTicks));
            revenue.setText("€" + rev.toString());
        }
    }


    public class getStats extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void...params) {
            try {
                String url = "http://18.218.18.192:8000/numtickets/";
//                    String url = "http://192.168.1.2:8000/numtickets/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Token " + token);

                JSONObject ev = new JSONObject();
                try{
                    ev.put("event", event.getId());
                }catch(JSONException e){
                    e.printStackTrace();
                }

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(ev.toString());
                wr.flush();

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
                        input = sb.toString();
                    }
                } else {
                    System.out.println(con.getResponseMessage());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b) {
                parseInput(input);
            }else{
                super.onPostExecute(b);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
