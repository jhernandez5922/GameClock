package jhernandez.gameclock.alarm.creation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jhernandez.gameclock.R;

/**
 * Created by Jason on 1/31/2016.
 */
public class WeekPicker extends LinearLayout implements View.OnClickListener {

    private final static String TAG = WeekPicker.class.getSimpleName();

    LayoutInflater mInflater;
    View rootView;
    boolean [] week;
    TextView [] weekViews;
    ArrayList<Integer> selectedItemsIndex;
    public final static String [] WEEK_NAMES = {
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
    };
    public final static int [] WEEK_ID = {
            R.id.sunday_button,
            R.id.monday_button,
            R.id.tuesday_button,
            R.id.wednesday_button,
            R.id.thursday_button,
            R.id.friday_button,
            R.id.saturday_button
    };

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
        selectedItemsIndex = new ArrayList<>();
        if (mInflater == null) {
            return;
        }
        weekViews = new TextView[7];
        for (int i = 0; i < 7; i++) {
            selectedItemsIndex.add(i);
            weekViews[i] = (TextView) rootView.findViewById(WEEK_ID[i]);
        }
        translateWeek();
        rootView.setOnClickListener(this);
    }

    public void setWeek(boolean[] week) {
        if (week.length < 7)
            return;
        this.week = week;
        for (int day = 0; day < 7; day++) {
            if (week[day])
                selectedItemsIndex.add(day);
            else if (selectedItemsIndex.contains(day))
                selectedItemsIndex.remove(day);
        }
        //translateWeek();
    }

    public boolean[] getWeek() {
        return week;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Days to Repeat")
                .setMultiChoiceItems(WEEK_NAMES, week,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedItemsIndex.add(which);
                                } else if (selectedItemsIndex.contains(which)) {
                                    selectedItemsIndex.remove((Integer) which);
                                }
                            }
                        })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        translateWeek();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void translateWeek() {
        for (int i = 0; i < week.length; i++) {
            week[i] = selectedItemsIndex.contains(i);
            if (week[i])
                weekViews[i].setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
            else
                weekViews[i].setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        }
    }
}
