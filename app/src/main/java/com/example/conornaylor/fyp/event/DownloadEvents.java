package com.example.conornaylor.fyp.event;

/**
 * Created by conornaylor on 25/03/2018.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ListAdapter;
import android.widget.TabHost;

import com.example.conornaylor.fyp.utilities.LocationData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by conornaylor on 17/02/2018.
 */

public class DownloadEvents {

    private TabHost tabHost;
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;
    private String tk = "token";
    private String userId = "id";
    private String token;
    private String userID;
    private getEventsTask mAuthTask;
    private JSONObject obj;
    private JSONArray jArray;
    private ListAdapter myUpcomingAdapter;
    private ListAdapter myPastAdapter;
    private Date date;


    public DownloadEvents(Context context){
        preferences = context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
        userID = preferences.getString(userId, null);
        mAuthTask = new getEventsTask();
    }

    public boolean execute(){
        try {
            return mAuthTask.execute().get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }
        return false;
    }



    public void makeEvents(JSONArray jArray) {
        try {
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        obj = jArray.getJSONObject(i);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    LocationData loc = new LocationData(obj.getDouble("loclat"), obj.getDouble("loclng"));
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    try {
                        date = formatter.parse(obj.getString("date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Event ev = new Event(
                            obj.getString("id"),
                            obj.getString("title"),
                            obj.getString("location"),
                            obj.getString("description"),
                            date,
                            obj.getInt("num_tickets"),
                            obj.getString("user"),
                            loc,
                            obj.getDouble("price"),
                            obj.getString("image"));
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }


    public class getEventsTask extends AsyncTask <Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void...params) {
            try {
                String url = "http://18.218.18.192:8000/events/"; // Galway
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
                        jArray = new JSONArray(sb.toString());
                        makeEvents(jArray);
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
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}