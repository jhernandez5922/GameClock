package jhernandez.gameclock.alarm.creation;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import jhernandez.gameclock.R;

public class CustomTimePicker extends RelativeLayout implements View.OnClickListener, RadialTimePickerDialogFragment.OnTimeSetListener{

    LayoutInflater mInflater;

    private static final String TAG = CustomTimePicker.class.getSimpleName();
    View view;
    NumberRangePicker hourSelect, minuteSelect;
    TextView amPm;
    ImageView radialPicker;
    boolean am;

    public CustomTimePicker(Context context) {
        super(context);
        init(context);

    }
    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {
            return;
        }
        view = mInflater.inflate(R.layout.timepicker_layout, this, true);
        hourSelect = (NumberRangePicker) view.findViewById(R.id.hour_picker);
        minuteSelect = (NumberRangePicker) view.findViewById(R.id.minute_picker);
        amPm = (TextView) view.findViewById(R.id.am_pm);
        radialPicker = (ImageView) view.findViewById(R.id.radial_dialog);
        //  TODO - ADD 24 HOUR OPTION
        // if (24 HOUR FORMAT)
        //      amPm.setVisibility(View.GONE);
        minuteSelect.setMode(0, 59, 0, 2);
        amPm.setOnClickListener(this);
        radialPicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.am_pm) {
            am = !am;
            amPm.setText(am ? "AM" : "PM");
        }
        else if (v.getId() == R.id.radial_dialog) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setStartTime(getHour(), getMinute())
                    .setThemeCustom(R.style.RadialTimePicker)
                    .setOnTimeSetListener(this);
            //TODO add 12 vs 24 hour format
//            if (12 HOUR FORMAT) {
//                rtpd.setForced12hFormat();
//            }
            FragmentManager manager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            rtpd.show(manager, "tag");
        }
    }

    public int getHour() {
        int hour = hourSelect.getTime();
        if (hour != 12)
            hour += isAm() ? 0 : 12;
        else
            hour = isAm() ? 24 : 12;
        return hour;
    }

    public int getMinute() {
        return minuteSelect.getTime();
    }

    public void setHour(int hour) {
        boolean am = true;
        if (hour > 12) {
            hour -= 12;
            am = false;
        }
        setAmPm(am);
        hourSelect.setCurrentTime(hour);
    }
    public void setMinute(int minute) {
        minuteSelect.setCurrentTime(minute);
    }

    private boolean isAm() {
        return am;
    }

    public void setAmPm (boolean amPm) {
        this.amPm.setText(amPm ? "AM" : "PM");
        this.am = amPm;
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hour, int minute) {
        setHour(hour);
        setMinute(minute);
    }
}