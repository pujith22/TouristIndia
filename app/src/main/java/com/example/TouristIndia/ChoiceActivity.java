package com.example.TouristIndia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ChoiceActivity extends AppCompatActivity {

    private static final int PERMISSION_FINE_LOCATION = 2;
    LinearLayout admin, user;
    String type = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        admin = findViewById(R.id.admin);
        user = findViewById(R.id.user);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "admin";
                checkPermissions();

            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "user";
                checkPermissions();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(ChoiceActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_FINE_LOCATION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start location preview Activity.

                Intent i = new Intent(ChoiceActivity.this, LoginActivity.class);
                i.putExtra("type", type);
                startActivity(i);

            } else {
                // Permission request was denied.

                Toast.makeText(ChoiceActivity.this, "Set Location Permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(ChoiceActivity.this, LoginActivity.class);
            i.putExtra("type", type);
            startActivity(i);
            // Permission is already available, start camera preview
        } else {
            // Permission is missing and must be requested.
            requestFineLocationPermission();
        }
    }

    private void requestFineLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.

            ActivityCompat.requestPermissions(ChoiceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }
}
