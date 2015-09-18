package jhernandez.gameclock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateAlarmActivityFragment extends Fragment {

    public CreateAlarmActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View createAlarmFragment = inflater.inflate(R.layout.fragment_create_alarm, container, false);

        return createAlarmFragment;
    }





}
