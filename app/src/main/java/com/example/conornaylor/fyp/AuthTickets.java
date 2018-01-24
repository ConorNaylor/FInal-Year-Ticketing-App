package com.example.conornaylor.fyp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuthTickets extends Fragment {

    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String tk = "token";
    private String userId = "id";
    private String token;
    private String userID;
    private Event event;
    private AuthTickets.authTicketsTask mAuthTask;
    private String out;
    private ImageView iv;


    public AuthTickets() {
        // Required empty public constructor
    }

    public static AuthTickets newInstance(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", e);
        AuthTickets fragment = new AuthTickets();
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
        return inflater.inflate(R.layout.fragment_auth_tickets, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readBundle(getArguments());

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
        userID = preferences.getString(userId, null);

        iv = getActivity().findViewById(R.id.go);
        iv.setVisibility(view.INVISIBLE);

        mAuthTask = new authTicketsTask();
        mAuthTask.execute();
    }

    public void makeToast(String str){
        try {
            JSONObject obj = new JSONObject(str);
            if(!obj.getString("id").isEmpty()){
                Toast.makeText(getActivity(), "Ticket Verified!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "Ticket Failed Verification!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class authTicketsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                    String url = "http://192.168.0.59:8000/checkticket/";
//                String url = "http://192.168.1.2:8000/checkticket/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoInput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Token " + token);

                JSONObject ev = new JSONObject();
                try{
                    ev.put("event", event.getId());
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
                        out = sb.toString();
                        System.out.println(sb.toString());
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
            super.onPostExecute(b);
            makeToast(out);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

