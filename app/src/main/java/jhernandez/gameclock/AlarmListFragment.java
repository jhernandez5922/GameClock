package jhernandez.gameclock;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jhernandez.gameclock.sqlite.AlarmContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlarmListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
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

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getContext(), null);
        mRecyclerView.setAdapter(mAdapter);


        FloatingActionButton newAlarm = (FloatingActionButton) v.findViewById(R.id.fab);
        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm a = new Alarm();
                getContext().getContentResolver().insert(AlarmContract.AlarmEntry.CONTENT_URI, a.contentValues);
                Cursor c = getContext().getContentResolver().query(AlarmContract.AlarmEntry.CONTENT_URI, null, null, null, null);
                Log.v(this.getClass().getSimpleName(), "Count: " + c.getCount());
                //startActivity(new Intent(getContext(), AlarmMenuActivity.class));
            }
        });
        return v;
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
}
