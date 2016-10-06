package com.dy.health;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class HealthService {

    private final Map<LocalDate, List<Record>> records = new HashMap<>();
    private final Map<String, TimeRange> namedTimeRanges;
    private final double minStepsPerDay;
    private final double minHoursOfMovementPerDay;
    private final double minKilocalsPerDay;
    private final double minLitersPerDay;

    public HealthService(HealthServiceSetup setup) {
        this.namedTimeRanges = setup.getNamedTimeRanges();
        this.minStepsPerDay = setup.getMinStepsPerDay();
        this.minHoursOfMovementPerDay = setup.getMinHoursOfMovementPerDay();
        this.minKilocalsPerDay = setup.getMinKilocalsPerDay();
        this.minLitersPerDay = setup.getMinLitersPerDay();
    }

    private static class Record {
        final String type; // drink
        final String name;
        final String measureUnit;
        final double quantity;
        final LocalDateTime dateTime; // should it just be LocalTime?
        final Duration duration;

        public Record(String type, String name, String measureUnit,
                      double quantity, LocalDateTime dateTime,
                      Duration duration) {
            this.type = type;
            this.name = name;
            this.measureUnit = measureUnit;
            this.quantity = quantity;
            this.dateTime = dateTime;
            this.duration = duration;
        }

        public Record(String type, String name, String measureUnit,
                      double quantity, LocalDateTime dateTime) {
            this(type, name, measureUnit, quantity, dateTime, Duration.ZERO);
        }

        @Override
        public String toString() {
            return "Record{" +
                    "type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", measureUnit='" + measureUnit + '\'' +
                    ", quantity=" + quantity +
                    ", dateTime=" + dateTime +
                    '}';
        }
    }

    private double calculate(String type, String measureUnit, String timeRange, LocalDate date) {
        Stream<Record> recordsForDate = records.get(date).stream();
        return recordsForDate
                .filter(isOf(type))
                .map(toQuantity(measureUnit, timeRange))
                .reduce(toSum())
                .orElse(0D);
    }

    private BinaryOperator<Double> toSum() {
        return (a, b) -> a + b;
    }

    private Function<Record, Double> toQuantity(String measureUnit, String timeRange) {
        return record -> {
            if (record.measureUnit.equals(measureUnit)) {
                if (isWithinTimeRange(timeRange, record.dateTime.toLocalTime())) {
                    return record.quantity;
                } else {
                    return 0D;
                }
            } else {
                return getTransformedQuantity(record, measureUnit);
            }
        };
    }

    private Predicate<Record> isOf(String type) {
        return record -> record.type.equals(type);
    }

    private double getTransformedQuantity(Record record, String targetMeasureUnit) {
        if (targetMeasureUnit.equals("liter") && record.measureUnit.equals("glass")) {
            return record.quantity * 0.25;
        } else if (targetMeasureUnit.equals("hour") && record.type.equals("move")) {
            return record.duration.toMinutes() / 60.0;
        } else {
            throw new IllegalStateException("Unable to transform " + record +
                    " into " + targetMeasureUnit); // to be implemented once feature is requested
        }
    }

    private boolean isWithinTimeRange(String timeRangeName, LocalTime time) {
        TimeRange timeRange = namedTimeRanges.get(timeRangeName);
        return timeRange != null && timeRange.isWithinTimeRange(time);

    }

    private double median(List<Double> list) {
        int size = list.size();
        if (size == 0) return 0;
        if (size == 1) return list.get(0);
        list.sort(Comparator.naturalOrder());
        if (size % 2 == 1) {
            return list.get(size / 2);
        } else {
            return (list.get(size / 2) + list.get(size / 2 - 1)) / 2.0;
        }
    }

    public PeriodReport getPeriodReport(LocalDate startDate, LocalDate endDate) {
        List<Double> steps = new ArrayList<>();
        List<Double> hours = new ArrayList<>();
        List<Double> kiloCals = new ArrayList<>();
        List<Double> liters = new ArrayList<>();
        LocalDate cursor = startDate;
        // Inject
        while (cursor.isBefore(endDate) || cursor.isEqual(endDate)) {
            List<Record> list = records.get(cursor);
            if (list == null) {
                steps.add(0D);
                hours.add(0D);
                kiloCals.add(0D);
                liters.add(0D);
            } else {
                steps.add(moved("step", cursor));
                hours.add(moved("hour", cursor));
                kiloCals.add(eaten("all", "kilocal", cursor));
                liters.add(drunk("liter", cursor));
            }
            cursor = cursor.plusDays(1);
        }
        // Create report
        return new PeriodReport.PeriodReportBuilder()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStepsMedian(median(steps))
                .setHoursToMoveMedian(median(hours))
                .setKilocalsMedian(median(kiloCals))
                .setLiquidLitersMedian(median(liters))
                .build();
    }

    public DayReport getDayReport(LocalDate currentDate) {
        UnfulfilledDayNormReport report = getUnfulfilledDayNormReport(currentDate);
        double stepsCompletionRate = 1.0 - (report.getStepsLeft() / minStepsPerDay);
        double hoursToMoveCompletionRate = 1.0 - (report.getHoursToMoveLeft() / minHoursOfMovementPerDay);
        double kilocalsCompletionRate = 1.0 - (report.getKiloCalsLeft() / minKilocalsPerDay);
        double liquidLitersCompletionRate = 1.0 - (report.getLiquidLitersLeft() / minLitersPerDay);
        return new DayReport(stepsCompletionRate,
                hoursToMoveCompletionRate,
                kilocalsCompletionRate,
                liquidLitersCompletionRate);
    }

    public UnfulfilledDayNormReport getUnfulfilledDayNormReport(LocalDate currentDate) {
        if (!records.containsKey(currentDate)) return new UnfulfilledDayNormReport(); // empty report
        double liquidLitersLeft = minLitersPerDay - calculate("drink", "liter", "all", currentDate);
        double kilocalsLeft = minKilocalsPerDay - calculate("food", "kilocal", "all", currentDate);
        double stepsLeft = minStepsPerDay - calculate("move", "step", "all", currentDate);
        double hoursToMoveLeft = minHoursOfMovementPerDay - calculate("move", "hour", "all", currentDate);
        liquidLitersLeft = (liquidLitersLeft < 0) ? 0 : liquidLitersLeft;
        kilocalsLeft = (kilocalsLeft < 0) ? 0 : kilocalsLeft;
        stepsLeft = (stepsLeft < 0) ? 0 : stepsLeft;
        hoursToMoveLeft = (hoursToMoveLeft < 0) ? 0 : hoursToMoveLeft;
        return new UnfulfilledDayNormReport(liquidLitersLeft, kilocalsLeft, stepsLeft, hoursToMoveLeft);
    }

    public void drink(String drinkName, String measureUnit, double quantity, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        records.putIfAbsent(date, new ArrayList<>());
        records.get(date).add(new Record("drink", drinkName, measureUnit, quantity, dateTime));
    }

    public double drunk(String measureUnit, LocalDate requestDate) {
        return calculate("drink", measureUnit, "all", requestDate);
    }

    public void eat(String foodName, String measureUnit, double quantity, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        records.putIfAbsent(date, new ArrayList<>());
        records.get(date).add(new Record("food", foodName, measureUnit, quantity, dateTime));
    }

    public double eaten(String meal, String measureUnit, LocalDate requestDate) {
        return calculate("food", measureUnit, meal, requestDate);
    }

    public double moved(String measureUnit, LocalDate requestDate) {
        return calculate("move", measureUnit, "all", requestDate);
    }

    public void move(String measureUnit, double quantity, LocalDateTime moveStart, LocalDateTime moveEnd) {
        if (moveStart.toLocalDate().isEqual(moveEnd.toLocalDate())) {
            LocalDate date = moveStart.toLocalDate();
            Duration duration = Duration.between(moveStart, moveEnd);
            records.putIfAbsent(date, new ArrayList<>());
            records.get(date).add(new Record("move", null, measureUnit, quantity, moveStart, duration));
        } else {
            throw new IllegalStateException(); // to be implemented once feature is requested
        }
    }
}
