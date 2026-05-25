package clock.panel;

import clock.entity.Clock;
import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;

import static clock.entity.Panel.PANEL_STOPWATCH;
import static clock.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link DisplayTimePanel} class.
 * DisplayTimePanel renders the stopwatch elapsed time in either
 * digital or analogue format and manages its own repaint thread.
 *
 * @author michael ball
 * @version since 3.0.4
 */
@ExtendWith(MockitoExtension.class)
class DisplayTimePanelTest
{
    private static final Logger logger = LogManager.getLogger(DisplayTimePanelTest.class);

    private Clock clock;
    private StopwatchPanel stopwatchPanel;
    private DisplayTimePanel displayTimePanel;

    @Mock
    Graphics g;

    @Mock
    FontMetrics fontMetrics;

    @BeforeAll
    static void beforeAll()
    {
        logger.info("Starting DisplayTimePanelTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        Stopwatch.stopwatchCounter = 0L;
        stopwatchPanel = new StopwatchPanel(new ClockFrame(clock));
        stopwatchPanel.getClockFrame().changePanels(PANEL_STOPWATCH);
        displayTimePanel = stopwatchPanel.getDisplayTimePanel();
    }

    @AfterEach
    void afterEach()
    {
        displayTimePanel.stop();
        stopwatchPanel.getClockFrame().stop();
        stopwatchPanel.getClockFrame().dispose();
    }

    @AfterAll
    static void afterAll()
    {
        logger.info("Concluding DisplayTimePanelTest.");
    }

    // ───────────────────────────────────────────────────
    // Constructor / initial state
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("Panel background is BLACK after construction")
    void testConstructorSetsBlackBackground()
    {
        assertEquals(Color.BLACK, displayTimePanel.getBackground(),
                "Background should be BLACK");
    }

    @Test
    @DisplayName("Panel preferred size matches ClockFrame.analogueSize after construction")
    void testConstructorSetsPreferredSize()
    {
        assertEquals(ClockFrame.analogueSize, displayTimePanel.getPreferredSize(),
                "Preferred size should match ClockFrame.analogueSize");
    }

    @Test
    @DisplayName("showAnaloguePanel defaults to false")
    void testShowAnaloguePanelDefaultsFalse()
    {
        assertFalse(displayTimePanel.isShowAnaloguePanel(),
                "showAnaloguePanel should be false by default");
    }

    @Test
    @DisplayName("Thread is not running immediately after construction")
    void testThreadNotRunningAfterConstruction()
    {
        assertFalse(displayTimePanel.isRunning(),
                "Thread should not be running after construction");
    }

    @Test
    @DisplayName("Default clockText is not null")
    void testDefaultClockTextNotNull()
    {
        assertNotNull(displayTimePanel.getClockText(),
                "clockText should not be null after construction");
    }

    // ───────────────────────────────────────────────────
    // start / stop / resume / isRunning
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("start() makes isRunning return true")
    void testStartMakesIsRunningTrue()
    {
        displayTimePanel.start();
        assertTrue(displayTimePanel.isRunning(), "Thread should be running after start()");
    }

    @Test
    @DisplayName("stop() makes isRunning return false")
    void testStopMakesIsRunningFalse()
    {
        displayTimePanel.start();
        displayTimePanel.stop();
        assertFalse(displayTimePanel.isRunning(), "Thread should not be running after stop()");
    }

    @Test
    @DisplayName("resume() starts the thread")
    void testResumeMakesIsRunningTrue()
    {
        displayTimePanel.resume();
        assertTrue(displayTimePanel.isRunning(), "Thread should be running after resume()");
    }

    @Test
    @DisplayName("Calling start() twice does not create a second thread")
    void testStartIsIdempotent()
    {
        displayTimePanel.start();
        final Thread first = displayTimePanel.thread;
        displayTimePanel.start();
        assertSame(first, displayTimePanel.thread, "Second start() should not replace the running thread");
    }

    @Test
    @DisplayName("stop() resets clockText to the static startText value")
    void testStopResetsClockText()
    {
        displayTimePanel.start();
        displayTimePanel.setClockText("01:23.456");
        displayTimePanel.stop();
        assertEquals(DisplayTimePanel.startText, displayTimePanel.getClockText(),
                "stop() should reset clockText to startText");
    }

    // ───────────────────────────────────────────────────
    // setClockText / getClockText
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("setClockText / getClockText round-trip")
    void testSetGetClockText()
    {
        displayTimePanel.setClockText("12:34.567");
        assertEquals("12:34.567", displayTimePanel.getClockText(),
                "getClockText should return the value passed to setClockText");
    }

    // ───────────────────────────────────────────────────
    // setShowAnaloguePanel / isShowAnaloguePanel
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("setShowAnaloguePanel(true) makes isShowAnaloguePanel return true")
    void testSetShowAnaloguePanelTrue()
    {
        displayTimePanel.setShowAnaloguePanel(true);
        assertTrue(displayTimePanel.isShowAnaloguePanel(),
                "isShowAnaloguePanel should return true after setShowAnaloguePanel(true)");
    }

    @Test
    @DisplayName("setShowAnaloguePanel(false) makes isShowAnaloguePanel return false")
    void testSetShowAnaloguePanelFalse()
    {
        displayTimePanel.setShowAnaloguePanel(true);
        displayTimePanel.setShowAnaloguePanel(false);
        assertFalse(displayTimePanel.isShowAnaloguePanel(),
                "isShowAnaloguePanel should return false after setShowAnaloguePanel(false)");
    }

    // ───────────────────────────────────────────────────
    // drawDigitalClock
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("drawDigitalClock does not throw with no current stopwatch")
    void testDrawDigitalClockNoCurrentStopwatch()
    {
        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> displayTimePanel.drawDigitalClock(g),
                "drawDigitalClock should not throw when no stopwatch is active");
    }

