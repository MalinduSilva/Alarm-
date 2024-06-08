package com.malindu.alarm15.utils;

import android.Manifest;

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


    /**
     * Prefix for the id for an alarm
     */
    public static final String ALARM_KEY  = "ALARM_";


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
}
