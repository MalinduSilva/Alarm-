package com.malindu.alarm15.models;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class WorldClockItem implements Comparable<WorldClockItem> {
    private static final String TAG = "WorldClockItem";
    private String city;
    private String timeZoneID;

    public WorldClockItem(String city, String timeZoneID) {
        this.city = city;
        this.timeZoneID = timeZoneID;
        //Log.d(TAG, "WorldClockItem: " + city +","+ timeZoneID);
    }

    public String getCity() {
        return city;
    }

    public String getTimeZoneID() {
        return timeZoneID;
    }

    public static List<WorldClockItem> getAllTimeZones() {
        List<WorldClockItem> list = new ArrayList<>();
        String[] timeZoneIds = TimeZone.getAvailableIDs();
        for (String timeZoneId : timeZoneIds) {
            String cityName = timeZoneId.substring(timeZoneId.lastIndexOf('/') + 1).replace('_', ' ');
            list.add(new WorldClockItem(cityName, timeZoneId));
        }
        //Log.d(TAG, "getAllTimeZones: " + list.size());
        return list;
    }

    @NonNull
    @Override
    public String toString() {
        return city + "|" + timeZoneID + "|" + "end";
    }

    public static WorldClockItem parseWCItem(String str) {
        String[] split = str.split("\\|");
        return new WorldClockItem(split[0], split[1]);
    }

    @Override
    public int compareTo(WorldClockItem o) {
        if (o != null) {
            return this.city.compareTo(o.city);
        }
        return 0;
    }
}
