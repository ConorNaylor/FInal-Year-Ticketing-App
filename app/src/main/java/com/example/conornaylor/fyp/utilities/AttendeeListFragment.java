package com.example.conornaylor.fyp.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.conornaylor.fyp.attendee.Attendee;
import com.example.conornaylor.fyp.attendee.AttendeeAdaptor;
import com.example.conornaylor.fyp.event.Event;
import com.example.conornaylor.fyp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeeListFragment extends Fragment {


    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private AttendeeListFragment.getUsers mAuthTask = null;
    private Event event;
    private TextView userView;
    private String input;
    private ArrayList<Attendee> attendees;
    private JSONArray jArray;
    private JSONObject obj;
    private Bundle bundle;
    private View mProgressView;
    private View mLoginFormView;
    private ListView attendeeList;
    private ListAdapter myAdapter;
    private Date date;


    public AttendeeListFragment() {
        // Required empty public constructor
    }

    public static AttendeeListFragment newInstance(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", e);
        AttendeeListFragment fragment = new AttendeeListFragment();
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
        return inflater.inflate(R.layout.fragment_event_attendee, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());

        getActivity().setTitle(event.getTitle());

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);

        mAuthTask = new AttendeeListFragment.getUsers();
        mAuthTask.execute();

        mProgressView = getActivity().findViewById(R.id.eventListProgress);
        mLoginFormView = getActivity().findViewById(R.id.main_content);

        attendees = new ArrayList<>();

        attendeeList = view.findViewById(R.id.attendeeList);

    }


    public void makeUsers(String in) {
        try {
            jArray = new JSONArray(in);
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    obj = jArray.getJSONObject(i);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    try {
                        date = formatter.parse(obj.getString("purchase_date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Attendee a = new Attendee(
                            obj.getString("username"),
                            obj.getString("user_id"),
                            obj.getString("ticket_id"),
                            date,
                            obj.getBoolean("entered"));
                    attendees.add(a);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeAdaptor();
    }

    public void makeAdaptor(){
        myAdapter = new AttendeeAdaptor(getActivity(), attendees);
        attendeeList.setAdapter(myAdapter);
        ((BaseAdapter) myAdapter).notifyDataSetChanged();
    }


    public class getUsers extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void...params) {
            try {
                String url = "http://18.218.18.192:8000/getusers/";
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
                        System.out.println(input);
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
                makeUsers(input);
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
