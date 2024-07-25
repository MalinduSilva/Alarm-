package com.malindu.alarm15.models;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class Alarm implements Serializable, Comparable<Alarm> {
    private String alarmID = "";
    private String alarmLabel = "";
    private boolean isTurnedOn = false;
    private Calendar alarmTime = Calendar.getInstance();
    private boolean[] weekdays = new boolean[7];
    private boolean sound = true;
    private boolean vibration = true;
    private boolean snooze = false;
    private boolean set_for_date = false; private boolean set_for_weekdays = false;
    private boolean set_for_today = false; private boolean set_for_tomorrow = true;

    public Alarm() {
        for (int i = 0; i <7; i++) {
            weekdays[i] = false;
        }
        Calendar c = Calendar.getInstance();
        alarmTime.set(Calendar.YEAR, c.get(Calendar.YEAR));
        alarmTime.set(Calendar.MONTH, c.get(Calendar.MONTH));
        alarmTime.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
        alarmTime.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
        alarmTime.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);
    }

    /**This method is used to get an alarm as string, for saving in shared preferences
     * @return
     */
    public String getStringObj() {
        return "Alarm{" + alarmID + "|" + alarmLabel + "|" + isTurnedOn + "|" + alarmTime.getTimeInMillis() + "|" + Arrays.toString(weekdays) + "|" +
                sound + "|" + vibration + "|" + snooze + "|" + set_for_date + "|" + set_for_weekdays + "|" + set_for_today + "|" + set_for_tomorrow + "}";
    }
    public static Alarm getAlarmObj(String str) {
        Alarm alarm = new Alarm();
        String alarmStr = str.substring(6, str.length() - 1);
        String[] alarmSplit = alarmStr.split("\\|");
        alarm.setAlarmID(alarmSplit[0]);
        alarm.setAlarmLabel(alarmSplit[1]);
        alarm.setTurnedOn(Boolean.parseBoolean(alarmSplit[2]));
        alarm.alarmTime.setTimeInMillis(Long.parseLong(alarmSplit[3]));
        String[] weekdays = alarmSplit[4].substring(1, alarmSplit[4].length()-1).split(",");
        //Log.d("Alarm", "getAlarmObj: " + Arrays.toString(weekdays));
        for (int i = 0; i < weekdays.length; i++) {
            alarm.setWeekdays(i, Boolean.parseBoolean(weekdays[i].trim()));
            //String s = " true";
            //Log.d("Alarm", "getAlarmObj: " + i + ":" + weekdays[i].trim() + "::::" + Boolean.parseBoolean(weekdays[i].trim()));
        }
        alarm.setSound(Boolean.parseBoolean(alarmSplit[5]));
        alarm.setVibration(Boolean.parseBoolean(alarmSplit[6]));
        alarm.setSnooze(Boolean.parseBoolean(alarmSplit[7]));
        alarm.setSet_for_date(Boolean.parseBoolean(alarmSplit[8]));
        alarm.setSet_for_weekdays(Boolean.parseBoolean(alarmSplit[9]));
        Log.d("TAG", "getAlarmObj: "+ alarm);
        alarm.setSet_for_today(Boolean.parseBoolean(alarmSplit[10]));
        alarm.setSet_for_tomorrow(Boolean.parseBoolean(alarmSplit[11]));
        Log.d("Alarm", "getAlarmObj: parse successful - " + alarm.getStringObj() + "---" + str);
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
            hour = alarm.getAlarmTime().get(Calendar.AM_PM) == Calendar.PM && hour == 0 ?  12 : hour;
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
    public String getAlarmDateAsText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM");
        String date = "test";
        Calendar calendar = Calendar.getInstance();
        if (isSet_for_today()) {
            date = "Today, ";// + calendar.get(Calendar.DAY_OF_WEEK) + " " + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.MONTH);
            date += dateFormat.format(getAlarmTime().getTime());
        } else if (isSet_for_tomorrow()) {
            date = "Tomorrow, ";// + calendar.get(Calendar.DAY_OF_WEEK + 1) + " " + calendar.get(Calendar.DATE + 1) + " " + calendar.get(Calendar.MONTH);
            date += dateFormat.format(getAlarmTime().getTime());
        } else if (isSet_for_date()) {
            date = dateFormat.format(getAlarmTime().getTime());
            //date = alarmTime.get(Calendar.DAY_OF_WEEK) + ", " + alarmTime.get(Calendar.DATE) + " " + alarmTime.get(Calendar.MONTH);
        } else if (isSet_for_weekdays()) {
//            date = "Every ";
//            date += weekdays[0] ? "Mon," : "";
//            date += weekdays[1] ? "Tue," : "";
//            date += weekdays[2] ? "Wed," : "";
//            date += weekdays[3] ? "Thu," : "";
//            date += weekdays[4] ? "Fri," : "";
//            date += weekdays[5] ? "Sat," : "";
//            date += weekdays[6] ? "Sun" : "";

//            String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
//            StringBuilder result = new StringBuilder("Every ");
//
//            boolean first = true;
//            for (int i = 0; i < weekdays.length; i++) {
//                if (weekdays[i]) {
//                    if (!first) {
//                        result.append(", ");
//                    }
//                    result.append(dayNames[i]);
//                    first = false;
//                }
//            }
//            date = result.toString();

            String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            StringBuilder result = new StringBuilder();

            boolean first = true;
            boolean anyDaySelected = false;
            for (int i = 0; i < weekdays.length; i++) {
                if (weekdays[i]) {
                    if (!first) {
                        result.append(", ");
                    } else {
                        result.append("Every ");
                        first = false;
                    }
                    result.append(dayNames[i]);
                    anyDaySelected = true;
                }
            }

            if (!anyDaySelected) {
                result.setLength(0); // Clear the StringBuilder
                //result.append("No days selected");
                Calendar c = Calendar.getInstance();
                if (alarmTime.after(c)) {
                    setSet_for_today(true);
                } else {
                    setSet_for_tomorrow(true);
                }
                return getAlarmDateAsText();
            }
            date = result.toString();
        }
        return  date;
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

    public boolean getWeekdays(int weekday) {
        return weekdays[weekday];
    }

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

    public boolean isSet_for_date() {
        return set_for_date;
    }

    public void setSet_for_date(boolean set_for_date) {
        this.set_for_date = set_for_date;
    }

    public boolean isSet_for_weekdays() {
        return set_for_weekdays;
    }

    public void setSet_for_weekdays(boolean set_for_weekdays) {
        this.set_for_weekdays = set_for_weekdays;
    }

    public boolean isSet_for_today() {
        return set_for_today;
    }

    public void setSet_for_today(boolean set_for_today) {
        this.set_for_today = set_for_today;
    }

    public boolean isSet_for_tomorrow() {
        return set_for_tomorrow;
    }

    public void setSet_for_tomorrow(boolean set_for_tomorrow) {
        this.set_for_tomorrow = set_for_tomorrow;
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
                ", set_for_date=" + set_for_date +
                ", set_for_weekdays=" + set_for_weekdays +
                '}';
    }

    @Override
    public int compareTo(Alarm other) {
        return Long.compare(this.alarmTime.getTimeInMillis(), other.alarmTime.getTimeInMillis());
    }
}
