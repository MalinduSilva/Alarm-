package com.malindu.alarm15.models;

import android.net.Uri;

public class AlarmSoundItem {
    private String title;
    private Uri uri;

    public AlarmSoundItem(String title, Uri uri) {
        this.title = title;
        this.uri = uri;
    }

    public String getTitle() { return title; }
    public Uri getUri() { return uri; }
}
