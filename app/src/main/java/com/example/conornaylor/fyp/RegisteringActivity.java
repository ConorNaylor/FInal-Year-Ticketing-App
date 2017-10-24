package com.example.conornaylor.fyp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegisteringActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private UserLoginTask mAuthTask = null;
    private String auth;
    private String token = "token";
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;

    // UI references.
    private EditText mEmailView;
    private EditText mName;
    private EditText mPasswordView;
    private View mProgressView;
    private View mRegFormView;
    private Button register;
    private CheckBox host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mName = (EditText) findViewById(R.id.name);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.register);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        register = (Button) findViewById(R.id.register);

        preferences = this.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mName.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check name
        if (TextUtils.isEmpty(name)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mAuthTask = new UserLoginTask(name, email, password);
                mAuthTask.execute((Void) null);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    /**
     * An asynchronous registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask< Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private final String mName;

        UserLoginTask(String name, String email, String password) {
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://192.168.0.59:8000/api-token-auth/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                JSONObject cred = new JSONObject();
                try {
                    cred.put("name", mName);
                    cred.put("username", mEmail);
                    cred.put("password", mPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(cred.toString());
                wr.flush();

                //Display what the POST request returns
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
                    auth = sb.toString();
                } else {
                    System.out.println(con.getResponseMessage());
                }
            } catch (Exception e) {
                Log.d("Uh Oh","No connection!, Check your network.");
                return false;
            }

            if (auth == null){
                return false;
            }else if(auth.contains("token")){
                e = preferences.edit();
                e.putString(token, auth.substring(10,auth.length() - 3));
                e.commit();
                return true;
            }
            else return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Intent myIntent = new Intent(RegisteringActivity.this, MainActivity.class);
                RegisteringActivity.this.startActivity(myIntent);
                finish();

            } else {
                mPasswordView.setError("Please ensure your password contains at least 8 characters and a number");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

}