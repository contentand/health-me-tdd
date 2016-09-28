package com.dy.health;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

public class HealthService {

    private final Map<LocalDate, List<Record>> records = new HashMap<>();
    private final Map<String, TimeRange> namedTimeRanges = getNamedTimeRanges();
    private final double MIN_STEPS_PER_DAY = 2000;
    private final double MIN_HOURS_OF_MOVEMENT_PER_DAY = 2;
    private final double MIN_KILOCALS_PER_DAY = 1300;
    private final double MIN_LITERS_PER_DAY = 2;

    private static class TimeRange {
        private final LocalTime start;
        private final LocalTime end;

        TimeRange(LocalTime startExclusive, LocalTime endInclusive) {
            this.start = startExclusive;
            this.end = endInclusive;
        }

        public boolean isWithinTimeRange(LocalTime time) {
            return time.isAfter(start) && (time.isBefore(end) || time.equals(end));
        }
    }

    private static class Record {
        final String type; // drink
        final String name;
        final String container;
        final double quantity;
        final LocalDateTime dateTime; // should it just be LocalTime?
        final Duration duration;

        public Record(String type, String name, String container,
                      double quantity, LocalDateTime dateTime,
                      Duration duration) {
            this.type = type;
            this.name = name;
            this.container = container;
            this.quantity = quantity;
            this.dateTime = dateTime;
            this.duration = duration;
        }

        public Record(String type, String name, String container,
                      double quantity, LocalDateTime dateTime) {
            this(type, name, container, quantity, dateTime, Duration.ZERO);
        }

        public Record(Record record) {
            this.type = record.type;
            this.name = record.name;
            this.container = record.container;
            this.quantity = record.quantity;
            this.dateTime = record.dateTime;
            this.duration = record.duration;
        }

        @Override
        public String toString() {
            return "Record{" +
                    "type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", container='" + container + '\'' +
                    ", quantity=" + quantity +
                    ", dateTime=" + dateTime +
                    '}';
        }
    }

    private double calculate(String type, String container, String timeRange, LocalDate date) {
        Stream<Record> recordsForDate = records.get(date).stream();
        return recordsForDate
                .filter(record -> record.type.equals(type))
                .map(record -> {
                    if (record.container.equals(container)) {
                        if (isWithinTimeRange(timeRange, record.dateTime.toLocalTime())) {
                            return record.quantity;
                        } else {
                            return 0D;
                        }
                    } else {
                        return getTransformedQuantity(record, container);
                    }
                })
                .reduce((quantity1, quantity2) -> quantity1 + quantity2)
                .orElse(0D);
    }

    private double getTransformedQuantity(Record record, String targetContainer) {
        if (targetContainer.equals("liter") && record.container.equals("glass")) {
            return record.quantity * 0.25;
        } else if (targetContainer.equals("hour") && record.type.equals("move")) {
            return record.duration.toMinutes() / 60.0;
        } else {
            throw new IllegalStateException("Unable to transform " + record +
                    " into " + targetContainer); // to be implemented once feature is requested
        }

    }

    private boolean isWithinTimeRange(String timeRangeName, LocalTime time) {
        TimeRange timeRange = namedTimeRanges.get(timeRangeName);
        return timeRange != null && timeRange.isWithinTimeRange(time);

    }

    private Map<String, TimeRange> getNamedTimeRanges() {
        Map<String, TimeRange> namedTimeRanges = new HashMap<>();
        namedTimeRanges.put("all", new TimeRange(LocalTime.of(0,0), LocalTime.of(23, 59)));
        namedTimeRanges.put("breakfast", new TimeRange(LocalTime.of(2,0), LocalTime.of(12, 0)));
        namedTimeRanges.put("lunch", new TimeRange(LocalTime.of(12,0), LocalTime.of(17, 0)));
        return namedTimeRanges;
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
        double stepsCompletionRate = 1.0 - (report.getStepsLeft() / MIN_STEPS_PER_DAY);
        double hoursToMoveCompletionRate = 1.0 - (report.getHoursToMoveLeft() / MIN_HOURS_OF_MOVEMENT_PER_DAY);
        double kilocalsCompletionRate = 1.0 - (report.getKiloCalsLeft() / MIN_KILOCALS_PER_DAY);
        double liquidLitersCompletionRate = 1.0 - (report.getLiquidLitersLeft() / MIN_LITERS_PER_DAY);
        return new DayReport(stepsCompletionRate,
                hoursToMoveCompletionRate,
                kilocalsCompletionRate,
                liquidLitersCompletionRate);
    }


    public UnfulfilledDayNormReport getUnfulfilledDayNormReport(LocalDate currentDate) {
        if (!records.containsKey(currentDate)) return new UnfulfilledDayNormReport(); // empty report
        List<Record> recordsForDay = records.get(currentDate);
        double liquidLitersLeft = MIN_LITERS_PER_DAY - calculate("drink", "liter", "all", currentDate);
        double kilocalsLeft = MIN_KILOCALS_PER_DAY - calculate("food", "kilocal", "all", currentDate);
        double stepsLeft = MIN_STEPS_PER_DAY - calculate("move", "step", "all", currentDate);
        double hoursToMoveLeft = MIN_HOURS_OF_MOVEMENT_PER_DAY - calculate("move", "hour", "all", currentDate);
        liquidLitersLeft = (liquidLitersLeft < 0) ? 0 : liquidLitersLeft;
        kilocalsLeft = (kilocalsLeft < 0) ? 0 : kilocalsLeft;
        stepsLeft = (stepsLeft < 0) ? 0 : stepsLeft;
        hoursToMoveLeft = (hoursToMoveLeft < 0) ? 0 : hoursToMoveLeft;
        return new UnfulfilledDayNormReport(liquidLitersLeft, kilocalsLeft, stepsLeft, hoursToMoveLeft);
    }

    public void drink(String drinkName, String container, double quantity, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        records.putIfAbsent(date, new ArrayList<>());
        records.get(date).add(new Record("drink", drinkName, container, quantity, dateTime));
    }

    public double drunk(String container, LocalDate requestDate) {
        return calculate("drink", container, "all", requestDate);
    }

    public void eat(String foodName, String container, double quantity, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        records.putIfAbsent(date, new ArrayList<>());
        records.get(date).add(new Record("food", foodName, container, quantity, dateTime));
    }

    public double eaten(String meal, String container, LocalDate requestDate) {
        return calculate("food", container, meal, requestDate);
    }

    public double moved(String container, LocalDate requestDate) {
        return calculate("move", container, "all", requestDate);
    }

    public void move(String container, double quantity, LocalDateTime moveStart, LocalDateTime moveEnd) {
        if (moveStart.toLocalDate().isEqual(moveEnd.toLocalDate())) {
            LocalDate date = moveStart.toLocalDate();
            Duration duration = Duration.between(moveStart, moveEnd);
            records.putIfAbsent(date, new ArrayList<>());
            records.get(date).add(new Record("move", null, container, quantity, moveStart, duration));
        } else {
            throw new IllegalStateException(); // to be implemented once feature is requested
        }
    }
}
