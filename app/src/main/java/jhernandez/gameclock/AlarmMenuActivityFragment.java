package jhernandez.gameclock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlarmMenuActivityFragment extends Fragment {

    public AlarmMenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Below are the main menu buttons to go further into the app

            tutorial -> Leads the user into the tutorial section which shows them the basics of how
                        the app works
            saves    -> Leads the user into the saved villages page where the user can select a village
                        or start a new one
            settings -> Allows the user to adjust the settings of the app and customize it

            quit     ->
        */
        View savesFragmentView = inflater.inflate(R.layout.fragment_saves, container, false);
        final Button alarm1 = (Button) savesFragmentView.findViewById(R.id.alarm1);
        alarm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CreateAlarmActivity.class), 1);
            }
        });

        final Button alarm2 = (Button) savesFragmentView.findViewById(R.id.alarm2);
        alarm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CreateAlarmActivity.class), 2);
            }
        });
        final Button alarm3 = (Button) savesFragmentView.findViewById(R.id.alarm3);
        alarm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CreateAlarmActivity.class), 3);
            }
        });
        final Button alarm4 = (Button) savesFragmentView.findViewById(R.id.alarm4);
        alarm4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CreateAlarmActivity.class), 4);
            }
        });
        final Button alarm5 = (Button) savesFragmentView.findViewById(R.id.alarm5);
        alarm5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CreateAlarmActivity.class), 5);
            }
        });


        return savesFragmentView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            String name = data.getStringExtra("result");
            int id;
            switch (requestCode) {
                case 1:
                    id = R.id.alarm1;
                    break;
                case 2:
                    id = R.id.alarm2;
                    break;
                case 3:
                    id = R.id.alarm3;
                    break;
                case 4:
                    id = R.id.alarm4;
                    break;
                case 5:
                    id = R.id.alarm5;
                    break;
                default:
                    return;
            }
            ((Button) getActivity().findViewById(id))
                    .setText(name);
            Toast.makeText(getContext(), "Alarm " + name + "Created", Toast.LENGTH_SHORT).show();
        }
    }
}
