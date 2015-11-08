package jhernandez.gameclock;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */


import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class AlarmSettings extends PreferenceActivity {


    private static final String TAG = AlarmSettings.class.getSimpleName();
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarm = new Alarm();

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
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
    **/
    private void setupSimplePreferencesScreen() {
        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        //dummy variable to grab headers
        PreferenceCategory headers = new PreferenceCategory(this);

        // Add 'general' preferences
        //set Header for general preferences
        headers.setTitle(R.string.pref_header_general);
        //get preferences from XML resource

        addPreferencesFromResource(R.xml.alarm_pref_general);

        //Set preferences to default values, only for the first time this called
        PreferenceManager.setDefaultValues(this, R.xml.alarm_pref_general, false);

        // Add 'notifications' preferences, and a corresponding header.
        //set header for notification preferences
        headers.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(headers);

        //get preferences from XML resources
        addPreferencesFromResource(R.xml.alarm_pref_notification);


        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("name"));
        bindPreferenceSummaryToValue(findPreference("tpKey"));
        findPreference("repeating").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        //get Values from select list
        Set<String> daysOfWeek = ((MultiSelectListPreference) findPreference("repeating")).getValues();

        //format values to set summary
        String days = getDays(daysOfWeek.toString());

        //set summary to preference with days of week
        findPreference("repeating").setSummary(days);

        //add value to activity above
        //intent.putExtra("days"+idx, days);

        //Set view to show Save button
        setContentView(R.layout.alarm_pref_footer);

        // Add onClick listener to SAVE footer button to allow
        // the settings to be saved and sent to the activity above
        // to set the alarm
        findViewById(R.id.save_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get checkbox state
                boolean checked = ((CheckBoxPreference) findPreference("active")).isChecked();

                //Add in whether the or not the alarm is active
                //TODO Replace with delete and move active to above screen
                //intent.putExtra("active"+idx, checked);

                //Format the time from the timePicker and
                Intent intent = new Intent();


                //------------ ADD TIME -----------------------------------------\\
                TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
                Calendar time = Calendar.getInstance();
                //TODO Compensate for API 23 vs API < API 23
                time.set(Calendar.HOUR, timePicker.getCurrentHour());
                time.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                time.set(Calendar.SECOND, 0);
                time.set(Calendar.MILLISECOND, 0);
                alarm.setAlarmTime(time.getTimeInMillis());


                //----------- ADD NAME ----------------------------------------\\
                alarm.setAlarmName(findPreference("name").getSummary().toString());

                //----------- ADD WEEK ----------------------------------------\\
                alarm.setEntireWeek(setWeek(findPreference("repeating").getSummary().toString()));

                //Add to Intent
                intent.putExtra("alarm", alarm);
                //Let menu activity know results from intent are okay
                setResult(RESULT_OK, intent);
                //Finish activity and go back
                finish();
            }
        });
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
    **/
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
            }else {
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
    **/
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
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
                return "Tues ";
            case 3:
                return "Wed ";
            case 4:
                return "Thur ";
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
