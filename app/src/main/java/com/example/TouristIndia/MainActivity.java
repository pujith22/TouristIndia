package com.example.TouristIndia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_FINE_LOCATION = 2;
    DatabaseReference mref;
    String TAG = "phoneauth";
    String name = "user";
    NavigationView navigationView;
    LinearLayout caution;
    EditText state, city;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tools = findViewById(R.id.toolbar);
        setSupportActionBar(tools);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < tools.getChildCount(); i++) {
            if (tools.getChildAt(i) instanceof ImageButton) {
                tools.getChildAt(i).setScaleX(1.5f);
                tools.getChildAt(i).setScaleY(1.5f);
            }
        }

        drawer = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        toggler = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(toggler);
        toggler.syncState();
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        Button listButton = findViewById(R.id.listButton);
        //Button mapButton = (Button)findViewById(R.id.mapButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent i = new Intent(MainActivity.this, ListActivity.class);
                i.putExtra("state", state.getText().toString());
                i.putExtra("city", city.getText().toString());
                // currentContext.startActivity(activityChangeIntent);
                MainActivity.this.startActivity(i);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.NearMe) {
            checkPermissions();
        } else if (id == R.id.Home) {
            drawer.closeDrawers();
        } else if (id == R.id.about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.faq) {
            Intent i = new Intent(MainActivity.this, FAQActivity.class);
            startActivity(i);
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this, ChoiceActivity.class);
            startActivity(i);
            finish();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggler.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_FINE_LOCATION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start location preview Activity.

                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);

            } else {
                // Permission request was denied.

                Toast.makeText(MainActivity.this, "Set Location Permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
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

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }
}