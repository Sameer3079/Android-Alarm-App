package lk.sliit.androidalarmsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startActivity(new Intent(context, AlarmRingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
