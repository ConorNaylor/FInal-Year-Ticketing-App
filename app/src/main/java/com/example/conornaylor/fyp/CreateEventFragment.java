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
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
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
public class CreateEventFragment extends Fragment {


    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private getEventsTask mAuthTask = null;
    private EditText eventName;
    private EditText eventDesc;
    private EditText eventLoc;
    private EditText eventDate;
    private Button createEvent;
    private String eventNameString;
    private String eventDescString;
    private String eventLocString;
    private String eventDateString;
    private Event event;
    private LocationData data = null;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);

        mAuthTask = new getEventsTask();

        eventName = (EditText) getActivity().findViewById(R.id.eventName);
        eventDesc = (EditText) getActivity().findViewById(R.id.eventDescription);
        eventLoc = (EditText) getActivity().findViewById(R.id.eventLocation);
        eventDate = (EditText) getActivity().findViewById(R.id.eventDate);
        createEvent = (Button) getActivity().findViewById(R.id.button_create_event);

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventNameString = eventName.getText().toString();
                eventDescString = eventDesc.getText().toString();
                eventLocString = eventLoc.getText().toString();
                eventDateString = eventDate.getText().toString();
                mAuthTask.execute();
            }
        });

    }


    public class getEventsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                    String url = "http://192.168.0.59:8000/events/";
//                String url = "http://192.168.1.10:8000/events/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Token " + token);
                con.setRequestMethod("PUT");

               JSONObject ev = new JSONObject();
                try{
                    ev.put("title", eventNameString);
                    ev.put("description", eventDescString);
                    ev.put("location", eventLocString);
                    ev.put("date", eventDateString);
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
                        System.out.println(sb.toString());
                    } else {
                        System.out.println("Nothing here boss.");
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new EventFragment());
                ft.commit();
                event = new Event(eventNameString, eventDescString, eventLocString, eventDateString, data);
            }else{

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
