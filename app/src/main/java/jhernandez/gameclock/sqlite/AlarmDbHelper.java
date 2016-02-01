package jhernandez.gameclock.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jhernandez.gameclock.alarm.Alarm;
import jhernandez.gameclock.sqlite.AlarmContract.AlarmEntry;

/**
 * Created by Jason on 11/6/2015.
 */
public class AlarmDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "alarms.db";

    private static final int DATABASE_VERSION = 2;


    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlarmEntry.COLUMN_NAME + " TEXT NOT NULL," +
                AlarmEntry.COLUMN_TIME + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_ACTIVE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_RINGTONE + " TEXT NOT NULL, " +
                AlarmEntry.COLUMN_SUN + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_MON + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_TUE + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_WED + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_THR + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_FRI + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_SAT + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_ALARM_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
    }


    public static String getAlarmName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_NAME));
    }

    public static long getAlarmTime(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(AlarmEntry.COLUMN_TIME));
    }

    public static boolean [] getAlarmWeek (Cursor cursor) {
        boolean [] week = new boolean [7];
        for (int i = 0; i < week.length; i++) {
            week[i] = cursor.getInt(cursor.getColumnIndex(Alarm.weekName[i])) == 1;
        }
        return week;
    }

    public static int getAlarmID(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(AlarmEntry._ID));
    }

    public static boolean getAlarmActive (Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_ACTIVE)) == 1;
    }

    public static String getAlarmRingtone (Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(AlarmEntry.COLUMN_RINGTONE));
    }


}
