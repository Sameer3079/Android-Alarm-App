package lk.sliit.androidalarmsystem;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlarmCreationActivity extends AppCompatActivity {

    TextView nameTxtView, hourTxtView, minuteTxtView;
    Spinner toneSelector;
    Button createButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creation);

        nameTxtView = findViewById(R.id.alarm_name);
        hourTxtView = findViewById(R.id.hour);
        minuteTxtView = findViewById(R.id.minute);
        toneSelector = findViewById(R.id.toneSelector);
        createButton = findViewById(R.id.createButton);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);

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
        editor = sharedPreferences.edit();
        String existingData = sharedPreferences.getString("alarms", "[]");

        String alarmName = nameTxtView.getText().toString();
        String hour = hourTxtView.getText().toString();
        String minute = minuteTxtView.getText().toString();

        try {
            JSONArray alarmsArray = new JSONArray(existingData);

            JSONObject alarmObject = new JSONObject();
            alarmObject.put("name", alarmName);
            String time = hour.concat(":").concat(minute);
            alarmObject.put("time", time);

            alarmsArray.put(alarmObject.toString());

            editor.putString("alarms", alarmsArray.toString());
            editor.apply();
            this.finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
