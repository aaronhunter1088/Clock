package v2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ITimeTest extends Object {

    @Before
    public void setUp() throws Exception {
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
        assertEquals(Time.Day.MONDAY.strValue, textOfMonday);
    }
}