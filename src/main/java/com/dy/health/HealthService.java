package com.dy.health;

import com.sun.org.apache.regexp.internal.RE;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class HealthService {

    private Map<LocalDate, List<Record>> records = new HashMap<>();


    private static class Record {
        String type; // drink
        String name;
        String container;
        double quantity;
        LocalDateTime dateTime; // should it just be LocalTime?
        Duration duration;

        public Record(String type, String name, String container, double quantity, LocalDateTime dateTime, Duration duration) {
            this.type = type;
            this.name = name;
            this.container = container;
            this.quantity = quantity;
            this.dateTime = dateTime;
            this.duration = duration;
        }

        public Record(String type, String name, String container, double quantity, LocalDateTime dateTime) {
            this(type, name, container, quantity, dateTime, Duration.ZERO);
        }

        public Record(Record record) {
            this.type = record.type;
            this.name = record.name;
            this.container = record.container;
            this.quantity = record.quantity;
            this.dateTime = record.dateTime;
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

    public void drink(String drinkName, String container, double quantity, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        records.putIfAbsent(date, new ArrayList<>());
        records.get(date).add(new Record("drink", drinkName, container, quantity, dateTime));
    }

    public double drunk(String container, LocalDateTime requestDateTime) {
        LocalDate date = requestDateTime.toLocalDate();
        return calculate("drink", container, "all", date);
    }

    public void eat(String foodName, String container, double quantity, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        records.putIfAbsent(date, new ArrayList<>());
        records.get(date).add(new Record("food", foodName, container, quantity, dateTime));
    }

    public double eaten(String meal, String container, LocalDateTime requestDateTime) {
        LocalDate date = requestDateTime.toLocalDate();
        return calculate("food", container, meal, date);
    }

    public double moved(String container, LocalDateTime requestDateTime) {
        LocalDate date = requestDateTime.toLocalDate();
        return calculate("move", container, "all", date);
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
                        System.out.println(container + " " + record.toString());
                        throw new IllegalStateException(); // to be improved later
                    }
                })
                .reduce((quantity1, quantity2) -> quantity1 + quantity2)
                .orElse(0D);
    }

    private boolean isWithinTimeRange(String timeRange, LocalTime localTime) {
        if (timeRange.equals("all")) {
            return true;
        } else if (timeRange.equals("breakfast")) {
            LocalTime breakfastStart = LocalTime.parse("02:00");
            LocalTime breakfastEnd = LocalTime.parse("12:00");
            return localTime.isAfter(breakfastStart) && localTime.isBefore(breakfastEnd);
        } else if (timeRange.equals("lunch")) {
            LocalTime lunchStart = LocalTime.parse("12:00");
            LocalTime lunchEnd = LocalTime.parse("17:00");
            return localTime.isAfter(lunchStart) && localTime.isBefore(lunchEnd);
        }
        return false;
    }


}
