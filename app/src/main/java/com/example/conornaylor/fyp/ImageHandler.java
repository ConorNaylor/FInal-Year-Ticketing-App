package com.example.conornaylor.fyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by conornaylor on 20/01/2018.
 */

public class ImageHandler {

    private putEventTask put = null;
    private getImageURLsTask get = null;
    private String eventId;
    private String token;
    private String encodedString;
    private JSONArray jArray;
    private JSONObject obj;
    private String input;
    private Event ev;
    private JSONObject json;
    private String imgURL;
    private Context context;
    private ImageView iv;
    private Bitmap bitmap;
    private String eventName;
    private String eventDesc;
    private String eventDate;
    private Double eventPrice;
    private String eventAdd;
    private int eventNumTicks;
    private String userId;

    public ImageHandler(String token, Bitmap bitmap, String eventName, String eventDesc, Double eventPrice, int eventNumTicks, String eventAdd, String eventDate, String userId) {
        this.bitmap = bitmap;
        this.token = token;
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.eventPrice = eventPrice;
        this.eventNumTicks = eventNumTicks;
        this.eventAdd = eventAdd;
        this.eventDate = eventDate;
        this.userId = userId;
        put = new putEventTask(token, bitmap, eventName, eventDesc, eventPrice, eventNumTicks, eventAdd, eventDate, userId);
        put.execute();
    }

    public ImageHandler(String token) {
        this.token = token;
        get = new getImageURLsTask();
        get.execute();
    }

    public class putEventTask extends AsyncTask<Void, Void, Boolean> {

        private Bitmap bitmap;
        private String eventName;
        private String eventDesc;
        private String eventDate;
        private Double eventPrice;
        private String eventAdd;
        private int eventNumTicks;
        private String token;
        private String userId;

        public putEventTask(String token, Bitmap bitmap, String name, String desc, Double price, int numTicks, String add, String date, String userId) {
            this.token = token;
            this.bitmap = bitmap;
            this.eventName = name;
            this.eventDesc = desc;
            this.eventAdd = add;
            this.eventPrice = price;
            this.eventNumTicks = numTicks;
            this.eventDate = date;
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://192.168.0.59:8000/events/";
//                String url = "http://192.168.1.5:8000/events/";
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


                JSONObject ev = new JSONObject();
                try {
                    ev.put("image", encodedString);
                    ev.put("title", eventName);
                    ev.put("description", eventDesc);
                    ev.put("location", eventAdd);
                    ev.put("date", "2017-10-10");
                    ev.put("num_tickets", eventNumTicks);
                    ev.put("price", eventPrice);
                    ev.put("user", userId);
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
            if (b) {
                super.onPostExecute(b);
//                parseEvent(input);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public void parseEvent(String in) {
        try {
            json = new JSONObject(in);
            Event.getEventByID(json.getString("event")).setImageURL(json.getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setEventURL(String input) {
        try {
            jArray = new JSONArray(input);
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    obj = jArray.getJSONObject(i);
                    ev = Event.getEventByID(obj.getString("event"));
                    ev.setImageURL(obj.getString("image"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class getImageURLsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://192.168.1.5:8000/eventphoto/";
//                String url = "http://192.168.1.2:8000/eventphoto/";
                URL object = new URL(url);

                HttpURLConnection c = (HttpURLConnection) object.openConnection();
                c.setDoInput(true);
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Authorization", "Token " + token);
                c.connect();

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
            super.onPostExecute(b);
//            setEventURL(input);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

