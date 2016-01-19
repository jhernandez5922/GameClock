package jhernandez.gameclock.sqlite;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jason on 11/6/2015.
 */
public class AlarmContract {

    public static final String CONTENT_AUTHORITY = "jhernandez.gameclock.sqlite.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ALARM = "alarms";


    public static final class AlarmEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARM).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARM;

        public static final String TABLE_NAME = "alarms";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_TIME = "time";

        public static final String COLUMN_ACTIVE = "active";

        public static final String COLUMN_SUN = "sunday";
        public static final String COLUMN_MON = "monday";
        public static final String COLUMN_TUE = "tuesday";
        public static final String COLUMN_WED = "wednesday";
        public static final String COLUMN_THR = "thursday";
        public static final String COLUMN_FRI = "friday";
        public static final String COLUMN_SAT = "saturday";

        public static Uri buildAlarmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
