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
}