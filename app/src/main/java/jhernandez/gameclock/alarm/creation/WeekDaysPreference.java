package jhernandez.gameclock.alarm.creation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jason on 1/20/2016.
 */
public class WeekDaysPreference extends MultiSelectListPreference {
    public WeekDaysPreference(Context context) {
        super(context);
    }

    public WeekDaysPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeekDaysPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeekDaysPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void setValues(Set<String> values) {
        if (values == null) {
            return;
        }
        final Set<String> newValues = new HashSet<>();
        newValues.addAll( values );
        super.setValues( newValues );
    }
}
