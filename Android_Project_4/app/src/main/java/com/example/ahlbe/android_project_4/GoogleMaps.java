package com.example.ahlbe.android_project_4;

//The codes below have been taken from: https://github.com/mitchtabian/Google-Maps-Google-Places/blob/67054c45d34ffa5e0a0564fc8c61de939522a6c4/app/src/main/java/codingwithmitch/com/googlemapsgoogleplaces/MapActivity.java
//For the complete tutorial: https://www.youtube.com/watch?v=Vt6H9TOmsuo

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {
    //private ArrayList<LatLng> points = new ArrayList<LatLng>();
    private static final long MAX_ALERT_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days
    private static final long MAX_HUE = 360;

    private ArrayList<Pair<LatLng,Date>> alerts;
    private FirebaseFirestore db;
    private static final String TAG = "GoogleMaps";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // Test Conan ID
    String conanID = "0x4a72b2b79943";


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted");
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "In on create");

        alerts = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings.Builder builder = new FirebaseFirestoreSettings.Builder();
        builder.setPersistenceEnabled(false);
        db.setFirestoreSettings(builder.build());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission not granted");
        } else {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
            {
                @Override
                public void onSuccess(Location location)
                {

                    Log.d(TAG, "in the onSuccess " + location.getLatitude() + location.getLongitude() + DEFAULT_ZOOM);
                    if(mMap != null) {
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
                    }
                }
            });
        }

        Log.d(TAG, "Retrieved Firestore reference");
        db.collection(getString(R.string.alert_collection)).whereEqualTo(getString(R.string.alert_conan_id), conanID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Log.d("GoogleMaps", "In query callback");
                alerts = new ArrayList<>();

                for (QueryDocumentSnapshot i : queryDocumentSnapshots) {
                    GeoPoint location = i.getGeoPoint(getString(R.string.alert_location));
                    Date time = i.getDate(getString(R.string.alert_time));

                    if(time != null && location != null){
                        Log.d(TAG, "Location: " + location.toString());
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Pair<LatLng,Date> pair = new Pair<>(latLng, time);
                        alerts.add(pair);
                    }
                }
                // Update map after all points have been added
                getDeviceLocation();
            }
        });

        setContentView(R.layout.activity_google_maps);
        getLocationPermission();
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            //        DEFAULT_ZOOM);
                            LatLng point;
                            Polyline line;

                            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                            for(Pair pair : alerts) {
                                LatLng latLng = (LatLng) pair.first;
                                options.add(latLng);
                            }
                            //add Marker in current position
                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM);
                            line = mMap.addPolyline(options); //add Polyline

                            Date currentTime = new Date();
                            for(Pair pair : alerts) {
                                LatLng latLng = (LatLng) pair.first;
                                Date time = (Date) pair.second;
                                long timeDifference = currentTime.getTime() - time.getTime();
                                Log.d(TAG, time.toString());
                                Log.d(TAG, "(Current time: " + currentTime.getTime() + "  -  Alert time: " + time.getTime() + ") = " + (timeDifference));
                                Log.d(TAG, timeDifference + " / " + MAX_ALERT_TIME + " = " + ((double) timeDifference / (double) MAX_ALERT_TIME));
                                if(timeDifference < MAX_ALERT_TIME) {
                                    long hue = new Double(((double) (timeDifference) / (double) MAX_ALERT_TIME) * (double) MAX_HUE).longValue();
                                    Log.d(TAG, new Long(hue).toString());
                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(time.toString())
                                            .icon(BitmapDescriptorFactory.defaultMarker(hue)));
                                }
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(GoogleMaps.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        Log.d(TAG, "zoom " + zoom);
        Log.d(TAG, "mMap object" + mMap);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(GoogleMaps.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
}
