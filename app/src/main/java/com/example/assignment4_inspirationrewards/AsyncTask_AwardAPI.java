package com.example.assignment4_inspirationrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncTask_AwardAPI extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private AwardActivity awardActivity;
    private static final String TAG = "AsyncTask_AwardAPI";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String addRewardsEndPoint = "/rewards";

    AsyncTask_AwardAPI(AwardActivity awardActivity) {
        this.awardActivity = awardActivity;
    }

    @Override
    protected void onPostExecute(String s) {
        awardActivity.progressBar.setVisibility(View.INVISIBLE);
        if (s.contains("error")) {
            awardActivity.sendResults(false, s);
        } else {
            awardActivity.sendResults(true, s);
        }

    }

    @Override
    protected String doInBackground(String... strings) {

        String RewardeduserName = strings[0];
        String RewardedName = strings[1];
        String notes = strings[2];
        int value = Integer.parseInt(strings[3]);
        String userName = strings[4];
        String password = strings[5];

        JSONObject target = new JSONObject();
        try {
            target.put("studentId", "A20435695");
            target.put("username", RewardeduserName);
            target.put("name", RewardedName);
            Log.d(TAG, "doInBackground: !!" + RewardedName);

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            target.put("date", dateFormat.format(Calendar.getInstance().getTime()));
            target.put("notes", notes);
            target.put("value", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject source = new JSONObject();
        try {
            source.put("studentId", "A20435695");
            source.put("username", userName);
            source.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject object = new JSONObject();
        try {
            object.put("target", target);
            object.put("source", source);
            Log.d(TAG, "doInBackground: " + object.toString());
            return doApi(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String doApi(String jsonObjectText) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseUrl + addRewardsEndPoint;


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
        awardActivity.progressBar.setVisibility(View.VISIBLE);
    }
}
