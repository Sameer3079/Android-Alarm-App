package lk.sliit.androidalarmsystem;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlarmCreationActivity extends AppCompatActivity {

    Button createButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creation);

        createButton = findViewById(R.id.createButton);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
//                editor.putString("alarms", "[]");
//                editor.apply();
                String existingData = sharedPreferences.getString("alarms", "[]");
                try {
                    JSONArray alarmsArray = new JSONArray(existingData);
                    JSONObject testObject = new JSONObject().put("name", "test_alarm");
                    alarmsArray.put(testObject.toString());

                    editor.putString("alarms", alarmsArray.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
