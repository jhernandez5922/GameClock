package jhernandez.gameclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

public class TimePreference extends Preference implements TimePicker.OnTimeChangedListener {


    private static final String TAG = AlarmSettings.class.getSimpleName();

    private int lastHour=0; //persisting hour picked
    private int lastMinute=0; //persisting minute picked
   // private int idx;
    private TimePicker timePicker = null;

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater li = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.timepicker_layout, parent, false);
        //TODO Remove dependency on R.id.timePicker, possible add in as input argument
        timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lastHour = prefs.getInt("lastHour", 12);
        lastMinute = prefs.getInt("lastMinute", 0);
        timePicker.setCurrentHour(lastHour);
        timePicker.setCurrentMinute(lastMinute);
        return v;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        lastHour = hourOfDay;
        lastMinute = minute;
    }
}