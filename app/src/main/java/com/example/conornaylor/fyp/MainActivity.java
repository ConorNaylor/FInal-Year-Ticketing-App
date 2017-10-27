package com.example.conornaylor.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;
    private String tk = "token";
    private String token;
    private getEventsTask mAuthTask;
    private JSONObject obj;
    private JSONArray jArray;
    private ArrayList<Event> events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        String[] events = {"Ed Sheeran", "Avicii", "BoltyWolty", "Messi"};
        ListView eventsList = (ListView) findViewById(R.id.eventList);
        ListAdapter myAdapter = new EventAdaptor(this, events);
        eventsList.setAdapter(myAdapter);

        eventsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        String event = String.valueOf((parent.getItemAtPosition(pos)));
                        Toast.makeText(MainActivity.this, event, Toast.LENGTH_LONG).show();
                    }
                }
        );

        preferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
        System.out.println("Token:"+token);

        mAuthTask = new MainActivity.getEventsTask();
        mAuthTask.execute();
        
    }

    public void makeEvents(JSONArray jArray){
        events = new ArrayList<>();
        try {
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    obj = jArray.getJSONObject(i);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                LocationData loc = new LocationData(0, 0);
                Event ev = new Event(
                        obj.getString("title"),
                        obj.getString("description"),
                        obj.getString("location"),
                        obj.getString("date"),
                        loc);
                events.add(ev);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Intent closeAppIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(closeAppIntent);
            finish();
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            // Handle the camera action
        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class getEventsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String url = "http://192.168.0.59:8000/events/";
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
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    if (sb.toString() != null) {
                        jArray = new JSONArray(sb.toString());
                        System.out.println(jArray.toString());
                        makeEvents(jArray);
                    } else {
                        Toast.makeText(MainActivity.this, "There are no events.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    System.out.println(con.getResponseMessage());
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "No connection!, Check your network.", Toast.LENGTH_SHORT).show();
                return false;
            }
            return false;
        }
    }
}
