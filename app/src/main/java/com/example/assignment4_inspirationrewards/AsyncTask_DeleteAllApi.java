package com.example.assignment4_inspirationrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncTask_DeleteAllApi extends AsyncTask<UserProfile, Void, String> {

    private static final String TAG = "AsyncTask_DeleteAllAPI";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String deleteAllEndPoint = "/allprofiles";

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;



    AsyncTask_DeleteAllApi(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPostExecute(String s) {

        if (s.contains("error")) {
            mainActivity.delete(false, s);
        } else {
            mainActivity.delete(true, s);
        }
    }

    @Override
    protected String doInBackground(UserProfile... user) {




        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", "A20435695");
            jsonObject.put("username", "admin");
            jsonObject.put("password", "12345");


            return doApi(jsonObject.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String doApi(String jsonObjectText) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String urlString = baseUrl + deleteAllEndPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObjectText);
            out.close();

            int responseCode = connection.getResponseCode();
            String responseText = connection.getResponseMessage();

            Log.d(TAG, "doAuth: " + responseCode + ": " + responseText);


            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                Log.d(TAG, "doApi: HTTP_OK");
                return result.toString();
            } else {

                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                Log.d(TAG, "doApi: Not OK");

                return result.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Invalid URL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        return null;
    }
}
