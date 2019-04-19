package lk.sliit.androidalarmsystem.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import lk.sliit.androidalarmsystem.AlarmDatabaseHelper;
import lk.sliit.androidalarmsystem.AlarmRejectStatus;
import lk.sliit.androidalarmsystem.domain.Alarm;
import lk.sliit.androidalarmsystem.domain.AlarmCommand;
import lk.sliit.androidalarmsystem.AlarmService;
import lk.sliit.androidalarmsystem.R;

public class AlarmCreationActivity extends AppCompatActivity {

    private final static String TAG = "APP-CreationActivity";

    private TextView nameTxtView, hourTxtView, minuteTxtView;
    private Spinner toneSelector;
    private Button createButton;
    private MediaPlayer mediaPlayer;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Started Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creation);

        nameTxtView = findViewById(R.id.alarm_name);
        hourTxtView = findViewById(R.id.hour);
        minuteTxtView = findViewById(R.id.minute);
        toneSelector = findViewById(R.id.toneSelector);
        createButton = findViewById(R.id.createButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tones_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toneSelector.setAdapter(adapter);
        toneSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isInitialized) {
                    Log.i(TAG, "Tone Selected");
                    try {
                        mediaPlayer.stop();
                    } catch (NullPointerException e) {

                    }
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), Alarm.getToneResId(position));
                    mediaPlayer.start();
                } else {
                    isInitialized = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Log.i(TAG, "Nothing Selected");
            }

        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm();
            }
        });
    }

    private void createAlarm() {

        AlarmDatabaseHelper db = new AlarmDatabaseHelper(getApplicationContext());

        String alarmName = nameTxtView.getText().toString();

        if (alarmName.trim().equals("")) {
            Toast.makeText(this, "Invalid Alarm Name", Toast.LENGTH_LONG).show();
            return;
        }

        String hour = hourTxtView.getText().toString();
        int hourInt = -1;
        try {
            hourInt = Integer.parseInt(hour.trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Time", Toast.LENGTH_LONG).show();
            return;
        }
        if (hourInt < 0 || hourInt >= 24) {
            Toast.makeText(this, "Invalid Time", Toast.LENGTH_LONG).show();
            return;
        }

        String minute = minuteTxtView.getText().toString();
        int minuteInt = -1;
        try {
            minuteInt = Integer.parseInt(minute.trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Time", Toast.LENGTH_LONG).show();
            return;
        }
        if (minuteInt < 0 || minuteInt >= 60) {
            Toast.makeText(this, "Invalid Time", Toast.LENGTH_LONG).show();
            return;
        }

        long toneId = toneSelector.getSelectedItemId();

        if (hourInt < 10) {
            hour = "0".concat(hourInt + "");
        }
        if (minuteInt < 10) {
            minute = "0".concat(minuteInt + "");
        }
        String time = hour.concat(":").concat(minute);

        AlarmRejectStatus status = db.doesRecordExist(-1, alarmName, time);

        if (status == AlarmRejectStatus.DUPLICATE_NAME) {
            Toast.makeText(this, "Alarm with the same name already exists", Toast.LENGTH_LONG);
            return;
        } else if (status == AlarmRejectStatus.DUPLICATE_TIME) {
            Toast.makeText(this, "An alarm is already set for this time", Toast.LENGTH_LONG);
            return;
        }

        Alarm alarm = new Alarm(alarmName, time, toneId, true);
        long id = db.save(alarm);

        if (id > 0) {
            Log.i(TAG, "Alarm Saved to Database");
        } else {
            Log.i(TAG, "Alarm Saving FAILED");
        }

        Intent setNewAlarmIntent = new Intent(this, AlarmService.class);
        setNewAlarmIntent.putExtra("command", AlarmCommand.SET_ALARM);
        setNewAlarmIntent.putExtra("alarmId", id);
        startService(setNewAlarmIntent);

        try {
            mediaPlayer.stop();
        } catch (NullPointerException e) {

        }

        Toast.makeText(getApplicationContext(), "Alarm has been created", Toast.LENGTH_LONG).show();
        this.finish();
    }
}
