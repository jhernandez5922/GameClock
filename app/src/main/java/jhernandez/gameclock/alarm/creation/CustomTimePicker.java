package jhernandez.gameclock.alarm.creation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jhernandez.gameclock.R;

public class CustomTimePicker extends RelativeLayout implements View.OnClickListener{

    LayoutInflater mInflater;

    private static final String TAG = CustomTimePicker.class.getSimpleName();
    View view;
    NumberRangePicker hourSelect, minuteSelect;
    TextView amPm;
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
        //  TODO - ADD 24 HOUR OPTION
        // if (24 HOUR FORMAT)
        //      amPm.setVisibility(View.GONE);
        minuteSelect.setMode(0, 59, 0, 2);
        amPm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        am = !am;
        amPm.setText(am ? "AM" : "PM");
    }

    public int getHour() {
        return hourSelect.getTime() + (isAm() ? 0 : 12);
    }

    public int getMinute() {
        return minuteSelect.getTime();
    }

    public void setHour(int hour) {
        hourSelect.setCurrentTime(hour);
    }
    public void setMinute(int minute) {
        minuteSelect.setCurrentTime(minute);
    }

    private boolean isAm() {
        return am;
    }
}