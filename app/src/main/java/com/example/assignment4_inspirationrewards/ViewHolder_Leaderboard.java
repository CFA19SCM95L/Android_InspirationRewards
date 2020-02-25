package com.example.assignment4_inspirationrewards;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder_Leaderboard extends RecyclerView.ViewHolder {
    public ImageView photo;
    public TextView fullName;
    public TextView position;
    public TextView point;

    public ViewHolder_Leaderboard(View view) {
        super(view);
        photo = view.findViewById(R.id.photo_awardList);
        fullName = view.findViewById(R.id.fullName_awardList);
        position = view.findViewById(R.id.position_awardList);
        point = view.findViewById(R.id.point_awardList);
    }
}