    @Test
    @DisplayName("drawDigitalClock does not throw when a stopwatch is active")
    void testDrawDigitalClockWithCurrentStopwatch()
    {
        final Stopwatch sw = new Stopwatch("Sw1", false, false, clock);
        clock.getListOfStopwatches().add(sw);
        stopwatchPanel.setCurrentStopwatch(sw);
        displayTimePanel.setClockText(sw.elapsedFormatted(sw.getAccumMilli(), STOPWATCH_READING_FORMAT));

        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> displayTimePanel.drawDigitalClock(g),
                "drawDigitalClock should not throw when a stopwatch is active");
    }

    // ───────────────────────────────────────────────────
    // drawAnalogueClock
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("drawAnalogueClock does not throw when a stopwatch is active")
    void testDrawAnalogueClockWithCurrentStopwatch()
    {
        final Stopwatch sw = new Stopwatch("Sw1", false, false, clock);
        clock.getListOfStopwatches().add(sw);
        stopwatchPanel.setCurrentStopwatch(sw);
        // Use STOPWATCH_PARSE_FORMAT so split by COLON yields 3 parts
        displayTimePanel.clockText = sw.elapsedFormatted(sw.getAccumMilli(), STOPWATCH_PARSE_FORMAT);

        when(g.getFontMetrics()).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(50);
        assertDoesNotThrow(() -> displayTimePanel.drawAnalogueClock(g),
                "drawAnalogueClock should not throw when a stopwatch is active");
    }

    // ───────────────────────────────────────────────────
    // paint
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("paint in digital mode does not throw")
    void testPaintDigitalModeDoesNotThrow()
    {
        displayTimePanel.setShowAnaloguePanel(false);
        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> displayTimePanel.paint(g),
                "paint in digital mode should not throw");
    }

    @Test
    @DisplayName("paint in analogue mode does not throw when a stopwatch is active")
    void testPaintAnalogueModeDoesNotThrow()
    {
        final Stopwatch sw = new Stopwatch("Sw1", false, false, clock);
        clock.getListOfStopwatches().add(sw);
        stopwatchPanel.setCurrentStopwatch(sw);
        displayTimePanel.setShowAnaloguePanel(true);

        when(g.getFontMetrics()).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(50);
        assertDoesNotThrow(() -> displayTimePanel.paint(g),
                "paint in analogue mode should not throw when a stopwatch is active");
    }
}
