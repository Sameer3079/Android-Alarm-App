package lk.sliit.androidalarmsystem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import lk.sliit.androidalarmsystem.R;

public class AlarmEditActivity extends AppCompatActivity {

    private TextView alarmNameTxt, hourTxt, minuteTxt;
    private Button updateButton;
    private Spinner toneSelectorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);
    }
}
