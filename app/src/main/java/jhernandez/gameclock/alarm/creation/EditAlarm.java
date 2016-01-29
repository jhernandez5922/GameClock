package jhernandez.gameclock.alarm.creation;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import jhernandez.gameclock.R;
import jhernandez.gameclock.alarm.Alarm;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class EditAlarm extends AppCompatActivity {

    Alarm alarm;

    private final static String FRAG_TAG = "Alarm Preference";
    private final static int [] WEEK_VIEWS = {
            R.id.sunday_button,
            R.id.monday_button,
            R.id.tuesday_button,
            R.id.wednesday_button,
            R.id.thursday_button,
            R.id.friday_button,
            R.id.saturday_button
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundle = new Bundle();
        Alarm alarm = getIntent().getParcelableExtra("alarm");
        bundle.putParcelable("alarm", alarm);
        AlarmEditFragment settingsFrag = new AlarmEditFragment();
        settingsFrag.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_frame,
                settingsFrag, FRAG_TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_alarm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        if (id == R.id.save_alarm) {
            //boolean checked = ((CheckBoxPreference) findPreference("active")).isChecked();
            //Add in whether the or not the alarm is active
            //TODO Replace with delete and move active to above screen
            //intent.putExtra("active"+idx, checked);
            //Format the time from the timePicker and
            Intent intent = new Intent();
            alarm = ((AlarmEditFragment) getFragmentManager().findFragmentByTag(FRAG_TAG)).finalizeAlarm();
            //Add to Intent
            intent.putExtra("alarm", alarm);
            //Let menu activity know results from intent are okay
            setResult(RESULT_OK, intent);
            //Finish activity and go back
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class AlarmEditFragment extends Fragment implements TextView.OnClickListener{

        Alarm alarm;
        boolean [] week;
        EditText name;
        CustomTimePicker timePicker;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            alarm = this.getArguments().getParcelable("alarm");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_create_alarm, container, false);
            name = (EditText) rootView.findViewById(R.id.edit_name);
            timePicker = (CustomTimePicker) rootView.findViewById(R.id.time_picker);
            TextView [] weekViews = new TextView[7];
            for (int i = 0; i < 7; i++) {
                weekViews[i] = (TextView) rootView.findViewById(WEEK_VIEWS[i]);
                weekViews[i].setOnClickListener(this);
            }
            if (alarm != null) {
                //SET TIME
                Calendar currentTime = Calendar.getInstance();
                currentTime.setTimeInMillis(alarm.getAlarmTime());
                timePicker.setHour(currentTime.get(Calendar.HOUR_OF_DAY));
                timePicker.setAmPm(currentTime.get(Calendar.HOUR_OF_DAY) > 12);
                timePicker.setMinute(currentTime.get(Calendar.MINUTE));
                name.setText(alarm.getAlarmName());
                week = alarm.getWeek();
                for (int i = 0; i < week.length; i++) {
                    weekViews[i].setTextColor(week[i] ? ContextCompat.getColor(rootView.getContext(), R.color.color_primary)
                            : ContextCompat.getColor(rootView.getContext(), R.color.color_primary_dark));
                }
            }
            else {
                alarm = new Alarm();
                week = new boolean[7];
                Arrays.fill(week, true);
                alarm.setEntireWeek(week);
            }
            return rootView;
        }

        public Alarm finalizeAlarm() {
            Calendar newTime = Calendar.getInstance();
            newTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            newTime.set(Calendar.MINUTE, timePicker.getMinute());
            newTime.set(Calendar.SECOND, 0);
            alarm.setEntireWeek(week);
            alarm.setAlarmTime(newTime.getTimeInMillis());
            if (name.getText().toString().equals("")) {
                alarm.setAlarmName("Alarm");
            }
            else
                alarm.setAlarmName(name.getText().toString());
            alarm.setActive(true);
            return alarm;
        }

        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            int index = getDay(tv.getText().toString());
            if (index == -1) {
                Log.e(FRAG_TAG, "INVALID DAY");
                return;
            }
            alarm.setWeekDay(index, !alarm.getWeekDay(index));
            tv.setTextColor(alarm.getWeekDay(index) ? ContextCompat.getColor(v.getContext(), R.color.color_primary)
                    : ContextCompat.getColor(v.getContext(), R.color.color_primary_dark));
        }
    }

    private static boolean [] setWeek(String value) {
        List<String> selections = new ArrayList<>(Arrays.asList(value.replaceAll("[^A-Za-z]", " ").split(" ")));
        selections.removeAll(Arrays.asList(""));
        boolean [] week = new boolean[7];
        if(selections.contains("Su"))
            week[0] = true;
        if(selections.contains("Mo"))
            week[1] = true;
        if (selections.contains("Tu"))
            week[2] = true;
        if(selections.contains("We"))
            week[3] = true;
        if (selections.contains("Th"))
            week[4] = true;
        if(selections.contains("Fr"))
            week[5] = true;
        if (selections.contains("Sa"))
            week[6] = true;
        return week;
    }

    private static int getDay(String value) {
        switch(value) {
            case "Su":
                return 0;
            case "Mo":
                return 1;
            case "Tu":
                return 2;
            case "We":
                return 3;
            case "Th":
                return 4;
            case "Fr":
                return 5;
            case "Sa":
                return 6;
            default:
                return -1;
        }
    }

    private static String dayOfWeek (int i) {
        switch(i) {
            case 0:
                return "Sun ";
            case 1:
                return "Mon ";
            case 2:
                return "Tue ";
            case 3:
                return "Wed ";
            case 4:
                return "Thu ";
            case 5:
                return "Fri ";
            case 6:
                return "Sat ";
            default:
                return "";
        }
    }
    private static String getDays(String value) {
        boolean[] week = setWeek(value);
        return getStringFromWeek(week);
    }


    private static String getStringFromWeek(boolean [] week) {
        StringBuilder days = new StringBuilder();
        LinkedList<Integer> next = new LinkedList<>();
        int hours = 24;
        for (int i = 0; i < 7; i++) {
            if (!week[i]){
                hours+=24;
                continue;
            }
            days.append(dayOfWeek(i));
            next.add(hours);
            hours = 24;
        }
        return days.toString();
    }
}
