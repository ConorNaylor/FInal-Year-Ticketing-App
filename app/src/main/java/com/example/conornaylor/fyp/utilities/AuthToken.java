package com.example.conornaylor.fyp.utilities;

/**
 * Created by conornaylor on 16/10/2017.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


public class AuthToken {
    private String token = null;
    private String username;
    private String password;

    public AuthToken(String username, String password){
        this.password = password;
        this.username = username;
    }

    public String Login() throws IOException, JSONException {
        String url = "http://18.218.18.192:8000/api-token-auth/";
        URL object = new URL(url);

        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");

        JSONObject cred = new JSONObject();
        cred.put("username", username);
        cred.put("password", password);
        //	 System.out.println(cred.toString());

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(cred.toString());
        wr.flush();

        //display what returns the POST request

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
            token = sb.toString();
            System.out.println(token);

        } else {
            System.out.println(con.getResponseMessage());
        }
        return token;
    }
}
