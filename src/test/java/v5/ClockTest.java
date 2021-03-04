package v5;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Calendar;


import static org.junit.Assert.assertEquals;
import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static v5.Time.AMPM.*;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    private static Clock clock;

    @BeforeClass
    public static void setup() throws ParseException, InvalidInputException
    {
        //clock = new Clock(0, 00, 55, FEBRUARY, SUNDAY, 21, 2021, PM);
        clock = new Clock();
    }

    @Before
    public void beforeEachTest() throws InvalidInputException
    { }

    @Test
    public void testClockTicksAsExpected()
    {

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