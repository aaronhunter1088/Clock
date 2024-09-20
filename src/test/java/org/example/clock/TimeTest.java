package org.example.clock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.time.DayOfWeek.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testExpectedIntegerValueForDayOfWeekIsAsExpected() {
        Stream.of(DayOfWeek.values()).forEach(day -> {
            var intValue = day.getValue();
            var testDay = DayOfWeek.of(intValue);
            assertEquals(day, testDay);
        });
    }

    @Test
    public void testStringValueIsReturned() {
        Stream.of(DayOfWeek.values()).forEach(day -> {
            var strValue = day.toString();
            assertEquals(strValue, day.name());
        });
    }
}