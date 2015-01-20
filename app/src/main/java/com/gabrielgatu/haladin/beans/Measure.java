package com.gabrielgatu.haladin.beans;

import java.util.Calendar;

/**
 * Created by gabrielgatu on 18/01/15.
 */
public class Measure {

    private String time;
    private String value;

    public Measure(String time, String value) {
        this.time = time;
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }
}
