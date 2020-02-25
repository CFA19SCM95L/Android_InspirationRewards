package com.example.assignment4_inspirationrewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    //from create or from login/ from edit/ from award

    public TextView fullName;
    public TextView userName;
    public TextView location;
    public ImageView photo;
    public TextView pointsGet;
    public TextView department;
    public TextView position;
    public TextView pointLeft;
    public TextView story;
    public String username;
    public String password;
    public ProgressBar progressBar;
    public static final int RESPONSE_CODE = 1;


    public TextView rewardCount;
    public RecyclerView recyclerView;
    private static final String TAG = "ProfileActivity";
    public String userProfile;
    public JSONObject jsonObject;


    private final List<Rewards> rewardsList = new ArrayList<>();

    private Adapter_Profile mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_scroll);

        Utilities.setupHomeIndicatorAndName(getSupportActionBar(), "Your Profile");
        fullName = findViewById(R.id.fullName_profile);
        userName = findViewById(R.id.username_profile);
        location = findViewById(R.id.location_profile);
        photo = findViewById(R.id.photo_profile);
        pointsGet = findViewById(R.id.pointsGet_profile);
        department = findViewById(R.id.department_profile);
        position = findViewById(R.id.position_profile);
        pointLeft = findViewById(R.id.pointsLeft_profile);
        story = findViewById(R.id.story_profile);
        progressBar = findViewById(R.id.progressBar_profile);
        rewardCount = findViewById(R.id.rewardCount_profile);
        recyclerView = findViewById(R.id.recyclerView_profile);
        mAdapter = new Adapter_Profile(rewardsList, this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("resultFromLogin")) {
            Log.d(TAG, "onCreate: data from login");
            userProfile = getIntent().getStringExtra("resultFromLogin");

            parseJSONDataAndSet(userProfile);
        } else if (getIntent().hasExtra("resultFromCreate")) {
            Log.d(TAG, "onCreate: data from create");
            userProfile = getIntent().getStringExtra("resultFromCreate");
            parseJSONDataAndSet(userProfile);
        } else if (getIntent().hasExtra("resultFromEdit")) {
            new CustomToast(this).show(getIntent().getStringExtra("resultFromEdit"));
            login(getIntent().getStringExtra("username"), getIntent().getStringExtra("password"));
        } else if (getIntent().hasExtra("resultFromLeaderboard")) {
            Log.d(TAG, "onCreate: data from leaderboard");
            login(getIntent().getStringExtra("username"), getIntent().getStringExtra("password"));
        }


    }

    private void parseJSONDataAndSet(String data) { //pointsGet add from array
        try {
            Log.d(TAG, "parseJSONDataAndSet: start " + data);

            jsonObject = new JSONObject(data);
            Log.d(TAG, "parseJSONDataAndSet: start ");

            username = jsonObject.getString("username");
            password = jsonObject.getString("password");


            fullName.setText(jsonObject.getString("lastName") + "," + jsonObject.getString("firstName"));
            userName.setText("(" + jsonObject.getString("username") + ")");
            location.setText(jsonObject.getString("location"));
            Log.d(TAG, "parseJSONDataAndSet: Before image");

            stringToPhoto(jsonObject.getString("imageBytes"));
            department.setText(jsonObject.getString("department"));
            position.setText(jsonObject.getString("position"));
            pointLeft.setText("" + jsonObject.getInt("pointsToAward"));
            story.setText(jsonObject.getString("story"));
            Log.d(TAG, "parseJSONDataAndSet: end ");


            JSONArray jsonArray = jsonObject.getJSONArray("rewards");
            for (int i = 0; i < jsonArray.length(); i++) {
                Rewards rewards = new Rewards();
                JSONObject object = jsonArray.getJSONObject(i);
                rewards.setUsername(object.getString("username"));
                rewards.setName(object.getString("name"));
                rewards.setDate(object.getString("date"));
                rewards.setNotes(object.getString("notes"));
                rewards.setValue(object.getInt("value"));
                rewardsList.add(rewards);
            }
            rewardCount.setText(rewardsList.size()+"");
            pointsGet.setText(getRewardPoints(rewardsList)+"");
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.d(TAG, "parseJSONDataAndSet: Error occur");
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent intent = new Intent(this, EditActivity.class);
                try {
                    intent.putExtra("username", jsonObject.getString("username"));
                    intent.putExtra("password", jsonObject.getString("password"));
                    intent.putExtra("admin", jsonObject.getString("admin"));
                    intent.putExtra("firstName", jsonObject.getString("firstName"));
                    intent.putExtra("lastName", jsonObject.getString("lastName"));
                    intent.putExtra("department", jsonObject.getString("department"));
                    intent.putExtra("position", jsonObject.getString("position"));
                    intent.putExtra("pointsLeft", jsonObject.getInt("pointsToAward"));
                    intent.putExtra("story", jsonObject.getString("story"));
                    intent.putExtra("photo", photoToString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
                return true;

            case R.id.sort_profile:
                Intent intent2 = new Intent(this, LeaderboardActivity.class);
                intent2.putExtra("resultFromProfile", "");
                intent2.putExtra("username", username);
                intent2.putExtra("password", password);
                intent2.putExtra("fullName", fullName.getText());
//                startActivity(intent2);
                startActivityForResult(intent2, RESPONSE_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void stringToPhoto(String completeImageData) {

        Log.d(TAG, "stringToPhoto: Start");

        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",") + 1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        photo.setImageBitmap(bitmap);

        Log.d(TAG, "stringToPhoto: End");

    }


    public void login(String username, String password) {
        Log.d(TAG, "login: " + username + ":" + password);
        new AsyncTask_LoginAPI(this).execute("A20435695", username, password);

    }

    public void sendResults(boolean success, String result) {
        String status = "";
        String message = "";

        if (success) {
            parseJSONDataAndSet(result);

        } else {
            try {
                JSONObject json = new JSONObject(result);
                String s = json.getString("errordetails");
                JSONObject jsonObject = new JSONObject(s);
                status = jsonObject.getString("status");
                message = jsonObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "sendResults: Error from exit");
            new CustomToast(this).show(status + ":\n" + message);
        }
    }
    private String photoToString() {

        Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESPONSE_CODE) { 
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Get data back from leaderboard back press");
                username = data.getStringExtra("username");
                password = data.getStringExtra("password");
                login(username, password);
            }
        }
    }

    private int getRewardPoints(List<Rewards> rewardsContents) {
        int sum = 0;
        for (Rewards content : rewardsContents) {
            sum += content.getValue();
        }
        return sum;
    }
}
