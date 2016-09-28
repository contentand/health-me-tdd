package com.dy.health;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.*;
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
}