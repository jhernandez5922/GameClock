package jhernandez.gameclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import jhernandez.gameclock.sqlite.AlarmContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlarmListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static int buttonCount = 0;
    private static final int FORECAST_LOADER = 0;

    public AlarmListFragment() {
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_alarm_list, container, false);


        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        //mRecyclerView.setItemAnimator(new  );

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        mAdapter = new MyAdapter(getContext(), null);

        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton newAlarm = (FloatingActionButton) v.findViewById(R.id.fab);
        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCount++;
                //Alarm a = new Alarm(("Alarm "+ buttonCount), 1230, new boolean[7]);
                //getContext().getContentResolver().insert(AlarmContract.AlarmEntry.CONTENT_URI, a.contentValues);
                //mAdapter.notifyItemInserted(mAdapter.getItemCount() + 1);
                startActivityForResult(new Intent(getContext(), AlarmSettings.class), 1);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            //Log.v(TAG, "CURRENT: " + data.getStringExtra("time"+requestCode));

            //Parse name from extras from activity executed
            Alarm alarm = data.getParcelableExtra("alarm");
            if (alarm.readyToInsert()) {
                Uri result = getContext().getContentResolver().insert(AlarmContract.AlarmEntry.CONTENT_URI, alarm.contentValues);
                mAdapter.notifyDataSetChanged();
                alarm.setID(result.getLastPathSegment());
            }
            //Create Alarm
            SetAlarm(alarm);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri alarms = AlarmContract.AlarmEntry.CONTENT_URI;
        // specify an adapter (see also next example)
        return new CursorLoader(getActivity(),
                alarms,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.mCursorAdapter.swapCursor(null);
    }

    private void SetAlarm (Alarm alarm) {
        /*
            This function sends the request to the AlarmManager to activate
            the alarm at the given time.
         */
        if (alarm.isActive()) {
            /*
                Formats the time send from settings screen to be read by
                alarm manager
             */

            if (alarm.invalidValues())
                return;
            if (alarm.getAlarmTime() == 0)  //time was set for alarm and
                return;
            Calendar alarmSetTime = Calendar.getInstance();
            alarmSetTime.setTimeInMillis(alarm.getAlarmTime());
            AlarmReceiver.getNearestDay(alarmSetTime, alarm.getWeek());
            //Set up alarm manager
            AlarmManager alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), alarm.getID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int currentApiVersion = Build.VERSION.SDK_INT;
            Log.v("GameClock Alarm Setting", "ALARM SET FOR: " + alarmSetTime.get(Calendar.DAY_OF_WEEK));
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT)
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmSetTime.getTimeInMillis(), alarmIntent);
            else
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmSetTime.getTimeInMillis(), alarmIntent);
            //Tell receiver to wake up device when alarm triggers
            ComponentName receiver = new ComponentName(getContext(), AlarmReceiver.class);
            PackageManager pm = getContext().getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
