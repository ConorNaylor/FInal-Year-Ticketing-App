package com.example.conornaylor.fyp.ticket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ListAdapter;
import android.widget.TabHost;

import com.example.conornaylor.fyp.event.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by conornaylor on 17/02/2018.
 */

public class DownloadTickets {

    public DownloadTickets(Context context){

        preferences = context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
        userID = preferences.getString(userId, null);

        mAuthTask = new getTicketsTask();
        mAuthTask.execute();
    }

    private TabHost tabHost;
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;
    private String tk = "token";
    private String userId = "id";
    private String token;
    private String userID;
    private DownloadTickets.getTicketsTask mAuthTask;
    private JSONObject obj;
    private JSONArray jArray;
    private ListAdapter myUpcomingAdapter;
    private ListAdapter myPastAdapter;


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
//                    String url = "http://192.168.0.59:8000/tickets/";
                String url = "http://18.218.18.192:8000/tickets/";
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

