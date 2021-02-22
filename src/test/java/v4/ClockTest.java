package v4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    private Clock clock;

    @BeforeClass
    public static void setup() throws ParseException
    {

    }

    @Before
    public void beforeEachTest() throws ParseException
    {

    }

    @Test
    public void testClockBecomesAMWhenMidnightStarts() throws ParseException
    {
        //(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int date, int year, Time.AMPM ampm)
        clock = new Clock(11, 59, 50, Time.Month.FEBRUARY, Time.Day.SUNDAY, 21, 2021, Time.AMPM.PM);
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