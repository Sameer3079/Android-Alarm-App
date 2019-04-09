package lk.sliit.androidalarmsystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AlarmService extends Service {

    SharedPreferences sharedPreferences;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);

//        sharedPreferences.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        String rawData = sharedPreferences.getString("alarms", "[]");
        try {
            JSONArray alarmsArray = new JSONArray(rawData);
            for (int x = 0; x < alarmsArray.length(); x++) {
                Object temp = alarmsArray.get(x);
                JSONObject alarm = new JSONObject(temp.toString());
                String time = alarm.getString("time");
                String[] timeArray = time.split(":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
                Intent intent2 = new Intent(this, AlarmReceiver.class);
                intent2.putExtra("alarmName", alarm.getString("name"));
                alarmIntent = PendingIntent.getBroadcast(this, x, intent2, 0);
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() +
//                        5 * 1000, alarmIntent);

        // Set the alarm to start at approximately 2:00 p.m.


        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);


        this.stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        System.out.println("onCreate");
        super.onCreate();
    }
}
