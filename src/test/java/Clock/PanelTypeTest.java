package Clock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static Clock.PanelType.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PanelTypeTest {

	@Test
	public void testClockFacesAreDifferent()
	{
		PanelType c1 = DIGITAL_CLOCK;
		PanelType c2 = ALARM;
		assertTrue(c1 != c2);
	}

	@Test
	public void testThatWeGetAllValues() {
		List<PanelType> panelTypes = new ArrayList<>();
		for (PanelType cf : PanelType.values()) {
			panelTypes.add(cf);
		}
		// x should be hard coded for future tests
		assertTrue(PanelType.values().length == panelTypes.size());
	}

	@Test
	public void testToStringPrintsFaceName()
	{
		PanelType cf = DIGITAL_CLOCK;
		assertEquals("Printed the Name", "AlarmFace", cf.toString());
	}
}
