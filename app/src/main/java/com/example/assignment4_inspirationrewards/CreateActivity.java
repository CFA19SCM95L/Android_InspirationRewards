package com.example.assignment4_inspirationrewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CreateActivity extends AppCompatActivity {

    private static final String TAG = "CreateActivity";

    public ImageView photo;
    public EditText username;
    public EditText password;
    public CheckBox checkAdmin;
    public EditText firstName;
    public EditText lastName;
    public EditText department;
    public EditText position;
    public TextView wordCount;
    public EditText story;
    private LocationManager locationManager;
    private Criteria criteria;
    private String location;
    public final int MY_FILE_REQUEST_CODE = 1;
    public final int REQUEST_IMAGE_CAPTURE = 2;
    public final int REQUEST_IMAGE_GALLERY = 3;
    public ProgressBar progressBar;


    public File file;


    /**
     * location go here
     **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Utilities.setupHomeIndicatorAndName(getSupportActionBar(), "Create Profile");
        photo = findViewById(R.id.photo_create);
        username = findViewById(R.id.username_create);
        password = findViewById(R.id.password_create);
        checkAdmin = findViewById(R.id.checkBox);
        firstName = findViewById(R.id.firstName_create);
        lastName = findViewById(R.id.lastName_create);
        department = findViewById(R.id.department_create);
        position = findViewById(R.id.position_create);
        wordCount = findViewById(R.id.wordCount_create);
        story = findViewById(R.id.story_create);
        progressBar = findViewById(R.id.progressBar_create);
        setupTextView();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_create:
                if (!checkType()) {
                    Toast.makeText(this, "Input Can not be empty", Toast.LENGTH_SHORT).show();
                    return false;
                }
                getLocation();
                doAsync();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupTextView() {
        story.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(360)
        });

        story.addTextChangedListener(
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

    @SuppressLint("MissingPermission")
    private void getLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
        if (currentLocation != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                location = city + ", " + state;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "CANNOT get location", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * get photo function
     */
    public void getPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_FILE_REQUEST_CODE);
        } else {
            optionDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == MY_FILE_REQUEST_CODE) {   // same code from step 6
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PERMISSION_GRANTED) {
                optionDialog();
                return;
            }
            Toast.makeText(this, "Need your permission to access file", Toast.LENGTH_SHORT).show();
        }
    }

    private void optionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take Picture From:");

        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doCamera();
            }
        }).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doGallery();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void doCamera() {
        file = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");  //set file name
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);     //camera
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));    //
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);           //go to another activity and return a file with
    }

    public void doGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);   //go to another activity and return
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {  //go back from gallery
            try {
                processGallery(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {   //go back from camera
            try {
                processCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processCamera() {
        Uri selectedImage = Uri.fromFile(file);
        photo.setImageURI(selectedImage);
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null) return;
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        photo.setImageBitmap(selectedImage);
    }

    private String photoToString() {

        Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void stringToPhoto(String completeImageData) {

        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",") + 1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        photo.setImageBitmap(bitmap);
    }

    /**
     * get photo function end
     */

    public boolean checkType() {
        if (username.getText().length() == 0 || password.getText().length() == 0
                || firstName.getText().length() == 0 || lastName.getText().length() == 0
                || department.getText().length() == 0 || position.getText().length() == 0 || story.getText().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Async
     */
    public void doAsync() {
        UserProfile user = new UserProfile();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setPointsToAward(1000);
        user.setDepartment(department.getText().toString());
        user.setStory(story.getText().toString());
        user.setPosition(position.getText().toString());
        user.setAdmin(checkAdmin.isChecked());
        user.setLocation(location);
        user.setPhoto(photoToString());
        user.setRewardRecords(new ArrayList<Rewards>());
        new AsyncTask_CreateAPI(this).execute(user);

    }

    public void sendResults(boolean success, String result) {
        String status="";
        String message="";
        if (success) {
            Log.d(TAG, "sendResults: ****success create");
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("resultFromCreate", result);
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
            Toast.makeText(this,status + ":" + message,Toast.LENGTH_LONG).show();
        }


    }
}
