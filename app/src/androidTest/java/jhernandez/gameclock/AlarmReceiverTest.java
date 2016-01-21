package jhernandez.gameclock;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.Calendar;

import jhernandez.gameclock.alarm.AlarmReceiver;

/**
 * Created by Jason on 1/20/2016.
 */
public class AlarmReceiverTest extends AndroidTestCase {

    public void testNearestDay() {
        boolean weekMon [] = {false, true, false, false, false, false, false}; // MONDAY ONLY
        boolean empty [] = new boolean[7];
        Arrays.fill(empty, false);
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(1453419000000L); // THU JAN 21 15:30
        Calendar time = AlarmReceiver.getNearestDate(startTime, weekMon);
        startTime.setTimeInMillis(1453764600000L);
        assertEquals(time, startTime);
        Calendar nullTime = AlarmReceiver.getNearestDate(startTime, empty);
        assertEquals(nullTime, null);
    }
}
