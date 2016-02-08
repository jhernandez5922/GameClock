package jhernandez.gameclock.alarm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import jhernandez.gameclock.R;
import jhernandez.gameclock.SettingsActivity;
import jhernandez.gameclock.sqlite.AlarmContract;

public class AlarmListActivity extends AppCompatActivity {

    public static String TAG = AlarmListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            //Parse name from extras from activity executed
            Alarm alarm = data.getParcelableExtra("alarm");
            if (alarm.readyToInsert()) {
                if (alarm.getID() < 0) {
                    Uri result = getContentResolver().insert(AlarmContract.AlarmEntry.CONTENT_URI, alarm.contentValues);
                    Log.d("GameClock Alarm Status", "NEW ALARM INSERTED");
                    alarm.setID(result.getLastPathSegment());
                }
                else {
                    getContentResolver().update(AlarmContract.AlarmEntry.CONTENT_URI, alarm.contentValues,"_id=" + alarm.getID(), null );
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
                    Log.d("GameClock Alarm Status", alarm.getAlarmName() + ": " + alarm.getID() + " UPDATED");
                    if (f instanceof AlarmListFragment) {
                        ((AlarmListFragment) f).alertAdapter();
                    }
                }
                //Signal Alarm Manager to set an alarm
                AlarmReceiver.setAlarm(getApplicationContext(), alarm);
            }
            else {
                Log.e("GameClock Alarm Status", "ALARM UNABLE TO INSERT");
            }
        }
    }
}
