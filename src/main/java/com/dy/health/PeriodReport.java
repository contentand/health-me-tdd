package com.dy.health;

import java.time.LocalDate;

public class PeriodReport {
    private final LocalDate startDate;
    private final LocalDate endDate;

    private final double stepsCompletionMedian;
    private final double hoursToMoveCompletionMedian;
    private final double kiloCalsCompletionMedian;
    private final double liquidLitersCompletionMedian;

    public PeriodReport(LocalDate startDate,
                        LocalDate endDate,
                        double stepsCompletionMedian,
                        double hoursToMoveCompletionMedian,
                        double kiloCalsCompletionMedian,
                        double liquidLitersCompletionMedian) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.stepsCompletionMedian = stepsCompletionMedian;
        this.hoursToMoveCompletionMedian = hoursToMoveCompletionMedian;
        this.kiloCalsCompletionMedian = kiloCalsCompletionMedian;
        this.liquidLitersCompletionMedian = liquidLitersCompletionMedian;
    }

    static class PeriodReportBuilder {
        private LocalDate startDate;
        private LocalDate endDate;

        private Double stepsCompletionMedian;
        private Double hoursToMoveCompletionMedian;
        private Double kiloCalsCompletionMedian;
        private Double liquidLitersCompletionMedian;

        public PeriodReportBuilder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public PeriodReportBuilder setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public PeriodReportBuilder setStepsCompletionMedian(Double stepsCompletionMedian) {
            this.stepsCompletionMedian = stepsCompletionMedian;
            return this;
        }

        public PeriodReportBuilder setHoursToMoveCompletionMedian(Double hoursToMoveCompletionMedian) {
            this.hoursToMoveCompletionMedian = hoursToMoveCompletionMedian;
            return this;
        }

        public PeriodReportBuilder setKiloCalsCompletionMedian(Double kiloCalsCompletionMedian) {
            this.kiloCalsCompletionMedian = kiloCalsCompletionMedian;
            return this;
        }

        public PeriodReportBuilder setLiquidLitersCompletionMedian(Double liquidLitersCompletionMedian) {
            this.liquidLitersCompletionMedian = liquidLitersCompletionMedian;
            return this;
        }

        public PeriodReport build() {
            if (startDate  == null
                    || endDate == null
                    || stepsCompletionMedian == null
                    || hoursToMoveCompletionMedian == null
                    || kiloCalsCompletionMedian == null
                    || liquidLitersCompletionMedian == null) {
                throw new NullPointerException("One of the PeriodReport properties is unset.");
            }
            return new PeriodReport(startDate, endDate,
                    stepsCompletionMedian,
                    hoursToMoveCompletionMedian,
                    kiloCalsCompletionMedian,
                    liquidLitersCompletionMedian);
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getStepsCompletionMedian() {
        return stepsCompletionMedian;
    }

    public double getHoursToMoveCompletionMedian() {
        return hoursToMoveCompletionMedian;
    }

    public double getKiloCalsCompletionMedian() {
        return kiloCalsCompletionMedian;
    }

    public double getLiquidLitersCompletionMedian() {
        return liquidLitersCompletionMedian;
    }
}
