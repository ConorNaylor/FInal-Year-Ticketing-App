package com.example.conornaylor.fyp.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conornaylor.fyp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NFCDisplayActivity extends AppCompatActivity {


    private TextView mStatusView;
    private NFCDisplayActivity.authTicketsTask mAuthTask;
    private String out;
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String tk = "token";
    private String ticketID;
    private String token;
    private View mProgressView;
    private ImageView enterView;


    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nfcdisplay);

            mStatusView = (TextView) findViewById(R.id.eventStatus);
            mProgressView = findViewById(R.id.progressBarAuth);
            enterView = (ImageView) findViewById(R.id.enterView);

            enterView.setVisibility(View.INVISIBLE);
            mStatusView.setText("Waiting for ticket...");

            preferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
            token = preferences.getString(tk, null);

            mAuthTask = new NFCDisplayActivity.authTicketsTask();
        }

        @Override
        protected void onResume(){
            super.onResume();
            Intent intent = getIntent();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                        NfcAdapter.EXTRA_NDEF_MESSAGES);

                NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
                ticketID = new String(message.getRecords()[0].getPayload());
                if(!ticketID.isEmpty()) {
                    mAuthTask.execute();
                }
            }
        }

    public void makeToast(String str){
        if(str != null){
            try {
                JSONObject obj = new JSONObject(str);
                if(!obj.getString("id").isEmpty()){
                    showProgress(false);
                    enterView.setVisibility(View.VISIBLE);
                    mStatusView.setText("Ticket: " + ticketID + ", verified for seat: " + obj.getString("seat") + ".");
                    Toast.makeText(this, "Ticket Verified!", Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStatusView.setText("Waiting for ticket...");
                            showProgress(true);
                            enterView.setVisibility(View.INVISIBLE);
                        }
                    }, 10000);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }else {
            Toast.makeText(this, "Ticket Failed Verification!", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        }
    }

    public class authTicketsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://18.218.18.192:8000/checkticket/";
//                String url = "http://192.168.1.5:8000/checkticket/";
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
                        System.out.println(out);
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

