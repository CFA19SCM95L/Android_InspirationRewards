package com.example.assignment4_inspirationrewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class AwardActivity extends AppCompatActivity {

    private static final String TAG = "AwardActivity";

    public UserProfile userProfile;
    public TextView fullName;
    public ImageView photo;
    public TextView pointGet;
    public TextView department;
    public TextView position;
    public TextView story;
    public EditText pointGive;
    public TextView wordCount;
    public EditText comment;
    public String username;
    public String password;
    public String userFullName;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award);
        fullName = findViewById(R.id.fullName_award);
        photo = findViewById(R.id.photo_award);
        pointGet = findViewById(R.id.pointGet_award);
        department = findViewById(R.id.department_award);
        position = findViewById(R.id.position_award);
        story = findViewById(R.id.story_award);
        pointGive = findViewById(R.id.pointToGive_award);
        wordCount = findViewById(R.id.wordCount_award);
        comment = findViewById(R.id.comment_award);
        progressBar = findViewById(R.id.progressBar_award);

        Log.d(TAG, "onCreate: Award");


        if (getIntent().hasExtra("username")) {
            userProfile = (UserProfile) getIntent().getSerializableExtra("profile");
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            userFullName = getIntent().getStringExtra("fullName");

        }

        String name = userProfile.getFirstName() + " " + userProfile.getLastName();  //////!!!!
        Utilities.setupHomeIndicatorAndName(getSupportActionBar(), name);
        setupTextView();

        fullName.setText(userProfile.getLastName() + "," + userProfile.getFirstName());
        stringToPhoto(userProfile.getPhoto());

        pointGet.setText(userProfile.getPointsGet() + "");
        department.setText(userProfile.getDepartment());
        story.setText(userProfile.getStory());
        position.setText(userProfile.getPosition());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_award, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_award:
                if (checkInput()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setIcon(R.drawable.icon);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new AsyncTask_AwardAPI(AwardActivity.this).execute(userProfile.getUsername(), userFullName,
                                    comment.getText().toString(), pointGive.getText().toString(), username, password);
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setMessage("Add rewards for " + fullName.getText() + " ?");
                    builder.setTitle("Add Rewards Points?");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    new CustomToast(this).show("Cannot be empty");
                    return false;
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupTextView() {
        comment.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(80)
        });

        comment.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        int len = s.toString().length();
                        String countText = len + "";
                        wordCount.setText(countText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {

                    }
                });
    }

    private void stringToPhoto(String completeImageData) {

        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",") + 1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        photo.setImageBitmap(bitmap);
    }


    public void sendResults(boolean success, String result) {
        String status = "";
        String message = "";
        if (success) {
            Log.d(TAG, "sendResults: ****success reward");
            Intent intent = new Intent(this, LeaderboardActivity.class);
            intent.putExtra("resultFromAward", result);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("fullName",userFullName);
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
            new CustomToast(this).show(status + ":\n" + message);
        }
    }

    private boolean checkInput() {
        return (comment.getText().length() != 0 && pointGive.getText().length() != 0);
    }

}
