package com.app.dwit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dwit.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    String strEtEmail;
    String strEtPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
//            mAuth.signOut();
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        }

    }

    public void login(View view) {
        if (!isEverythingValid()) {
            Toast.makeText(this, "Invalid Arguments", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(strEtEmail, strEtPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                finish();
            }
        });
    }

    private boolean isEverythingValid() {
        strEtEmail = etEmail.getText().toString();
        strEtPassword = etPassword.getText().toString();

        if (strEtEmail.isEmpty()) {
            etEmail.setError("Field Empty");
            return false;
        }

        if (strEtPassword.isEmpty()) {
            etPassword.setError("Field Empty");
            return false;
        }

        if (!strEtEmail.contains("@") | !strEtEmail.contains(".com")) {
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

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}