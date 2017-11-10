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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
public class CreateTicketFragment extends Fragment {

    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private Ticket ticket;
    private CreateTicketFragment.createTicketTask mAuthTask = null;
    private Event event;
    private Button button;
    private TextView eventName;
    private TextView eventPrice;
    private String input;

    public CreateTicketFragment() {
        // Required empty public constructor
    }

    public static CreateTicketFragment newInstance(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", e);
        CreateTicketFragment fragment = new CreateTicketFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            event = (Event)bundle.getSerializable("event");
        }
    }

    private void makeTicket(String input) {
        try {
            JSONObject obj = new JSONObject(input);
        }catch(JSONException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_ticket, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);

        mAuthTask = new createTicketTask();

        button = getActivity().findViewById(R.id.confirm_purchase);
        eventName = getActivity().findViewById(R.id.confirmEventName);
        eventPrice = getActivity().findViewById(R.id.confirmPurchasePrice);

        eventName.setText(event.getTitle());
//        eventPrice.setText(event.getPrice().toString());

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, new AccountFragment());
                        ft.commit();
                    }
                }
        );
    }


    public class createTicketTask extends AsyncTask<Void, Void, Boolean> {
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
                    ev.put("event", event.getId());
                    ev.put("price", 10.50);
                    ev.put("seat", "5A");
                    ev.put("user", 1);
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new EventFragment());
                ft.commit();
                makeTicket(input);
            }else{

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
