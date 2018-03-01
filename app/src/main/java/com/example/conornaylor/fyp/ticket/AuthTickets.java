package com.example.conornaylor.fyp.ticket;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.conornaylor.fyp.event.Event;
import com.example.conornaylor.fyp.R;

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
public class AuthTickets extends AppCompatActivity {

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
    private String ticketID;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_auth_tickets);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event = Event.getEventByID(extras.getString("event_id"));
        }

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        preferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
        userID = preferences.getString(userId, null);

        iv = (ImageView)findViewById(R.id.go);
        iv.setVisibility(View.INVISIBLE);

        mAuthTask = new authTicketsTask();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
//        Intent intent = new Intent(this.getApplicationContext(), this.getClass());
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
//                    NfcAdapter.EXTRA_NDEF_MESSAGES);
//
//            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
//            makeToast(new String(message.getRecords()[0].getPayload()));
//        } else
//            makeToast("Waiting for ticket");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    System.out.println(messages[i]);
                }
                mAuthTask.execute();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    public void makeToast(String str){
        if(str != null){
            try {
                JSONObject obj = new JSONObject(str);
                if(!obj.getString("id").isEmpty()){
                    Toast.makeText(this, "Ticket Verified!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }else {
            Toast.makeText(this, "Ticket Failed Verification!", Toast.LENGTH_SHORT).show();
        }
    }

    public class authTicketsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
//                    String url = "http://192.168.0.59:8000/checkticket/";
                String url = "http://18.218.18.192:8000/checkticket/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoInput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Token " + token);

                JSONObject ev = new JSONObject();
                try{
                    ev.put("ticket_id", ticketID);
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

