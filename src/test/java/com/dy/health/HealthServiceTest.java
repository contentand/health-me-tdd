package com.dy.health;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class HealthServiceTest {

    private HealthService healthService;

    @Before
    public void setUp() throws Exception {
        this.healthService = new HealthService();
    }

    @Test
    public void canDrinkWaterInGlasses() throws Exception {
        // given
        String beverage = "water";
        String container = "glass";
        double quantityDrunk = 1;
        String dateTime = "2016-09-28T08:00:00";
        String currentDateTime = "2016-09-28T23:59:59";
        // when
        healthService.drink(beverage, container, quantityDrunk, LocalDateTime.parse(dateTime));
        // then
        double precision = 0.00001;
        double actualQuantityDrunk = healthService.drunk(container, LocalDateTime.parse(currentDateTime));
        assertEquals(quantityDrunk, actualQuantityDrunk, precision);
    }

    @Test
    public void canHaveBreakfast() throws Exception {
        //given
        String food = null;
        String container = "kilocal";
        double quantityEaten = 200;
        String breakfastDateTime = "2016-09-28T08:30:00";
        String lunchDateTime = "2016-09-28T13:30:00";
        String dinnerDateTime = "2016-09-28T19:30:00";
        String supperDateTime = "2016-09-28T22:30:00";
        String currentDateTime = "2016-09-28T23:59:59";
        //when
        healthService.eat(food, container, quantityEaten, LocalDateTime.parse(breakfastDateTime));
        healthService.eat(food, container, quantityEaten, LocalDateTime.parse(lunchDateTime));
        healthService.eat(food, container, quantityEaten, LocalDateTime.parse(dinnerDateTime));
        healthService.eat(food, container, quantityEaten, LocalDateTime.parse(supperDateTime));
        //then
        String meal = "breakfast";
        double precision = 0.00001;
        double actualBreakfastEaten = healthService.eaten(meal, container, LocalDateTime.parse(currentDateTime));
        assertEquals(quantityEaten, actualBreakfastEaten, precision);
    }

    @Test
    public void canDrinkSeveralGlassesOfWaterAtDifferentTimes() throws Exception {
        // given
        String beverage = "water";
        String container = "glass";
        double quantityDrunk = 1;
        String dateTime1 = "2016-09-28T08:00:00";
        String dateTime2 = "2016-09-28T10:00:00";
        String currentDateTime = "2016-09-28T23:59:59";
        // when
        healthService.drink(beverage, container, quantityDrunk, LocalDateTime.parse(dateTime1));
        healthService.drink(beverage, container, quantityDrunk, LocalDateTime.parse(dateTime2));
        // then
        double precision = 0.00001;
        double actualQuantityDrunk = healthService.drunk(container, LocalDateTime.parse(currentDateTime));
        assertEquals(quantityDrunk * 2, actualQuantityDrunk, precision);
    }

    @Test
    public void canHaveLunch() throws Exception {
        //given
        String food = null;
        String container = "kilocal";
        double quantityEaten = 200;
        double lunchQuantityEaten = 1403;
        String breakfastDateTime = "2016-09-28T08:30:00";
        String lunchDateTime = "2016-09-28T13:30:00";
        String dinnerDateTime = "2016-09-28T19:30:00";
        String supperDateTime = "2016-09-28T22:30:00";
        String currentDateTime = "2016-09-28T23:59:59";
        //when
        healthService.eat(food, container, quantityEaten, LocalDateTime.parse(breakfastDateTime));
        healthService.eat(food, container, lunchQuantityEaten, LocalDateTime.parse(lunchDateTime));
        healthService.eat(food, container, quantityEaten, LocalDateTime.parse(dinnerDateTime));
        healthService.eat(food, container, quantityEaten, LocalDateTime.parse(supperDateTime));
        //then
        String meal = "lunch";
        double precision = 0.00001;
        double actualBreakfastEaten = healthService.eaten(meal, container, LocalDateTime.parse(currentDateTime));
        assertEquals(lunchQuantityEaten, actualBreakfastEaten, precision);
    }

    @Test
    public void canCountSteps() throws Exception {
        //given
        String moveStart = "2016-09-28T09:30:00";
        String moveEnd = "2016-09-28T11:30:00";
        String container = "step";
        double quantity = 1234;
        String currentDateTime = "2016-09-28T23:59:59";
        //when
        healthService.move(container, quantity, LocalDateTime.parse(moveStart),LocalDateTime.parse(moveEnd));
        //then
        double precision = 0.00001;
        double actualMoved = healthService.moved(container, LocalDateTime.parse(currentDateTime));
        assertEquals(quantity, actualMoved, precision);
    }

    @Test
    public void canReportHowMuchIsLeftForTheDay() throws Exception {
        // given
        String currentDateTime = "2016-09-28T23:59:59";
        // when
        healthService.drink("water", "glass", 1, LocalDateTime.parse("2016-09-28T08:14:00"));
        healthService.eat("sandwich", "kilocal", 204, LocalDateTime.parse("2016-09-28T08:30:00"));
        healthService.move("step", 300,
                LocalDateTime.parse("2016-09-28T09:30:00"),
                LocalDateTime.parse("2016-09-28T10:00:00"));
        healthService.drink("water", "glass", 2, LocalDateTime.parse("2016-09-28T10:14:00"));
        healthService.move("step", 1000,
                LocalDateTime.parse("2016-09-28T11:30:00"),
                LocalDateTime.parse("2016-09-28T12:00:00"));
        healthService.drink("water", "glass", 2, LocalDateTime.parse("2016-09-28T13:14:00"));
        healthService.move("step", 500,
                LocalDateTime.parse("2016-09-28T13:30:00"),
                LocalDateTime.parse("2016-09-28T14:00:00"));
        healthService.eat("pizza", "kilocal", 504, LocalDateTime.parse("2016-09-28T14:30:00"));
        healthService.drink("water", "glass", 3, LocalDateTime.parse("2016-09-28T14:44:00"));
        healthService.eat("pelmeni", "kilocal", 704, LocalDateTime.parse("2016-09-28T19:30:00"));
        // then
        double precision = 0.00001;
        ReportLeft reportLeft = healthService.reportLeft(LocalDateTime.parse(currentDateTime));
        assertEquals(200, reportLeft.getStepsLeft(), precision); // 2000
        assertEquals(0.5, reportLeft.getHoursToMoveLeft(), precision); // 2
        assertEquals(0, reportLeft.getKiloCalsLeft(), precision); //1300
        assertEquals(0, reportLeft.getLiquidLitersLeft(), precision); // 2000
    }
}