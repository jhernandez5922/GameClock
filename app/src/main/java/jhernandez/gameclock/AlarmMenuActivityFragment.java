package jhernandez.gameclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Arrays;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlarmMenuActivityFragment extends Fragment {
    private static final String TAG = "MyActivity";
    private String [] names;
    public AlarmMenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View savesFragmentView = inflater.inflate(R.layout.fragment_alarm_manager, container, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!pref.contains("alarm_names")) {
            names = new String[5];
            Arrays.fill(names, "New Alarm");
        }
        else
            names = pref.getString("alarm_names", "").split(",");
        //Launch new activity when button is pressed
        final Button [] alarms = new Button[5];
        for (int i = 0; i < 5; i++) {
            alarms[i] = (Button) savesFragmentView.findViewById(getID(i+1));
            alarms[i].setText(names[i]);
            alarms[i].setOnClickListener(new onClickListenerWithID(i+1));
        }
        return savesFragmentView;
    }

    private int getID (int id) {
        switch (id) {
            case 1:
                return R.id.alarm1;
            case 2:
                return R.id.alarm2;
            case 3:
                return R.id.alarm3;
            case 4:
                return R.id.alarm4;
            case 5:
                return R.id.alarm5;
            default:
                return 0;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            int id = getID(requestCode);
            //Log.v(TAG, "CURRENT: " + data.getStringExtra("time"+requestCode));

            //Parse name from extras from activity executed
            String name = data.getStringExtra("name"+requestCode) + " at " + data.getStringExtra("time"+requestCode);

            //Set Button Name
            Log.v(TAG, String.valueOf(requestCode));
            ((Button) getActivity().findViewById(id))
                    .setText(name);
            names[requestCode - 1] = name;
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            pref.edit().putString("alarm_names", TextUtils.join(",", names)).apply();
            //Toast.makeText(getContext(), data.getStringExtra("time" + requestCode) + "Created", Toast.LENGTH_SHORT).show();

            //Create Alarm
            SetAlarm(getContext(), data.getExtras(), requestCode);
        }
    }
    public class onClickListenerWithID implements View.OnClickListener {
        private final int id;

        public onClickListenerWithID(int id) {
            this.id = id;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AlarmSettings.class);
            Log.v(TAG, String.valueOf(id));
            intent.putExtra("requestCode", id);
            String [] nameSplit = names[id -1].split(" ");
            String time = "";
            if (nameSplit.length > 3) {
                time = nameSplit[2] + nameSplit[3];
                intent.putExtra("previousTime"+id, time);
            }
            Log.v(TAG, "TEST" + time);
            startActivityForResult(intent, id);
        }
    }

    private void SetAlarm (Context context, Bundle extras, int idx){
        /*
            This function sends the request to the AlarmManager to activate
            the alarm at the given time.
         */
        if(extras.getBoolean("active"+idx)) {
            /*
                Formats the time send from settings screen to be read by
                alarm manager
             */

            String time = "12:00 PM"; //dummy time
            boolean [] week = extras.getBooleanArray("days"+idx);
            if (week == null)
                week = new boolean[7];
            if (extras.containsKey("time" + idx))  //time was set for alarm and
                time = extras.getString("time" + idx); //pull data from bundle

            time = time != null ? time.replace(":", " ") : "12 00 PM"; //if extra didn't exist, use base time formatted
            Log.v(TAG, time);
            //TODO add 12 hour format if statement
            String[] split = time.split(" "); //get hour, minute, AM/PM
            if (split[2].equals("PM")) //offset PM if 12 hour format
                split[0] = String.valueOf(Integer.parseInt(split[0]) + 12);

            //Set time alarm will use
            Calendar alarm = Calendar.getInstance();
            alarm.setTimeInMillis(System.currentTimeMillis());
            alarm.set(Calendar.HOUR_OF_DAY, Integer.valueOf(split[0]));
            alarm.set(Calendar.MINUTE, Integer.valueOf(split[1]));
            alarm.set(Calendar.SECOND, 0);
            AlarmReceiver.getNearestDay(alarm, week);
            //Set up alarm manager
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, idx, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int currentApiVersion = Build.VERSION.SDK_INT;
            Log.v("GameClock Alarm Setting", "ALARM SET FOR: " + alarm.get(Calendar.DAY_OF_WEEK));
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT)
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), alarmIntent);
            else
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), alarmIntent);
        //Tell receiver to wake up device when alarm triggers
            ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
//        else {
//            if (alarmIntent != null)
//                alarmMgr.cancel(alarmIntent);
//            Toast.makeText(context, extras.getString("name") + " at " + extras.getString("time") + " canceled.",Toast.LENGTH_SHORT ).show();
//        }
    }


}
