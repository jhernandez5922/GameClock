package jhernandez.gameclock.alarm;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;

import jhernandez.gameclock.R;
import jhernandez.gameclock.alarm.creation.EditAlarm;
import jhernandez.gameclock.sqlite.AlarmContract;

/**
 * This class contains the adapter needed to read the information from the SQLite database created
 * and present it with a RecyclerView
 * Created by Jason on 11/3/2015.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    CursorAdapter mCursorAdapter;
    private Context mContext;
    public int removed = 0;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        protected TextView titleText;
        protected CardView card;
        protected SwipeLayout swipeLayout;
        protected View editButton;
        protected ImageView deleteButton;
        protected Switch active;
        protected TextView timeView;
        Alarm alarm;

        public ViewHolder(View v) {
            super(v);
            titleText = (TextView) v.findViewById(R.id.card_title);
            card = (CardView) v;
            active = (Switch) v.findViewById(R.id.active);
            swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
            deleteButton = (ImageView) v.findViewById(R.id.delete);
            editButton = v.findViewById(R.id.bottom_wrapper_2);
            timeView = (TextView) v.findViewById(R.id.digital_clock);
            active.setChecked(true);
        }

        public void updateActive() {
            if (alarm != null)
                active.setChecked(alarm.isActive());
            else {
                active.setChecked(false);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            alarm.setActive(isChecked);
            int color = ContextCompat.getColor(buttonView.getContext(), isChecked ?
                    R.color.text_primary :
                    R.color.text_secondary);
            titleText.setTextColor(color);
            timeView.setTextColor(color);
            if (isChecked)
                AlarmReceiver.setAlarm(buttonView.getContext().getApplicationContext(), alarm);
            else
                AlarmReceiver.deactivateAlarm(buttonView.getContext().getApplicationContext(), alarm);
        }

    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public AlarmAdapter(Context context, Cursor cursor) {
        mContext = context;
        if (cursor == null) {
            cursor = context.getContentResolver().query(
                    AlarmContract.AlarmEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor != null)
                cursor.moveToFirst();
            else
                return;
        }
        mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.alarm_list_card, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView) view.findViewById(R.id.card_title)).setText(
                        cursor.getString(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NAME)));
                long id = cursor.getLong(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_TIME));
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
                ((TextView) view.findViewById(R.id.digital_clock)).setText(sdf.format(id));
                setUpWeek(view, cursor);


            }
        };
    }
    // Create new views (invoked by the layout manager)
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Cursor c = mCursorAdapter.getCursor();
        final int holderPosition = holder.getAdapterPosition();
        c.moveToPosition(holderPosition);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.editButton);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(holderPosition);
                notifyItemRemoved(holderPosition);
                notifyItemRangeChanged(holderPosition, mCursorAdapter.getCount());
                AlarmReceiver.deactivateAlarm(mContext, holder.alarm);
                Toast.makeText(view.getContext(), "Deleted " + holder.titleText.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = mCursorAdapter.getCursor();
                c.moveToPosition(holderPosition);
                AlarmReceiver.deactivateAlarm(mContext.getApplicationContext(), holder.alarm);
                Intent intent = new Intent(mContext, EditAlarm.class);
                intent.putExtra("alarm", holder.alarm);
                ((AlarmListActivity) mContext).startActivityForResult(intent, 1);
            }
        });
        if (holder.alarm == null) {
            holder.alarm = new Alarm(c);
        }
        holder.active.setOnCheckedChangeListener(holder);
        holder.updateActive();
        mCursorAdapter.bindView(holder.card, mContext, c);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public void removeItem(int idx) {
        mCursorAdapter.getCursor().moveToPosition(idx);
        removed = idx;
        long id = mCursorAdapter.getCursor()
                .getLong(mCursorAdapter.getCursor().getColumnIndex("_id"));
        mContext.getContentResolver().delete(
                AlarmContract.AlarmEntry.CONTENT_URI,
                "_id=" + String.valueOf(id),
                null
        );
        mCursorAdapter.getCursor().moveToFirst();

    }

    public void notifyUpdates() {
        mCursorAdapter.notifyDataSetChanged();
    }


    private void setUpWeek(View v, Cursor cursor) {

//        final WeekPicker week = (WeekPicker) v.findViewById(R.id.week_picker);
//        final Alarm current = new Alarm(cursor);
//        week.setWeek(current.getWeek());
//        week.setWeekOnClickListener(new TextView.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    week.onClick(v);
//                    current.setEntireWeek(week.getWeek());
//                    AlarmReceiver.setAlarm(mContext.getApplicationContext(), current);
//                    mContext.getContentResolver().update(AlarmContract.AlarmEntry.CONTENT_URI, current.contentValues,"_id=" + current.getID(), null );
//                }
//            });
        }
}
