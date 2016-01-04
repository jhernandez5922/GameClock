package jhernandez.gameclock;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Jason on 9/5/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * This function tells the device what to do now that an alarm has been triggered
     * As of 11/8/15, the function sends a notification to the device with the time.
     * @param context: Context of the application
     * @param intent: Intent that send the request
     * @author Jason Hernandez
     */
    @Override
    public void onReceive(Context context, Intent intent) {

//        //Send Toast to show alarm was received --TESTING ONLY
//        Toast.makeText(context, "It worked!", Toast.LENGTH_SHORT).show();


        //Deactivate alarm repeating
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context,AlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context, 0, i, 0);
        am.cancel(p);
        p.cancel();



        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Alarm alarm = intent.getParcelableExtra("alarm");
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

    public static void setAlarm (Context context, Alarm alarm) {
        /*
            This function sends the request to the AlarmManager to activate
            the alarm at the given time.
         */
        if (alarm.isActive()) {
            /*
                Formats the time send from settings screen to be read by
                alarm manager
             */

            if (alarm.invalidValues())
                return;
            if (alarm.getAlarmTime() == 0)
                return;
            Calendar alarmSetTime = Calendar.getInstance();
            alarmSetTime.setTimeInMillis(alarm.getAlarmTime());
            int day = AlarmReceiver.getNearestDay(alarmSetTime, alarm.getWeek());
            alarm.advanceDays(day - alarmSetTime.get(Calendar.DAY_OF_WEEK));
            alarmSetTime.set(Calendar.DAY_OF_WEEK, day);
            //Set up alarm manager
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("alarm", alarm);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm.getID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int currentApiVersion = Build.VERSION.SDK_INT;
            SimpleDateFormat sdf = new SimpleDateFormat("M:d:h:mm a");
            Log.v("GameClock Alarm Setting", "ALARM SET FOR: " + sdf.format(alarmSetTime.getTime()));
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT)
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmSetTime.getTimeInMillis(), alarmIntent);
            else
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmSetTime.getTimeInMillis(), alarmIntent);
            //Tell receiver to wake up device when alarm triggers
            ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}