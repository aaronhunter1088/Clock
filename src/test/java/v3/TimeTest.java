package v3;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeTest extends Object {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testTimeIsCreated() {
        Time time = Time.Time;
        assertNotNull(time);
    }

    @Test
    public void testValueIsReturned() {
        Time.Day monday = Time.Day.MONDAY;
        int value = monday.getValue();
        assertSame(1, value);
    }

    @Test
    public void testStringValueIsReturned() {
        Time.Day monday = Time.Day.MONDAY;
        String textOfMonday = monday.getStrValue();
        assertEquals("Monday", textOfMonday);
    }
}