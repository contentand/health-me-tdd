package com.dy.health;

import java.util.HashMap;
import java.util.Map;

public class HealthServiceSetup {
    private Map<String, TimeRange> namedTimeRanges = new HashMap<>();
    private double minStepsPerDay;
    private double minHoursOfMovementPerDay;
    private double minKilocalsPerDay;
    private double minLitersPerDay;

    public Map<String, TimeRange> getNamedTimeRanges() {
        return namedTimeRanges;
    }

    public HealthServiceSetup setNamedTimeRanges(Map<String, TimeRange> namedTimeRanges) {
        this.namedTimeRanges = namedTimeRanges;
        return this;
    }

    public double getMinStepsPerDay() {
        return minStepsPerDay;
    }

    public HealthServiceSetup setMinStepsPerDay(double minStepsPerDay) {
        this.minStepsPerDay = minStepsPerDay;
        return this;
    }

    public double getMinHoursOfMovementPerDay() {
        return minHoursOfMovementPerDay;
    }

    public HealthServiceSetup setMinHoursOfMovementPerDay(double minHoursOfMovementPerDay) {
        this.minHoursOfMovementPerDay = minHoursOfMovementPerDay;
        return this;
    }

    public double getMinKilocalsPerDay() {
        return minKilocalsPerDay;
    }

    public HealthServiceSetup setMinKilocalsPerDay(double minKilocalsPerDay) {
        this.minKilocalsPerDay = minKilocalsPerDay;
        return this;
    }

    public double getMinLitersPerDay() {
        return minLitersPerDay;
    }

    public HealthServiceSetup setMinLitersPerDay(double minLitersPerDay) {
        this.minLitersPerDay = minLitersPerDay;
        return this;
    }
}
