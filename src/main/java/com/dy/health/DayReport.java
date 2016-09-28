package com.dy.health;

public class DayReport {
    private double stepsCompletionRate;
    private double hoursToMoveCompletionRate;
    private double kiloCalsCompletionRate;
    private double liquidLitersCompletionRate;

    public DayReport(double stepsCompletionRate, double hoursToMoveCompletionRate,
                     double kiloCalsCompletionRate, double liquidLitersCompletionRate) {
        this.stepsCompletionRate = stepsCompletionRate;
        this.hoursToMoveCompletionRate = hoursToMoveCompletionRate;
        this.kiloCalsCompletionRate = kiloCalsCompletionRate;
        this.liquidLitersCompletionRate = liquidLitersCompletionRate;
    }

    public double getStepsCompletionRate() {
        return stepsCompletionRate;
    }

    public double getHoursToMoveCompletionRate() {
        return hoursToMoveCompletionRate;
    }

    public double getKiloCalsCompletionRate() {
        return kiloCalsCompletionRate;
    }

    public double getLiquidLitersCompletionRate() {
        return liquidLitersCompletionRate;
    }
}
