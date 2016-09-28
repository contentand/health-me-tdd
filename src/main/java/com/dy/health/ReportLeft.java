package com.dy.health;

public class ReportLeft {
    private double liquidLitersLeft;
    private double kiloCalsLeft;
    private double stepsLeft;
    private double hoursToMoveLeft;

    public ReportLeft() {}

    public ReportLeft(double liquidLitersLeft, double kiloCalsLeft, double stepsLeft, double hoursToMoveLeft) {
        this.liquidLitersLeft = liquidLitersLeft;
        this.kiloCalsLeft = kiloCalsLeft;
        this.stepsLeft = stepsLeft;
        this.hoursToMoveLeft = hoursToMoveLeft;
    }

    public double getLiquidLitersLeft() {
        return liquidLitersLeft;
    }

    public void setLiquidLitersLeft(double liquidLitersLeft) {
        this.liquidLitersLeft = liquidLitersLeft;
    }

    public double getKiloCalsLeft() {
        return kiloCalsLeft;
    }

    public void setKiloCalsLeft(double kiloCalsLeft) {
        this.kiloCalsLeft = kiloCalsLeft;
    }

    public double getStepsLeft() {
        return stepsLeft;
    }

    public void setStepsLeft(double stepsLeft) {
        this.stepsLeft = stepsLeft;
    }

    public double getHoursToMoveLeft() {
        return hoursToMoveLeft;
    }

    public void setHoursToMoveLeft(double hoursToMoveLeft) {
        this.hoursToMoveLeft = hoursToMoveLeft;
    }
}
