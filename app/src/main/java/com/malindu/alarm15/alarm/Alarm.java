package com.malindu.alarm15.alarm;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;

public class Alarm implements Serializable {
    private String alarmID = "";
    private String alarmLabel = "";
    private boolean isTurnedOn = false;
    private Calendar alarmTime = Calendar.getInstance();
    private boolean[] weekdays = new boolean[7];
    private boolean sound = true;
    private boolean vibration = true;
    private boolean snooze = false;

    public Alarm() {
        for (int i = 0; i <7; i++) {
            weekdays[i] = false;
        }
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);
    }

    /**This method is used to get an alarm as string, for saving in shared preferences
     * @return
     */
    public String getStringObj() {
        return "Alarm{" + alarmID + "|" + alarmLabel + "|" + isTurnedOn + "|" + alarmTime.getTimeInMillis() + "|" + Arrays.toString(weekdays) + "|" +
                sound + "|" + vibration + "|" + snooze + "}";
    }
    public static Alarm getAlarmObj(String str) {
        Alarm alarm = new Alarm();
        String alarmStr = str.substring(6, str.length() - 1);
        String[] alarmSplit = alarmStr.split("\\|");
        alarm.setAlarmID(alarmSplit[0]);
        alarm.setAlarmLabel(alarmSplit[1]);
        alarm.setTurnedOn(Boolean.parseBoolean(alarmSplit[2]));
        alarm.alarmTime.setTimeInMillis(Long.parseLong(alarmSplit[3]));
        String[] weekdays = alarmSplit[4].substring(1, alarmSplit[4].length()-1).split(","); for (int i = 0; i < weekdays.length; i++) {alarm.setWeekdays(i, Boolean.parseBoolean(weekdays[i]));}
        alarm.setSound(Boolean.parseBoolean(alarmSplit[5]));
        alarm.setVibration(Boolean.parseBoolean(alarmSplit[6]));
        alarm.setSnooze(Boolean.parseBoolean(alarmSplit[7]));
        Log.d("Alarm", "getAlarmObj: parse successful - " + alarm.getAlarmID());
        return alarm;
    }

    public static String getAlarmTimeAsText(Context context, Alarm alarm) {
        String time = "";
        int hour;
        String am_pm = "";
        if (DateFormat.is24HourFormat(context)) {
            hour = alarm.getAlarmTime().get(Calendar.HOUR_OF_DAY);
        } else {
            hour = alarm.getAlarmTime().get(Calendar.HOUR);
            am_pm = alarm.getAlarmTime().get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM";
        }
        time = String.valueOf(hour);
        if (alarm.getAlarmTime().get(Calendar.MINUTE) < 10) {
            time += ":0" + alarm.getAlarmTime().get(Calendar.MINUTE);
        } else {
            time += ":" + alarm.getAlarmTime().get(Calendar.MINUTE);
        }
        time += am_pm;
        return time;
    }

    public void setDate(int year, int month, int day_of_month) {
        alarmTime.set(Calendar.YEAR, year);
        alarmTime.set(Calendar.MONTH, month);
        alarmTime.set(Calendar.DAY_OF_MONTH, day_of_month);
    }
    public void setTime(int hour_of_day, int minute) {
        alarmTime.set(Calendar.HOUR_OF_DAY, hour_of_day);
        alarmTime.set(Calendar.MINUTE, minute);
    }

    public String getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(String alarmID) {
        this.alarmID = alarmID;
    }

    public String getAlarmLabel() {
        return alarmLabel;
    }

    public void setAlarmLabel(String alarmLabel) {
        this.alarmLabel = alarmLabel;
    }

    public boolean getTurnedOn() {
        return isTurnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        isTurnedOn = turnedOn;
    }

    public Calendar getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Calendar alarmTime) {
        this.alarmTime = alarmTime;
    }

    public boolean getWeekdays(int weekday) { return weekdays[weekday]; }

    public void setWeekdays(int weekday, boolean state) {
        this.weekdays[weekday] = state;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public boolean isSnooze() {
        return snooze;
    }

    public void setSnooze(boolean snooze) {
        this.snooze = snooze;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "alarmID='" + alarmID + '\'' +
                ", alarmLabel='" + alarmLabel + '\'' +
                ", isTurnedOn=" + isTurnedOn +
                ", alarmTime=" + alarmTime.get(Calendar.YEAR)+"."+alarmTime.get(Calendar.MONTH)+"."+alarmTime.get(Calendar.DAY_OF_MONTH)+" "+alarmTime.get(Calendar.HOUR_OF_DAY)+":"+alarmTime.get(Calendar.MINUTE)+"."+alarmTime.get(Calendar.SECOND)+"."+alarmTime.get(Calendar.MILLISECOND)+
                ", weekdays=" + Arrays.toString(weekdays) +
                ", sound=" + sound +
                ", vibration=" + vibration +
                ", snooze=" + snooze +
                '}';
    }
}
