package jhernandez.gameclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
    private int lastHour=0; //persisting hour picked
    private int lastMinute=0; //persisting minute picked
    private TimePicker timePicker = null;

    //Retrieve hour from time format (i.e 4:30)
    public static int getHour(String time) {

        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }
    //Retrieve minute from time format
    public static int getMinute(String time) {
        String[] pieces=time.split(":");
        pieces[1] = pieces[1].substring(0, 2);
        return(Integer.parseInt(pieces[1]));
    }

    //Constructor
    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        timePicker =new TimePicker(getContext());
        timePicker.setCurrentHour(lastHour);
        timePicker.setCurrentMinute(lastMinute);
        return(timePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        if(lastHour >= 0 && lastMinute >= 0) {
            timePicker.setCurrentHour(lastHour);
            timePicker.setCurrentMinute(lastMinute);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            String am_pm = "";
            if(true) { //TODO ADD 12 HOUR FORMAT
                lastHour = timePicker.getCurrentHour();
                lastMinute = timePicker.getCurrentMinute();
                if (lastHour >= 12) { //if PM
                    lastHour = lastHour == 12 ? 12 : lastHour - 12;
                    am_pm = " PM";
                } else { //if AM
                    lastHour = lastHour == 0 ? 12 : lastHour;
                    am_pm = " AM";
                }
            }
            String time=String.valueOf(lastHour)+":"+String.format("%02d", lastMinute)+am_pm;

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00 AM");
            }
            else {
                time=getPersistedString(defaultValue.toString());
                time = time.substring(0, time.length()-3);
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }
}