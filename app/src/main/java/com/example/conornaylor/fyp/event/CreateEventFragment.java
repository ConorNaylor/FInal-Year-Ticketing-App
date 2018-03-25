package com.example.conornaylor.fyp.event;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conornaylor.fyp.R;
import com.example.conornaylor.fyp.utilities.DatePickerFragment;
import com.example.conornaylor.fyp.utilities.ImageHandler;
import com.example.conornaylor.fyp.utilities.LocationData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private String userId = "id";
    private String userID;
    private putEventsTask mAuthTask = null;
    private View mCreateEventView;
    private View mProgressView;
    private EditText eventName;
    private EditText eventNumTicks;
    private EditText eventPrice;
    private EditText eventDesc;
    private Button eventLoc;
    public static TextView dateText;
    private Button eventDate;
    private ImageView imageview;
    private Button createEvent;
    private Button eventImage;
    private String eventNameString;
    private String eventDescString;
    private String eventLocString;
    private String eventDateString;
    private String eventDateStringUp;
    private String eventImagineString;
    private Double eventPriceD;
    private int eventNumTicksInt;
    private String address;
    private Date date;
    private Bitmap bitmap;
    ImageHandler imageUp;
    private String encodedString;
    private Event event;
    private LocationData data = null;
    private String encodedImage;
    private String selectedImagePath;
    private JSONObject obj;
    public static final int pickImage = 1;
    private String input;
    private boolean pictureChosen = false;
    private View focusView = null;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private int timeOrPlacePicker;
    private double latitude;
    private double longitude;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }


    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);
        userID = preferences.getString(userId, null);

        mAuthTask = new putEventsTask();

        mCreateEventView = getActivity().findViewById(R.id.event_form);
        mProgressView = getActivity().findViewById(R.id.create_event_progress);

        eventPrice = getActivity().findViewById(R.id.priceEvent);
        eventNumTicks = getActivity().findViewById(R.id.numTickets);
        eventName = getActivity().findViewById(R.id.eventName);
        eventDesc = getActivity().findViewById(R.id.eventDescription);
        eventLoc = getActivity().findViewById(R.id.eventLocation);
        eventDate = getActivity().findViewById(R.id.eventDate);
        createEvent = getActivity().findViewById(R.id.button_create_event);
        eventImage = getActivity().findViewById(R.id.button_image);
        imageview = getActivity().findViewById(R.id.imageView);
        dateText = getActivity().findViewById(R.id.dateText);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();

        eventLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeOrPlacePicker = 0;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImage);
            }
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             timeOrPlacePicker = 1;
                                             DialogFragment datePicker = new DatePickerFragment();
                                             datePicker.show(getActivity().getSupportFragmentManager(), "Date Picker");
                                         }
                                     }
        );


        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventNameString = eventName.getText().toString();
                eventDescString = eventDesc.getText().toString();
                eventLocString = address;
                eventDateString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(DatePickerFragment.formattedDate);
                if (TextUtils.isEmpty(eventNameString)) {
                    eventName.setError(getString(R.string.error_field_required));
                    focusView = eventName;
                } else if (TextUtils.isEmpty(eventLocString)) {
                    eventLoc.setError(getString(R.string.error_field_required));
                    focusView = eventLoc;
                } else if (TextUtils.isEmpty(eventDescString)) {
                    eventDesc.setError(getString(R.string.error_field_required));
                    focusView = eventDesc;
                } else if (TextUtils.isEmpty(eventPrice.getText().toString())) {
                    eventPrice.setError(getString(R.string.error_field_required));
                    focusView = eventPrice;
                } else if(Double.valueOf(eventPrice.getText().toString()) > 999){
                    eventPrice.setError("Price must not exceed €999");
                    focusView = eventPrice;
                } else if (TextUtils.isEmpty(eventNumTicks.getText().toString())) {
                    eventNumTicks.setError(getString(R.string.error_field_required));
                    focusView = eventNumTicks;
                } else if(Integer.valueOf(eventNumTicks.getText().toString()) == 0){
                    eventNumTicks.setError("Must be greater than 0");
                    focusView = eventNumTicks;
                }else if (TextUtils.isEmpty(eventDateString)) {
                    eventDate.setError(getString(R.string.error_field_required));
                    focusView = eventDate;
                } else if (!pictureChosen) {
                    eventImage.setError(getString(R.string.error_field_required));
                    focusView = eventImage;
                } else {
                    eventNumTicksInt = Integer.valueOf(eventNumTicks.getText().toString());
                    eventPriceD = Double.valueOf(eventPrice.getText().toString());
                    mAuthTask = new putEventsTask();
                    mAuthTask.execute();
                    showProgress(true);
                }
            }
        });
    }

    public static void dateEdited() throws ParseException {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(DatePickerFragment.formattedDate);
        dateText.setText(date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 1) {
            if (timeOrPlacePicker == 1) {
                if (null != data) {
                    decodeUri(data.getData());
                }
            } else if (timeOrPlacePicker == 0) {
                Place place = PlacePicker.getPlace(data, getActivity());
                StringBuilder stBuilder = new StringBuilder();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                address = String.format("%s", place.getAddress());
                stBuilder.append("Address: ");
                stBuilder.append(address);
                Toast.makeText(getActivity(), stBuilder.toString(), Toast.LENGTH_SHORT).show();
                eventLoc.setHint(address);
            }
        }
    }

    public void decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getActivity().getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            imageview.setImageBitmap(bitmap);

            pictureChosen = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void makeEvent(String input) {
        try {
            obj = new JSONObject(input);
            LocationData loc = new LocationData(latitude, longitude);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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
                    userID,
                    loc,
                    obj.getDouble("price"),
                    obj.getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new EventsListFragment()).addToBackStack("create");
        ft.commit();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class putEventsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://18.218.18.192:8000/events/";
                URL object = new URL(url);

                HttpURLConnection c = (HttpURLConnection) object.openConnection();
                c.setDoInput(true);
                c.setRequestMethod("PUT");
                c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Authorization", "Token " + token);
                c.setDoOutput(true);
                c.connect();
                OutputStream output = c.getOutputStream();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);

                eventDateStringUp = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(DatePickerFragment.formattedDate);

                JSONObject ev = new JSONObject();
                try {
                    ev.put("image", encodedString);
                    ev.put("title", eventNameString);
                    ev.put("description", eventDescString);
                    ev.put("location", eventLocString);
                    ev.put("date", eventDateStringUp);
                    ev.put("num_tickets", eventNumTicksInt);
                    ev.put("price", eventPriceD);
                    ev.put("user", userID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                output.write(ev.toString().getBytes("utf-8"));
                output.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = c.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(c.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    if (sb.toString() != null) {
                        input = sb.toString();
                        System.out.println(input);
                    }
                } else {
                    System.out.println(c.getResponseMessage());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            mAuthTask = null;
            if (b) {
                super.onPostExecute(b);
                makeEvent(input);
            }else{
                showProgress(false);
                createEvent.setError("Failed to create event.");
                createEvent.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
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

            mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateEventView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
