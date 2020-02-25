package com.example.assignment4_inspirationrewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    private final List<UserProfile> userProfileList = new ArrayList<>();
    private static final String TAG = "LeaderboardActivity";

    public String username;
    public String password;
    public String fullName;
    public ProgressBar progressBar;

    public RecyclerView recyclerView;
    private Adapter_Leaderboard mAdapter; // Data to recyclerview adapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        recyclerView = findViewById(R.id.recyclerView_leaderboard);
        progressBar = findViewById(R.id.progressBar_leaderboard);

        mAdapter = new Adapter_Leaderboard(userProfileList, this);
        mAdapter.username = username;

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Utilities.setupHomeIndicatorAndName(getSupportActionBar(), "Inspiration Leaderboard");

        if (getIntent().hasExtra("resultFromProfile")) {
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            fullName = getIntent().getStringExtra("fullName");
            mAdapter.username = username;
            new AyncTask_LeaderboardAPI(this).execute(username, password);
        } else if (getIntent().hasExtra("resultFromAward")) {
            new CustomToast(this).show(getIntent().getStringExtra("resultFromAward"));
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            fullName = getIntent().getStringExtra("fullName");
            mAdapter.username = username;
            new AyncTask_LeaderboardAPI(this).execute(username, password);
        }


    }

    //test
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leaderboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("resultFromLeaderboard", "");
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //


    public void sendResults(boolean success, String result) {
        String status = "";
        String message = "";

        if (success) {
            parseData(result);
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
            new CustomToast(this).show(status + ":\n" + message);
        }

    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks

        int pos = recyclerView.getChildLayoutPosition(v);
        UserProfile userProfile = userProfileList.get(pos);
        Intent intent = new Intent(this, AwardActivity.class);
        intent.putExtra("profile", userProfile);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("fullName", fullName);
        Log.d(TAG, "onClick: Go to Award");
        startActivity(intent);


    }

    public void parseData(String data) {
        Log.d(TAG, "parseData: " + data);

        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                final UserProfile profilesData = new UserProfile();
                profilesData.setFirstName(object.getString("firstName"));
                profilesData.setLastName(object.getString("lastName"));
                profilesData.setUsername(object.getString("username"));
                profilesData.setStory(object.getString("story"));
                profilesData.setPosition(object.getString("position"));
                profilesData.setPointsToAward(object.getInt("pointsToAward"));
                profilesData.setAdmin(object.getBoolean("admin"));
                profilesData.setDepartment(object.getString("department"));
                profilesData.setPhoto(object.getString("imageBytes"));
                profilesData.setLocation(object.getString("location"));
                Log.d(TAG, "parseJSONData: " + object.toString());
                String val = "null";
                if (object.has("rewards")) {
                    if (object.getString("rewards").startsWith(val)) {
                        String s = object.getString("rewards");
                        String ans = s.replace(val, "");
                        Log.d(TAG, "parseJSONData: " + String.valueOf(ans));
                    } else {
                        JSONArray objectJSONArray = object.getJSONArray("rewards");
                        for (int j = 0; j < objectJSONArray.length(); j++) {
                            Rewards rewardsContent = new Rewards();
                            JSONObject jsonObject = objectJSONArray.getJSONObject(j);
                            rewardsContent.setUsername(jsonObject.getString("username"));
                            rewardsContent.setName(jsonObject.getString("name"));                   //full name
                            rewardsContent.setDate(jsonObject.getString("date"));
                            rewardsContent.setNotes(jsonObject.getString("notes"));                 // comments
                            rewardsContent.setValue(jsonObject.getInt("value"));                    // reward value
                            profilesData.rewardRecords.add(rewardsContent);
                        }
                    }
                }
                int totalPoint = getRewardPoints(profilesData.rewardRecords);
                profilesData.setPointsGet(totalPoint);
                userProfileList.add(profilesData);
                Log.d(TAG, "parseData: Before sort");
                Collections.sort(userProfileList, new Comparator<UserProfile>() {
                    @Override
                    public int compare(UserProfile o1, UserProfile o2) {
                        int sum_lhs = 0;
                        for (Rewards content : o1.rewardRecords) {
                            sum_lhs += content.getValue();
                        }
                        int sum_rhs = 0;
                        for (Rewards content : o2.rewardRecords) {
                            sum_rhs += content.getValue();
                        }
                        return sum_rhs - sum_lhs;
                    }
                });

            }
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private Bitmap stringToPhoto(String completeImageData) {

        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",") + 1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }

    private int getRewardPoints(List<Rewards> rewardsContents) {
        int sum = 0;
        for (Rewards content : rewardsContents) {
            sum += content.getValue();
        }
        return sum;
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("username", username);
        data.putExtra("password", password);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }


}
