package com.malindu.alarm15.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.malindu.alarm15.MainActivity;
import com.malindu.alarm15.R;
import com.malindu.alarm15.models.LocationAlarm;

import java.util.List;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private List<LocationAlarm> locationAlarms;
    private Location userLocation;
    private int activeCount;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //locationAlarms = loadLocationAlarms(getApplicationContext());
        LocationUtils.createFGSNotificationChannel(getApplicationContext());
        getLocation();
        startForeground(1, getFGSNotification("Tracking location..."));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        locationAlarms = LocationUtils.getAllLocationAlarms(getApplicationContext());
        return START_NOT_STICKY;
    }

    private void getLocation() {
        LocationRequest locationRequest = new LocationRequest
                .Builder(5000)
                .setMinUpdateIntervalMillis(2000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: no permissions");
            Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_LONG).show();
            return;
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                userLocation = locationResult.getLastLocation();
                if (userLocation != null) {
                    Log.d(TAG, "Count: " + activeCount + " -- " + "UserLocation - " + userLocation.getLatitude()+":"+userLocation.getLongitude());
                    checkLocationAlarms(userLocation);
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                //super.onLocationResult(locationResult);
//                userLocation = locationResult.getLastLocation();
//                if (userLocation != null) {
//                    Log.d(TAG, "Count: " + " -- " + "UserLocation - " + userLocation.getLatitude()+":"+userLocation.getLongitude());
//                    checkLocationAlarms(userLocation);
//                }
//
//            }
//        }, Looper.myLooper());
    }

    private void checkLocationAlarms(Location location) {
        activeCount = 0;
        for (LocationAlarm alarm : locationAlarms) {
            if (alarm.isTurnedOn() && isWithinRange(location, alarm)) {
                showNotification("You are within range of: " + alarm.getTitle());
                // Optionally: Turn off the alarm if it should only trigger once
                //TODO allow user to set trigger once or trigger everytime
                alarm.setTurnedOn(false); // has to write in shared preferences
                if (LocationUtils.getActiveAlarmCount(getApplicationContext()) == 0) {
                    Log.d(TAG, "Service stopped");
                    stopSelf();
                }
            }
            if (alarm.isTurnedOn()) { activeCount++; }
        }
        if (activeCount == 0) { stopSelf(); }
    }

    private boolean isWithinRange(Location location, LocationAlarm alarm) {
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), alarm.getLatLng().latitude, alarm.getLatLng().longitude, results);
        return results[0] <= alarm.getRange();
    }

    private Notification getFGSNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, Constants.CHANNEL_ID_LOCATION_FGS)
                .setContentTitle("Location Alarm Service")
                .setContentText(text)
                .setSmallIcon(R.drawable.icon_pin_drop)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID_LOCATION_ALARM)
                .setSmallIcon(R.drawable.icon_pin_drop)
                .setContentTitle("Location Alarm")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }
}