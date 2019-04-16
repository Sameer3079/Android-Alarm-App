package lk.sliit.androidalarmsystem.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lk.sliit.androidalarmsystem.AlarmDatabaseHelper;
import lk.sliit.androidalarmsystem.QuestionDatabaseHelper;
import lk.sliit.androidalarmsystem.domain.Alarm;
import lk.sliit.androidalarmsystem.domain.AlarmCommand;
import lk.sliit.androidalarmsystem.AlarmReceiver;
import lk.sliit.androidalarmsystem.AlarmService;
import lk.sliit.androidalarmsystem.CustomRecyclerViewAdapter;
import lk.sliit.androidalarmsystem.R;
import lk.sliit.androidalarmsystem.domain.Question;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "APP-MainActivity";
    RecyclerView recyclerView;
    CustomRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Started Activity");
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

        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(getApplicationContext());

        Intent startAlarmServiceIntent = new Intent(this, AlarmService.class);
        startAlarmServiceIntent.putExtra("command", AlarmCommand.SET_ALL);
        startService(startAlarmServiceIntent);
        alarmDatabaseHelper.close();
        refreshAlarms();

        QuestionDatabaseHelper db = new QuestionDatabaseHelper(this);
        try {
            db.fillDatabase();
        } catch (SQLiteConstraintException exception) {
            Log.i(TAG, "Questions have been inserted");
        }
    }

    private void refreshAlarms() {

        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(getApplicationContext());
        List<Alarm> alarms = alarmDatabaseHelper.readAll();

        ArrayList<Alarm> alarmsArray = new ArrayList<>();

        int alarmCount = alarms.size();
        for (int x = 0; x < alarmCount; x++) {
            Alarm alarm = alarms.get(x);
            alarmsArray.add(alarm);
        }
        alarmDatabaseHelper.close();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerViewAdapter(this, alarmsArray);
        recyclerView.setAdapter(adapter);
        Log.i(TAG, "Alarms Refreshed");
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

            // Remove the Alarm records from the database
            AlarmDatabaseHelper alarmDatabaseHelper =
                    new AlarmDatabaseHelper(getApplicationContext());
            List<Alarm> alarms = alarmDatabaseHelper.readAll();
            alarmDatabaseHelper.deleteAll();
            alarmDatabaseHelper.close();

            // Cancel the alarms
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            for (Alarm alarm : alarms) {
                alarmManager.cancel(PendingIntent.getBroadcast(this, (int) alarm.getId(),
                        new Intent(this, AlarmReceiver.class)
                                .putExtra("alarmName", alarm.getName()), 0));
            }
            Log.i(TAG, "Deleted All Alarms");

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
