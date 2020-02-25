package com.example.assignment4_inspirationrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class Adapter_Profile extends RecyclerView.Adapter<ViewHolder_Profile> {

    private List<Rewards> rewardsList;
    private ProfileActivity profileActivity;

    public Adapter_Profile(List<Rewards> rewardsList, ProfileActivity pa) {
        this.rewardsList = rewardsList;
        profileActivity = pa;
    }

    @NonNull
    @Override
    public ViewHolder_Profile onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_profile, parent, false);

        return new ViewHolder_Profile(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Profile holder, int position) {

        Rewards reward = rewardsList.get(position);

        holder.date.setText(reward.getDate());
        holder.comment.setText(reward.getNotes());
        holder.fullName.setText(reward.getName());
        holder.point.setText(reward.getValue()+"");
    }

    @Override
    public int getItemCount() {
        return rewardsList.size();
    }

}
