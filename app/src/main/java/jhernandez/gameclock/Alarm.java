package jhernandez.gameclock;

import android.content.ContentValues;

import java.util.Calendar;

import jhernandez.gameclock.sqlite.AlarmContract.AlarmEntry;

/**
 * Created by Jason on 11/6/2015.
 */
public class Alarm {

    private String name;
    private long time;
    private boolean week [];
    private static final String weekName [] = {
            AlarmEntry.COLUMN_SUN,
            AlarmEntry.COLUMN_MON,
            AlarmEntry.COLUMN_TUE,
            AlarmEntry.COLUMN_WED,
            AlarmEntry.COLUMN_THR,
            AlarmEntry.COLUMN_FRI,
            AlarmEntry.COLUMN_SAT
    };
    ContentValues contentValues;

    public Alarm() {
        name = "Alarm";
        time = Calendar.getInstance().getTimeInMillis();
        week = new boolean[7];
        contentValues = new ContentValues();
        putToCV();
    }
    public Alarm(String name, long time, boolean [] week) {
        this.name = name;
        this.time = time;
        if (!this.setEntireWeek(week));
            this.week = new boolean[7];
    }
    public String getAlarmName() { return name; }
    public long getAlarmTime() {return time;}
    public boolean [] getWeek() {return week;}

    public void setAlarmName(String name) {this.name = name; putNameToCV();}
    public void setAlarmTime(long time) {this.time = time; putTimeToCV();}
    public void setWeekDay (int day, boolean active) {this.week[day] = active; putDayToCV(day);}
    public boolean setEntireWeek(boolean [] week) {
        if (week != null && week.length == 7) {
            this.week = week;
            return true;
        }
        return false;
    }

    private void putToCV() {
        putNameToCV();
        putTimeToCV();
        putWeekToCV();

    }

    private void putNameToCV() {
        contentValues.put(AlarmEntry.COLUMN_NAME, name);
    }
    private void putTimeToCV() {
        contentValues.put(AlarmEntry.COLUMN_TIME, time);
    }
    private void putWeekToCV() {
        for (int i  = 0; i < 7; i++) {
            putDayToCV(i);
        }
    }
    private void putDayToCV(int i) {
        contentValues.put(weekName[i], week[i]);
    }
}
