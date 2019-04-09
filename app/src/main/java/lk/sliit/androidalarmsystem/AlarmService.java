package lk.sliit.androidalarmsystem;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class AlarmService extends Service {

    SharedPreferences sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
//        sharedPreferences.// TODO:
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        System.out.println("onCreate");
        super.onCreate();
    }
}
