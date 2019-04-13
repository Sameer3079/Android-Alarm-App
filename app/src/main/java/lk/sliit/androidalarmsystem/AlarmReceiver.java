package lk.sliit.androidalarmsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private String TAG = "APP-AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive() - Received Broadcast, alarmId = " + intent.getStringExtra("alarmId"));

        Intent ringAlarmIntent = new Intent(context, AlarmRingActivity.class);
        ringAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ringAlarmIntent.putExtra("alarmId", intent.getStringExtra("alarmId"));

        context.startActivity(ringAlarmIntent);
    }

}
