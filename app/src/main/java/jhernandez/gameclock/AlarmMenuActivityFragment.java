package jhernandez.gameclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlarmMenuActivityFragment extends Fragment {
    private String [] names;
    public AlarmMenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View savesFragmentView = inflater.inflate(R.layout.fragment_saves, container, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!pref.contains("alarm_names")) {
            names = new String[5];
            Arrays.fill(names, "New Alarm");
        }
        else
            names = pref.getString("alarm_names", "").split(",");
        //Launch new activity when button is pressed
        final Button [] alarms = new Button[5];
        alarms[0] = (Button) savesFragmentView.findViewById(R.id.alarm1);
        alarms[0].setText(names[0]);
        alarms[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmSettings.class);
                intent.putExtra("requestCode", 1);
                startActivityForResult(intent, 1);
            }
        });
        alarms[1] = (Button) savesFragmentView.findViewById(R.id.alarm2);
        alarms[1].setText(names[1]);
        alarms[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmSettings.class);
                intent.putExtra("requestCode", 2);
                startActivityForResult(intent, 2);
            }
        });
        alarms[2] = (Button) savesFragmentView.findViewById(R.id.alarm3);
        alarms[2].setText(names[2]);
        alarms[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmSettings.class);
                intent.putExtra("requestCode", 3);
                startActivityForResult(intent, 3);
            }
        });
        alarms[3] = (Button) savesFragmentView.findViewById(R.id.alarm4);
        alarms[3].setText(names[3]);
        alarms[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmSettings.class);
                intent.putExtra("requestCode", 4);
                startActivityForResult(intent, 4);
            }
        });
        alarms[4] = (Button) savesFragmentView.findViewById(R.id.alarm5);
        alarms[4].setText(names[4]);
        alarms[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmSettings.class);
                intent.putExtra("requestCode", 5);
                startActivityForResult(intent, 5);
            }
        });
        return savesFragmentView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            int id;
            String name = data.getStringExtra("name"+requestCode) + " at " + data.getStringExtra("time"+requestCode);

            switch (requestCode) {
                case 1:
                    id = R.id.alarm1;
                    names[0] = name;
                    break;
                case 2:
                    id = R.id.alarm2;
                    names[1] = name;
                    break;
                case 3:
                    id = R.id.alarm3;
                    names[2] = name;
                    break;
                case 4:
                    id = R.id.alarm4;
                    names[3] = name;
                    break;
                case 5:
                    id = R.id.alarm5;
                    names[4] = name;
                    break;
                default:
                    return;
            }

            //Set Button Name
            ((Button) getActivity().findViewById(id))
                    .setText(name);
            names[requestCode - 1] = name;
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            pref.edit().putString("alarm_names", TextUtils.join(",", names)).apply();
            Toast.makeText(getContext(), data.getStringExtra("name" + requestCode) + "Created", Toast.LENGTH_SHORT).show();

            //Create Alarm
            SetAlarm(getContext(), data.getExtras(), requestCode);
        }
    }

    private void SetAlarm (Context context, Bundle extras, int idx){
        /*
            This function sends the request to the AlarmManager to activate
            the alarm at the given time.
         */

        //Parse extras needed
        String time = "12:00 PM";
        if (extras.containsKey("time"+idx))
            time = extras.getString("time"+idx);
        time = time != null ? time.replace(":", " ") : "12 00 PM";
        String [] split = time.split(" ");
        if (split[2].equals("PM"))
            split[0] = String.valueOf(split[0]) + 12;


        //Set up alarm manager
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, idx, intent, 0);

        //Set time alarm will use
        Calendar alarm = Calendar.getInstance();
        alarm.setTimeInMillis(System.currentTimeMillis());
        alarm.set(Calendar.HOUR_OF_DAY, Integer.valueOf(split[0]));
        alarm.set(Calendar.MINUTE, Integer.valueOf(split[1]));
        alarm.set(Calendar.SECOND, 0);

        //Set alarm for alarm manager
        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), alarmIntent);

        //Tell receiver to wake up device when alarm triggers
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }
}
