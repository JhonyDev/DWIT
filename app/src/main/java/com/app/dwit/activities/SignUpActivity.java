package com.app.dwit.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.app.dwit.BuildConfig;
import com.app.dwit.Info.Info;
import com.app.dwit.R;
import com.app.dwit.Utils.ImagePicker;
import com.app.dwit.models.User;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SignUpActivity extends AppCompatActivity implements Info {

    EditText etFirstName;
    EditText etLastName;
    EditText etEmail;
    EditText etPassword;
    String strEtFirstName;
    String strEtLastName;
    String strEtEmail;
    String strEtPassword;
    SimpleDraweeView ivProfileImage;
    Bitmap bitmap;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        ivProfileImage = findViewById(R.id.iv_profile_image);

        mAuth = FirebaseAuth.getInstance();
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

    public void initGalleryAccess() {
        if (isStoragePermissionGranted()) {
            openGallery();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                final Uri imageUri = data.getData();
                if (imageUri != null) {
                    ivProfileImage.setImageURI(imageUri);
                    bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                }
            }
            Log.i(TAG, "onActivityResult: " + data);

        }
    }

    public void signUp(View view) {
        if (!isEverythingValid()) {
            Toast.makeText(this, "Invalid Arguments", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(strEtEmail, strEtPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                if (bitmap != null) {
                    uploadImage(bitmap);
                } else {
                    Toast.makeText(this, "Please upload a profile image", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        bitmap.recycle();
        this.bitmap.recycle();

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

    private void writeDataToFirebase(String urlToImage) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String id = FirebaseAuth.getInstance().getUid();
        User user = new User(id, strEtFirstName, strEtLastName, strEtEmail,
                strEtPassword, urlToImage);
        DatabaseReference myRef = database.getReference(USERS).child(id);
        myRef.setValue(user);
        startActivity(new Intent(SignUpActivity.this, MapsActivity.class));
        finish();
    }

    private boolean isEverythingValid() {
        strEtFirstName = etFirstName.getText().toString();
        strEtLastName = etLastName.getText().toString();
        strEtEmail = etEmail.getText().toString();
        strEtPassword = etPassword.getText().toString();

        if (strEtFirstName.isEmpty()) {
            etFirstName.setError("Field Empty");
            return false;
        }
        if (strEtLastName.isEmpty()) {
            etLastName.setError("Field Empty");
            return false;
        }
        if (strEtEmail.isEmpty()) {
            etEmail.setError("Field Empty");
            return false;
        }
        if (strEtPassword.isEmpty()) {
            etPassword.setError("Field Empty");
            return false;
        }

        if (!strEtEmail.contains("@") | !strEtEmail.contains(".")) {
            etEmail.setError("Invalid Email");
            return false;
        }

        if (strEtPassword.length() < 6) {
            etPassword.setError("Must be at least 6 characters");
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void openGallery(View view) {
        initGalleryAccess();
    }
}