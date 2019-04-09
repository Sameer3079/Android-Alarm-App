package lk.sliit.androidalarmsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private String TAG = "ALARM_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive()");
        context.startActivity(new Intent(context, AlarmRingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
