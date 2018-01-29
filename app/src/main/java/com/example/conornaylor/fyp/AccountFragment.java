package com.example.conornaylor.fyp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.onClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private TextView mEmailView;
    private TextView mNameView;
    private CheckBox makeEvents;
    private ArrayList<Event> myEvents;
    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;
    private String userIdString = "id";
    private String tk = "token";
    private String usernameString = "username";
    private String canMakeEvs = "canMakeEvent";
    private String userEmailString = "email";
    private String userID;
    private String username;
    private String userEmail;
    private String token;
    private boolean canMakeEvents;
    private boolean checked;
    private setBooleanTask mAuthTask = null;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Account");

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        e = preferences.edit();
        userID = preferences.getString(userIdString, null);
        userEmail = preferences.getString(userEmailString, null);
        username = preferences.getString(usernameString, null);
        canMakeEvents = preferences.getBoolean(canMakeEvs, false);
        token = preferences.getString(tk, null);


        mEmailView = view.findViewById(R.id.email);
        mNameView = view.findViewById(R.id.name);
        makeEvents = view.findViewById(R.id.createEventBox);

        makeEvents.setChecked(canMakeEvents);
        mEmailView.setText(userEmail);
        mNameView.setText(username);

        mEmailView.setSelected(false);
        mNameView.setSelected(false);

        mEmailView.setEnabled(false);
        mNameView.setEnabled(false);

        myEvents = new ArrayList();
        for(Event e: Event.getEvents()) {
            if(userID.equals(e.getUserId())) {
                myEvents.add(e);
            }
        }

        ListView accountEventsList = view.findViewById(R.id.yourEvents);
        ListAdapter myAccountAdapter = new EventAdaptor(getActivity(), myEvents);
        accountEventsList.setAdapter(myAccountAdapter);

        accountEventsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        String event = String.valueOf((parent.getItemAtPosition(pos)));
                        Toast.makeText(getActivity(), Event.getEventByID(event).getTitle(), Toast.LENGTH_SHORT).show();
                        Fragment fragment = ViewEventFragment.newInstance((Event)parent.getItemAtPosition(pos));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment).addToBackStack("viewEvent");
                        ft.commit();
                    }
                }
        );

        makeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = makeEvents.isChecked();
                mAuthTask = new setBooleanTask();
                mAuthTask.execute();
                e.putBoolean(canMakeEvs, checked);
                e.apply();
            }
        });


    }

    public class setBooleanTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
//                String url = "http://192.168.0.59:8000/event/";
                String url = "http://192.168.1.13:8000/profile/";
                URL object = new URL(url);

                HttpURLConnection c = (HttpURLConnection) object.openConnection();
                c.setDoInput(true);
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Authorization", "Token " + token);
                c.setDoOutput(true);
                c.connect();
                OutputStream output = c.getOutputStream();

                JSONObject ev = new JSONObject();
                try {
                    ev.put(canMakeEvs, checked);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                output.write(ev.toString().getBytes("utf-8"));
                output.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = c.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(c.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                } else {
                    System.out.println(c.getResponseMessage());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            mAuthTask = null;
            super.onPostExecute(b);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

}
