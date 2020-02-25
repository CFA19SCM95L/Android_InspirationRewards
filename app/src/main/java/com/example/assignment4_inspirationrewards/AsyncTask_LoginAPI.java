package com.example.assignment4_inspirationrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncTask_LoginAPI extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncTask_LoginAPI";
    private static final String loginEndPoint = "/login";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private ProfileActivity profileActivity;
    boolean isMain;

    AsyncTask_LoginAPI(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        isMain = true;
    }

    AsyncTask_LoginAPI(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
    }


    @Override
    protected void onPostExecute(String s) {
        if (isMain) {
            mainActivity.progressBar.setVisibility(View.INVISIBLE);
            if (s.contains("error")) {
                mainActivity.sendResults(false, s);
            } else {
                mainActivity.sendResults(true, s);
            }
        } else {
            profileActivity.progressBar.setVisibility(View.INVISIBLE);
            if (s.contains("error")) {
                profileActivity.sendResults(false, s);
            } else {
                profileActivity.sendResults(true, s);
            }
        }


    }

    @Override
    protected String doInBackground(String... strings) {
        String stuId = strings[0];
        String uName = strings[1];
        String pswd = strings[2];

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", stuId);
            jsonObject.put("username", uName);
            jsonObject.put("password", pswd);

            return doAuth(jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String doAuth(String jsonObjectText) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com" + loginEndPoint;


            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
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
                Log.d(TAG, "doAuth: HTTP_OK");
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                return result.toString();
            } else {
                Log.d(TAG, "doAuth: HTTP_NOT_OK");

                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(isMain){
            mainActivity.progressBar.setVisibility(View.VISIBLE);
        } else {
            profileActivity.progressBar.setVisibility(View.VISIBLE);
        }
    }


}
