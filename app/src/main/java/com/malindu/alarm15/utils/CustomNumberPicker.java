package com.malindu.alarm15.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import java.lang.reflect.Field;

public class CustomNumberPicker extends NumberPicker {
    public CustomNumberPicker(Context context) {
        super(context);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setNumberPickerTextSize(this, 50);
        setNumberPickerTextColor(this, Color.RED);
    }

    private void setNumberPickerTextSize(NumberPicker numberPicker, int textSize) {
        int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextSize(textSize);
            }
        }
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        try {
            Field[] fields = numberPicker.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("mSelectorWheelPaint")) {
                    field.setAccessible(true);
                    Paint paint = (Paint) field.get(numberPicker);
                    paint.setColor(color);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
