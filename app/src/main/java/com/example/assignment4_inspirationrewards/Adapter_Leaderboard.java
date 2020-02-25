package com.example.assignment4_inspirationrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class Adapter_Leaderboard extends RecyclerView.Adapter<ViewHolder_Leaderboard> {
    private static final String TAG = "Adapter_Leaderboard";

    private List<UserProfile> userList;
    private LeaderboardActivity leaderboardActivity;
    public String username;

    public Adapter_Leaderboard(List<UserProfile> userList, LeaderboardActivity la) {
        this.userList = userList;
        leaderboardActivity = la;
    }

    @NonNull
    @Override
    public ViewHolder_Leaderboard onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_leaderboard, parent, false);

        itemView.setOnClickListener(leaderboardActivity);

        return new ViewHolder_Leaderboard(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Leaderboard holder, int position) {

        UserProfile userProfile = userList.get(position);
        Log.d(TAG, "onBindViewHolder: before Change color " + userProfile.getUsername()+":"+ username);

        if (userProfile.getUsername().equals(username)) {
            Log.d(TAG, "onBindViewHolder: Change color");
            holder.fullName.setTextColor(Color.GREEN);
            holder.position.setTextColor(Color.GREEN);
            holder.point.setTextColor(Color.GREEN);
        }

        holder.photo.setImageBitmap(stringToPhoto(userProfile.getPhoto()));
        holder.fullName.setText(userProfile.getLastName() + "," + userProfile.getFirstName());
        holder.position.setText(userProfile.getPosition() + "," + userProfile.getDepartment());
        holder.point.setText(userProfile.getPointsGet()+ "");

    }

    @Override
    public int getItemCount() {
        return userList.size();
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
}
