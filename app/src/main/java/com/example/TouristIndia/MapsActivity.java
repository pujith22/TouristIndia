package com.example.TouristIndia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    DatabaseReference mref;
    NavigationView navigationView;
    String TAG = "maps check";
    double lat = 0;
    double lon = 0;
    SupportMapFragment mapFragment;
    HeatmapTileProvider mHeatmap;
    ArrayList<LatLng> list = new ArrayList<LatLng>();
    Location mylocation;
    Location nearest;
    private GoogleMap mMap;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggler;
    private LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        Toolbar tools = findViewById(R.id.toolbar);
        setSupportActionBar(tools);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tools.setNavigationIcon(R.drawable.menu);
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


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        juszoom();

        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        positionRecenter();

        mref = FirebaseDatabase.getInstance().getReference();

        mref.child("Patients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double min = -1;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String coordinate = ds.getValue(String.class);
                    assert coordinate != null;
                    String[] cc = coordinate.split(",");

                    double lt = Float.parseFloat(cc[0]);
                    double lg = Float.parseFloat(cc[1]);

                    list.add(new LatLng(lt, lg));
                    Location temp = new Location("x");
                    temp.setLatitude(lt);
                    temp.setLongitude(lg);

                    if (mylocation != null) {
                        if (min == -1) {

                            min = mylocation.distanceTo(temp);
                            nearest = temp;
                        } else {
                            double dist = mylocation.distanceTo(temp);
                            if (min > dist) {
                                min = dist;
                                nearest = temp;
                            }
                        }
                    }

                }

                Log.d("mapsauth", list.size() + "--1");
                mHeatmap = new HeatmapTileProvider.Builder()
                        .data(list)
                        .build();
                mHeatmap.setRadius(80);
                TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mHeatmap));

                if (mylocation != null) {

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(nearest.getLatitude(), nearest.getLongitude()));
                    markerOptions.title("Nearest to your location!");
                    markerOptions.icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    mMap.addMarker(markerOptions);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("Covid", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putFloat("Distance", (float) (min / 1000));
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MapsActivity.this, "Database Not Loaded!!", Toast.LENGTH_SHORT).show();

            }
        });

        Log.d("mapsauth", list.size() + "--2");


//        mClusterManager = new ClusterManager<>(this,mMap);
//        mMap.setOnCameraIdleListener(mClusterManager);
//        mMap.setOnMarkerClickListener(mClusterManager);


//        mClusterManager.cluster();
//
//        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerClusterItem>() {
//            @Override
//            public boolean onClusterItemClick(MarkerClusterItem item) {
//                Toast.makeText(MapsActivity.this,item.getTitle(),Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });


    }

    private void positionRecenter() {
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 240, 240, 0);
        locationButton.setScaleX(1.3f);
        locationButton.setScaleY(1.3f);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void juszoom() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        Location locationCt = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (locationCt != null) {
            LatLng latLng = new LatLng(locationCt.getLatitude(),
                    locationCt.getLongitude());

            mylocation = locationCt;

            lat = locationCt.getLatitude();
            lon = locationCt.getLongitude();

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }


    private void markData() {

        mref = FirebaseDatabase.getInstance().getReference();

        mref.child("Patients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String coordinate = ds.getValue(String.class);
                    String[] cc = coordinate.split(",");

                    double lt = Float.parseFloat(cc[1]);
                    double lg = Float.parseFloat(cc[2]);

                    list.add(new LatLng(lt, lg));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Database Not Loaded!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.NearMe) {
            drawer.closeDrawers();
        } else if (id == R.id.Home) {
            Intent i = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.faq) {
            Intent i = new Intent(MapsActivity.this, FAQActivity.class);
            startActivity(i);
        } else if (id == R.id.about) {
            Intent i = new Intent(MapsActivity.this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MapsActivity.this, ChoiceActivity.class);
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
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onStart() {
        super.onStart();
//        turnGPSOn();
        turnongps();
    }
//

    private void turnongps() {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        sendBroadcast(intent);
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
