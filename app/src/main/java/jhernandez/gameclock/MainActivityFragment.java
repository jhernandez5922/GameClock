package jhernandez.gameclock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jhernandez.gameclock.game.TestActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
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
        //Button to Alarms Menu
        View inputFragmentView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        final Button alarms = (Button) inputFragmentView.findViewById(R.id.main_villages);
        alarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AlarmMenuActivity.class));
            }
        });
        final Button tutorial = (Button) inputFragmentView.findViewById(R.id.main_tutorial);
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AlarmListActivity.class));
            }
        });
        //Button to Launch game --DEBUG ONLY
        final Button settings = (Button) inputFragmentView.findViewById(R.id.main_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TestActivity.class));
            }
        });

        //Button to Quit App
        final Button quit = (Button) inputFragmentView.findViewById(R.id.main_quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                System.exit(0);
            }
        });
        return inputFragmentView;
    }
}
