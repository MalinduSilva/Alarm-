package com.malindu.alarm15.models;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<String> getSearchCityList(String str) {
        List<WorldClockItem> tz_list = getAllTimeZones();
        List<String> city_list = new ArrayList<>();
        if (str.isEmpty()) {
            for (int i = 0; i < tz_list.size(); i++) {
                city_list.add(tz_list.get(i).city);
            }
        } else {
            for (int i = 0; i < tz_list.size(); i++) {
                String city = tz_list.get(i).city;
                if (city.contains(str)) {
                    city_list.add(city);
                }
            }
        }
        //return city_list.toArray(new String[0]);
        return city_list;
    }

    public static WorldClockItem getWCItemFromCity(String city) {
        List<WorldClockItem> tz_list = getAllTimeZones();
        int count = 0;
        WorldClockItem worldClockItem = new WorldClockItem("", "");
        for (int i = 0; i < tz_list.size(); i++) {
            if (tz_list.get(i).city.equals(city)) {
                worldClockItem.city = tz_list.get(i).city;
                worldClockItem.timeZoneID = tz_list.get(i).timeZoneID;
                count++;
            }
        }
        Log.d(TAG, "getWCItemFromCity: count = " + count);
        return worldClockItem;
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
