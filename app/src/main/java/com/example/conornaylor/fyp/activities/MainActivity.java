package com.example.conornaylor.fyp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.conornaylor.fyp.ticket.Ticket;
import com.example.conornaylor.fyp.ticket.ViewTicketFragment;
import com.example.conornaylor.fyp.utilities.AccountFragment;
import com.example.conornaylor.fyp.event.Event;
import com.example.conornaylor.fyp.event.EventsListFragment;
import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.ticket.DownloadTickets;
import com.example.conornaylor.fyp.ticket.TicketsListFragment;
import com.example.conornaylor.fyp.utilities.LocationData;
import com.example.conornaylor.fyp.utilities.SerializableManager;

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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String tokenString = "token";
    private String userIdString = "id";
    private String canMakeEventString = "canMakeEvents";
    private String token;
    private String userID;
    private boolean canMakeEvent;
    private getEventsTask mAuthTask;
    private getEventsTask mAuthTaskRefresh;
    private JSONObject obj;
    private JSONArray jArray;
    private JSONArray jsonArray;
    private Boolean doubleBackToExitPressedOnce = false;
    private Handler mHandler = new Handler();
    private SerializableManager sm = new SerializableManager();
    private Date date;
    private Fragment fragment;
    private DownloadTickets dt;
    private NfcAdapter nfcAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(NfcAdapter.getDefaultAdapter(this) != null){
        nfcAdaptor = NfcAdapter.getDefaultAdapter(this);
        nfcAdaptor.setNdefPushMessage(null, this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tokenString, null);
        userID = preferences.getString(userIdString, null);
        canMakeEvent = preferences.getBoolean(canMakeEventString, false);

        mAuthTask = new MainActivity.getEventsTask();
        try {
            Boolean done = mAuthTask.execute().get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        dt = new DownloadTickets(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

                    LocationData loc = new LocationData(0, 0);
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new EventsListFragment());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (getVisibleFragment() instanceof ViewTicketFragment){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new TicketsListFragment()).addToBackStack("blah");
            ft.commit();
        }else if (getVisibleFragment() instanceof TicketsListFragment){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new EventsListFragment());
            ft.commitAllowingStateLoss();
        }else if (getVisibleFragment() instanceof EventsListFragment){
            if (doubleBackToExitPressedOnce) {
                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                Ticket.deleteTickets();
                SharedPreferences settings = this.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Click BACK again to Log Out", Toast.LENGTH_SHORT).show();

            mHandler.postDelayed(mRunnable, 2000);
        }else if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
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

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
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
            Ticket.deleteTickets();
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            SharedPreferences settings = this.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
            settings.edit().clear().apply();
            return true;
        }
        if (id == R.id.refresh) {
            mAuthTaskRefresh = new MainActivity.getEventsTask();
            mAuthTaskRefresh.execute();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new EventsListFragment());
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
           fragment = new EventsListFragment();
        } else if (id == R.id.nav_account) {
            fragment = new AccountFragment();
        } else if (id == R.id.nav_tickets) {
            fragment = new TicketsListFragment();
        } else if (id == R.id.nav_share) {

        }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment).addToBackStack("fragment");;
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
            makeEvents(jArray);
            super.onPostExecute(b);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
