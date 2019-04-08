package lk.sliit.androidalarmsystem;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlarmCreationActivity extends AppCompatActivity {

    TextView nameTxtView, hourTxtView, minuteTxtView;
    Button createButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creation);

        nameTxtView = findViewById(R.id.alarm_name);
        createButton = findViewById(R.id.createButton);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);

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


        try {
            JSONArray alarmsArray = new JSONArray(existingData);
            JSONObject testObject = new JSONObject().put("name", alarmName);
            alarmsArray.put(testObject.toString());

            editor.putString("alarms", alarmsArray.toString());
            editor.apply();
            this.finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
