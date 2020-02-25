package com.example.assignment4_inspirationrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncTask_EditAPI extends AsyncTask<UserProfile, Void, String> {

    private static final String TAG = "AsyncTask_EditAPI";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String updateEndPoint = "/profiles";

    @SuppressLint("StaticFieldLeak")
    private EditActivity editActivity;

    AsyncTask_EditAPI(EditActivity editActivity) {
        this.editActivity = editActivity;
    }

    @Override
    protected void onPostExecute(String s) {

        editActivity.progressBar.setVisibility(View.INVISIBLE);
        if (s.contains("error")) {
            editActivity.sendResults(false, s);
        } else {
            editActivity.sendResults(true, s);
        }
    }

    @Override
    protected String doInBackground(UserProfile... user) {

        String studentId = user[0].getStudentId();
        String userUsername = user[0].getUsername();
        String userPassword = user[0].getPassword();
        String userFirstName = user[0].getFirstName();
        String userLastName = user[0].getLastName();
        int pointsToAward = user[0].getPointsToAward();
        String userDepartment = user[0].getDepartment();
        String userStory = user[0].getStory();
        String userPosition = user[0].getPosition();
        boolean userAdmin = user[0].isAdmin();
        String userLocation = user[0].getLocation();
        String userPhoto = user[0].getPhoto();


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", studentId);
            jsonObject.put("username", userUsername);
            jsonObject.put("password", userPassword);
            jsonObject.put("firstName", userFirstName);
            jsonObject.put("lastName", userLastName);
            jsonObject.put("pointsToAward", pointsToAward);
            jsonObject.put("department", userDepartment);
            jsonObject.put("story", userStory);
            jsonObject.put("position", userPosition);
            jsonObject.put("admin", userAdmin);
            jsonObject.put("location", userLocation);
            jsonObject.put("imageBytes", userPhoto);
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("rewardRecords", jsonArray);

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
            String urlString = baseUrl + updateEndPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        editActivity.progressBar.setVisibility(View.VISIBLE);
    }
}
