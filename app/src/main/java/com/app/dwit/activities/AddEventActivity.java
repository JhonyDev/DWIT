package com.app.dwit.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.dwit.Info.Info;
import com.app.dwit.R;
import com.app.dwit.Utils.ImagePicker;
import com.app.dwit.models.Event;
import com.app.dwit.models.User;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity implements Info {

    SimpleDraweeView svEventImage;
    Bitmap bmpEventImage;

    EditText etAddress;
    EditText etTitle;
    EditText etDescription;
    EditText etDate;
    EditText etStartsAt;
    EditText etEndsAt;

    String strEtAddress;
    String strEtTitle;
    String strEtDescription;
    String strEtDate;
    String strEtStartsAt;
    String strEtEndsAt;
    String latitude;
    String longitude;

    int PLACE_PICKER_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initViews();


    }

    private void initViews() {
        svEventImage = findViewById(R.id.sv_event_image);
        etAddress = findViewById(R.id.et_address);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etDate = findViewById(R.id.et_date);
        etStartsAt = findViewById(R.id.et_starts_at);
        etEndsAt = findViewById(R.id.et_ends_at);
    }

    public void initGalleryAccess() {
        if (isStoragePermissionGranted()) {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                final Uri imageUri = data.getData();
                if (imageUri != null) {
                    svEventImage.setImageURI(imageUri);
                    bmpEventImage = ImagePicker.getImageFromResult(this, resultCode, data);
                }
            }
            Log.i(TAG, "onActivityResult: " + data);

        }

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            String lat = String.valueOf(place.getLatLng().latitude);
            String lng = String.valueOf(place.getLatLng().longitude);
            latitude = lat;
            longitude = lng;
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                String address = addresses.get(0).getAddressLine(0);
                etAddress.setText(address);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private boolean isEverythingValid() {
        strEtAddress = etAddress.getText().toString();
        strEtTitle = etTitle.getText().toString();
        strEtDescription = etDescription.getText().toString();
        strEtDate = etDate.getText().toString();
        strEtStartsAt = etStartsAt.getText().toString();
        strEtEndsAt = etEndsAt.getText().toString();

        if (strEtAddress.isEmpty()) {
            etAddress.setError("Field Empty");
            return false;
        }
        if (strEtTitle.isEmpty()) {
            etTitle.setError("Field Empty");
            return false;
        }
        if (strEtDescription.isEmpty()) {
            etDescription.setError("Field Empty");
            return false;
        }
        if (strEtDate.isEmpty()) {
            etDate.setError("Field Empty");
            return false;
        }

        if (strEtStartsAt.isEmpty()) {
            etStartsAt.setError("Field Empty");
            return false;
        }

        if (strEtEndsAt.isEmpty()) {
            etEndsAt.setError("Field Empty");
            return false;
        }
        if (bmpEventImage == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (latitude.equals("")) {
            Toast.makeText(this, "Please select a location again", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (longitude.equals("")) {
            Toast.makeText(this, "Please select a location again", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void submit(View view) {

        if (!isEverythingValid()) {
            Toast.makeText(this, "Invalid Arguments", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage(bmpEventImage);

    }

    private void writeDataToFirebase(String url) {
        String eventId = UUID.randomUUID().toString();

        Event event = new Event(eventId, url, strEtAddress, strEtTitle, strEtDescription,
                strEtDate, strEtStartsAt, strEtEndsAt, latitude, longitude);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child(eventId);
        myRef.setValue(event);
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra(KEY_EVENT_ID, eventId);
        startActivity(intent);
        finish();


    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        bitmap.recycle();
        this.bmpEventImage.recycle();

        final StorageReference ref;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseStorage.getInstance().getReference("Images").child(user.getUid());
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading....");
        pd.show();
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            new Handler().postDelayed(pd::dismiss, 500);
            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
            result.addOnSuccessListener(uri -> {
                String urlToImage = uri.toString();
                writeDataToFirebase(urlToImage);
            });
            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(getApplication(), "Uploading failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            pd.setMessage("Uploaded - " + (int) progress + "%");
        });


    }

    public void goPlacePicker(View view) {
        try {
            PlacePicker.IntentBuilder placePicker = new PlacePicker.IntentBuilder();
            this.startActivityForResult(placePicker.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    public void requestPermission(View view) {
        initGalleryAccess();
    }

    public void showDatePicker(View view) {
        final Calendar newCalendar = Calendar.getInstance();
        new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            Log.i(TAG, "showDatePicker: day : " + dayOfMonth);
            Log.i(TAG, "showDatePicker: month : " + (monthOfYear + 1));
            Log.i(TAG, "showDatePicker: year : " + year);
            String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            etDate.setText(date);
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }


    public void showTimePicker(View view) {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            String startTime = selectedHour + ":" + selectedMinute;
            etStartsAt.setText(startTime);
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void showEndsTimePicker(View view) {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            String endTime = selectedHour + ":" + selectedMinute;
            etEndsAt.setText(endTime);
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
}