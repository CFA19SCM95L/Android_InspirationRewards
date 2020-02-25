package com.example.assignment4_inspirationrewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    public EditText username;
    public EditText password;
    public Button loginButton;
    public CheckBox remember;
    public Button createButton;
    public ProgressBar progressBar;
    public final int MY_LOCATION_REQUEST_CODE = 1;
    public final int MY_FILE_REQUEST_CODE = 2;
    public final int MY_BOTH_REQUEST_CODE = 3;
    private static final String TAG = "MainActivity";

    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.userName_login);
        password = findViewById(R.id.password_login);
        loginButton = findViewById(R.id.loginButton_login);
        remember = findViewById(R.id.rememberCheck_login);
        createButton = findViewById(R.id.createAccountButton_login);
        progressBar = findViewById(R.id.progressBar_create);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, MY_BOTH_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_FILE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }


        myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        prefsEditor = myPrefs.edit();

        String savedUsername = myPrefs.getString("username", "");
        String savedPassword = myPrefs.getString("password", "");
        boolean saved = myPrefs.getBoolean("remember", false);

        if (saved) {
            username.setText(savedUsername);
            password.setText(savedPassword);
            remember.setChecked(true);
        }


    }

//    public void doRemember(View view) {
//        if (remember.isChecked()) {
//            prefsEditor.putString("username", username.getText().toString());
//            prefsEditor.putString("password", password.getText().toString());
//            prefsEditor.putBoolean("remember", remember.isChecked());
//            prefsEditor.apply();
//        } else {
//            prefsEditor.clear();
//            prefsEditor.apply();
//        }
//
//    }


    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    public void login(View view) {

        if (remember.isChecked()) {
            prefsEditor.putString("username", username.getText().toString());
            prefsEditor.putString("password", password.getText().toString());
            prefsEditor.putBoolean("remember", remember.isChecked());
            prefsEditor.apply();
        } else {
            prefsEditor.clear();
            prefsEditor.apply();
        }

        new AsyncTask_LoginAPI(this).execute("A20435695", username.getText().toString(), password.getText().toString());

    }

    public void sendResults(boolean success, String result) {
        String status = "";
        String message = "";

        if (success) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("resultFromLogin", result);
            startActivity(intent);
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
            new CustomToast(this).show(status + ":\n" + message.toUpperCase());
        }
    }

    public void deleteAll(View v) {
        new AsyncTask_DeleteAllApi(this).execute();
    }

    //delete all users
    public void delete(boolean success, String result) {
        String status = "";
        String message = "";

        if (success) {
            Toast.makeText(this, "Delete users successfully", Toast.LENGTH_LONG).show();

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
            Toast.makeText(this, status + ":" + message, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_BOTH_REQUEST_CODE:
                if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PERMISSION_GRANTED && permissions[1].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[1] == PERMISSION_GRANTED) { //user permit
                    return;
                }
                Toast.makeText(this, "Need your permission", Toast.LENGTH_SHORT).show();
            case MY_FILE_REQUEST_CODE:
                if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PERMISSION_GRANTED) { //user permit
                    return;
                }
                Toast.makeText(this, "Need your permission", Toast.LENGTH_SHORT).show();
            case MY_LOCATION_REQUEST_CODE:
                if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PERMISSION_GRANTED) { //user permit
                    return;
                }
                Toast.makeText(this, "Need your permission", Toast.LENGTH_SHORT).show();
        }
    }

}
