package com.dy.health;

public class UnfulfilledDayNormReport {
    private final double liquidLitersLeft;
    private final double kiloCalsLeft;
    private final double stepsLeft;
    private final double hoursToMoveLeft;

    public UnfulfilledDayNormReport() {
        this(0, 0, 0, 0);
    }

    public UnfulfilledDayNormReport(double liquidLitersLeft, double kiloCalsLeft,
                                    double stepsLeft, double hoursToMoveLeft) {
        this.liquidLitersLeft = liquidLitersLeft;
        this.kiloCalsLeft = kiloCalsLeft;
        this.stepsLeft = stepsLeft;
        this.hoursToMoveLeft = hoursToMoveLeft;
    }

    public double getLiquidLitersLeft() {
        return liquidLitersLeft;
    }

    public double getKiloCalsLeft() {
        return kiloCalsLeft;
    }

    public double getStepsLeft() {
        return stepsLeft;
    }

    public double getHoursToMoveLeft() {
        return hoursToMoveLeft;
    }
}
