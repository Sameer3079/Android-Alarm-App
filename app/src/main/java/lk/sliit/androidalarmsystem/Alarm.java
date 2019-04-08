package lk.sliit.androidalarmsystem;

public class Alarm {

    private String name;
    private String time;
    private long alarmToneId;

    public Alarm(String name, String time, long alarmToneId) {
        this.name = name;
        this.time = time;
        this.alarmToneId = alarmToneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getAlarmToneId() {
        return alarmToneId;
    }

    public void setAlarmToneId(long alarmToneId) {
        this.alarmToneId = alarmToneId;
    }

}
