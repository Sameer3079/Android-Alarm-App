package lk.sliit.androidalarmsystem.domain;

import java.io.Serializable;

public enum AlarmCommand implements Serializable {

    CANCEL_ALL,
    SET_ALL,

    SET_ALARM,
    UPDATE_ALARM,
    CANCEL_ALARM,

}
