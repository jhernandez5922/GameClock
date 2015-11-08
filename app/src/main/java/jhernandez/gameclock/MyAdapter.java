package jhernandez.gameclock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import jhernandez.gameclock.sqlite.AlarmContract;

/**
 * Created by Jason on 11/3/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    CursorAdapter mCursorAdapter;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected TextView titleText;
        protected CardView card;

        public ViewHolder(View v, final Cursor cursor) {
            super(v);
            titleText = (TextView) v.findViewById(R.id.card_title);
            card = (CardView) v;
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, Cursor cursor) {
        mContext = context;
        if (cursor == null) {
            cursor = context.getContentResolver().query(
                    AlarmContract.AlarmEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        cursor.moveToFirst();
        }
        mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.alarm_list_card, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView) view).setText(cursor.getString(1));
            }
        };
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        final View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        final MyAdapter.ViewHolder holder = new ViewHolder(v, mCursorAdapter.getCursor());
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(
                        v.getContext());
                adb.setTitle("Remove this alarm?");
                adb.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(holder.getAdapterPosition());
                            }
                        });
                adb.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                adb.show();

                return false;
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editAlarm = new Intent(v.getContext(), AlarmSettings.class);
                v.getContext().startActivity(editAlarm);
            }
        });
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Cursor c = mCursorAdapter.getCursor();
        c.moveToPosition(holder.getAdapterPosition());
        mCursorAdapter.bindView(holder.titleText, mContext, mCursorAdapter.getCursor());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    private void removeItem(int idx) {
        mCursorAdapter.getCursor().moveToPosition(idx);
        long id = mCursorAdapter.getCursor()
                .getLong(mCursorAdapter.getCursor().getColumnIndex("_id"));
        mContext.getContentResolver().delete(
                AlarmContract.AlarmEntry.CONTENT_URI,
                "_id=" + String.valueOf(id),
                null
        );
        mCursorAdapter.getCursor().moveToFirst();
        this.notifyItemRemoved(idx);
    }
}
