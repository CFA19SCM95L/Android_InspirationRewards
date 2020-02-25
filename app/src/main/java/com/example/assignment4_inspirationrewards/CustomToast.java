package com.example.assignment4_inspirationrewards;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    public Activity activity;

    public  CustomToast(Activity activity){
        this.activity = activity;
    }
    public void show(String message) {
        View v = activity.getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.toast));
        TextView textView = v.findViewById(R.id.toastText);
        textView.setText(message);
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 600);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.show();
    }
}
