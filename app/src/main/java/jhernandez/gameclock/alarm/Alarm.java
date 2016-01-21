package jhernandez.gameclock.alarm;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import jhernandez.gameclock.sqlite.AlarmContract.AlarmEntry;
import jhernandez.gameclock.sqlite.AlarmDbHelper;

/**
 *  This class maintains the necessary information for an alarm
 *
 *  The class implements Parcelable to be packaged into intents and sent
 *  between activities
 *  See android.os.Parcelable for more
 *
 *
 *
 * Created by Jason on 11/6/2015.
 */
public class Alarm implements Parcelable {

    /** CLASS MEMBERS
     *  String name -> The name of the alarm, to help identify for users
     *  long time -> The next time the alarm will be triggered
     *  boolean week [] -> An array to indicate whether or not a weekday will trigger an alarm
     *  int ID -> the unique ID given to an alarm when created in the database
     *  boolean active -> To indicate if the alarm will trigger at the next specified time
     *  ContentValues contentValues -> Formats all the previous class members to allow them to
     *                                  be entered into the database.
     *                                  See android.content.ContentValues for more
     *  String weekName[] -> A list of strings to quickly access the column indices of the database
     *                      to quickly map them. See jhernandez.gameclock.sqlite.AlarmContract for more
     */
    private static final String TAG = "GameClock Alarm Test";
    private String name;
    private long time;
    private boolean week [];
    private int ID;
    private boolean active;
    ContentValues contentValues;
    public static final String weekName [] = {
            AlarmEntry.COLUMN_SUN,
            AlarmEntry.COLUMN_MON,
            AlarmEntry.COLUMN_TUE,
            AlarmEntry.COLUMN_WED,
            AlarmEntry.COLUMN_THR,
            AlarmEntry.COLUMN_FRI,
            AlarmEntry.COLUMN_SAT
    };
/** CONSTRUCTORS **/
    //Default constructor
    public Alarm() {
        this.name = "";
        this.time = 0;
        this.week = new boolean[7];
        this.contentValues = new ContentValues();
        this.ID = -1;
        this.active = true;
        putToCV();
    }

    //Takes inputs and maps to Alarm class
    public Alarm(String name, long time, boolean [] week, int ID, boolean active) {
        this.name = name;
        this.time = time;
        if (!this.setEntireWeek(week));
            this.week = new boolean[7];
        this.contentValues = new ContentValues();
        this.ID = ID;
        this.active = active;
        putToCV();
    }

    //Puts Parcelable values into an Alarm
    protected Alarm(Parcel in) {
        this.name = in.readString();
        this.time = in.readLong();
        this.week = in.createBooleanArray();
        this.active = in.readInt() == 1;
        this.contentValues = ContentValues.CREATOR.createFromParcel(in);
    }

    //Puts Database values into an Alarm
    public Alarm(Cursor cursor) {
        this.name = AlarmDbHelper.getAlarmName(cursor);
        this.time = AlarmDbHelper.getAlarmTime(cursor);
        this.week = AlarmDbHelper.getAlarmWeek(cursor);
        this.ID = AlarmDbHelper.getAlarmID(cursor);
        this.active = AlarmDbHelper.getAlarmActive(cursor);
        this.contentValues = new ContentValues();
        putToCV();
    }

/** GETTERS **/
    public String getAlarmName() { return name; }
    public long getAlarmTime() {return time;}
    public boolean [] getWeek() {return week;}
    public boolean getWeekDay(int day) {return week[day];}
    public int getID() {return ID;}
    public boolean isActive() {return active;}

/** VALIDATORS **/
    public boolean isValid() {
        if (this.name.equals("")) {
            Log.e(TAG, "ALARM NAME INVALID");
            return false;
        }
        if (this.time < 0) {
            Log.e(TAG, "ALARM TIME INVALID");
            return false;
        }
        if (week.length != 7) {
            Log.e(TAG, "ALARM WEEK SETUP INVALID");
            return false;
        }
        else if (this.ID < 0) {
            Log.e(TAG, "ALARM ID INVALID");
            return false;
        }
        else {
            if (!active) {
                Log.v(TAG, "ALARM NOT ACTIVE");
            }
            return active;
        }
    }
    public boolean readyToInsert() {
        if (contentValues == null)
            return false;
        if (!contentValues.containsKey(AlarmEntry.COLUMN_NAME))
            return false;
        if (!contentValues.containsKey(AlarmEntry.COLUMN_TIME))
            return false;
        if (!contentValues.containsKey(AlarmEntry.COLUMN_ACTIVE))
            return false;
        for (int i = 0; i < 7; i++) {
            if (!contentValues.containsKey(weekName[i]))
                return false;
        }
        return true;
    }

/** SETTERS **/
    public void setActive(boolean active) {
        this.active = active;
        putActiveToCV();
    }
    public void setAlarmName(String name) {this.name = name; putNameToCV();}
    public void setAlarmTime(long time) {this.time = time; putTimeToCV();}
    public void setWeekDay (int day, boolean isActiveDay) {this.week[day] = isActiveDay; putDayToCV(day, this.week[day] ? 1 : 0);}
    public void setID(String id) {
        this.ID = Integer.parseInt(id);
    }
    public boolean setEntireWeek(boolean [] week) {
        if (week != null && week.length == 7) {
            this.week = week;
            putWeekToCV();
            return true;
        }
        return false;
    }

/** MISC **/
    //Given N days, update the alarm trigger time forward N days.
    public void advanceDays(long days) {
        days = TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
        this.time += days;
    }


/** CONTENT VALUE UPDATERS **/
    private void putToCV() {
        putNameToCV();
        putTimeToCV();
        putWeekToCV();
        putActiveToCV();
    }
    private void putNameToCV() {
        contentValues.put(AlarmEntry.COLUMN_NAME, name);
    }
    private void putTimeToCV() {
        contentValues.put(AlarmEntry.COLUMN_TIME, time);
    }
    private void putActiveToCV() {
        contentValues.put(AlarmEntry.COLUMN_ACTIVE, active);
    }
    private void putWeekToCV() {
        for (int i  = 0; i < 7; i++) {
            putDayToCV(i, week[i] ? 1 : 0);
        }
    }
    private void putDayToCV(int i, int value) {
        contentValues.put(weekName[i], value);
    }


/** PARCELABLE METHODS **/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeLong(time);
        dest.writeBooleanArray(week);
        dest.writeInt(active ? 1 : 0);
        contentValues.writeToParcel(dest, 0);
    }
    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

}
