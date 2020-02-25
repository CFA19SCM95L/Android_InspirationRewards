package com.example.assignment4_inspirationrewards;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder_Profile extends RecyclerView.ViewHolder{

    public TextView date;
    public TextView comment;
    public TextView fullName;
    public TextView point;

    public ViewHolder_Profile(View view) {
        super(view);
        date = view.findViewById(R.id.date_profileList);
        comment = view.findViewById(R.id.comment_profileList);
        fullName = view.findViewById(R.id.fullName_profileList);
        point = view.findViewById(R.id.point_profileList);
    }
}
