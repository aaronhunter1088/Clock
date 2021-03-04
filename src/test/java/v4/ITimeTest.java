package v4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static v4.Time.Day.*;

@RunWith(MockitoJUnitRunner.class)
public class ITimeTest extends Object {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testValueIsReturned() {
        Time.Day monday = MONDAY;
        int value = monday.getValue();
        assertSame(1, value);
    }

    @Test
    public void testStringValueIsReturned() {
        Time.Day monday = MONDAY;
        String textOfMonday = monday.getStrValue();
        assertEquals(MONDAY.strValue, textOfMonday);
    }
}