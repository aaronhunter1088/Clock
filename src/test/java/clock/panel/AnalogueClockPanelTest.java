package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.*;

import static clock.entity.Panel.PANEL_ALARM;
import static clock.entity.Panel.PANEL_ANALOGUE_CLOCK;
import static clock.util.Constants.AM;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link AnalogueClockPanel} class
 *
 * @author Michael Ball
 * @version 2.9
 */
public class AnalogueClockPanelTest
{
    private static final Logger logger = LogManager.getLogger(AnalogueClockPanelTest.class);

    Clock clock;

    AnalogueClockPanel analogueClockPanel;

    @Mock
    Graphics g;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting AnalogueClockPanelTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        clock = new Clock(11, 30, 0, JANUARY, WEDNESDAY, 1, 2025, AM); // 11:30 AM
        analogueClockPanel = new AnalogueClockPanel(new ClockFrame(clock));
        analogueClockPanel.getClockFrame().changePanels(PANEL_ANALOGUE_CLOCK);
    }

    @AfterEach
    void afterEach()
    {}

    @Test
    @DisplayName("Test AnalogueClockPanel Initialization")
    void testAnalogueClockPanelInitialization()
    {
        Assertions.assertNotNull(analogueClockPanel, "AnalogueClockPanel should not be null");
        Assertions.assertEquals(PANEL_ANALOGUE_CLOCK, analogueClockPanel.getClockFrame().getPanelType(), "Current panel should be ANALOGUE_CLOCK");
        Assertions.assertEquals(clock, analogueClockPanel.getClock(), "Clock should match the initialized clock");
        Assertions.assertTrue(analogueClockPanel.isShowDigitalTimeOnAnalogueClock(), "Digital time should be shown on analogue clock by default");

        doNothing().when(g).setFont(any());
        doNothing().doNothing().doNothing().when(g).setColor(any());
        doNothing().when(g).fillRect(anyInt(), anyInt(), anyInt(), anyInt());

        assertDoesNotThrow(() -> {
            analogueClockPanel.paint(g);
        }, "Painting the AnalogueClockPanel should not throw an exception");
    }

    @Test
    @DisplayName("Test AnalogueClockPanel Initialization with Show Digital Time Off")
    void testAnalogueClockPanelInitializationShowDigitalTimeOff()
    {
        analogueClockPanel.setShowDigitalTimeOnAnalogueClock(false);
        Assertions.assertNotNull(analogueClockPanel, "AnalogueClockPanel should not be null");
        Assertions.assertEquals(PANEL_ANALOGUE_CLOCK, analogueClockPanel.getClockFrame().getPanelType(), "Current panel should be ANALOGUE_CLOCK");
        Assertions.assertEquals(clock, analogueClockPanel.getClock(), "Clock should match the initialized clock");
        Assertions.assertFalse(analogueClockPanel.isShowDigitalTimeOnAnalogueClock(), "Digital time should not be shown on analogue clock");

        doNothing().when(g).setFont(any());
        doNothing().doNothing().doNothing().when(g).setColor(any());
        doNothing().when(g).fillRect(anyInt(), anyInt(), anyInt(), anyInt());

        assertDoesNotThrow(() -> {
            analogueClockPanel.paint(g);
        }, "Painting the AnalogueClockPanel should not throw an exception");
    }

    @Test
    @DisplayName("Test Stopping the AnalogueClockPanel Thread")
    void testStopAnalogueClockPanelThread()
    {
        analogueClockPanel.stop();
        assertNull(analogueClockPanel.getThread(), "Thread should be null after stopping");
    }
}
