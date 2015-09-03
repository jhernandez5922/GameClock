package jhernandez.gameclock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

        final Button createAlarm = (Button) createAlarmFragment.findViewById(R.id.create_alarm_button);
        createAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((EditText) getActivity().findViewById(R.id.edit_alarm_name))
                                        .getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", text);
                getActivity().setResult(getActivity().RESULT_OK, returnIntent);
                getActivity().finish();
            }
        });
        return createAlarmFragment;
    }

}
