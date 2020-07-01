package com.example.TouristIndia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_FINE_LOCATION = 2;
    NavigationView navigationView;
    String[] ques = new String[]{"Who are we?",
            "What is the purpose of this app?",
            "Are you giving the results manually?",
            "You are requesting GPS access. Do you store our location data and track us?",
            "“I have an idea for you”. Where can I share my idea on adding any features of this application to handle epidemics?",
            "Where does the user data be stored?"};
    String[] ans = new String[]{"We are Android Developer Enthusiasts from BML Munjal University",
            "To help tourists find must visited places they are going to visit according to their popularity.",
            "Behind the Scene we are scrapping travel websites like wikitravel and many more, and providing the results compiling them in real time.",
            "No we wont keep your location data. It is just used to fetch your current location to better help you.",
            "You can write to us at kpsk12345@gmail.com",
            "We use google cloud services to store user information and is highly secured."};
    List<faqitem> faqItemList = new ArrayList<faqitem>();
    RecyclerView recycler;
    FaqAdapter adapter;
    LinearLayoutManager layoutManager;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

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

        for (int i = 0; i < ques.length; i++) {
            faqItemList.add(new faqitem(ques[i], ans[i]));
        }

        layoutManager = new LinearLayoutManager(this);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(layoutManager);
        adapter = new FaqAdapter(this, faqItemList);
        recycler.setAdapter(adapter);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.NearMe) {
            checkPermissions();
        } else if (id == R.id.Home) {
            Intent i = new Intent(FAQActivity.this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.about) {
            Intent i = new Intent(FAQActivity.this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.faq) {
            drawer.closeDrawers();
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(FAQActivity.this, ChoiceActivity.class);
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

                Intent i = new Intent(FAQActivity.this, MapsActivity.class);
                startActivity(i);

            } else {
                // Permission request was denied.

                Toast.makeText(FAQActivity.this, "Set Location Permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(FAQActivity.this, MapsActivity.class);
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

            ActivityCompat.requestPermissions(FAQActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }
}
