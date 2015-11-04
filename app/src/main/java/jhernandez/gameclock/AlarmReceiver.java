package jhernandez.gameclock;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Jason on 9/5/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        //Send Toast to show alarm was received --TESTING ONLY
//        Toast.makeText(context, "It worked!", Toast.LENGTH_SHORT).show();


        //Deactivate alarm repeating
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context,AlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context, 0, i, 0);
        am.cancel(p);
        p.cancel();



        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.explosion)
                .setContentTitle("Alarm went off!")
                .setContentText("Time: "+ System.currentTimeMillis());


        Notification n = builder.build();
        nm.notify(0, n);
    }

    public static int getNearestDay (Calendar alarm, boolean [] week) {
        Calendar currentTime = Calendar.getInstance();
        int day = alarm.get(Calendar.DAY_OF_WEEK);
        if (currentTime.after(alarm))
            day++;
        for (int i = 0; i < 7; i++) {
            if (week[day - 1]) {
                if(day == alarm.get(Calendar.DAY_OF_WEEK) && i + 1 == 7)
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
}