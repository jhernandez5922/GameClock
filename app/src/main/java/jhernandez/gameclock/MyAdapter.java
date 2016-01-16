package jhernandez.gameclock;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.text.SimpleDateFormat;

import jhernandez.gameclock.sqlite.AlarmContract;

/**
 * Created by Jason on 11/3/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    CursorAdapter mCursorAdapter;
    private Context mContext;
    public int removed = 0;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected TextView titleText;
        protected CardView card;
        SwipeLayout swipeLayout;
        ImageView buttonDelete;

        public ViewHolder(View v, final Cursor cursor) {
            super(v);
            titleText = (TextView) v.findViewById(R.id.card_title);
            card = (CardView) v;
            swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
            buttonDelete = (ImageView) v.findViewById(R.id.delete);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "onItemSelected: " + titleText.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
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
                ((TextView) view.findViewById(R.id.card_title)).setText(
                        cursor.getString(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_NAME)));
                long id = cursor.getLong(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_TIME));
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                ((TextView) view.findViewById(R.id.digital_clock)).setText(sdf.format(id));
                setUpWeek(view, cursor);


            }
        };
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        final View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        final MyAdapter.ViewHolder holder = new ViewHolder(v, mCursorAdapter.getCursor());
//        v.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                AlertDialog.Builder adb = new AlertDialog.Builder(
//                        v.getContext());
//                adb.setTitle("Remove this alarm?");
//                adb.setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                removeItem(holder.getAdapterPosition());
//                            }
//                        });
//                adb.setNegativeButton("NO",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                adb.show();
//
//                return false;
//            }
//        });
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent editAlarm = new Intent(v.getContext(), AlarmSettings.class);
//                v.getContext().startActivity(editAlarm);
//            }
//        });
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Cursor c = mCursorAdapter.getCursor();
        c.moveToPosition(holder.getAdapterPosition());
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        holder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mCursorAdapter.getCount());
                Toast.makeText(view.getContext(), "Deleted " + holder.titleText.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });
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
    private void setUpWeek(View v, Cursor cursor) {

        int [] weekViews = new int[] {
                R.id.sunday_button,
                R.id.monday_button,
                R.id.tuesday_button,
                R.id.wednesday_button,
                R.id.thursday_button,
                R.id.friday_button,
                R.id.saturday_button
        };
        boolean [] week = new boolean[7];
        for (int i = 0; i < 7; i++) {
            int temp = cursor.getInt(cursor.getColumnIndex(Alarm.weekName[i]));
            week[i] = temp == 1;
            TextView tv = (TextView) v.findViewById(weekViews[i]);
            tv.setTextColor(
                    week[i] ? ContextCompat.getColor(v.getContext(), R.color.color_primary)
                            : ContextCompat.getColor(v.getContext(), R.color.color_verify_green)
            );
        }
    }
}
