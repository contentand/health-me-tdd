package com.dy.health;

import java.time.LocalDate;

public class PeriodReport {
    private final LocalDate startDate;
    private final LocalDate endDate;

    private final double stepsMedian;
    private final double hoursMovedMedian;
    private final double kilocalsMedian;
    private final double liquidLitersMedian;

    public PeriodReport(LocalDate startDate,
                        LocalDate endDate,
                        double stepsMedian,
                        double hoursMovedMedian,
                        double kilocalsMedian,
                        double liquidLitersMedian) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.stepsMedian = stepsMedian;
        this.hoursMovedMedian = hoursMovedMedian;
        this.kilocalsMedian = kilocalsMedian;
        this.liquidLitersMedian = liquidLitersMedian;
    }

    static class PeriodReportBuilder {
        private LocalDate startDate;
        private LocalDate endDate;

        private Double stepsMedian;
        private Double hoursToMoveMedian;
        private Double kilocalsMedian;
        private Double liquidLitersMedian;

        public PeriodReportBuilder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public PeriodReportBuilder setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public PeriodReportBuilder setStepsMedian(Double stepsMedian) {
            this.stepsMedian = stepsMedian;
            return this;
        }

        public PeriodReportBuilder setHoursToMoveMedian(Double hoursToMoveMedian) {
            this.hoursToMoveMedian = hoursToMoveMedian;
            return this;
        }

        public PeriodReportBuilder setKilocalsMedian(Double kilocalsMedian) {
            this.kilocalsMedian = kilocalsMedian;
            return this;
        }

        public PeriodReportBuilder setLiquidLitersMedian(Double liquidLitersMedian) {
            this.liquidLitersMedian = liquidLitersMedian;
            return this;
        }

        public PeriodReport build() {
            if (startDate  == null
                    || endDate == null
                    || stepsMedian == null
                    || hoursToMoveMedian == null
                    || kilocalsMedian == null
                    || liquidLitersMedian == null) {
                throw new NullPointerException("One of the PeriodReport properties is unset.");
            }
            return new PeriodReport(startDate, endDate,
                    stepsMedian,
                    hoursToMoveMedian,
                    kilocalsMedian,
                    liquidLitersMedian);
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getStepsMedian() {
        return stepsMedian;
    }

    public double getHoursMovedMedian() {
        return hoursMovedMedian;
    }

    public double getKilocalsMedian() {
        return kilocalsMedian;
    }

    public double getLiquidLitersMedian() {
        return liquidLitersMedian;
    }
}
