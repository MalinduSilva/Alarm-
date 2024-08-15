package com.malindu.alarm15.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.malindu.alarm15.R;

import java.util.Calendar;

public class SunPathView extends View {
    private static final String TAG = "SunPathView";
    private Paint paint;
    private RectF oval;
    private DashPathEffect dashPathEffect;
    private View clockUpperLayout;
    private ImageView sun, moon;
    private Slider slider;


    public SunPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        clockUpperLayout = getRootView();
        //sun = clockUpperLayout.findViewById(R.id.image_sun);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.ash_border));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        oval = new RectF();
        dashPathEffect = new DashPathEffect(new float[]{10, 30}, 0);
        paint.setPathEffect(dashPathEffect);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        sun = getRootView().findViewById(R.id.image_sun);
        moon = getRootView().findViewById(R.id.image_moon);
        slider = getRootView().findViewById(R.id.slider);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                float alpha;
                if (value <= 5) {
                    alpha = 255;
                } else if (value <= 10) {
                    alpha = (value * (-51)) + 510;
                } else if (value <= 16) {
                    alpha = 0;
                } else {
                    alpha = (value - 16) * 255 / 7;
                }
                int color = Color.argb((int) alpha, 0,0,0);
                clockUpperLayout.setBackgroundColor(color);
                Log.d(TAG, "value: " + value + ", alpha: " + alpha);
            }
        });

        int width = getWidth();
        int height = getHeight();
//        int radius = Math.min(width, height) / 2;
        int left = sun.getWidth() / 2;
        int right = width - left;
        int radius = (right - left) / 2;
        int top = clockUpperLayout.getTop() + (sun.getHeight());
        int bottom = clockUpperLayout.getBottom();
        //top = bottom - radius - 100;
        //bottom = bottom + radius;
        top = bottom - radius;
        bottom = bottom + radius;
        //Log.d(TAG, "onDraw: width-" + width + ",height-" + height +","+ top+","+ bottom);
        //oval.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);
        oval.set(left, top, right, bottom);

        // Draw a semi-circle (180 degrees arc)
        canvas.drawArc(oval, 180, 180, false, paint);
        //canvas.drawRect(left, top, right, bottom, paint); //guides

        //------------------------
        // Calculate center and semi-axes
        double centerX = (left + right) / 2.0;
        double centerY = (top + bottom) / 2.0;
        double a = (right - left) / 2.0; // semi-major axis
        double b = (bottom - top) / 2.0; // semi-minor axis
        // Angle in degrees (e.g., 270 for the middle of the bottom arc)
        Calendar currentTime = Calendar.getInstance();
        double timeFraction = currentTime.get(Calendar.HOUR_OF_DAY) + currentTime.get(Calendar.MINUTE) / 60.0;
        double sunAngle = ((timeFraction - 6) * 15) + 180;
        double moonAngle = (timeFraction - 6) * 15;
        double angle = Math.toRadians(sunAngle); //190;
        Log.d(TAG, "onDraw: timeFraction-" + timeFraction + ", degrees-" + sunAngle + ", radians-" + angle);
        double angleRadians = Math.toRadians(180);
        // Calculate (x, y) on the ellipse
        double x_sun = centerX + a * Math.cos(Math.toRadians(sunAngle)) - ((double) sun.getWidth() / 2);
        double y_sun = centerY + b * Math.sin(Math.toRadians(sunAngle)) - ((double) sun.getHeight() / 2);
        double x_moon = centerX + a * Math.cos(Math.toRadians(moonAngle)) - ((double) moon.getWidth() / 2);
        double y_moon = centerY + b * Math.sin(Math.toRadians(moonAngle)) - ((double) moon.getHeight() / 2);

        sun.setX((float) x_sun); sun.setY((float) y_sun);
        moon.setX((float) x_moon); moon.setY((float) y_moon);

//        int colorValue = (int) ((timeFraction / 24) * 255);
//        // Ensuring the color is at most 255
//        int alphaValue = (int) ((1 - Math.abs(timeFraction - 0.5) * 2) * 255);
//        int clampedAlphaValue = Math.max(0, Math.min(alphaValue, 255));
//        int clampedColorValue = Math.min(colorValue, 255);
//        int backgroundColor = Color.argb(clampedAlphaValue, 0, 0, 0);
        //clockUpperLayout.setBackgroundColor(backgroundColor);

        double alpha;
        if (timeFraction <= 5) {
            alpha = 255;
        } else if (timeFraction <= 10) {
            alpha = (timeFraction * (-51)) + 510;
        } else if (timeFraction <= 16) {
            alpha = 0;
        } else if (timeFraction <= 19){
            alpha = (timeFraction - 16) * 255 / 3;
        } else {
            alpha = 255;
        }
        int color = Color.argb((int) alpha, 0, 0, 0);
        clockUpperLayout.setBackgroundColor(color);
    }
}
