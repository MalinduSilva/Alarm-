package com.malindu.alarm15.utils;

import android.Manifest;

import java.util.ArrayList;

public class Constants {

    /**
     * File name of the preferences file used across the app.
     */
    public static final String ALARM_PREFERENCES_FILE = "ALARM_PREFERENCES_FILE";


    /**
     * Key which stores if the app is launched for the first time
     */
    public static final String ALARM_PREFERENCES_KEY_FIRST_LAUNCH_APP = "ALARM_PREFERENCES_KEY_FIRST_LAUNCH_APP";


    /**
     * Key which stores if the location features are launched for the first time
     */
    public static final String ALARM_PREFERENCES_KEY_FIRST_LAUNCH_LOCATION = "ALARM_PREFERENCES_KEY_FIRST_LAUNCH_LOCATION";


    /**
     * Key which stores the ID of the fragment which is currently selected.
     */
    public static final String SELECTED_FRAGMENT_ID_KEY = "SELECTED_FRAGMENT_ID";


    // Request codes
    public static final int PERMISSION_REQUEST_CODE = 1001;
    public static final int PERMISSION_REQUEST_CODE_LOCATION = 1002;


    /**
     * Prefix for the id for an alarm
     */
    public static final String ALARM_KEY  = "ALARM_ID_";


    /**
     * Key which stores the number of alarms set so far. Combined with {@link Constants#ALARM_KEY}, it is used to make id for alarms.
     */
    public static final String ALARM_COUNT_KEY  = "ALARM_COUNT";


    /**
     * Channel ID for the notification channel for sending alarm notifications
     */
    public static final String CHANNEL_ID_ALARM = "alarm_channel";


    /**
     * Notification ID for alarms
     */
    public static final int NOTIFICATION_ID = 1;


    /**
     * Required alarms for location features
     */
    public static final String[] REQUIRED_PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    public static final String[] REQUIRED_PERMISSIONS_ALARM = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    /** This method can be called to get a list of keys that are in app's shared preferences file, but not an alarm entry
     * @return {@link ArrayList<String>}
     * 2024-July-08 Update: WTF is this?
     */
    public static ArrayList<String> getNonAlarmSPKeys() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ALARM_COUNT_KEY);
        return list;
    }

    public static final String ACTION_SNOOZE = "com.malindu.alarm15.ACTION_SNOOZE";
    public static final String ACTION_DISMISS = "com.malindu.alarm15.ACTION_DISMISS";
    public static final String ACTION_START_ALARM = "com.malindu.alarm15.ACTION_START_ALARM";
    public static final String ACTION_STOP_ALARM = "com.malindu.alarm15.ACTION_STOP_ALARM";

    /**
     * Default zoom level for the Google Map in {@link com.malindu.alarm15.ui.LocationAddNewDialog}
     */
    public static final float DEFAULT_MAP_ZOOM = 15f;

    /**
     * Default zoom level adjustment when clicking zoom-in or zoom-out buttons
     */
    public static final float DEFAULT_MAP_ZOOM_ADJUST = 0.5F;

    /**
     * Channel ID for the notification channel for sending location alarm notifications
     */
    public static final String CHANNEL_ID_LOCATION_ALARM = "location_alarm_channel";

    /**
     * Channel ID for Foreground Service's persistent notification
     */
    public static final String CHANNEL_ID_LOCATION_FGS = "LocationAlarmServiceChannel";


    /**
     * Notification ID for location alarms
     */
    public static final int NOTIFICATION_ID_LOCATION = 2;

    /**
     * Prefix for the id for a location alarm
     */
    public static final String LOCATION_ALARM_KEY  = "LOCATION_ALARM_ID_";

    /**
     * Used to set action of the intent which is passed to {@link LocationService}.
     * Having this action set in the intent can help the service to identify if a new alarm is added, and can update itself accordingly.
     * Currently not used in the app, hence //TODO
     */
    public static final String ACTION_NEW_LOCATION_ALARM = "ACTION_NEW_LOCATION_ALARM";
    public static final String ACTION_CANCEL_LOCATION_ALARM = "ACTION_NEW_LOCATION_ALARM";
    public static final String ACTION_START_LOCATION_SERVICE = "ACTION_START_LOCATION_SERVICE";


    /**
     * Getting device location gives null location for unknown reason. But restarting the app solves it.
     * Looks like a device and/or network related issue. Therefore the getDeviceLocation() method retries to get device location, up to 3 times
     */
    public static final int MAX_RETRY_COUNT = 3;

    /**
     * Default range when a range is not provided for a proximity alarm.
     */
    public static final int DEFAULT_RANGE = 100;

    /**
     * Default accuracy of an exact location alarm.
     * Due to unpredictable signal strength, this value is set to 100m by default.
     * But the user can change it in settings. //TODO
     */
    public static int DEFAULT_RANGE_EXACT = 100;
}
