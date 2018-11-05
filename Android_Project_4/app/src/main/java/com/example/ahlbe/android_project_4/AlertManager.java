package com.example.ahlbe.android_project_4;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class AlertManager implements LocationListener {
    private static final String[] PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    protected LocationManager locationManager;
    private Date time;
    private Location location;
    private Collection<Beacon> collection;

    public AlertManager(Context context, Collection collection) {
        Log.d("AlertManager", "Alert manager constructor");
        this.collection = collection;
        time = Calendar.getInstance().getTime();

        Iterator<Beacon> iterator = collection.iterator();
        while (iterator.hasNext()){
            Beacon beacon = iterator.next();
            Log.d("AlertManager", "Conan ID: " + beacon.getId2());
        }

        Log.d("AlertManager", "Initialize a location manager");
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            Log.d("AlertManager", "Requesting location update");
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("AlertManager", "Location: " + location.toString());
        Iterator<Beacon> iterator = collection.iterator();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("AlertManager", "Status changed: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("AlertManager", "Provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("AlertManager", "Provider disabled: " + provider);
    }
}
