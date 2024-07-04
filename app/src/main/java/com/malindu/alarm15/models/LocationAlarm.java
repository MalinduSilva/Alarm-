package com.malindu.alarm15.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LocationAlarm {
    private Location location;
    private int range;
    private boolean exact, proximity;
    private LatLng latLng;

    public LocationAlarm() {
        range = 5;
        exact = false;
        proximity = true;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean isExact() {
        return exact;
    }

    public void setExact(boolean exact) {
        this.exact = exact;
    }

    public boolean isProximity() {
        return proximity;
    }

    public void setProximity(boolean proximity) {
        this.proximity = proximity;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void set() {
    }
}
