package jhernandez.gameclock.alarm.creation;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import jhernandez.gameclock.R;

/**
 * Created by Jason on 1/31/2016.
 */
public class WeekPicker extends LinearLayout implements View.OnClickListener {


    LayoutInflater mInflater;
    View rootView;
    boolean [] week;
    TextView [] weekViews;
    public final static int [] WEEK_VIEWS_NAMES = {
            R.id.sunday_button,
            R.id.monday_button,
            R.id.tuesday_button,
            R.id.wednesday_button,
            R.id.thursday_button,
            R.id.friday_button,
            R.id.saturday_button
    };
    private final static String TAG = WeekPicker.class.getSimpleName();

    public WeekPicker(Context context) {
        super(context);
        init(context);
    }

    public WeekPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeekPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setWeekOnClickListener(View.OnClickListener listener) {
        for (int i = 0; i < 7; i++) {
            View v = rootView.findViewById(WEEK_VIEWS_NAMES[i]);
            v.setOnClickListener(listener);
        }
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = mInflater.inflate(R.layout.week_picker, this, true);
        week = new boolean[7];
        Arrays.fill(week, true);
        if (mInflater == null) {
            return;
        }
        weekViews = new TextView[7];
        setWeekOnClickListener(this);
    }

    public void setWeek(boolean[] week) {
        if (week.length < 7)
            return;
        this.week = week;
        for (int i = 0; i < 7; i++) {
            TextView temp = (TextView) findViewById(WEEK_VIEWS_NAMES[i]);
            setDayButton(temp, this.week[i]);
        }
    }

    public boolean[] getWeek() {
        return week;
    }

    @Override
    public void onClick(View v) {
        TextView tv = (TextView) v;
        int index = getDay(tv.getText().toString());
        if (index == -1) {
            Log.e(TAG, "INVALID DAY");
            return;
        }
        week[index] = !week[index];
        setDayButton(tv, week[index]);
    }




    private void setDayButton(TextView day, boolean on) {
        day.setBackgroundResource(on ?
                R.drawable.week_gradient :
                R.drawable.week_gradient_inverse);
        day.setTextColor(on ?
                ContextCompat.getColor(day.getContext(), R.color.black) :
                ContextCompat.getColor(day.getContext(), R.color.accent));
    }
    public static int getDay(String value) {
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
}
