package com.malindu.alarm15.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmReceiverDismiss extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmSound);
//        if (ringtone != null && ringtone.isPlaying()) {
//            ringtone.stop();
//        }
        Intent serviceIntent = new Intent(context, AlarmSoundService.class);
        serviceIntent.setAction(Constants.ACTION_STOP_ALARM);
        context.startService(serviceIntent);
    }
}