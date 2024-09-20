package org.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.IntStream;


import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static org.example.clock.ClockConstants.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    static { System.setProperty("appName", "ClockTest"); }
    private static final Logger logger = LogManager.getLogger(ClockTest.class);
    private Clock clock;

    @Before
    public void beforeEach() throws InvalidInputException {
        clock = new Clock();
    }

    @Test
    public void testBeginningDayLightSavingsTimeIsProperlySet() throws InvalidInputException {
        clock = new Clock(5, 42, 0, MARCH, THURSDAY, 2, 2021, PM);
        assertEquals("For 2021, Beginning DST Day should be 14th", 14, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth());
        assertEquals("For 2021, Ending DST Day should be 7th", 7, clock.getEndDaylightSavingsTimeDate().getDayOfMonth());

        clock = new Clock(5, 42, 0, MARCH, THURSDAY, 3, 2022, PM);
        assertEquals("For 2022, Beginning DST Day should be 13th", 13, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth());
        assertEquals("For 2022, Ending DST Day should be 6th", 6, clock.getEndDaylightSavingsTimeDate().getDayOfMonth());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsFalseWhenNotBeginningDST() throws InvalidInputException
    {
        clock = new Clock(5, 42, 0, MARCH, THURSDAY, 3, 2022, PM);
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
        clock = new Clock(5, 42, 0, NOVEMBER, SATURDAY, 5, 2022, PM);
        assertFalse(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsTrueWhenIsBeginningDST() throws InvalidInputException
    {
        clock = new Clock(5, 42, 0, MARCH, SUNDAY, 13, 2022, PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsTrueWhenIsEndingDST() throws InvalidInputException
    {
        clock = new Clock(5, 42, 0, NOVEMBER, SUNDAY, 6, 2022, PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsTodayDaylightSavingsDay() throws InvalidInputException
    {
        LocalDate today = LocalDate.now();
        clock = new Clock(12,0,0, today.getMonth(), today.getDayOfWeek(),
                today.getDayOfMonth(), today.getYear(), AM);
        boolean actual = clock.isDaylightSavingsTime();
        boolean expected = clock.getBeginDaylightSavingsTimeDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")).equals(today.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))) ||
                clock.getEndDaylightSavingsTimeDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")).equals(today.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        if (actual) {
            if (expected) logger.info("Today is DST day. This is expected.");
            else logger.error("Today should be DST but clock thinks it's not");
            assertTrue("Expected today to be DST day", expected);
        }
        else {
            if (!expected) logger.info("Today is not DST day. This is expected.");
            else logger.error("Today should not be DST but clock thinks it is");
            assertFalse("Expected today to not be DST", expected);
        }
    }

    @Test
    public void testClockBecomesAMWhenMidnightStarts() throws InvalidInputException
    {
        //      new Clock(int hours, int minutes, int seconds, Month month, Day day, int date, int year, AMPM ampm)
        clock = new Clock(11, 59, 57, FEBRUARY, SUNDAY, 21, 2021, PM);
        clock.setTestingClock(true);
        tick(3);
        assertEquals("Hours should be 12", 12, clock.getHours());
        assertEquals("Minutes should be 0", 0, clock.getMinutes());
        assertEquals("Seconds should be 0", 0, clock.getSeconds());
        assertEquals("Month should be February", FEBRUARY, clock.getMonth());
        assertEquals("Day should be Monday", MONDAY, clock.getDayOfWeek());
        assertEquals("Date should be 22", 22, clock.getDayOfMonth());
        assertEquals("Year should be 2021", 2021, clock.getYear());
        assertEquals("AMPM should be AM", AM, clock.getAMPM());
    }

    @Test
    public void testWhenClockInMilitaryTimeAlarmStillTriggers() throws InvalidInputException
    {
        clock = new Clock(1, 0, 59, MARCH, SATURDAY, 6, 2021, PM);
        clock.setShowMilitaryTime(true);
        Alarm alarm = new Alarm(1, 1, PM, false, new ArrayList<>(){{add(SATURDAY);}}, clock);
        clock.setListOfAlarms(new ArrayList<>(){{add(alarm);}});

        assertEquals(1, clock.getListOfAlarms().size());

        clock.tick();
        clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
        assertTrue(clock.getAlarmPanel().isAlarmIsGoingOff());
    }

    @Test
    public void testTurnOffDSTSetting() throws InvalidInputException {
        LocalDate endDSTDate = clock.getEndDaylightSavingsTimeDate();
        clock = new Clock(1, 59, 50, endDSTDate.getMonth(), endDSTDate.getDayOfWeek(), endDSTDate.getDayOfMonth(), endDSTDate.getYear(), AM);
        clock.getClockMenuBar().getToggleDSTSetting().doClick(); // DST turned off; enabled:false
        var expectedValue = Turn+SPACE+on+SPACE+DST_SETTING;
        assertEquals("Expected setting to be on", expectedValue, clock.getClockMenuBar().getToggleDSTSetting().getText());

        tick(10);

        assertEquals("Expected hours to be 2", 2, clock.getHours());
    }

    @Test
    public void testKeepDSTSettingOn() throws InvalidInputException {
        LocalDate endDSTDate = clock.getEndDaylightSavingsTimeDate();
        clock = new Clock(1, 59, 50, endDSTDate.getMonth(), endDSTDate.getDayOfWeek(), endDSTDate.getDayOfMonth(), endDSTDate.getYear(), AM);
        var expectedValue = Turn+SPACE+off+SPACE+DST_SETTING;
        assertEquals("Expected setting to be off", expectedValue, clock.getClockMenuBar().getToggleDSTSetting().getText());

        tick(10);

        assertEquals("Expected hours to be 1", 1, clock.getHours());
    }

    private void tick(int times) {
        for (int i=0; i<times; i++) {
            clock.tick();
        }
    }

}