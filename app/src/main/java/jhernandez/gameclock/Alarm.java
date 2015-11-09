package jhernandez.gameclock;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import jhernandez.gameclock.sqlite.AlarmContract.AlarmEntry;

/**
 * Created by Jason on 11/6/2015.
 */
public class Alarm implements Parcelable {

    private String name;
    private long time;
    private boolean week [];
    private int ID;
    public static final String weekName [] = {
            AlarmEntry.COLUMN_SUN,
            AlarmEntry.COLUMN_MON,
            AlarmEntry.COLUMN_TUE,
            AlarmEntry.COLUMN_WED,
            AlarmEntry.COLUMN_THR,
            AlarmEntry.COLUMN_FRI,
            AlarmEntry.COLUMN_SAT
    };
    ContentValues contentValues;

    //Constructors
    public Alarm() {
        name = "";
        time = 0;
        week = new boolean[7];
        contentValues = new ContentValues();
        ID = -1;
        putToCV();
    }
    public Alarm(String name, long time, boolean [] week, int ID) {
        this.name = name;
        this.time = time;
        if (!this.setEntireWeek(week));
            this.week = new boolean[7];
        contentValues = new ContentValues();
        this.ID = ID;
        putToCV();
    }

    protected Alarm(Parcel in) {
        name = in.readString();
        time = in.readLong();
        week = in.createBooleanArray();
        contentValues = ContentValues.CREATOR.createFromParcel(in);
    }

    //Getters
    public String getAlarmName() { return name; }
    public long getAlarmTime() {return time;}
    public boolean [] getWeek() {return week;}
    public int getID() {return ID;}

    //Validators
    public boolean isActive() {return true;}
    public boolean invalidValues() {
        if (this.name.equals(""))
            return true;
        if (this.time <= 0)
            return true;
        if (week.length != 7)
            return true;
        else
            return false;
    }
    public boolean readyToInsert() {
        if (contentValues == null)
            return false;
        if (!contentValues.containsKey(AlarmEntry.COLUMN_NAME))
            return false;
        if (!contentValues.containsKey(AlarmEntry.COLUMN_TIME))
            return false;
        for (int i = 0; i < 7; i++) {
            if (!contentValues.containsKey(weekName[i]))
                return false;
        }
        return true;
    }

    //Setters
    public void setAlarmName(String name) {this.name = name; putNameToCV();}
    public void setAlarmTime(long time) {this.time = time; putTimeToCV();}
    public void setWeekDay (int day, boolean active) {this.week[day] = active; putDayToCV(day, this.week[day] ? 1 : 0);}
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

    //ContentValue putters
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
            putDayToCV(i, week[i] ? 1 : 0);
        }
    }
    private void putDayToCV(int i, int value) {
        contentValues.put(weekName[i], value);
    }


    //Parcelable Functions
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeLong(time);
        dest.writeBooleanArray(week);
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
