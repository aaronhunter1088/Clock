package clock.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static clock.entity.Panel.PANEL_DIGITAL_CLOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link DigitalClockPanel} class
 *
 * @author michael ball
 * @version since 2.9
 */
public class DigitalClockPanelTest
{
    private static final Logger logger = LogManager.getLogger(DigitalClockPanelTest.class);

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting DigitalClockPanelTest...");
    }

    @BeforeEach
    void beforeEach() {}

    @AfterEach
    void afterEach() {}

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", DigitalClockPanelTest.class.getSimpleName()); }

    @Test
    @DisplayName("Test DigitalClockPanel Initialization")
    void testDigitalClockPanelInitialization()
    {
        DigitalClockPanel digitalClockPanel = new DigitalClockPanel(new ClockFrame());
        digitalClockPanel.getClockFrame().changePanels(PANEL_DIGITAL_CLOCK);
        assertEquals(PANEL_DIGITAL_CLOCK, digitalClockPanel.getClockFrame().getPanelType(), "Current panel should be " + PANEL_DIGITAL_CLOCK);
        assertNotNull(digitalClockPanel, "DigitalClockPanel should not be null");
        assertNotNull(digitalClockPanel.getGridBagConstraints(), "GridBagConstraints should not be null");
        assertNotNull(digitalClockPanel.getGridBagLayout(), "GridBagLayout should not be null");
        assertNotNull(digitalClockPanel.getClock(), "Clock should not be null");
    }
}
