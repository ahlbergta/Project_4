package com.example.ahlbe.android_project_4;

import android.app.Application;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

public class DemoApplication extends Application implements BootstrapNotifier, BeaconConsumer, RangeNotifier {
    private static final String ROCKET_ID = "0xd38dd9b09451";
    private static final String GROOT_ID = "0x4a72b2b79943";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconManager beaconManager;
    private AlertManager alertManager;

    @Override
    public void onCreate() {
        Log.d("BLE_Background_Scanner", "Start of onCreate");
        super.onCreate();

        // Create the beacon manager and add Eddystone format to beacon parser
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        // Set beacon manager scan timing
        beaconManager.setRegionStatePersistenceEnabled(false);
        beaconManager.setForegroundBetweenScanPeriod(30001);   // 3000 ms
        beaconManager.setForegroundScanPeriod(BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD);
//        beaconManager.setBackgroundBetweenScanPeriod(50001);
        beaconManager.setBackgroundScanPeriod(BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD);

        beaconManager.setRangeNotifier(this);

        // Create the conan region
        Identifier conanNamespaceId = Identifier.parse("0x436f6e616e446f796c65");
        Region region = new Region("conan-region", conanNamespaceId, null, null);

        // Create the region bootstrap using the region
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        // TEST CODE
        // Subscribe to conanID
//        subscribe(ROCKET_ID);
//        subscribe(GROOT_ID);

        Log.d("BLE_Background_Scanner", "End of onCreate");
    }

    private void subscribe(String id){
        FirebaseMessaging.getInstance().subscribeToTopic(id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = getString(R.string.msg_subscribed);
//                        if (!task.isSuccessful()) {
//                            msg = getString(R.string.msg_subscribe_failed);
//                        }
                        Log.d("Subscribe", "In subscription callback listener");
                    }
                });
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d("BLE_Background_Scanner", "Conan beacon found: " + region.getId2());
        try{
            beaconManager.startRangingBeaconsInRegion(region);
        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d("BLE_Background_Scanner", "No Conan beacons found, ending beacon ranging");
        try{
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d("BLE_Background_Scanner", "In didDetermineStateForRegion, current state: " + i);
//        if(i==1){
//            didEnterRegion(region);
//        }
//        if(i==0){
//            didExitRegion(region);
//        }
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d("BLE_Background_Scanner", "Beacon service ready");
//        beaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        // Code for when Conan device is ranged
        Log.d("BLE_Background_Scanner", "Start beacon range callback");
        if(collection.size() > 0){
            alertManager = new AlertManager(this.getApplicationContext(), collection);
        }
    }
}
