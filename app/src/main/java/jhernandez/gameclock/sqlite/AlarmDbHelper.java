package jhernandez.gameclock.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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
}
