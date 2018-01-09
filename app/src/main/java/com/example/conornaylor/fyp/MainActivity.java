package com.example.conornaylor.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MyPreferences = "preferences";
    public static final String EventPreferences = "eventPreferences";
    private SharedPreferences preferences;
    private SharedPreferences eventPreferences;
    private SharedPreferences.Editor e;
    private SharedPreferences.Editor e2;
    private String tokenString = "token";
    private String userIdString = "id";
    private String token;
    private String userID;
    private getEventsTask mAuthTask;
    private getEventsTask mAuthTaskRefresh;
    private JSONObject obj;
    private JSONArray jArray;
    private JSONArray jsonArray;
    private Boolean doubleBackToExitPressedOnce = false;
    private Handler mHandler = new Handler();
    private SerializableManager sm = new SerializableManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tokenString, null);
        userID = preferences.getString(userIdString, null);

        mAuthTask = new MainActivity.getEventsTask();
        try {
            Boolean done = mAuthTask.execute().get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new EventFragment());
        ft.commit();
    }

//    public JSONArray getEventPreferences() {
//        String array = "";
//        eventPreferences = getSharedPreferences(EventPreferences, Context.MODE_PRIVATE);
//        Map<String, ?> allEntries = eventPreferences.getAll();
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            array = array + entry.getValue().toString();
//            System.out.println(array);
//        }
//        try {
//            jsonArray = new JSONArray(array);
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }
//        return jsonArray;
//    }
//
//    public void saveEventPreferences(Event event){
//        eventPreferences = getSharedPreferences(EventPreferences, Context.MODE_PRIVATE);
//        e2 = eventPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(event);
//        e2.putString(event.getId(), json);
//        e2.commit();
//    }

    public void makeEvents(JSONArray jArray){
        try {
//            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                        try {
                            obj = jArray.getJSONObject(i);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    LocationData loc = new LocationData(0, 0);
                    Event ev = new Event(
                            obj.getString("id"),
                            obj.getString("title"),
                            obj.getString("location"),
                            obj.getString("description"),
                            obj.getString("date"),
                            obj.getInt("num_tickets"),
                            obj.getString("user"),
                            loc);
                }
//            }
//            else return;
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
            SharedPreferences settings = this.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
            settings.edit().clear().apply();
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK again to Log Out", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            SharedPreferences settings = this.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
            settings.edit().clear().apply();
            return true;
        }
        if (id == R.id.refresh) {
            mAuthTaskRefresh = new MainActivity.getEventsTask();
            mAuthTaskRefresh.execute();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new EventFragment());
            ft.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_events) {
           fragment = new EventFragment();
        } else if (id == R.id.nav_account) {
            fragment = new AccountFragment();
        } else if (id == R.id.nav_tickets) {
            fragment = new ViewTicketsFragment();
        } else if (id == R.id.nav_share) {

        }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class getEventsTask extends AsyncTask <Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void...params) {
                try {
//                    String url = "http://192.168.0.59:8000/events/";
                    String url = "http://192.168.1.10:8000/events/";
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
//                            System.out.println(sb.toString());
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
        protected void onPostExecute(Boolean b) { super.onPostExecute(b); }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
