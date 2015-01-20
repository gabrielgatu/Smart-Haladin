package com.gabrielgatu.haladin.beans;

import java.io.Serializable;

/**
 * Created by gabrielgatu on 19/01/15.
 */
public class InfoBox implements Serializable {

    private String title;
    private String value;

    public InfoBox(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}
