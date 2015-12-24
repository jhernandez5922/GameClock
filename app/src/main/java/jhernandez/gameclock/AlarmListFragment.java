package jhernandez.gameclock;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jhernandez.gameclock.sqlite.AlarmContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlarmListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView emptyView;
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

        emptyView = (TextView) v.findViewById(R.id.empty_view);
        emptyView.setText("No Alarms set, Click the little clock below to get started!");

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        //TODO Animations for RecyclerView?

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        mAdapter = new MyAdapter(getContext(), null);

        mRecyclerView.setAdapter(mAdapter);

        //Add more alarms FAB
        //TODO Add drawable to FAB
        FloatingActionButton newAlarm = (FloatingActionButton) v.findViewById(R.id.fab);
        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), AlarmSettings.class), 1);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {

            //Parse name from extras from activity executed
            Alarm alarm = data.getParcelableExtra("alarm");
            if (alarm.readyToInsert()) {
                Uri result = getContext().getContentResolver().insert(AlarmContract.AlarmEntry.CONTENT_URI, alarm.contentValues);
                //mAdapter.notifyDataSetChanged();
                alarm.setID(result.getLastPathSegment());
            }
            //Create Alarm
            AlarmReceiver.setAlarm(getContext(), alarm);
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


    /**
     * Executes when the loader finishes a task, while also checking if the
     * current list is empty. It replaces the view with a textView that informs
     * the user to add a new alarm
     *
     * @param loader: Loader that finished
     * @param data: Cursor where the loader altered data
     * @author Jason Hernandez
     **/
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() > mAdapter.mCursorAdapter.getCount()) {
            mAdapter.mCursorAdapter.swapCursor(data);
            mAdapter.notifyItemInserted(mAdapter.getItemCount());
        }
        else if (data.getCount() < mAdapter.mCursorAdapter.getCount()) {
            mAdapter.mCursorAdapter.swapCursor(data);
            if (mAdapter.removed > 0 && mAdapter.removed < mAdapter.getItemCount())
                mAdapter.notifyItemRemoved(mAdapter.removed);
            else
                mAdapter.notifyDataSetChanged();
            mAdapter.removed = -1;
        }
        else {
            mAdapter.mCursorAdapter.swapCursor(data);
            mAdapter.notifyDataSetChanged();
        }
        if (mAdapter.getItemCount() == 0){
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.mCursorAdapter.swapCursor(null);
    }

}
