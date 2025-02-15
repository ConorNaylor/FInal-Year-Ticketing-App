package com.example.conornaylor.fyp.ticket;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
public class CreateTicketFragment extends Fragment {

    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private String userId = "id";
    private String userID;
    private Ticket ticket;
    private CreateTicketFragment.createTicketTask mAuthTask = null;
    private CreateTicketFragment.getSeatTask getSeatTask = null;
    private Event event;
    private Button button;
    private TextView eventName;
    private TextView eventPrice;
    private String input;
    private String numberTicketsInput;
    private ProgressBar spinner;
    private View mProgressView;
    private View mLoginFormView;
    private String seat;


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
            ticket = new Ticket(
                    obj.getString("id"),
                    obj.getString("seat"),
                    obj.getString("user"),
                    event,
                    false
            );
        }catch(JSONException e ) {
            e.printStackTrace();
        }
    }

    private String getSeat(String in){
        try {
            JSONObject obj = new JSONObject(in);
            int s = obj.getInt("count");
            if(s >= event.getNumTicks()){
                getSeatTask = null;
                showProgress(false);
                Toast.makeText(getActivity(),"Sorry, this event is sold out.",Toast.LENGTH_SHORT).show();
                return null;
            }
            int count = 0;
            int sum = s;
            int[] ints = new int[100];
            char[] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
            for (int i = 0; i < 100; ++i) {
                ints[i] = i + 1;
            }
            while(sum - 100 > 100) {
                count++;
                sum = sum - 100;
            }

            return chars[count] + Integer.toString(ints[sum]);
        }catch(JSONException e ) {
            e.printStackTrace();
        }
        return null;
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
        userID = preferences.getString(userId, null);

        mAuthTask = new createTicketTask();

        button = getActivity().findViewById(R.id.confirm_purchase);
        eventName = getActivity().findViewById(R.id.confirmEventName);
        eventPrice = getActivity().findViewById(R.id.confirmPurchasePrice);
        mLoginFormView = getActivity().findViewById(R.id.event_form);
        mProgressView = getActivity().findViewById(R.id.create_ticket_progress);

        eventName.setText(event.getTitle());
        if(event.getPrice() <= 0){
            eventPrice.setText("Free");
        }else {
            eventPrice.setText("€" + event.getPrice().toString());
        }

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSeatTask = new getSeatTask();
                        getSeatTask.execute();
                        showProgress(true);
                    }
                }
        );
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class createTicketTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://18.218.18.192:8000/tickets/";
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
                    ev.put("seat", seat);
                    ev.put("user", userID);
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
            mAuthTask = null;
            showProgress(false);
            if(b) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new TicketsListFragment());
                ft.commit();
                makeTicket(input);
            }else{

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class getSeatTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://18.218.18.192:8000/numtickets/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Token " + token);
                con.setRequestMethod("POST");

                JSONObject ev = new JSONObject();
                try {
                    ev.put("event", event.getId());
                } catch (JSONException e) {
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
                        numberTicketsInput = sb.toString();
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
            getSeatTask = null;
            if (b) {
                seat = getSeat(numberTicketsInput);
                if(seat != null) {
                    mAuthTask.execute();
                }
            }
        }

        @Override
        protected void onCancelled () {
            getSeatTask = null;
        }
    }
}
