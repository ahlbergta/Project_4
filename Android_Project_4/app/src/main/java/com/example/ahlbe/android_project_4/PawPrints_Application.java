package com.example.ahlbe.android_project_4;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class PawPrints_Application extends Application implements BootstrapNotifier, BeaconConsumer, RangeNotifier {
    private static final String TAG = "PawPrints_Application";

//    --------------------- Test Code
    private static final String ROCKET_ID = "0xd38dd9b09451";
    private static final String GROOT_ID = "0x4a72b2b79943";
//    --------------------- End Test Code

    private static final int CONAN_RANGING_NOTIFICATION_ID = 101;
    private static final long SCAN_RATE_MAX = 0;
    private static final long SCAN_RATE_MIN = 30000;
    private static final long CACHE_REFRESH = 1000 * 60 * 15; // 15 minutes
    private FirebaseFirestore db;
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconManager beaconManager;
    private AlertManager alertManager;
    private ArrayList<ConanCache> conanCache;
    private ArrayList<String> ownedPets;

    @Override
    public void onCreate() {
        Log.d(TAG, "Start of onCreate");
        super.onCreate();

        conanCache = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings.Builder builder = new FirebaseFirestoreSettings.Builder();
        builder.setPersistenceEnabled(false);
        db.setFirestoreSettings(builder.build());

        ownedPets = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            CollectionReference ref = db.collection("Pets");
            Query query = ref.whereArrayContains(getString(R.string.pet_owners), user.getUid());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "Query complete, task successful: " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null) {
                            for (QueryDocumentSnapshot doc : result) {
                                String id = (String) doc.get(getString(R.string.pet_conan_id));
                                ownedPets.add(id);
                            }
                        } else {
                            Log.d(TAG, "Result is empty");
                        }
                    }
                }
            });
        }

        // TODO: If the user is logged in get the list of their pets
        /*
        if(currentUser != null){
            ownedPets = ...
        } else {
            ownedPets = null;
        }
        */

        // Create the beacon manager and add Eddystone format to beacon parser
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        // Enable foreground BLE scanning by setting a notification
        ForegroundRangingSetup();

        // Set beacon manager initial scan timing
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setRegionStatePersistenceEnabled(false);
        beaconManager.setForegroundBetweenScanPeriod(SCAN_RATE_MAX);
        beaconManager.setForegroundScanPeriod(BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD);
        beaconManager.setBackgroundBetweenScanPeriod(SCAN_RATE_MAX);
        beaconManager.setBackgroundScanPeriod(BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD);

        beaconManager.addRangeNotifier(this);

        // Create the conan region
        Identifier conanNamespaceId = Identifier.parse(getString(R.string.conan_namespace));
        Region region = new Region("conan-region", conanNamespaceId, null, null);

        // Create the region bootstrap using the region
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        // ------------------ TEST CODE
        // Subscribe to conanID
//        SubscriptionManager.subscribe(GROOT_ID);
//        SubscriptionManager.subscribe(ROCKET_ID);
        // ------------------ END TEST CODE

        Log.d(TAG, "End of onCreate");
    }

    public void ClearCache() {
        Log.d(TAG, "Clearing Conan cache");
        conanCache = new ArrayList<>();
    }

    public void AddPet(String id) {
        ownedPets.add(id);
        SubscriptionManager.subscribe(id);
    }

    public ArrayList<String> getPets() {
        return ownedPets;
    }

    private void ForegroundRangingSetup(){
        Log.d(TAG, "In ForegroundRangingSetup");
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Scanning for Conan beacons");

        // Create notification channel if version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Conan Ranging Channel", "Conan ranging notification channel", NotificationManager.IMPORTANCE_DEFAULT);
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

    private ConanCache cached(String id){
        for(ConanCache c : conanCache) {
            if(c.getID().equals(id)){
                return c;
            }
        }
        return null;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        // Code for when Conan device is ranged
        Log.d(TAG, "Beacons found: " + collection.toString());

        Date now = new Date();
        // Remove beacon from collection if it is in the cache, otherwise add it to the cache
        ArrayList<ConanCache> newCache = new ArrayList<>();
        for(Beacon beacon : collection) {
            String id = beacon.getId2().toHexString();
            ConanCache cachedConan = cached(id);

            // Only keep cached ids that are in the collection
            if(cachedConan != null) {
                // Remove beacons from the collection that are in the cache, add the id to the updated cache
                Log.d(TAG, "Cached beacon found: " + id + ", removing from the collection");
                collection.remove(beacon);
            }
            else {
                // Add a new beacon to the cache
            }

            // Add the beacon to the updated cache if there is not an expired cache object in the old cache
            if(cachedConan == null || now.getTime() - cachedConan.getTime() < CACHE_REFRESH){
                if(cachedConan != null) {
                    Log.d(TAG, "Time cached: " + (now.getTime() - cachedConan.getTime()) + " less than" + CACHE_REFRESH);
                    newCache.add(cachedConan);
                } else {
                    Log.d(TAG, "Adding new ID: " + id + " to the cache");
                    newCache.add(new ConanCache(id));
                }
            }
        }
        // Update the Conan ID cache
        Log.d(TAG, "Updating Conan cache");
        conanCache = newCache;

        Log.d(TAG, "Updated collection: " + collection.toString());

        // If there are any beacons in the collection create alerts for them
        if(collection.size() > 0){
            Log.d(TAG, "New beacons detected, creating alerts and setting max scan rate");

            // Set max scan rate
            beaconManager.setForegroundBetweenScanPeriod(SCAN_RATE_MIN);
            beaconManager.setBackgroundBetweenScanPeriod(SCAN_RATE_MIN);
            try{
                beaconManager.updateScanPeriods();
            } catch (RemoteException e){
                e.printStackTrace();
            }
            alertManager = new AlertManager(this.getApplicationContext(), collection);
        }
        else {
            Log.d(TAG, "No new beacons detected, decreasing scan rate to " + SCAN_RATE_MIN + "ms");
            // Set min scan rate and update the beacon manager
            beaconManager.setForegroundBetweenScanPeriod(SCAN_RATE_MIN);
            beaconManager.setBackgroundBetweenScanPeriod(SCAN_RATE_MIN);
            try{
                beaconManager.updateScanPeriods();
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }
}
