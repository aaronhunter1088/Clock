package v5;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.time.LocalDate;


import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static org.junit.Assert.*;
import static v5.Time.AMPM.*;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    private static Clock clock;

    @BeforeClass
    public static void setup() throws ParseException, InvalidInputException
    {
        clock = new Clock();
    }

    @Before
    public void beforeEachTest() throws InvalidInputException, ParseException
    {
        clock = new Clock();
    }

    @Test
    public void testBeginningDayLightSavingsTimeIsProperlySet() throws ParseException, InvalidInputException
    {
        clock.setDaylightSavingsTimeDates();
        assertEquals("For 2021, Beginning DST Day should be 14th", 14, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth());
        assertEquals("For 2021, Ending DST Day should be 7th", 7, clock.getEndDaylightSavingsTimeDate().getDayOfMonth());

        clock = new Clock(5, 42, 0, MARCH, THURSDAY, 3, 2022, PM);
        clock.setDaylightSavingsTimeDates();
        assertEquals("For 2022, Beginning DST Day should be 13th", 13, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth());
        assertEquals("For 2022, Ending DST Day should be 6th", 6, clock.getEndDaylightSavingsTimeDate().getDayOfMonth());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsFalseWhenNotBeginningDST()
    {
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
    public void testIsDateDaylightSavingsDayReturnsFalseWhenNotEndingDST() throws InvalidInputException, ParseException
    {
        clock = new Clock(5, 42, 0, NOVEMBER, SATURDAY, 5, 2022, PM);
        assertFalse(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsTrueWhenIsBeginningDST() throws InvalidInputException, ParseException
    {
        clock = new Clock(5, 42, 0, MARCH, SUNDAY, 13, 2022, PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testIsTodayDaylightSavingsDayReturnsTrueWhenIsEndingDST() throws InvalidInputException, ParseException
    {
        clock = new Clock(5, 42, 0, NOVEMBER, SUNDAY, 6, 2022, PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    public void testClockBecomesAMWhenMidnightStarts() throws ParseException, InvalidInputException
    {
        //(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int date, int year, Time.AMPM ampm)
        clock = new Clock(11, 59, 50, FEBRUARY, SUNDAY, 21, 2021, PM);
        for(int i = 10; i > 0; i--)
        {
            clock.tick();
        }
        assertEquals("Hours should be 12", 12, clock.getHours());
        assertEquals("Minutes should be 0", 0, clock.getMinutes());
        assertEquals("Seconds should be 0", 0, clock.getSeconds());
        assertEquals("Month should be February", FEBRUARY, clock.getMonth());
        assertEquals("Day should be Monday", MONDAY, clock.getDayOfWeek());
        assertEquals("Date should be 22", 22, clock.getDayOfMonth());
        assertEquals("Year should be 2021", 2021, clock.getYear());
        assertEquals("AMPM should be AM", Time.AMPM.AM, clock.getAMPM());
    }
}