package jhernandez.gameclock.alarm.creation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import jhernandez.gameclock.R;

/**
 * Created by Jason on 1/31/2016.
 */
public class WeekPicker extends LinearLayout implements View.OnClickListener {


    LayoutInflater mInflater;
    View rootView;
    boolean [] week;
    TextView [] weekViews;
    private final static int [] WEEK_VIEWS_NAMES = {
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

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = mInflater.inflate(R.layout.week_picker, this, true);
        week = new boolean[7];
        if (mInflater == null) {
            return;
        }
        weekViews = new TextView[7];
        for (int i = 0; i < 7; i ++) {
            weekViews[i] = (TextView) rootView.findViewById(WEEK_VIEWS_NAMES[i]);
            weekViews[i].setOnClickListener(this);
        }
    }

    public void setWeek(boolean[] week) {
        this.week = week;
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
        tv.setBackgroundResource(week[index] ?
                R.drawable.week_gradient :
                R.drawable.week_gradient_inverse);
    }

    private static int getDay(String value) {
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
