package jhernandez.gameclock.alarm.creation;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;

import jhernandez.gameclock.R;
import jhernandez.gameclock.alarm.Alarm;


/**
 *
 */
public class EditAlarm extends AppCompatActivity implements View.OnClickListener {

    Alarm alarm;

    private final static String FRAG_TAG = "Alarm Edit/Create";

    private final static int TONE_PICKER = 867;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageButton save = (ImageButton) findViewById(R.id.save_alarm);
        save.setOnClickListener(this);
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
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_edit_alarm, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_alarm:
                Intent intent = new Intent();
                alarm = ((AlarmEditFragment) getFragmentManager().findFragmentByTag(FRAG_TAG)).finalizeAlarm();
                //Add to Intent
                intent.putExtra("alarm", alarm);
                //Let menu activity know results from intent are okay
                setResult(RESULT_OK, intent);
                break;
        }
        finish();
    }

    /**
     * This class is an extenstion of the Fragment class and is used to display the contents in
     * which the user will create or edit an alarm. This screen is shared between the two, only
     * the starting data is different.
     *
     * Members:
     *  Alarm alarm -> The current alarm with the settings the user defined
     *  boolean [] week -> An array for each day of the week, to determine if this day is active or not
     *  EditText name -> The EditText box used to define the name of the alarm
     *  CustomTimePicker timePicker -> used to determine the time in which the alarm will trigger
     *      see jhernandez.gameclock.alarm.creation.CustomTimePicker for more
     *  Context appContext -> the Context of the activity, used for the RingtoneManager
     *  TextView currentRingtone -> The text to represent the user's currently choosen Ringtone
     */
    public static class AlarmEditFragment extends Fragment implements View.OnClickListener{

        Alarm alarm;
        EditText name;
        CustomTimePicker timePicker;
        Context appContext;
        TextView currentRingtone;
        WeekPicker week;

        /**
         * This is the first function called to create the fragment, it grabs the alarm (if present) and
         * sets the member variable appContext to the activity's context
         * @param savedInstanceState: The state of this instance if previously saved
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            alarm = this.getArguments().getParcelable("alarm");
            appContext = getActivity();
        }

        /**
         * This function inflates the screen the user will use to set the parameters of the alarm
         * If this screen was prompted via the FAB to create:
         *  -this will define a new alarm, with default settings
         * If this screen was prompted via the Edit tab:
         *  -this will use the current state of the alarm to populate the data
         *
         * @param inflater: used to bring the view to the screen
         * @param container: used to define the ViewGroup in which the inflated view will be contained within
         * @param savedInstanceState: The state in which the instance was at currently if opened previously
         * @return the View that will be brought to the front of the screen
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_create_alarm, container, false);
            name = (EditText) rootView.findViewById(R.id.edit_name);
            timePicker = (CustomTimePicker) rootView.findViewById(R.id.time_picker);
            TextView ringtoneTitle = (TextView) rootView.findViewById(R.id.ringtone_button);
            currentRingtone = (TextView) rootView.findViewById(R.id.current_ringtone);
            //Set both title and description onClickListeners
            ringtoneTitle.setOnClickListener(this);
            currentRingtone.setOnClickListener(this);
            week = (WeekPicker) rootView.findViewById(R.id.week_picker);
            //Set to Current Settings or Default, depending on Editing or Creating an alarm, respectively
            if (alarm != null) {
                Calendar currentTime = Calendar.getInstance();
                currentTime.setTimeInMillis(alarm.getAlarmTime());
                timePicker.setHour(currentTime.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(currentTime.get(Calendar.MINUTE));
                name.setText(alarm.getAlarmName());
                week.setWeek(alarm.getWeek());
            }
            else {
                alarm = new Alarm();
                boolean [] weekTemp = new boolean[7];
                Arrays.fill(weekTemp, true);
                alarm.setEntireWeek(weekTemp);
                week.setWeek(weekTemp);
            }
            return rootView;
        }

        /**
         * This function gathers all of the information the users has selected and packages
         * it into an Alarm object, see jhernandez.gameclock.alarm.Alarm for more
         * @return The most update to alarm based on the settings chosen
         */
        public Alarm finalizeAlarm() {
            //Set Time
            Calendar newTime = Calendar.getInstance();
            newTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            newTime.set(Calendar.MINUTE, timePicker.getMinute());
            newTime.set(Calendar.SECOND, 0);
            alarm.setAlarmTime(newTime.getTimeInMillis());
            //Set Week
            alarm.setEntireWeek(week.getWeek());
            //Set Name
            if (name.getText().toString().equals("")) {
                alarm.setAlarmName("Alarm");
            }
            else
                alarm.setAlarmName(name.getText().toString());
            //Set Active
            alarm.setActive(true);
            return alarm;
        }

        /**
         * This function provides a method for the OnClickListener implementation for this object
         * It supports clicking for
         *  R.id.ringtone_button & R.id.current_ringtone
         *  These two are current used to open the dialog window to pick a ringtone for the alarm
         *
         * @param view: The view that was clicked, the switch statement grabs it's ID and decides which
         *            code to execute
         */
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ringtone_button:
                case R.id.current_ringtone:
                    final Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(appContext, RingtoneManager.TYPE_ALARM);
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                    startActivityForResult(intent, EditAlarm.TONE_PICKER);
            }
        }

        /**
         * This function gets the result from the ringtone dialog and adds them to the alarm.
         *
         * @param requestCode: The original function that called the request, used in order to execute
         *                   the correct code
         * @param resultCode: The status of the closing of the dialog, checks to make sure it is okay
         *                  to execute code needed
         * @param data: The intent that was sent back to this one, it can contain extras used to perform
         *            actions
         */
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == TONE_PICKER) {
                    //Toast.makeText(getActivity(), "CORRECT", Toast.LENGTH_SHORT).show();
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    Ringtone current = RingtoneManager.getRingtone(appContext, uri);
                    String ringtoneName = current.getTitle(appContext);
                    currentRingtone.setText(ringtoneName);
                }
            }
        }
    }
}
