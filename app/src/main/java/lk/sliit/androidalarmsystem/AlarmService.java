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
    private List<Alarm> alarmsList = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(this);

        List<Alarm> alarmsArray = alarmDatabaseHelper.readAll();

        /*  if command = NEW_ALARM
            if command = DELETE_ALARM
            if command = MODIFIED_ALARM
            if command =  DELETE_ALL
         */


//        if (alarmsList.equals(null)) {
//            alarmsList = alarmsArray;
        // Set all alarms in database
        for (Alarm alarm : alarmsArray) {
            String time = alarm.getTime();
            String[] timeArray = time.split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DATE, 1);
            }
            Intent intent2 = new Intent(this, AlarmReceiver.class);
            intent2.putExtra("alarmName", alarm.getName());
            alarmIntent = PendingIntent.getBroadcast(this, alarm.getId(), intent2, 0);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
//        } else {
//            List<Alarm> newAlarms = new ArrayList<>();
//
//            // Find new alarms
//            for (Alarm alarm1 : alarmsList) {
//
//                Alarm tempAlarm = null;
//                for (int x = 0; x < alarmsArray.size(); x++) {
//                    Alarm alarm2 = alarmsArray.get(x);
//                    if (alarm1.getId() == alarm2.getId() && alarm1.getName() == alarm2.getName() && alarm1.getTime() == alarm2.getTime()) {
//                        tempAlarm = alarm2;
//                        break;
//                    }
//                }
//                if (tempAlarm.equals(null)) {
//                    Log.i(TAG, "New Alarm has been added");
//                    newAlarms.add(tempAlarm);
//                }
//            }

//            List<Alarm> removedAlarms = alarmsList;
//            // Find removed alarms
//            for (Alarm alarm1 : alarmsArray) {
//
//                Alarm tempAlarm = null;
//                for(Alarm alarm2: alarmsList){
//                    if (alarm1.getId() == alarm2.getId() && alarm1.getName() == alarm2.getName() && alarm1.getTime() == alarm2.getTime()) {
//                        tempAlarm = alarm2;
//                        break;
//                    }
//                }
//                if (tempAlarm.equals(null)) {
//                    newAlarms.add(tempAlarm);
//                }
//            }

//        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }
}
