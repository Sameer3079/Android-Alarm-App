package lk.sliit.androidalarmsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    SharedPreferences.Editor editor;
    RecyclerView recyclerView;
    CustomRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        startService(new Intent(this, AlarmService.class));
        refreshAlarms();


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
            editor.putString("alarms", "[]");
            editor.apply();
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
