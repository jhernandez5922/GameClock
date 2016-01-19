package jhernandez.gameclock.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jhernandez.gameclock.R;

/**
 * This class holds the methods to receive alarm triggers and even to set or deactivate alarms.
 *
 *
 *
 * Created by Jason on 9/5/2015.
 * Last updated: 1/16/15 by Jason
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

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

        Alarm alarm = intent.getParcelableExtra("alarm");
        AlarmReceiver.deactivateAlarm(context, alarm);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        builder.setSmallIcon(R.drawable.explosion)
                .setContentTitle(alarm.getAlarmName() + " went off!")
                .setContentText("Time: " + sdf.format(alarm.getAlarmTime()));
        Notification n = builder.build();
        nm.notify(0, n);

        AlarmReceiver.setAlarm(context, alarm);

    }

    /**
     * This function returns the nearest valid day for the alarm to trigger next
     * @param setTime: time in which the alarm is currently set to
     * @param week: list of each day of the week, and whether or not they are active
     * @return returns the valid day, or -1 if anything goes wrong
     */
    public static int getNearestDay (Calendar setTime, boolean [] week) {
        Calendar currentTime = Calendar.getInstance();
        int day = setTime.get(Calendar.DAY_OF_WEEK);
        if (currentTime.after(setTime))
            day++;
        if (day > Calendar.SATURDAY)
            day = 1;
        for (int i = 0; i < 7; i++) {
            if (week[day - 1]) {
                if(day == setTime.get(Calendar.DAY_OF_WEEK) && i + 1 == 7)
                    return 8;
                return day;
            }
            else {
                if (day == Calendar.SATURDAY) {
                    day = Calendar.SUNDAY;
                }
                else
                    day++;
            }
        }
        return -1;
    }

    /**
     *  This function sends the request to the AlarmManager to deactivate the given alarm.
     * @param context
     * @param alarm
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
            int day = AlarmReceiver.getNearestDay(alarmSetTime, alarm.getWeek());
            if (day == -1) {
                return;
            }
            alarm.advanceDays(day - alarmSetTime.get(Calendar.DAY_OF_WEEK));
            alarmSetTime.set(Calendar.DAY_OF_WEEK, day);
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
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/y h:mm a");
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