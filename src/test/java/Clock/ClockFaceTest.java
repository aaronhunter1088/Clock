package Clock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static Clock.ClockFace.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ClockFaceTest {

	@Test
	public void testClockFacesAreDifferent()
	{
		ClockFace c1 = CLOCKPANEL;
		ClockFace c2 = ALARMPANEL;
		assertTrue(c1 != c2);
	}

	@Test
	public void testThatWeGetAllValues() {
		List<ClockFace> clockFaces = new ArrayList<>();
		for (ClockFace cf : ClockFace.values()) {
			clockFaces.add(cf);
		}
		// x should be hard coded for future tests
		assertTrue(ClockFace.values().length == clockFaces.size());
	}

	@Test
	public void testToStringPrintsFaceName()
	{
		ClockFace cf = CLOCKPANEL;
		assertEquals("Printed the Name", "AlarmFace", cf.toString());
	}
}
