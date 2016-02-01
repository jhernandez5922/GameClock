package jhernandez.gameclock.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * This class holds the methods to receive alarm triggers and even to set or deactivate alarms.
 *
 *
 *
 * Created by Jason on 9/5/2015.
 * Last updated: 1/16/15 by Jason
 *
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    /**
     * This function tells the device what to do now that an alarm has been triggered
     * As of 11/8/15, the function sends a notification to the device with the time.
     * @param context: Context of the application
     * @param intent: Intent that send the request
     *
     *
     */
    @Override
    public void onReceive(Context context, Intent intent) {

//        //Send Toast to show alarm was received --TESTING ONLY
//        Toast.makeText(context, "It worked!", Toast.LENGTH_SHORT).show();


        //Deactivate alarm repeating

        //WakeLocker.acquire(context);
        Log.d(this.getClass().getSimpleName(), "ALARM RECEIVED");
        Alarm alarm = intent.getParcelableExtra("alarm");
        if (alarm == null) {
            return;
        }
        Intent alarmTriggered = new Intent(context, GameBridge.class);
        alarmTriggered.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmTriggered.putExtra("alarm", alarm);
        context.startActivity(alarmTriggered);


    }

    /**
     * This function returns the nearest valid day for the alarm to trigger next
     * @param hour: hour in which the alarm is currently set to
     * @param minute: minute in which the alarm is currently set to
     * @param week: list of each day of the week, and whether or not they are active
     * @return returns the valid day, or -1 if anything goes wrong
     */
    public static Calendar getNearestDate(int hour, int minute, boolean[] week) {
        Calendar currentTime = Calendar.getInstance(), setTime = Calendar.getInstance();
        setTime.set(Calendar.HOUR_OF_DAY, hour);
        setTime.set(Calendar.MINUTE, minute);
        int day = setTime.get(Calendar.DAY_OF_WEEK);
        int increase = 0;
        if (currentTime.after(setTime)) {
            day++;
            increase++;
        }
        for (; increase < 7; increase++) {
            if (day == 8) {
                day = 1;
            }
            if (week[day - 1]) {
                break;
            }
            day++;
        }
        if (increase == 7 && !week[day - 1]) {
            increase = currentTime.after(setTime) ? 1 : 0;
            setTime.set(Calendar.DAY_OF_WEEK, setTime.get(Calendar.DAY_OF_WEEK) + increase);
            return setTime;
        }
        increase += setTime.get(Calendar.DAY_OF_YEAR);
        setTime.set(Calendar.DAY_OF_WEEK, day);
        if (!setTime.after(currentTime)){
            GregorianCalendar cal = new GregorianCalendar();
            if (increase >= 365) {
                if (cal.isLeapYear(setTime.get(Calendar.YEAR)) && increase <= 366)
                    setTime.set(Calendar.DAY_OF_YEAR, 366 - increase);
                else
                    setTime.set(Calendar.DAY_OF_YEAR, 365 - increase);
            } else
                setTime.set(Calendar.DAY_OF_YEAR, increase);
        }

        return setTime;
    }

    /**
     *  This function sends the request to the AlarmManager to deactivate the given alarm.
     * @param context: To allow the intent to be within the correct context
     * @param alarm: The alarm to be deactivated
     */
    public static void deactivateAlarm(Context context, Alarm alarm) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context, alarm.getID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(p);
        p.cancel();
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/y h:mm a");
        Log.v("GameClock Alarm Setting", "alarm disabled: " + sdf.format(alarm.getAlarmTime()));
        alarm.setActive(false);
        Toast.makeText(context, alarm.getAlarmName() + " disabled for: " + sdf.format(alarm.getAlarmTime()), Toast.LENGTH_SHORT).show();

    }


    /**
     * This function sends the request to the AlarmManager to activate the given alarm
     * @param context: To allow the intent to be within the correct context
     * @param alarm: The alarm to be set
     *
     */
    public static void setAlarm (Context context, Alarm alarm) {

        if (alarm.isValid()) {

            //Get next time and update alarm
            Calendar alarmSetTime = Calendar.getInstance();
            alarmSetTime.setTimeInMillis(alarm.getAlarmTime());
            alarmSetTime = AlarmReceiver.getNearestDate(alarmSetTime.get(Calendar.HOUR_OF_DAY), alarmSetTime.get(Calendar.MINUTE), alarm.getWeek());
            if (alarmSetTime == null)
                return;
            alarm.setAlarmTime(alarmSetTime.getTimeInMillis());


            //Set up alarm manager
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent =  new Intent(context, AlarmReceiver.class);
            intent.putExtra("alarm", alarm);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm.getID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Set alarm
            int currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT)
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmSetTime.getTimeInMillis(), alarmIntent);
            else
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmSetTime.getTimeInMillis(), alarmIntent);

            //Notify set
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/y h:mm a", Locale.US);
            Log.v("GameClock Alarm Setting", alarm.getAlarmName().toUpperCase() + " SET FOR: " + sdf.format(alarmSetTime.getTime()));
            Toast.makeText(context, alarm.getAlarmName() + " set for: " + sdf.format(alarm.getAlarmTime()), Toast.LENGTH_SHORT).show();


            //Tell receiver to wake up device when alarm triggers
            ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}