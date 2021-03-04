package v5;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;

import static java.time.DayOfWeek.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeTest extends Object {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testTimeIsCreated() {
        v4.Time time = v4.Time.Time;
        assertNotNull(time);
    }

    @Test
    public void testValueIsReturned() {
        DayOfWeek monday = MONDAY;
        int value = monday.getValue();
        assertSame(1, value);
    }

    @Test
    public void testStringValueIsReturned() {
        DayOfWeek monday = MONDAY;
        String textOfMonday = monday.toString();
        assertEquals("Monday", textOfMonday);
    }
}