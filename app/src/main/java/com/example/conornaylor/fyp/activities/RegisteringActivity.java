package com.example.conornaylor.fyp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.conornaylor.fyp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisteringActivity extends AppCompatActivity {

    private UserRegTask mAuthTask = null;
    private String auth;
    public static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;
    private String tokenString = "token";
    private String usernameString = "username";
    private String userEmailString = "email";
    private String userIdString = "id";
    private View focusView;

    // UI references.
    private EditText mEmailView;
    private EditText mNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mRegFormView;
    private Button register;
    private CheckBox host;
    private JSONObject jsonObject;
    private JSONObject obj;
    boolean cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mNameView = (EditText) findViewById(R.id.regNameView);


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


        mRegFormView = findViewById(R.id.reg_form);
        mProgressView = findViewById(R.id.reg_progress);

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            attemptRegister();
                                        }
                                    }
        );

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

//        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        cancel = false;
        focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

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
            showProgress(true);
            mAuthTask = new UserRegTask(name, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * An asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegTask extends AsyncTask< Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private final String mName;

        UserRegTask(String name, String email, String password) {
            System.out.println("Getting this far");
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://18.218.18.192:8000/makeusers/"; //Galway
//                String url = "http://192.168.1.5:8000/makeusers/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                JSONObject cred = new JSONObject();
                try {
                    cred.put("username", mName);
                    cred.put("email", mEmail);
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
                    System.out.println(sb.toString());
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
                try {
                    jsonObject = new JSONObject(auth);
                    e.putString(tokenString, jsonObject.getString(tokenString));
                    e.putString(userIdString, jsonObject.getJSONObject("user").getString(userIdString));
                    e.putString(userEmailString, jsonObject.getJSONObject("user").getString(userEmailString));
                    e.putString(usernameString, jsonObject.getJSONObject("user").getString(usernameString));
                }catch(JSONException error){
                    error.printStackTrace();
                }
                e.commit();
                return true;
            }
            else return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                if (auth.contains("token")) {
                    e = preferences.edit();
                    try {
                        jsonObject = new JSONObject(auth);
                        e.putString(tokenString, jsonObject.getString(tokenString));
                        e.putString(userIdString, jsonObject.getJSONObject("user").getString(userIdString));
                        e.putString(userEmailString, jsonObject.getJSONObject("user").getString(userEmailString));
                        e.putString(usernameString, jsonObject.getJSONObject("user").getString(usernameString));
                    } catch (JSONException error) {
                        error.printStackTrace();
                    }
                    e.commit();
                }
                Intent myIntent = new Intent(RegisteringActivity.this, MainActivity.class);
                RegisteringActivity.this.startActivity(myIntent);
                finish();
            } else {
                showProgress(false);
                mPasswordView.setError("Provide a unique username and email!");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}