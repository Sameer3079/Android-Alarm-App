package lk.sliit.androidalarmsystem;

public class Alarm {

    private long id;
    private String name;
    private String time;
    private long alarmToneId;
    private boolean enabled;

    Alarm(long id, String name, String time, long alarmToneId, boolean enabled) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.alarmToneId = alarmToneId;
        this.enabled = enabled;
    }

    Alarm(String name, String time, long alarmToneId, boolean enabled) {
        this.name = name;
        this.time = time;
        this.alarmToneId = alarmToneId;
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    long getAlarmToneId() {
        return alarmToneId;
    }

    public void setAlarmToneId(long alarmToneId) {
        this.alarmToneId = alarmToneId;
    }

    boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
