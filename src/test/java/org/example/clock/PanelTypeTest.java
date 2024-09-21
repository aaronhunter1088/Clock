package org.example.clock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.example.clock.ClockPanel.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PanelTypeTest {

	@Test
	public void testClockFacesAreDifferent()
	{
		ClockPanel c1 = PANEL_DIGITAL_CLOCK;
		ClockPanel c2 = PANEL_ALARM;
		assertTrue(c1 != c2);
	}

	@Test
	public void testThatWeGetAllValues() {
		List<ClockPanel> clockPanels = new ArrayList<>();
		for (ClockPanel cf : ClockPanel.values()) {
			clockPanels.add(cf);
		}
		// x should be hard coded for future tests
		assertTrue(ClockPanel.values().length == clockPanels.size());
	}

	@Test
	public void testToStringPrintsFaceName()
	{
		ClockPanel cf = PANEL_DIGITAL_CLOCK;
		assertEquals("Printed the Name", "PANEL_DIGITAL_CLOCK", cf.toString());
	}
}
