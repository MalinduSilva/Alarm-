package com.malindu.alarm15.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.malindu.alarm15.R;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.utils.Constants;

public class StopwatchFragment extends Fragment {

    public StopwatchFragment() {
        // Required empty public constructor
    }
    public static StopwatchFragment newInstance() { return new StopwatchFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        Button button = view.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Test", Toast.LENGTH_SHORT).show();
                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                Intent fullscreenIntent = new Intent(getContext(), AlarmRingFullscreenActivity.class);
                fullscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent fullscreenPendingIntent = PendingIntent.getActivity(getContext(), 0, fullscreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            "Test_notification",
                            "Test Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setSound(null, null);
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), Constants.CHANNEL_ID_ALARM)
                        .setSmallIcon(R.drawable.icon_alarm_outlined)
                        .setContentTitle("Test title")
                        .setContentText("Test content")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        //.setCategory(NotificationCompat.CATEGORY_ALARM)
                        //.setFullScreenIntent(fullscreenPendingIntent, true)
                        .setAutoCancel(true)
                        .setSound(null);

                notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
            }
        });
        return view;
    }
}