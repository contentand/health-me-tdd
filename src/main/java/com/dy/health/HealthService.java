package com.dy.health;

import com.sun.org.apache.regexp.internal.RE;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        public Record(String type, String name, String container, double quantity, LocalDateTime dateTime) {
            this.type = type;
            this.name = name;
            this.container = container;
            this.quantity = quantity;
            this.dateTime = dateTime;
        }

        public Record(Record record) {
            this.type = record.type;
            this.name = record.name;
            this.container = record.container;
            this.quantity = record.quantity;
            this.dateTime = record.dateTime;
        }
    }

    public void drink(String drinkName, String container, double quantity, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        records.putIfAbsent(date, new ArrayList<>());
        records.get(date).add(new Record("drink", drinkName, container, quantity, dateTime));
    }

    public double drunk(String container, LocalDateTime requestDateTime) {
        LocalDate date = requestDateTime.toLocalDate();
        return calculate("drink", container, date);
    }

    private double calculate(String type, String container, LocalDate date) {
        Stream<Record> recordsForDate = records.get(date).stream();
        return recordsForDate
                .filter(record -> record.type.equals(type))
                .map(record -> {
                    if (record.container.equals(container)) {
                        return record.quantity;
                    } else {
                        throw new IllegalStateException(); // to be improved later
                    }
                })
                .reduce((quantity1, quantity2) -> quantity1 + quantity2)
                .orElse(0D);
    }



}
