package lk.sliit.androidalarmsystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import lk.sliit.androidalarmsystem.domain.Alarm;
import lk.sliit.androidalarmsystem.domain.AlarmCommand;

public class AlarmService extends Service {

    private final static String TAG = "APP-AlarmService";
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    AlarmDatabaseHelper alarmDatabaseHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Object commandObj = intent.getSerializableExtra("command");
        AlarmCommand command = (AlarmCommand) commandObj;

        switch (command) {
            case SET_ALARM:
                setNew(intent);
                break;
            case CANCEL_ALARM:
                cancelOne();
                break;
            case UPDATE_ALARM:
                update(intent);
                break;
            case CANCEL_ALL:
                cancelAll();
                break;
            case SET_ALL:
                setAll();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void setNew(Intent intent) {
        long id = intent.getLongExtra("alarmId", 0);
        Alarm alarm = alarmDatabaseHelper.read(id);
        setOne(alarm);
        Log.i(TAG, "New Alarm Set");
    }

    private void update(Intent intent) {

        long id = intent.getLongExtra("alarmId", -1);

        if (id == -1) {
            Toast.makeText(this, "Failed to find alarm", Toast.LENGTH_LONG);
            Log.i(TAG, "Failed to find alarm");
            return;
        }

        // Get Details of the alarm
        AlarmDatabaseHelper db = new AlarmDatabaseHelper(this);
        Alarm alarm = db.read(id);
        if (alarm.isEnabled()) {
            setOne(alarm);
        } else {
            alarmMgr.cancel(PendingIntent.getBroadcast(this, (int) alarm.getId(),
                    new Intent(this, AlarmReceiver.class), 0));
        }
        Log.i(TAG, "Alarm Updated");
    }

    // Done within the Main Activity itself
    private void cancelAll() {
        Log.i(TAG, "All Alarms Cancelled");
    }

    // TODO: Implement
    private void cancelOne() {
        Log.i(TAG, "Alarm Cancelled");
    }

    private void setAll() {
        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(this);

        List<Alarm> alarmsArray = alarmDatabaseHelper.readEnabled();
        for (Alarm alarm : alarmsArray) {
            setOne(alarm);
        }
        Log.i(TAG, "All Alarms Set");

    }

    private void setOne(Alarm alarm) {
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
        intent2.putExtra("alarmId", alarm.getId() + "");
        alarmIntent = PendingIntent.getBroadcast(this, (int) alarm.getId(), intent2, 0);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                alarmIntent);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service Created");
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        super.onCreate();
    }
}
