package Clock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static java.time.DayOfWeek.*;

@RunWith(MockitoJUnitRunner.class)
public class ITimeTest extends Object {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testValueIsReturned() {
        DayOfWeek monday = MONDAY;
        int value = monday.getValue();
        assertSame(1, value);
    }

    @Test
    public void testStringValueIsReturned()
    {
        DayOfWeek monday = MONDAY;
        String textOfMonday = monday.toString();
        assertEquals(MONDAY.toString(), textOfMonday);
    }
}