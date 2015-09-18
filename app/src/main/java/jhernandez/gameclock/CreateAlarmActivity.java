package jhernandez.gameclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateAlarmActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);
//        final Button createAlarm = (Button) findViewById(R.id.create_alarm_button);
//        createAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Grab text from EditText field for alarm name
//                String text = ((EditText) findViewById(R.id.edit_alarm_name))
//                        .getText().toString();
//
//
//                //If alarm name is empty, add generic name
//                if (text.replace(" ", "").equals(""))
//                    text = "Alarm 1";
//                TimePicker time = (TimePicker) findViewById(R.id.timePicker);
//                String s = SetAlarm(CreateAlarmActivity.this, time); //Tell the system to set an alarm at given time
//
//                //Return time and name to Alarm Menu screen
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", text + " at " + s);
//                setResult(RESULT_OK, returnIntent);
//                finish();
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String SetAlarm (Context context, TimePicker time){
        /*
            This function sends the request to the AlarmManager to activate
            the alarm at the given time.
         */


        //Parse the time the user gave
        int apiVersion = android.os.Build.VERSION.SDK_INT, hour, min;

        if (apiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            //New API levels
            hour = time.getHour();
            min = time.getMinute();
        } else { //For older API levels
            hour = time.getCurrentHour();
            min = time.getCurrentMinute();
        }


        alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar alarm = Calendar.getInstance();
        alarm.setTimeInMillis(System.currentTimeMillis());

        //set time alarm will use
        alarm.set(Calendar.HOUR_OF_DAY, hour);
        alarm.set(Calendar.MINUTE, min);
        alarm.set(Calendar.SECOND, 0);

        //Set alarm for alarm manager
        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), alarmIntent);

        //Tell receiver to wake up device when alarm triggers
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        //Return formatted time for UI
        return String.valueOf(hour) + ":" + String.valueOf(min);
    }

}

