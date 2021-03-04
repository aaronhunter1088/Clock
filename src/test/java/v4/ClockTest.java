package v4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    private static Clock clock;

    @BeforeClass
    public static void setup() throws ParseException, InvalidInputException
    {
        clock = new Clock(0, 00, 55, Time.Month.FEBRUARY, Time.Day.SUNDAY, 21, 2021, Time.AMPM.PM);

    }

    @Before
    public void beforeEachTest() throws ParseException, InvalidInputException
    {
        clock.setCalendar(Calendar.getInstance());
        clock.getCalendar().set(Calendar.MONTH, clock.convertTimeMonthToInt(clock.getMonth())-1);
        clock.getCalendar().set(Calendar.DATE, clock.getDate());
        clock.getCalendar().set(Calendar.YEAR, clock.getYear());
        clock.getCalendar().set(Calendar.HOUR, clock.getHours());
        clock.getCalendar().set(Calendar.MINUTE, clock.getMinutes());
        clock.getCalendar().set(Calendar.SECOND, clock.getSeconds());
        clock.getCalendar().set(Calendar.AM_PM, clock.convertTimeAMPMToInt(clock.getAMPM()));
        clock.getCalendar().set(Calendar.MILLISECOND,0);
        clock.getCalendar().setTime(clock.getCalendar().getTime());
        System.out.println("Calendar date: " + clock.getCalendar().getTime());
    }

    @Test
    public void testClocksCalendarIsSetupProperly()
    {
        assertEquals("Hours should be 0", 0, clock.getCalendar().get(Calendar.HOUR));
        assertEquals("Minutes should be 0", 0, clock.getMinutes());
        assertEquals("Seconds should be 55", 55, clock.getSeconds());
        assertEquals("Hours String should be 00", "00", clock.getHoursAsStr());
        assertEquals("Minutes String should be 00", "00", clock.getMinutesAsStr());
        assertEquals("Seconds String should be 55", "55", clock.getSecondsAsStr());
        assertEquals("Month should be February", Time.Month.FEBRUARY, clock.getMonth());
        assertEquals("Day should be Sunday", Time.Day.SUNDAY, clock.getDay());
        assertEquals("Date should be 21", 21, clock.getDate());
        assertEquals("Year should be 2021", 2021, clock.getYear());
        assertEquals("AMPM should be PM", Time.AMPM.PM, clock.getAMPM());
    }

    @Test
    public void testClockBecomesAMWhenMidnightStarts() throws ParseException, InvalidInputException
    {
        //(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int date, int year, Time.AMPM ampm)
        clock = new Clock(11, 59, 50, Time.Month.FEBRUARY, Time.Day.SUNDAY, 21, 2021, Time.AMPM.PM);
        System.out.println("Date: " + clock.getCalendar().getTime());
        for(int i = 10; i > 0; i--)
        {
            clock.tick();
        }
        assertEquals("Hours should be 12", 12, clock.getHours());
        assertEquals("Minutes should be 0", 0, clock.getMinutes());
        assertEquals("Seconds should be 0", 0, clock.getSeconds());
        assertEquals("Month should be February", Time.Month.FEBRUARY, clock.getMonth());
        assertEquals("Day should be Monday", Time.Day.MONDAY, clock.getDay());
        assertEquals("Date should be 22", 22, clock.getDate());
        assertEquals("Year should be 2021", 2021, clock.getYear());
        assertEquals("AMPM should be AM", Time.AMPM.AM, clock.getAMPM());
    }
}