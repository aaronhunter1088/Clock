package org.example.clock;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;


import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    private static Clock clock;

    @BeforeEach
    public void beforeEach() throws InvalidInputException {
        clock = new Clock();
    }

    @Test
    public void testBeginningDayLightSavingsTimeIsProperlySet() throws InvalidInputException {
        clock = new Clock(5, 42, 0, MARCH, THURSDAY, 2, 2021, Time.PM);
        assertEquals("For 2021, Beginning DST Day should be 14th", 14, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth());
        assertEquals("For 2021, Ending DST Day should be 7th", 7, clock.getEndDaylightSavingsTimeDate().getDayOfMonth());

        clock = new Clock(5, 42, 0, MARCH, THURSDAY, 3, 2022, Time.PM);
        //clock.setDaylightSavingsTimeDates();
        assertEquals("For 2022, Beginning DST Day should be 13th", 13, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth());
        assertEquals("For 2022, Ending DST Day should be 6th", 6, clock.getEndDaylightSavingsTimeDate().getDayOfMonth());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsFalseWhenNotBeginningDST() throws InvalidInputException
    {
        clock = new Clock(5, 42, 0, MARCH, THURSDAY, 3, 2022, Time.PM);
        assertFalse(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testLocalDatesAreCompared()
    {
        LocalDate today = LocalDate.now();
        assertEquals("Dates should be equal", clock.getDate(), today);

        LocalDate anotherDay = LocalDate.of(2022, 8, 29);
        assertNotEquals("Date should not be equal", clock.getDate(), anotherDay);
    }

    @Test
    public void testIsDateDaylightSavingsDayReturnsFalseWhenNotEndingDST() throws InvalidInputException
    {
        clock = new Clock(5, 42, 0, NOVEMBER, SATURDAY, 5, 2022, Time.PM);
        assertFalse(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsTrueWhenIsBeginningDST() throws InvalidInputException
    {
        clock = new Clock(5, 42, 0, MARCH, SUNDAY, 13, 2022, Time.PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsTrueWhenIsEndingDST() throws InvalidInputException
    {
        clock = new Clock(5, 42, 0, NOVEMBER, SUNDAY, 6, 2022, Time.PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsGenericDayDaylightSavingsDay() throws InvalidInputException
    {
        clock = new Clock(12,0,0, LocalDate.now().getMonth(), LocalDate.now().getDayOfWeek(),
                LocalDate.now().getDayOfMonth(), LocalDate.now().getYear(), Time.AM);
        boolean isDayDaylightSavingsDay = clock.isDaylightSavingsTime();

    }

    @Test
    public void testClockBecomesAMWhenMidnightStarts() throws InvalidInputException
    {
        //(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int date, int year, Time.AMPM ampm)
        clock = new Clock(11, 59, 57, FEBRUARY, SUNDAY, 21, 2021, Time.PM);
        clock.testingClock = true;
        for(int i = 3; i > 0; i--) {
            clock.tick();
        }
        assertEquals("Hours should be 12", 12, clock.getHours());
        assertEquals("Minutes should be 0", 0, clock.getMinutes());
        assertEquals("Seconds should be 0", 0, clock.getSeconds());
        assertEquals("Month should be February", FEBRUARY, clock.getMonth());
        assertEquals("Day should be Monday", MONDAY, clock.getDayOfWeek());
        assertEquals("Date should be 22", 22, clock.getDayOfMonth());
        assertEquals("Year should be 2021", 2021, clock.getYear());
        assertEquals("AMPM should be AM", Time.AM, clock.getAMPM());
    }

    @Test
    public void testWhenClockInMilitaryTimeAlarmStillTriggers() throws InvalidInputException
    {
        clock = new Clock(1, 0, 59, MARCH, SATURDAY, 6, 2021, Time.PM);
        clock.setShowMilitaryTime(true);
        Alarm alarm = new Alarm(1, 1, Time.PM, false, new ArrayList<>(){{add(SATURDAY);}}, clock);
        clock.setListOfAlarms(new ArrayList<>(){{add(alarm);}});

        assertEquals(1, clock.getListOfAlarms().size());

        clock.tick();
        clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
        assertTrue(clock.getAlarmPanel().isAlarmIsGoingOff());
    }

    @Test
    public void testClockIncreasesByOneMinute(){

    }

}