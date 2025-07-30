package clock.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static clock.panel.Panel.*;
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
        Panel c1 = PANEL_DIGITAL_CLOCK;
        Panel c2 = PANEL_ALARM;
        assertNotSame(c1, c2);
    }

    @Test
    void testThatWeGetAllValues()
    {
        List<Panel> clockPanels = new ArrayList<>();
        for (Panel cf : Panel.values()) {
            clockPanels.add(cf);
        }
        // x should be hard coded for future tests
        assertTrue(Panel.values().length == clockPanels.size());
    }

    @Test
    void testToStringPrintsFaceName()
    {
        Panel cf = PANEL_DIGITAL_CLOCK;
        assertEquals("PANEL_DIGITAL_CLOCK", cf.toString(), "Printed the Name");
    }
}
