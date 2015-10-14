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
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class AlarmSettings extends PreferenceActivity {


    private static final String TAG = AlarmSettings.class.getSimpleName();
    private static Intent intent;
    private static int idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent();
        idx = getIntent().getIntExtra("requestCode", 0);

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
        intent.putExtra("days"+idx, days);

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
                intent.putExtra("active"+idx, checked);

                //Format the time from the timePicker and
                String am_pm;
                TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
                int hour = timePicker.getCurrentHour(); //get set time
                int minute = timePicker.getCurrentMinute();
                if (true) { //TODO ADD 12 HOUR FORMAT
                    if (hour >= 12) { //if PM
                        hour = hour == 12 ? 12 : hour - 12;
                        am_pm = " PM";
                    } else { //if AM
                        hour = hour == 0 ? 12 : hour;
                        am_pm = " AM";
                    }
                }
                else {} //TODO ADD 24 HOUR FORMAT

                //Format for string to be sent to alarm manager
                String time = String.valueOf(hour) + ":"
                        + String.format("%02d", minute)
                        + am_pm;
                //Add to Extras to be sent back
                intent.putExtra("time"+idx, time);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putInt("lastHour", timePicker.getCurrentHour()).putInt("lastMinute", timePicker.getCurrentMinute()).apply();
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
                Toast.makeText(preference.getContext(), "days" + idx, Toast.LENGTH_LONG).show();
                intent.putExtra("days"+idx, days);
                //Toast.makeText(preference.getContext(), (String) selections.toArray()[0], Toast.LENGTH_LONG).show();

//            } else if (preference instanceof TimePreference) {
//                //For TimePreference, changes to 12 Hour or 24 Hour format
//                // than puts time in the preference summary
//                if (stringValue.length() > 1) {
//                    Log.d(TAG, "INDEX VALUE: " +String.valueOf(idx));
//                    intent.putExtra("time" + idx, stringValue);
//                    preference.setSummary(stringValue);
//                }
            }else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
                if(preference.getKey().equals("name"))
                    intent.putExtra("name"+idx, stringValue);
//                Toast.makeText(preference.getContext(), stringValue, Toast.LENGTH_LONG).show();
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

    private static String getDays(String value) {
        List<String> selections = new ArrayList<>(Arrays.asList(value.replaceAll("[^A-Za-z]", " ").split(" ")));
        selections.removeAll(Arrays.asList(""));
        StringBuilder days = new StringBuilder();
        if(selections.contains("Sun"))
            days.append("Sun ");
        if(selections.contains("Mon"))
            days.append("Mon ");
        if (selections.contains("Tue"))
            days.append("Tues ");
        if(selections.contains("Wed"))
            days.append("Wed ");
        if (selections.contains("Thu"))
            days.append("Thur ");
        if(selections.contains("Fri"))
            days.append("Fri ");
        if (selections.contains("Sat"))
            days.append("Sat ");
        if(days.toString().equals(""))
            days.append("Non-Repeating");
        return days.toString();
    }

}
