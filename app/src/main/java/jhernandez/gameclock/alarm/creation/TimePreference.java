package jhernandez.gameclock.alarm.creation;

import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import jhernandez.gameclock.R;

public class TimePreference extends Preference implements TimePicker.OnClickListener {


    private static final String TAG = EditAlarm.class.getSimpleName();

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
        if (timePicker != null) {
            lastHour = timePicker.getCurrentHour();
            lastMinute = timePicker.getCurrentMinute();
        }
        View v = li.inflate(R.layout.timepicker_layout, parent, false);
        //TODO Remove dependency on R.id.timePicker, possible add in as input
        timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        timePicker.setCurrentHour(lastHour);
        timePicker.setCurrentMinute(lastMinute);
        return v;
    }

    @Override
    public void onClick(View v) {
        lastHour = timePicker.getCurrentHour();
        lastMinute = timePicker.getCurrentMinute();
    }

    public TimePicker getTimePicker() {

        return timePicker;
    }

    public int getHour() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            lastHour = timePicker.getCurrentHour();
        } else {
            lastHour = timePicker.getHour();
        }

        return lastHour;
    }

    public void setHour(int hour) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.setCurrentHour(hour);
        } else {
            timePicker.setHour(hour);
        }
    }
    @SuppressWarnings("depcrated")
    public int getMinute() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.getCurrentMinute();
        } else {
            lastMinute = timePicker.getMinute();
        }
        return lastMinute;
    }

    public void setMinute(int minute) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.setCurrentHour(minute);
        } else {
            timePicker.setHour(minute);
        }
    }
}