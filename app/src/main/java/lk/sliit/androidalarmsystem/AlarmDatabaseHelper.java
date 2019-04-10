package lk.sliit.androidalarmsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    private final static String TABLE_NAME = "alarms";
    private final static String DB_NAME = "ctse_alarms.db";

    AlarmDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (id integer PRIMARY KEY, alarmName text, time string, tone number, enabled boolean)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void save(Alarm alarm) {
        int id = 0;
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT max(id) FROM " + TABLE_NAME, null);
        cursor.moveToNext();
        if (!cursor.isAfterLast()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("alarmName", alarm.getName());
        cv.put("time", alarm.getTime());
        cv.put("tone", alarm.getAlarmToneId());
        cv.put("enabled", alarm.isEnabled());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    Alarm read(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT value FROM " + TABLE_NAME + " WHERE id = " + id, null);
        cursor.moveToNext();
        String alarmName = cursor.getString(cursor.getColumnIndex("alarmName"));
        String time = cursor.getString(cursor.getColumnIndex("time"));
        int toneId = cursor.getInt(cursor.getColumnIndex("tone"));
        boolean enabled = cursor.getInt(cursor.getColumnIndex("enabled")) > 0;
        Alarm alarm = new Alarm(id, alarmName, time, toneId, enabled);
        cursor.close();
        return alarm;
    }

    List<Alarm> readAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToNext();
        int idIdx = cursor.getColumnIndex("id");
        int nameIdx = cursor.getColumnIndex("alarmName");
        int timeIdx = cursor.getColumnIndex("time");
        int toneIdx = cursor.getColumnIndex("tone");
        int enabledIdx = cursor.getColumnIndex("enabled");
        List<Alarm> data = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(idIdx);
            String name = cursor.getString(nameIdx);
            String time = cursor.getString(timeIdx);
            long tone = cursor.getLong(toneIdx);
            boolean enabled = cursor.getInt(enabledIdx) > 0;
            data.add(new Alarm(id, name, time, tone, enabled));
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }
}
