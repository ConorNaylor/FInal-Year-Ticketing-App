package com.example.conornaylor.fyp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
public class CreateEventFragment extends Fragment{

    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String token;
    private String tk = "token";
    private String userId = "id";
    private String userID;
    private getEventsTask mAuthTask = null;
    private EditText eventName;
    private EditText eventNumTicks;
    private EditText eventPrice;
    private EditText eventDesc;
    private EditText eventLoc;
    private TextView dateText;
    private Button eventDate;
    private ImageView imageview;
    private ImageView mapImage;
    private Button createEvent;
    private Button eventImage;
    private String eventNameString;
    private String eventDescString;
    private String eventLocString;
    private String eventDateString;
    private String eventImagineString;
    private Double eventPriceD;
    private int eventNumTicksInt;
    private Date date;
    private Bitmap bitmap;
    ImageHandler imageUp;

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
        userID = preferences.getString(userId, null);

        mAuthTask = new getEventsTask();

        eventPrice = getActivity().findViewById(R.id.priceEvent);
        eventNumTicks = getActivity().findViewById(R.id.numTickets);
        eventName = getActivity().findViewById(R.id.eventName);
        eventDesc = getActivity().findViewById(R.id.eventDescription);
        eventLoc = getActivity().findViewById(R.id.eventLocation);
        eventDate = getActivity().findViewById(R.id.eventDate);
        createEvent = getActivity().findViewById(R.id.button_create_event);
        eventImage = getActivity().findViewById(R.id.button_image);
        imageview = getActivity().findViewById(R.id.imageView);
        mapImage = getActivity().findViewById(R.id.mapImage);
        dateText = getActivity().findViewById(R.id.dateText);

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

        eventDate.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             DialogFragment datePicker = new DatePickerFragment();
                                             datePicker.show(getActivity().getSupportFragmentManager(), "Date Picker");
                                         }
                                     }
        );


        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateText.setText(DatePickerFragment.formattedDate);
                eventNameString = eventName.getText().toString();
                eventDescString = eventDesc.getText().toString();
                eventLocString = eventLoc.getText().toString();
                eventDateString = DatePickerFragment.formattedDate;
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
                    focusView = eventNumTicks;
                } else if (TextUtils.isEmpty(eventNumTicks.getText().toString())) {
                    eventNumTicks.setError(getString(R.string.error_field_required));
                    focusView = eventNumTicks;
                } else if (TextUtils.isEmpty(eventDateString)) {
                    eventDate.setError(getString(R.string.error_field_required));
                    focusView = eventDate;
                } else if (!pictureChosen) {
                    eventImage.setError(getString(R.string.error_field_required));
                    focusView = eventImage;
                } else {
                    eventNumTicksInt = Integer.valueOf(eventNumTicks.getText().toString());
                    eventPriceD = Double.valueOf(eventPrice.getText().toString());
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

//    public String convertBitmapToString(Bitmap bitmap){
//        String encodedImage = "";
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        try {
//            encodedImage= URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return encodedImage;
//    }

    public void makeEvent(String input) {
        try {
            obj = new JSONObject(input);
            LocationData loc = new LocationData(0, 0);
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
                    obj.getString(userId),
                    loc,
                    obj.getDouble("price"));
            imageUp = new ImageHandler(bitmap, ev.getId(), token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class getEventsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                    String url = "http://192.168.0.59:8000/events/";
//                String url = "http://192.168.1.2:8000/events/";
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
                    ev.put("num_tickets", eventNumTicksInt);
                    ev.put("user", userID);
                    ev.put("price", eventPriceD);
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
                        System.out.println(input);
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
                ft.replace(R.id.container, new EventsListFragment()).addToBackStack("eventsList");
                ft.commit();
                makeEvent(input);
            }else{
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new CreateEventFragment());
                ft.commit();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
