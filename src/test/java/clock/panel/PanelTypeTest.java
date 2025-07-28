package clock.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static clock.panel.ClockPanel.*;
import static org.junit.jupiter.api.Assertions.*;

class PanelTypeTest
{
    private static final Logger logger = LogManager.getLogger(PanelTypeTest.class);

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting PanelTypeTest...");
    }

    @BeforeEach
    void beforeEach() {}

    @AfterEach
    void afterEach() {}

    @Test
    void testClockFacesAreDifferent()
    {
        ClockPanel c1 = PANEL_DIGITAL_CLOCK;
        ClockPanel c2 = PANEL_ALARM;
        assertNotSame(c1, c2);
    }

    @Test
    void testThatWeGetAllValues()
    {
        List<ClockPanel> clockPanels = new ArrayList<>();
        for (ClockPanel cf : ClockPanel.values()) {
            clockPanels.add(cf);
        }
        // x should be hard coded for future tests
        assertTrue(ClockPanel.values().length == clockPanels.size());
    }

    @Test
    void testToStringPrintsFaceName()
    {
        ClockPanel cf = PANEL_DIGITAL_CLOCK;
        assertEquals("PANEL_DIGITAL_CLOCK", cf.toString(), "Printed the Name");
    }
}
