package com.dy.health;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HealthServiceTest {

    private static final String ALL = "all";
    private static final String BREAKFAST = "breakfast";
    private static final String LUNCH = "lunch";
    private static final String WATER = "water";
    private static final String GLASS = "glass";
    private static final String KILO_CALORIE = "kilocal";
    private static final String STEP = "step";
    private static final String SANDWICH = "sandwich";
    private static final String PIZZA = "pizza";
    private static final String PELMENI = "pelmeni";
    private static HealthServiceSetup setup;
    private HealthService healthService;
    private LocalDate currentDate = LocalDate.parse("2016-09-28");
    private double precision = 0.00001;

    private static Map<String, TimeRange> getNamedTimeRanges() {
        Map<String, TimeRange> namedTimeRanges = new HashMap<>();
        namedTimeRanges.put(ALL, new TimeRange(LocalTime.of(0,0), LocalTime.of(0, 0)));
        namedTimeRanges.put(BREAKFAST, new TimeRange(LocalTime.of(2,0), LocalTime.of(12, 0)));
        namedTimeRanges.put(LUNCH, new TimeRange(LocalTime.of(12,0), LocalTime.of(17, 0)));
        return namedTimeRanges;
    }

    @BeforeClass
    public static void globalSetup() {
        setup = new HealthServiceSetup()
                .setNamedTimeRanges(getNamedTimeRanges())
                .setMinHoursOfMovementPerDay(2)
                .setMinStepsPerDay(2000)
                .setMinKilocalsPerDay(1300)
                .setMinLitersPerDay(2);
    }

    @Before
    public void setUp() throws Exception {
        this.healthService = new HealthService(setup);
    }

    @Test
    public void canDrinkWaterInGlasses() throws Exception {
        // given
        double quantityDrunk = 1;
        // when
        drink(quantityDrunk, GLASS, WATER, "2016-09-28T08:00:00");
        // then
        double actualQuantityDrunk = healthService.drunk(GLASS, currentDate);
        assertEquals(quantityDrunk, actualQuantityDrunk, precision);
    }

    @Test
    public void canHaveBreakfast() throws Exception {
        //given
        double quantityEaten = 200;
        String breakfastDateTime = "2016-09-28T08:30:00";
        String lunchDateTime = "2016-09-28T13:30:00";
        String dinnerDateTime = "2016-09-28T19:30:00";
        String supperDateTime = "2016-09-28T22:30:00";
        //when
        eat(quantityEaten, KILO_CALORIE, null, breakfastDateTime);
        eat(quantityEaten, KILO_CALORIE, null, lunchDateTime);
        eat(quantityEaten, KILO_CALORIE, null, dinnerDateTime);
        eat(quantityEaten, KILO_CALORIE, null, supperDateTime);
        //then
        double actualBreakfastEaten = healthService.eaten(BREAKFAST, KILO_CALORIE, currentDate);
        assertEquals(quantityEaten, actualBreakfastEaten, precision);
    }

    @Test
    public void canDrinkSeveralGlassesOfWaterAtDifferentTimes() throws Exception {
        // given
        double quantityDrunk = 1;
        // when
        drink(quantityDrunk, GLASS, WATER, "2016-09-28T08:00:00");
        drink(quantityDrunk, GLASS, WATER, "2016-09-28T10:00:00");
        // then
        double actualQuantityDrunk = healthService.drunk(GLASS, currentDate);
        assertEquals(quantityDrunk * 2, actualQuantityDrunk, precision);
    }

    @Test
    public void canHaveLunch() throws Exception {
        //given
        double quantityEaten = 200;
        double lunchQuantityEaten = 1403;
        String breakfastDateTime = "2016-09-28T08:30:00";
        String lunchDateTime = "2016-09-28T13:30:00";
        String dinnerDateTime = "2016-09-28T19:30:00";
        String supperDateTime = "2016-09-28T22:30:00";
        //when
        eat(quantityEaten, KILO_CALORIE, null, breakfastDateTime);
        eat(lunchQuantityEaten, KILO_CALORIE, null, lunchDateTime);
        eat(quantityEaten, KILO_CALORIE, null, dinnerDateTime);
        eat(quantityEaten, KILO_CALORIE, null, supperDateTime);
        //then
        double actualBreakfastEaten = healthService.eaten(LUNCH, KILO_CALORIE, currentDate);
        assertEquals(lunchQuantityEaten, actualBreakfastEaten, precision);
    }

    @Test
    public void canCountSteps() throws Exception {
        //given
        String moveStart = "2016-09-28T09:30:00";
        String moveEnd = "2016-09-28T11:30:00";
        double quantity = 1234;
        //when
        move(quantity, STEP, moveStart, moveEnd);
        //then
        double actualMoved = healthService.moved(STEP, currentDate);
        assertEquals(quantity, actualMoved, precision);
    }

    @Test
    public void canReportHowMuchIsLeftForTheDay() throws Exception {
        // given
        // when
        drink(1, GLASS, WATER, "2016-09-28T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-28T08:30:00");
        move(300, STEP, "2016-09-28T09:30:00", "2016-09-28T10:00:00");
        drink(2, GLASS, WATER, "2016-09-28T10:14:00");
        move(1000, STEP, "2016-09-28T11:30:00", "2016-09-28T12:00:00");
        drink(2, GLASS, WATER, "2016-09-28T13:14:00");
        move(500, STEP, "2016-09-28T13:30:00", "2016-09-28T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-28T14:30:00");
        drink(3, GLASS, WATER, "2016-09-28T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-28T19:30:00");
        // then
        UnfulfilledDayNormReport unfulfilledDayNormReport = healthService.getUnfulfilledDayNormReport(currentDate);
        assertEquals(200, unfulfilledDayNormReport.getStepsLeft(), precision); // 2000
        assertEquals(0.5, unfulfilledDayNormReport.getHoursToMoveLeft(), precision); // 2
        assertEquals(0, unfulfilledDayNormReport.getKiloCalsLeft(), precision); //1300
        assertEquals(0, unfulfilledDayNormReport.getLiquidLitersLeft(), precision); // 2000
    }

    @Test
    public void canReportDayStatistics() throws Exception {
        // given
        // when
        drink(1, GLASS, WATER, "2016-09-28T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-28T08:30:00");
        move(300, STEP, "2016-09-28T09:30:00", "2016-09-28T10:00:00");
        drink(2, GLASS, WATER, "2016-09-28T10:14:00");
        move(1000, STEP, "2016-09-28T11:30:00", "2016-09-28T12:00:00");
        drink(2, GLASS, WATER, "2016-09-28T13:14:00");
        move(500, STEP, "2016-09-28T13:30:00", "2016-09-28T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-28T14:30:00");
        drink(3, GLASS, WATER, "2016-09-28T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-28T19:30:00");
        // then
        DayReport dayReport = healthService.getDayReport(currentDate);
        assertEquals(0.9, dayReport.getStepsCompletionRate(), precision);
        assertEquals(0.75, dayReport.getHoursToMoveCompletionRate(), precision);
        assertEquals(1.0, dayReport.getKiloCalsCompletionRate(), precision);
        assertEquals(1.0, dayReport.getLiquidLitersCompletionRate(), precision);
    }

    @Test
    public void canReportStatisticsForFourDayPeriod() throws Exception {
        // given
        String firstDate = "2016-09-25";
        // when
        // day 1 : 3 days ago
        drink(1, GLASS, WATER, "2016-09-25T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-25T08:30:00");
        move(300, STEP, "2016-09-25T09:30:00", "2016-09-25T10:00:00");
        drink(2, GLASS, WATER, "2016-09-25T10:14:00");
        move(1000, STEP, "2016-09-25T11:30:00", "2016-09-25T12:00:00");
        drink(2, GLASS, WATER, "2016-09-25T13:14:00");
        move(500, STEP, "2016-09-25T13:30:00", "2016-09-25T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-25T14:30:00");
        drink(3, GLASS, WATER, "2016-09-25T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-25T19:30:00");
        // day 2 : 2 days ago
            // nothing happened
        // day 3 : 1 day ago
        drink(1, GLASS, WATER, "2016-09-27T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-27T08:30:00");
        move(300, STEP, "2016-09-27T09:30:00", "2016-09-27T10:00:00");
        drink(2, GLASS, WATER, "2016-09-27T10:14:00");
        move(1000, STEP, "2016-09-27T11:30:00", "2016-09-27T12:00:00");
        move(500, STEP, "2016-09-27T13:30:00", "2016-09-27T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-27T14:30:00");
        drink(3, GLASS, WATER, "2016-09-27T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-27T19:30:00");
        // day 4 : today
        drink(1, GLASS, WATER, "2016-09-28T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-28T08:30:00");
        drink(2, GLASS, WATER, "2016-09-28T10:14:00");
        move(1000, STEP, "2016-09-28T11:30:00", "2016-09-28T12:00:00");
        drink(2, GLASS, WATER, "2016-09-28T13:14:00");
        move(500, STEP, "2016-09-28T13:30:00", "2016-09-28T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-28T14:30:00");
        drink(3, GLASS, WATER, "2016-09-28T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-28T19:30:00");
        // then
        PeriodReport periodReport = healthService.getPeriodReport(LocalDate.parse(firstDate),
                currentDate);
        assertEquals(1650, periodReport.getStepsMedian(), precision);
        assertEquals(1.25, periodReport.getHoursMovedMedian(), precision);
        assertEquals(1412, periodReport.getKilocalsMedian(), precision);
        assertEquals(1.75, periodReport.getLiquidLitersMedian(), precision);
    }

    @Test
    public void canReportStatisticsForFiveDayPeriod() throws Exception {
        // given
        String firstDate = "2016-09-24";
        // when
        // day 1 : 4 days ago
            // nothing happened
        // day 2 : 3 days ago
        drink(1, GLASS, WATER, "2016-09-25T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-25T08:30:00");
        move(300, STEP, "2016-09-25T09:30:00", "2016-09-25T10:00:00");
        drink(2, GLASS, WATER, "2016-09-25T10:14:00");
        move(1000, STEP, "2016-09-25T11:30:00", "2016-09-25T12:00:00");
        drink(2, GLASS, WATER, "2016-09-25T13:14:00");
        move(500, STEP, "2016-09-25T13:30:00", "2016-09-25T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-25T14:30:00");
        drink(3, GLASS, WATER, "2016-09-25T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-25T19:30:00");
        // day 3 : 2 days ago
        // nothing happened
        // day 4 : 1 day ago
        drink(1, GLASS, WATER, "2016-09-27T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-27T08:30:00");
        move(300, STEP, "2016-09-27T09:30:00", "2016-09-27T10:00:00");
        drink(2, GLASS, WATER, "2016-09-27T10:14:00");
        move(1000, STEP, "2016-09-27T11:30:00", "2016-09-27T12:00:00");
        move(500, STEP, "2016-09-27T13:30:00", "2016-09-27T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-27T14:30:00");
        drink(3, GLASS, WATER, "2016-09-27T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-27T19:30:00");
        // day 5 : today
        drink(1, GLASS, WATER, "2016-09-28T08:14:00");
        eat(204, KILO_CALORIE, SANDWICH, "2016-09-28T08:30:00");
        drink(2, GLASS, WATER, "2016-09-28T10:14:00");
        move(1000, STEP, "2016-09-28T11:30:00", "2016-09-28T12:00:00");
        drink(2, GLASS, WATER, "2016-09-28T13:14:00");
        move(500, STEP, "2016-09-28T13:30:00", "2016-09-28T14:00:00");
        eat(504, KILO_CALORIE, PIZZA, "2016-09-28T14:30:00");
        drink(3, GLASS, WATER, "2016-09-28T14:44:00");
        eat(704, KILO_CALORIE, PELMENI, "2016-09-28T19:30:00");
        // then
        PeriodReport periodReport = healthService.getPeriodReport(LocalDate.parse(firstDate),
                currentDate);
        assertEquals(1500, periodReport.getStepsMedian(), precision);
        assertEquals(1.0, periodReport.getHoursMovedMedian(), precision);
        assertEquals(1412, periodReport.getKilocalsMedian(), precision);
        assertEquals(1.5, periodReport.getLiquidLitersMedian(), precision);
    }

    private void move(double quantity, String measureUnit, String startTime, String endTime) {
        healthService.move(measureUnit, quantity,
                LocalDateTime.parse(startTime),
                LocalDateTime.parse(endTime));
    }

    private void eat(double quantity, String measureUnit, String food, String time) {
        healthService.eat(food, measureUnit, quantity, LocalDateTime.parse(time));
    }

    private void drink(double quantity, String measureUnit, String drink, String time) {
        healthService.drink(drink, measureUnit, quantity, LocalDateTime.parse(time));
    }
}