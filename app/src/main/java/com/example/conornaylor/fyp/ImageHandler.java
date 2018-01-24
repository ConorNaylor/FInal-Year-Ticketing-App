package com.example.conornaylor.fyp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.R.attr.bitmap;

/**
 * Created by conornaylor on 20/01/2018.
 */

public class ImageHandler {

    private Bitmap bitmap;
    private putImageTask put = null;
    private getImageURLsTask get = null;
    private String eventId;
    private String token;
    private String encodedString;
    private JSONArray jArray;
    private JSONObject obj;
    private String input;
    private Event ev;

    public ImageHandler(Bitmap bitmap, String eventId, String token){
        this.bitmap = bitmap;
        this.eventId = eventId;
        this.token = token;
        put = new putImageTask(bitmap, eventId);
        put.execute();
    }

    public ImageHandler(String token){
        this.token = token;
        get = new getImageURLsTask();
        get.execute();
    }

    public class putImageTask extends AsyncTask<Void, Void, Boolean> {

        private Bitmap bitmap;
        private String eventId;

        public putImageTask(Bitmap bitmap, String eventId){
            this.bitmap = bitmap;
            this.eventId = eventId;
        }
        @Override
        protected Boolean doInBackground(Void...params) {
            try {
                String url = "http://192.168.0.59:8000/eventphoto/";
//                String url = "http://192.168.1.2:8000/eventphoto/";
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
                try{
                    ev.put("image", encodedString);
                    ev.put("event", eventId);
                }catch(JSONException e){
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
                        System.out.println("This is the output" + sb.toString());
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
        protected void onPostExecute(Boolean b) { super.onPostExecute(b); }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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
        protected Boolean doInBackground(Void...params) {
            try {
//                String url = "http://192.168.0.59:8000/eventphoto/";
                String url = "140.203.241.66:8000/eventphoto/";    //College
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
            setEventURL(input);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String token;
        InputStream is;

        public DownloadImageTask(ImageView bmImage, String token) {
            this.bmImage = bmImage;
            this.token = token;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bm = null;
            URL object = null;
            try {
                object = new URL(urldisplay);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) object.openConnection();
                conn.setRequestProperty("Authorization", "Token " + token);
                conn.setDoInput(true);
                conn.connect();

            int length = conn.getContentLength();
            if (length > 0) {
                is = conn.getInputStream();
                }
                bm = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bm;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
