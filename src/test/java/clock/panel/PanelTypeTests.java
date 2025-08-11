package clock.panel;

import clock.entity.Panel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static clock.entity.Panel.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Panel} class
 *
 * @author Michael Ball
 * @version 2.9
 */
class PanelTypeTests
{
    private static final Logger logger = LogManager.getLogger(PanelTypeTests.class);

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting PanelTypeTests...");
    }

    @BeforeEach
    void beforeEach() {}

    @AfterEach
    void afterEach() {}

    @Test
    @DisplayName("Panel Types are Different")
    void testClockFacesAreDifferent()
    {
        assertNotSame(PANEL_DIGITAL_CLOCK, PANEL_ALARM);
    }

    @Test
    @DisplayName("Panel in List are All There")
    void testThatWeGetAllValues()
    {
        List<Panel> clockPanels = new ArrayList<>(Arrays.asList(values()));
        assertEquals(values().length, clockPanels.size());
    }

    @Test
    @DisplayName("Panel name and toString are the same")
    void testToStringPrintsFaceName()
    {
        assertEquals(PANEL_DIGITAL_CLOCK.name(), PANEL_DIGITAL_CLOCK.toString(), "Expected the name to be the same");
    }
}
