package com.example.ahlbe.android_project_4;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import org.altbeacon.beacon.Beacon;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlertManager implements LocationListener {
    protected LocationManager locationManager;
    private Context context;
    private Date time;
    private Collection<Beacon> collection;

    public AlertManager(Context context, Collection<Beacon> collection) {
        Log.d("AlertManager", "Alert manager constructor");
        this.context = context;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Iterator<Beacon> iterator = collection.iterator();
        while(iterator.hasNext()){
            Beacon beacon = iterator.next();
            final String conanID = beacon.getId2().toHexString();

            // Create and add alert to database
            Map<String, Object> alert = new HashMap<>();
            GeoPoint pointLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            alert.put("location", pointLocation);
            alert.put("time", time);
            alert.put("conanID", conanID);

            Log.d("AlertManager", "Adding alert: " + alert.toString());
            db.collection("Alerts").add(alert);

            // Generate notification if pet is lost
            db.collection("Pets").whereEqualTo("conanID", conanID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("ProximalNotification", "Query complete");
                    QuerySnapshot query = task.getResult();
                    if(query.size() == 1){
                        DocumentSnapshot pet = query.getDocuments().get(0);
                        Log.d("ProximalNotification", "Pet Name: " + pet.get("pName"));
                        Log.d("ProximalNotification", "Pet Name: " + pet.get("pNotes"));
                        //if((int) pet.get("status") == 1){
                            String pet_name = (String) pet.get("pName");
                            String pet_notes = (String) pet.get("pNotes");
                            Log.d("ProximalNotification", pet_name + " is lost! Creating alert notification");
                            AlertNotification notification = new AlertNotification(context);
                            notification.Notify(pet_name, pet_notes);
                            Log.d("ProximalNotification", "Notification has been created");
                        //}
                    }
                    else if(query.size() == 0){
                        Log.d("ProximalNotification", "Error: pet profile with conan ID: " + conanID + " not found");
                    }
                    else{
                        Log.d("ProximalNotification", "Error: more than 1 pet profile with conan ID: " + conanID);
                    }
                }
            });
        }
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
