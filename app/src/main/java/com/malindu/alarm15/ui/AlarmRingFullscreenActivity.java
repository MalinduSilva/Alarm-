package com.malindu.alarm15.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.malindu.alarm15.R;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.utils.AlarmService;
import com.malindu.alarm15.utils.Constants;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AlarmRingFullscreenActivity extends AppCompatActivity {
    private static final String TAG = "AlarmRingFullscreenActivity";
    private ImageButton dismissButton, dismissButtonShade;
    private Button snoozeButton;
    private TextView alarmLabel, alarmHour, alarmMinute, alarmAmpm, alarmDayOfWeek, alarmDate, alarmMonth;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm_ring_fullscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fullscreenmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // For displays with camera notch
            Window window = getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(layoutParams);
        }

        dismissButton = findViewById(R.id.dismissButton);
        snoozeButton = findViewById(R.id.snoozeButton);
        alarmLabel = findViewById(R.id.alarmLabel);
        alarmHour = findViewById(R.id.alarmHour);
        alarmMinute = findViewById(R.id.alarmMinute);
        alarmAmpm = findViewById(R.id.alarmAmpm);
        alarmDayOfWeek = findViewById(R.id.alarmDayOfWeek);
        alarmDate = findViewById(R.id.alarmDate);
        alarmMonth = findViewById(R.id.alarmMonth);
        dismissButtonShade = findViewById(R.id.dismissButtonShade);

        Context context = getApplicationContext();
        Intent intent = getIntent();
        Alarm alarm = Alarm.getAlarmObj(intent.getStringExtra("AlarmStr"));
        setData(alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();
        //animateDismissButtonShade();

        // Intent for dismiss action
        Intent dismissIntent = new Intent(context, AlarmService.class);
        dismissIntent.putExtra("AlarmStr", alarm.getStringObj());
        dismissIntent.setAction(Constants.ACTION_DISMISS);
//        PendingIntent dismissPendingIntent = PendingIntent.getService(context, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for dismiss action
        Intent snoozeIntent = new Intent(context, AlarmService.class);
        snoozeIntent.putExtra("AlarmStr", alarm.getStringObj());
        snoozeIntent.setAction(Constants.ACTION_SNOOZE);
//        PendingIntent snoozePendingIntent = PendingIntent.getService(context, 2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startAlarmService(dismissIntent);
                finish();
            }
        });
        if (alarm.isSnooze()) { snoozeButton.setVisibility(View.VISIBLE); } else { snoozeButton.setVisibility(View.INVISIBLE); }
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarmService(snoozeIntent);
                finish();
            }
        });

        ViewTreeObserver observer = dismissButtonShade.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            /**
             * Listen to the layout completion event.
             */
            @Override
            public void onGlobalLayout() {
                dismissButtonShade.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                animateDismissButtonShade();
            }
        });
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
//                return super.onFling(e1, e2, velocityX, velocityY);
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
                        || Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    startAlarmService(dismissIntent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        dismissButton.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void animateDismissButtonShade() {
        final int startWidth = dismissButtonShade.getWidth();
        final int endWidth = startWidth * 2;
        final int startHeight = dismissButtonShade.getHeight();
        final int endHeight = startHeight * 2;

        ValueAnimator widthAnimator = ValueAnimator.ofInt(startWidth, endWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = dismissButtonShade.getLayoutParams();
                layoutParams.width = (int) animation.getAnimatedValue();
                dismissButtonShade.setLayoutParams(layoutParams);
            }
        });

        ValueAnimator heightAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        heightAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = dismissButtonShade.getLayoutParams();
            layoutParams.height = (int) animation.getAnimatedValue();
            dismissButtonShade.setLayoutParams(layoutParams);
        });

        widthAnimator.setDuration(1000);
        widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimator.setRepeatMode(ValueAnimator.REVERSE);
        widthAnimator.setRepeatCount(ValueAnimator.INFINITE);

        heightAnimator.setDuration(1000);
        heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        heightAnimator.setRepeatMode(ValueAnimator.REVERSE);
        heightAnimator.setRepeatCount(ValueAnimator.INFINITE);

        widthAnimator.start();
        heightAnimator.start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_POWER){
            Intent intent = new Intent(this, AlarmService.class);
            intent.setAction(Constants.ACTION_SILENCE_ALARM);
            startAlarmService(intent);
            Log.d(TAG, "onKeyDown: " + keyCode + ":" + event.toString());
        }
        return true;
    }

    private void startAlarmService(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void hideSystemUI() {
        Log.d(TAG, "hideSystemUI: ");
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        WindowInsetsController insetsController = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            insetsController = decorView.getWindowInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets.Builder insetsBuilder = new WindowInsets.Builder(insets);
                insetsBuilder.setInsets(WindowInsets.Type.systemBars(), insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()));
                return insetsBuilder.build();
            });
        }
    }

    private void setData(Alarm alarm) {
        Calendar alarmTime = alarm.getAlarmTime();
//        String dayOfWeek = "";
//        switch (alarmTime.get(Calendar.DAY_OF_WEEK)) {
//            case Calendar.SUNDAY: dayOfWeek = getString(R.string.label_sunday_short); break;
//            case Calendar.MONDAY: dayOfWeek = getString(R.string.label_monday_short); break;
//            case Calendar.TUESDAY: dayOfWeek = getString(R.string.label_tuesday_short); break;
//            case Calendar.WEDNESDAY: dayOfWeek = getString(R.string.label_wednesday_short); break;
//            case Calendar.THURSDAY: dayOfWeek = getString(R.string.label_thursday_short); break;
//            case Calendar.FRIDAY: dayOfWeek = getString(R.string.label_friday_short); break;
//            case Calendar.SATURDAY: dayOfWeek = getString(R.string.label_saturday_short); break;
//        }
        String label = alarm.getAlarmLabel(); if (label == null || label.isEmpty()) { label = getString(R.string.label_alarm); }
        String hour = String.valueOf(alarmTime.get(Calendar.HOUR));
        String minute = String.valueOf(alarmTime.get(Calendar.MINUTE));
        String ampm = alarmTime.get(Calendar.AM_PM) == Calendar.AM ? getString(R.string.am) : getString(R.string.pm);
        String date = String.valueOf(alarmTime.get(Calendar.DATE));
        String[] months = new DateFormatSymbols().getMonths();
        String[] daysOfWeek = new DateFormatSymbols().getShortWeekdays(); //getWeekdays()
        String dayOfWeek = daysOfWeek[alarmTime.get(Calendar.DAY_OF_WEEK)];
        String month = months[alarmTime.get(Calendar.MONTH)];

        alarmLabel.setText(label);
        alarmHour.setText(hour);
        alarmMinute.setText(minute);
        alarmAmpm.setText(ampm);
        alarmDayOfWeek.setText(dayOfWeek);
        alarmDate.setText(date);
        alarmMonth.setText(month);
    }
}