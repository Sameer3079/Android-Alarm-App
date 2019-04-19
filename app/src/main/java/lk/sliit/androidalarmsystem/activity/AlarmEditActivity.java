package lk.sliit.androidalarmsystem.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
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
import lk.sliit.androidalarmsystem.AlarmService;
import lk.sliit.androidalarmsystem.R;
import lk.sliit.androidalarmsystem.domain.Alarm;
import lk.sliit.androidalarmsystem.domain.AlarmCommand;

public class AlarmEditActivity extends AppCompatActivity {

    private final static String TAG = "APP-AlarmEditActivity";

    private TextView alarmNameTxt, hourTxt, minuteTxt;
    private Button updateButton;
    private Spinner toneSelectorSpinner;
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        Log.i(TAG, "Started Activity");

        initializeComponents();

        Intent intent = getIntent();
        long alarmId = intent.getLongExtra("alarmId", 0);

        if (alarmId == 0) {
            Snackbar.make(findViewById(R.id.edit_alarm_activity), "An Error Occurred", Snackbar.LENGTH_LONG).show();
            return;
        }

        setAlarmData(alarmId);
    }

    private void initializeComponents() {
        alarmNameTxt = findViewById(R.id.alarmNameTxt);
        hourTxt = findViewById(R.id.hourTxt);
        minuteTxt = findViewById(R.id.minuteTxt);

        updateButton = findViewById(R.id.updateButton);

        toneSelectorSpinner = findViewById(R.id.toneSelectorSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tones_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toneSelectorSpinner.setAdapter(adapter);
        toneSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
    }

    private void setAlarmData(long alarmId) {
        AlarmDatabaseHelper db = new AlarmDatabaseHelper(this);
        alarm = db.read(alarmId);

        alarmNameTxt.setText(alarm.getName());

        String time = alarm.getTime();
        String[] timeData = time.split(":");

        hourTxt.setText(timeData[0]);
        minuteTxt.setText(timeData[1]);

        toneSelectorSpinner.setSelection(
                Integer.parseInt(new Long(alarm.getAlarmToneId()).toString()));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    private void update() {
        Log.i(TAG, "Update Button Clicked");

        AlarmDatabaseHelper db = new AlarmDatabaseHelper(getApplicationContext());

        String alarmName = alarmNameTxt.getText().toString();

        if (alarmName.trim().equals("")) {
            Toast.makeText(this, "Invalid Alarm Name", Toast.LENGTH_LONG).show();
            return;
        }

        String hour = hourTxt.getText().toString();
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

        String minute = minuteTxt.getText().toString();
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

        long toneId = toneSelectorSpinner.getSelectedItemId();

        if (hourInt < 10) {
            hour = "0".concat(hourInt + "");
        }
        if (minuteInt < 10) {
            minute = "0".concat(minuteInt + "");
        }
        String time = hour.concat(":").concat(minute);

        AlarmRejectStatus status = db.doesRecordExist(alarm.getId(), alarmName, time);

        if (status == AlarmRejectStatus.DUPLICATE_NAME) {
            Toast.makeText(this, "Alarm with the same name already exists", Toast.LENGTH_LONG).show();
            return;
        } else if (status == AlarmRejectStatus.DUPLICATE_TIME) {
            Toast.makeText(this, "An alarm is already set for this time", Toast.LENGTH_LONG).show();
            return;
        }

        Alarm alarm = new Alarm(this.alarm.getId(), alarmName, time, toneId, true);
        int numOfRowsAffected = db.update(alarm);

        if (numOfRowsAffected > 0) {
            Log.i(TAG, "Alarm Saved to Database");
        } else {
            Log.i(TAG, "Alarm Saving FAILED");
        }

        Intent setNewAlarmIntent = new Intent(this, AlarmService.class);
        setNewAlarmIntent.putExtra("command", AlarmCommand.UPDATE_ALARM);
        setNewAlarmIntent.putExtra("alarmId", this.alarm.getId());
        startService(setNewAlarmIntent);

        try {
            mediaPlayer.stop();
        } catch (NullPointerException e) {

        }

        Toast.makeText(getApplicationContext(), "Alarm has been updated", Toast.LENGTH_LONG).show();
        this.finish();
    }
}
