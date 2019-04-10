package lk.sliit.androidalarmsystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "APP - MainActivity";
    RecyclerView recyclerView;
    CustomRecyclerViewAdapter adapter;
    AlarmDatabaseHelper alarmDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate Lifecycle Method");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmCreationActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        alarmDatabaseHelper = new AlarmDatabaseHelper(getApplicationContext());
        startService(new Intent(this, AlarmService.class));
        refreshAlarms();


    }

    private void refreshAlarms() {


        List<Alarm> alarms = alarmDatabaseHelper.readAll();

        ArrayList<Alarm> alarmsArray = new ArrayList<>();

        int alarmCount = alarms.size();
        for (int x = 0; x < alarmCount; x++) {
            Alarm alarm = alarms.get(x);
            alarmsArray.add(alarm);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerViewAdapter(this, alarmsArray);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        } else if (id == R.id.action_delete_all) {
            List<Alarm> alarms = alarmDatabaseHelper.readAll();
            alarmDatabaseHelper.deleteAll();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            for (Alarm alarm : alarms) {
                alarmManager.cancel(PendingIntent.getBroadcast(this, alarm.getId(),
                        new Intent(this, AlarmReceiver.class)
                                .putExtra("alarmName", alarm.getName()), 0));
            }
            // TODO: Cancel all Alarms
            refreshAlarms();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refreshAlarms();
    }
}
