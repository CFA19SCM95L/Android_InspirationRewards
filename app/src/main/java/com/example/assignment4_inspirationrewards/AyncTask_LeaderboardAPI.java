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

public class AyncTask_LeaderboardAPI extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private LeaderboardActivity leaderboardActivity;
    private static final String TAG = "AyncTask_LeaderboardAPI";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String getAllProfilesEndPoint = "/allprofiles";


    AyncTask_LeaderboardAPI(LeaderboardActivity leaderboardActivity) {
        this.leaderboardActivity = leaderboardActivity;
    }

    @Override
    protected void onPostExecute(String s) {
        leaderboardActivity.progressBar.setVisibility(View.INVISIBLE);
        if (s.contains("error")) {
            leaderboardActivity.sendResults(false, s);
        } else {
            leaderboardActivity.sendResults(true, s);
        }


    }

    @Override
    protected String doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", "A20435695");
            jsonObject.put("username", username);
            jsonObject.put("password", password);

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

            String urlString = baseUrl + getAllProfilesEndPoint;


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

            Log.d(TAG, "doApi: " + responseCode + ": " + responseText);


            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                Log.d(TAG, "doApi: HTTP_OK");
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                return result.toString();
            } else {
                Log.d(TAG, "doApi: HTTP_NOT_OK");

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
        leaderboardActivity.progressBar.setVisibility(View.VISIBLE);
    }
}
