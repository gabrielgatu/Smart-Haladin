package com.gabrielgatu.haladin.beans;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by gabrielgatu on 18/01/15.
 */
public class DataFlow {

    private String place;
    private String name;
    private String measureType;
    private double fps;
    private String apiName;
    private ArrayList<Measure> measures;

    private String orderedBy;
    private String methodUsed;

    public DataFlow(String place, String name, String measureType, double fps, String apiName) {
        this.place = place;
        this.name = name;
        this.measureType = measureType;
        this.fps = fps;
        this.apiName = apiName;

        measures = new ArrayList<>();
    }

    public void addMeasure(Measure measure) {
        measures.add(measure);
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getMeasureType() {
        return measureType;
    }

    public ArrayList<Measure> getMeasures() {
        return measures;
    }

    public double getFps() {
        return fps;
    }

    public void setMeasures(ArrayList<Measure> measures) {
        this.measures = measures;
    }

    public String getApiName() {
        return apiName;
    }

    public String getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(String orderedBy) {
        this.orderedBy = orderedBy;
    }

    public String getMethodUsed() {
        return methodUsed;
    }

    public void setMethodUsed(String methodUsed) {
        this.methodUsed = methodUsed;
    }
}
