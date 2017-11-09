package com.example.conornaylor.fyp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment {


    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private getEventsTask mAuthTask = null;
    private EditText eventName;
    private EditText eventNumTicks;
    private EditText eventPrice;
    private EditText eventDesc;
    private EditText eventLoc;
    private EditText eventDate;
    private ImageView imageview;
    private ImageView mapImage;
    private Bitmap bitmap;
    private Button createEvent;
    private Button eventImage;
    private String eventNameString;
    private String eventDescString;
    private String eventLocString;
    private String eventDateString;
    private Double eventPriceD;
    private int eventNumTicksInt;

    private Event event;
    private LocationData data = null;
    private String encodedImage;
    private String selectedImagePath;
    private JSONObject obj;
    public static final int pickImage = 1;
    private String input;
    private boolean pictureChosen = false;
    private View focusView = null;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        token = preferences.getString(tk, null);

        mAuthTask = new getEventsTask();

        eventPrice = getActivity().findViewById(R.id.eventName);
        eventNumTicks = getActivity().findViewById(R.id.eventDescription);
        eventName = getActivity().findViewById(R.id.eventName);
        eventDesc = getActivity().findViewById(R.id.eventDescription);
        eventLoc = getActivity().findViewById(R.id.eventLocation);
        eventDate = getActivity().findViewById(R.id.eventDate);
        createEvent = getActivity().findViewById(R.id.button_create_event);
        eventImage = getActivity().findViewById(R.id.button_image);
        imageview = getActivity().findViewById(R.id.imageView);
        mapImage = getActivity().findViewById(R.id.mapImage);

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImage);
            }
        });

        mapImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Toast.makeText(getActivity(), "Choose your location.", Toast.LENGTH_SHORT).show();
          }
      });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventNameString = eventName.getText().toString();
                eventDescString = eventDesc.getText().toString();
                eventLocString = eventLoc.getText().toString();
                eventDateString = eventDate.getText().toString();
                if(TextUtils.isEmpty(eventNameString)){
                    eventName.setError(getString(R.string.error_field_required));
                    focusView = eventName;
                }else if(TextUtils.isEmpty(eventLocString)){
                    eventLoc.setError(getString(R.string.error_field_required));
                    focusView = eventLoc;
                }else if(TextUtils.isEmpty(eventDescString)){
                    eventDesc.setError(getString(R.string.error_field_required));
                    focusView = eventDesc;
                }else if(TextUtils.isEmpty(eventPrice.getText().toString())){
                    eventNumTicksInt = Integer.parseInt(eventNumTicks.getText().toString());
                    eventNumTicks.setError(getString(R.string.error_field_required));
                    focusView = eventNumTicks;
                }else if(TextUtils.isEmpty(eventNumTicks.getText().toString())){
                    eventPriceD = Double.parseDouble(eventNumTicks.getText().toString());
                    eventPrice.setError(getString(R.string.error_field_required));
                    focusView = eventPrice;
                }else if(TextUtils.isEmpty(eventDateString)){
                    eventDate.setError(getString(R.string.error_field_required));
                    focusView = eventDate;
                }else if(pictureChosen == false){
                    eventImage.setError(getString(R.string.error_field_required));
                    focusView = eventImage;
                } else{
                    mAuthTask.execute();
                    }
                }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1 && null != data) {
            decodeUri(data.getData());
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
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            imageview.setImageBitmap(bitmap);
            pictureChosen = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

            public void makeEvent(String input){
                try {
                    obj = new JSONObject(input);
                    LocationData loc = new LocationData(0, 0);
                    Event ev = new Event(
                    obj.getString("id"),
                    obj.getString("title"),
                    obj.getString("description"),
                    obj.getString("location"),
                    obj.getString("date"),
                    obj.getDouble("price"),
                    obj.getInt("numticks"),
                    loc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class getEventsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                    String url = "http://192.168.0.59:8000/events/";
//                String url = "http://192.168.1.10:8000/events/";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Token " + token);
                con.setRequestMethod("PUT");

               JSONObject ev = new JSONObject();
                try{
                    ev.put("title", eventNameString);
                    ev.put("description", eventDescString);
                    ev.put("location", eventLocString);
                    ev.put("date", eventDateString);
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
                        input = sb.toString();
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

            if(b) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new EventFragment());
                ft.commit();
                makeEvent(input);
            }else{

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
