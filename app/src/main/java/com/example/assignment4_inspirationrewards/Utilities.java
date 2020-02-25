package com.example.assignment4_inspirationrewards;

import androidx.appcompat.app.ActionBar;


class Utilities {

    static void setupHomeIndicatorAndName(ActionBar actionBar, String name) {


        if (actionBar != null) {
            if (name.equals("Inspiration Leaderboard")||name.equals("Edit Profile")||name.equals("Create Profile")) {
                actionBar.setTitle(name);
                actionBar.setHomeAsUpIndicator(R.drawable.arrow_with_logo);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                actionBar.setTitle(name);
                actionBar.setHomeAsUpIndicator(R.drawable.icon);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }


}
