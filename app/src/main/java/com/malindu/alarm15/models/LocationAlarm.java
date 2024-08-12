package com.malindu.alarm15.models;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Calendar;

public class LocationAlarm implements Serializable {
    public static final String ALERT_TYPE_ALARM = "ALERT_TYPE_ALARM";
    public static final String ALERT_TYPE_NOTIFICATION = "ALERT_TYPE_NOTIFICATION";
    private Location location;
    private int range;
    private boolean exact, proximity;
    private LatLng latLng;
    private boolean turnedOn = false;
    /**
     * Title of the location. i.e: The name of location.
     */
    private String title = "";
    private String address = "";
    private String note_title = "";
    private String note = "";
    private Calendar dateCreated;
    private String locationAlarmID;
    private String alertType;

    // Getters
    public String getLocationAlarmID() { return locationAlarmID; }
    public boolean isTurnedOn() { return turnedOn; }
    public Location getLocation() { return location; }
    public int getRange() { return range; }
    public boolean isExact() { return exact; }
    public boolean isProximity() { return proximity; }
    public LatLng getLatLng() { return latLng; }
    public String getTitle() { return title; }
    public String getAddress() { return address; }
    public String getNote_title() { return note_title; }
    public String getNote() { return note; }
    public Calendar getDateCreated() { return dateCreated; }
    public String getAlertType() { return alertType; }

    // Setters
    public void setLocationAlarmID(String locationAlarmID) { this.locationAlarmID = locationAlarmID; }
    public void setTurnedOn(boolean turnedOn) { this.turnedOn = turnedOn; }
    public void setLocation(Location location) { this.location = location; }
    public void setRange(int range) { this.range = range; }
    public void setExact(boolean exact) { this.exact = exact; }
    public void setProximity(boolean proximity) { this.proximity = proximity; }
    public void setLatLng(LatLng latLng) { this.latLng = latLng; }
    public void setTitle(String title) { this.title = title; }
    public void setAddress(String address) { this.address = address; }
    public void setNote_title(String note_title) { this.note_title = note_title; }
    public void setNote(String note) { this.note = note; }
    public void setDateCreated(long dateCreated) { this.dateCreated.setTimeInMillis(dateCreated); }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    // Constructor
    public LocationAlarm() {
        range = 1000;
        alertType = ALERT_TYPE_ALARM;
        exact = false;
        proximity = true;
    }

    @NonNull
    @Override
    public String toString() {
        return /* 0 */ locationAlarmID + "|" +
                /* 1 */ turnedOn + "|" +
                /* 2 */ latLng.latitude + ":" + latLng.longitude + "|" +
                /* 3 */ exact + "|" +
                /* 4 */ range + "|" +
                /* 5 */ title + "|" +
                /* 6 */ address + "|" +
                /* 7 */ note_title + "|" +
                /* 8 */ note + "|" +
                /* 9 */ alertType + "|" +  "end"; // "end" is added to prevent the empty note getting ignored by split() method
                /* 10 */ //dateCreated.getTimeInMillis();
    }
}
