package jhernandez.gameclock.alarm.creation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
public class EditAlarm extends AppCompatPreferenceActivity {

    Alarm alarm;

    private final static String FRAG_TAG = "Alarm Preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarm = getIntent().getParcelableExtra("alarm");
        if (alarm == null) {
            alarm = new Alarm();
        }
        setupActionBar();

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new GeneralPreferenceFragment(), FRAG_TAG).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                // Show the Up button in the action bar.
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
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

            alarm = ((GeneralPreferenceFragment) getFragmentManager().findFragmentByTag(FRAG_TAG)).finalizeAlarm();


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


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.equals(GeneralPreferenceFragment.class.getName());
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }


            } else if (preference instanceof MultiSelectListPreference) {
                //For MultiSelectListPreference, parse days checked and set in summary
                String days = getDays(stringValue);
                preference.setSummary(days);
//                }
            } else if (preference instanceof TimePreference) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                TimePreference timePicker = (TimePreference) preference;

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's
        // current value.
        if (preference.getKey().equals("repeating")) {
            Set<String> days = new HashSet<>(7);
            days.add("Sun");
            days.add("Mon");
            days.add("Tue");
            days.add("Wed");
            days.add("Thr");
            days.add("Fri");
            days.add("Sat");
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    days.toString());
        }
        else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.alarm_pref_general);


            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("tpKey"));
            bindPreferenceSummaryToValue(findPreference("repeating"));
            bindPreferenceSummaryToValue(findPreference("name"));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);
            if (v != null) {
                ListView lv = (ListView) v.findViewById(android.R.id.list);
                lv.setPadding(0, 0, 0, 0);
            }
            return v;
        }

        public Alarm finalizeAlarm() {
            Alarm alarm = new Alarm();
            TimePicker timePicker = ((TimePreference) findPreference("tpKey")).getTimePicker();
            Calendar time = Calendar.getInstance();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                time.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                time.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            } else {
                time.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                time.set(Calendar.MINUTE, timePicker.getMinute());
            }
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);
            alarm.setAlarmTime(time.getTimeInMillis());


            //----------- ADD NAME ----------------------------------------\\
            alarm.setAlarmName(findPreference("name").getSummary().toString());

            //----------- ADD WEEK ----------------------------------------\\
            alarm.setEntireWeek(setWeek(findPreference("repeating").getSummary().toString()));

            alarm.setActive(true);

            return alarm;
        }
    }

    private static boolean [] setWeek(String value) {
        List<String> selections = new ArrayList<>(Arrays.asList(value.replaceAll("[^A-Za-z]", " ").split(" ")));
        selections.removeAll(Arrays.asList(""));
        boolean [] week = new boolean[7];
        if(selections.contains("Sun"))
            week[0] = true;
        if(selections.contains("Mon"))
            week[1] = true;
        if (selections.contains("Tue"))
            week[2] = true;
        if(selections.contains("Wed"))
            week[3] = true;
        if (selections.contains("Thu"))
            week[4] = true;
        if(selections.contains("Fri"))
            week[5] = true;
        if (selections.contains("Sat"))
            week[6] = true;
        return week;
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
        StringBuilder days = new StringBuilder();
        boolean [] week = setWeek(value);
        // intent.putExtra("days"+idx, week);
        Queue<Integer> next = new LinkedList<>();
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
        //TODO FIX
        // intent.putExtra("nextDay"+idx,(Serializable) next);
        return days.toString();
    }
}
