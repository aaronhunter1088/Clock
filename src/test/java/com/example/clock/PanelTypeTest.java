package com.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.example.clock.ClockPanel.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PanelTypeTest
{
	static { System.setProperty("appName", PanelTypeTest.class.getSimpleName()); }
	private static final Logger logger = LogManager.getLogger(PanelTypeTest.class);

	@BeforeClass
	public static void beforeClass()
	{
		logger.info("Starting PanelTypeTest...");
	}

	@Before
	public void beforeEach() {}

	@After
	public void afterEach() {}

	@Test
	public void testClockFacesAreDifferent()
	{
		ClockPanel c1 = PANEL_DIGITAL_CLOCK;
		ClockPanel c2 = PANEL_ALARM;
		assertTrue(c1 != c2);
	}

	@Test
	public void testThatWeGetAllValues()
	{
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
