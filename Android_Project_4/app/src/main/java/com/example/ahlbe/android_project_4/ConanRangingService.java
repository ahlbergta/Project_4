package com.example.ahlbe.android_project_4;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class ConanRangingService extends JobIntentService implements RangeNotifier {
    private AlertManager alertManager;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        if(collection.size() > 0){
            alertManager = new AlertManager(this.getApplicationContext(), collection);
        }
    }
}
