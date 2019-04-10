package lk.sliit.androidalarmsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlarmCreationActivity extends AppCompatActivity {

    TextView nameTxtView, hourTxtView, minuteTxtView;
    Spinner toneSelector;
    Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm();
            }
        });
    }

    private void createAlarm() {

        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(getApplicationContext());

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
            hour = "0".concat(hour);
        }
        if (minuteInt < 10) {
            minute = "0".concat(minute);
        }
        String time = hour.concat(":").concat(minute);

        Alarm alarm = new Alarm(alarmName, time, toneId, true);
        alarmDatabaseHelper.save(alarm);
        this.finish();
    }
}
