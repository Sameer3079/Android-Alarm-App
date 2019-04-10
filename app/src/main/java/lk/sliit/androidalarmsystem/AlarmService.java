package lk.sliit.androidalarmsystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmService extends Service {

    private final static String TAG = "APP - AlarmService";
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    AlarmDatabaseHelper alarmDatabaseHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*  if command = NEW_ALARM
            if command = DELETE_ALARM
            if command = MODIFIED_ALARM
            if command =  DELETE_ALL
         */

        String command = intent.getStringExtra("command");

        Log.i(TAG, "onStartCommand, Command = " + command);

        switch (command) {
            case "NEW_ALARM":
                setNew(intent);
                break;
            case "DELETE_ALARM":
                deleteOne();
                break;
            case "MODIFIED_ALARM":
                update();
                break;
            case "DELETE_ALL":
                deleteAll();
                break;
            case "SET_ALL":
                setAll();
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void setNew(Intent intent) {
        Log.i(TAG, "setNew");
        long id = intent.getLongExtra("alarmId", 0);
        Log.i(TAG, "id = " + id);
        Alarm alarm = alarmDatabaseHelper.read(id);
        setOne(alarm);
    }

    private void update() {

    }

    private void deleteAll() {
        // Done within the Main Activity itself
    }

    private void deleteOne() {

    }

    private void setAll() {
        Log.i(TAG, "setAll");
        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(this);

        List<Alarm> alarmsArray = alarmDatabaseHelper.readAll();
        for (Alarm alarm : alarmsArray) {
            setOne(alarm);
        }

    }

    private void setOne(Alarm alarm) {
        Log.i(TAG, "setOne, alarmName = " + alarm.getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);

        String time = alarm.getTime();
        String[] timeArray = time.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }
        Intent intent2 = new Intent(this, AlarmReceiver.class);
        intent2.putExtra("alarmName", alarm.getName());
        alarmIntent = PendingIntent.getBroadcast(this, (int) alarm.getId(), intent2, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        super.onCreate();
    }
}
