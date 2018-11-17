package com.example.ahlbe.android_project_4;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.Collection;

public class PawPrints_Application extends Application implements BootstrapNotifier, BeaconConsumer, RangeNotifier {
    private static final String TAG = "PawPrints_Application";

//    --------------------- Test Code
    private static final String ROCKET_ID = "0xd38dd9b09451";
    private static final String GROOT_ID = "0x4a72b2b79943";
//    --------------------- End Test Code

    private static final int CONAN_RANGING_NOTIFICATION_ID = 101;
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconManager beaconManager;
    private AlertManager alertManager;
    private ArrayList<String> conanCache;

    @Override
    public void onCreate() {
        Log.d(TAG, "Start of onCreate");
        super.onCreate();

        conanCache = new ArrayList<>();

        // Create the beacon manager and add Eddystone format to beacon parser
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        // Enable foreground BLE scanning by setting a notification
        ForegroundRangingSetup();

        // Set beacon manager scan timing
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setRegionStatePersistenceEnabled(false);
        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.setForegroundScanPeriod(BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD);

        beaconManager.addRangeNotifier(this);

        // Create the conan region
        Identifier conanNamespaceId = Identifier.parse(getString(R.string.conan_namespace));
        Log.d(TAG, conanNamespaceId.toString());
        Region region = new Region("conan-region", conanNamespaceId, null, null);

        // Create the region bootstrap using the region
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        // ------------------ TEST CODE
        // Subscribe to conanID
        SubscriptionManager.subscribe(GROOT_ID);
        SubscriptionManager.subscribe(ROCKET_ID);
        // ------------------ END TEST CODE

        Log.d(TAG, "End of onCreate");
    }

    private void ForegroundRangingSetup(){
        Log.d(TAG, "In ForegroundRangingSetup");
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Scanning for Conan beacons");

        // Create notification channel if version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Conan Ranging Channel",
                    "Conan ranging notification channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for notifying users of background BLE Conan scanning");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        beaconManager.enableForegroundServiceScanning(builder.build(), CONAN_RANGING_NOTIFICATION_ID);
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Conan beacon found");
        try{
            //Start background scanning service
            beaconManager.startRangingBeaconsInRegion(region);
        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "No Conan beacons found, ending beacon ranging");
        try{
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d(TAG, "In didDetermineStateForRegion, current state: " + i);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "Beacon service ready");
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        // Code for when Conan device is ranged
        Log.d(TAG, "Start beacon range callback");

        // Remove beacon from collection if it is in the cache, otherwise add it to the cache
        ArrayList<String> newCache = new ArrayList<>();
        for(Beacon beacon : collection) {
            String id = beacon.getId2().toHexString();

            // Only keep cached ids that are in the collection
            if(conanCache.contains(id)) {
                // Remove beacons from the collection that are in the cache, add the id to the updated cache
                Log.d(TAG, "Cached beacon found: " + id);
                newCache.add(id);
                collection.remove(beacon);
            }
            else {
                // Add a new beacon to the cache
                Log.d(TAG, "New beacon found: " + id);
                newCache.add(id);
            }
        }
        conanCache = newCache;

        // If there are any beacons in the collection create alerts for them
        if(collection.size() > 0){
            alertManager = new AlertManager(this.getApplicationContext(), collection);
        }
        else {
            // Decrease scan rate if there are no beacons in the collection
        }
    }
}
