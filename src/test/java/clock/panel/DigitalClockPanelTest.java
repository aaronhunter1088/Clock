package clock.panel;

import clock.entity.Alarm;
import clock.entity.Clock;
import clock.entity.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.List;

import static clock.entity.Panel.PANEL_DIGITAL_CLOCK;
import static clock.util.Constants.*;
import static java.time.DayOfWeek.MONDAY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link DigitalClockPanel} class
 *
 * @author michael ball
 * @version since 3.0.4
 */
@ExtendWith(MockitoExtension.class)
public class DigitalClockPanelTest
{
    private static final Logger logger = LogManager.getLogger(DigitalClockPanelTest.class);

    Clock clock;
    DigitalClockPanel digitalClockPanel;

    @Mock
    Graphics g;

    @Mock
    FontMetrics fontMetrics;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting DigitalClockPanelTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        digitalClockPanel = new DigitalClockPanel(new ClockFrame(clock));
        digitalClockPanel.getClockFrame().changePanels(PANEL_DIGITAL_CLOCK);
    }

    @AfterEach
    void afterEach()
    {
        digitalClockPanel.getClockFrame().stop();
        digitalClockPanel.getClockFrame().dispose();
    }

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", DigitalClockPanelTest.class.getSimpleName()); }

    // ───────────────────────────────────────────────────
    // Initialization
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("Test DigitalClockPanel Initialization")
    void testDigitalClockPanelInitialization()
    {
        assertEquals(PANEL_DIGITAL_CLOCK, digitalClockPanel.getClockFrame().getPanelType(), "Current panel should be " + PANEL_DIGITAL_CLOCK);
        assertNotNull(digitalClockPanel, "DigitalClockPanel should not be null");
        assertNotNull(digitalClockPanel.getGridBagConstraints(), "GridBagConstraints should not be null");
        assertNotNull(digitalClockPanel.getGridBagLayout(), "GridBagLayout should not be null");
        assertNotNull(digitalClockPanel.getClock(), "Clock should not be null");
    }

    // ───────────────────────────────────────────────────
    // setClock / getClock
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("setClock updates the clock reference returned by getClock")
    void testSetClockUpdatesClock()
    {
        final Clock newClock = new Clock();
        digitalClockPanel.setClock(newClock);
        assertSame(newClock, digitalClockPanel.getClock(),
                "getClock should return the clock set via setClock");
    }

    // ───────────────────────────────────────────────────
    // stop
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("stop() does not throw an exception")
    void testStopDoesNotThrow()
    {
        assertDoesNotThrow(() -> digitalClockPanel.stop(),
                "stop() should not throw an exception");
    }

    @Test
    @DisplayName("After stop(), calling start() creates a new thread")
    void testStopThenStartRestarts()
    {
        digitalClockPanel.stop();
        assertDoesNotThrow(() -> digitalClockPanel.start(),
                "start() after stop() should not throw");
    }

    // ───────────────────────────────────────────────────
    // paint / drawStructure — no alarms or timers going off
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("paint does not throw when no alarms or timers are going off")
    void testPaintNoAlarmsOrTimers()
    {
        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> digitalClockPanel.paint(g),
                "paint should not throw when no alarms or timers are active");
    }

    @Test
    @DisplayName("drawStructure does not throw when no alarms or timers are going off")
    void testDrawStructureNoAlarmsOrTimers()
    {
        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> digitalClockPanel.drawStructure(g),
                "drawStructure should not throw when there are no active alarms or timers");
    }

    // ───────────────────────────────────────────────────
    // drawStructure — single alarm going off
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("drawStructure does not throw when a single alarm is going off")
    void testDrawStructureSingleAlarmGoingOff()
    {
        final Alarm alarm = new Alarm("Wake Up", 7, 0, AM, List.of(MONDAY), false, clock);
        alarm.setIsAlarmGoingOff(true);
        clock.getListOfAlarms().add(alarm);

        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> digitalClockPanel.drawStructure(g),
                "drawStructure should not throw with one alarm going off");
    }

    @Test
    @DisplayName("drawStructure does not throw when multiple alarms are going off")
    void testDrawStructureMultipleAlarmsGoingOff()
    {
        for (int i = 1; i <= 3; i++)
        {
            final Alarm alarm = new Alarm("Alarm" + i, i, 0, AM, List.of(MONDAY), false, clock);
            alarm.setIsAlarmGoingOff(true);
            clock.getListOfAlarms().add(alarm);
        }
        when(g.getFont()).thenReturn(ClockFrame.font40);
        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> digitalClockPanel.drawStructure(g),
                "drawStructure should not throw with multiple alarms going off");
    }

    // ───────────────────────────────────────────────────
    // drawStructure — single timer going off
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("drawStructure does not throw when a single timer is going off")
    void testDrawStructureSingleTimerGoingOff()
    {
        final Timer timer = new Timer(0, 0, 5, "Egg Timer", clock);
        timer.setTimerGoingOff(true);
        clock.getListOfTimers().add(timer);

        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> digitalClockPanel.drawStructure(g),
                "drawStructure should not throw with one timer going off");
    }

    @Test
    @DisplayName("drawStructure does not throw when multiple timers are going off")
    void testDrawStructureMultipleTimersGoingOff()
    {
        for (int i = 1; i <= 2; i++)
        {
            final Timer timer = new Timer(0, i, 0, "Timer" + i, clock);
            timer.setTimerGoingOff(true);
            clock.getListOfTimers().add(timer);
        }
        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> digitalClockPanel.drawStructure(g),
                "drawStructure should not throw with multiple timers going off");
    }

    // ───────────────────────────────────────────────────
    // drawStructure — both alarms and timers going off
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("drawStructure does not throw when both alarms and timers are going off")
    void testDrawStructureBothAlarmAndTimerGoingOff()
    {
        final Alarm alarm = new Alarm("Alarm1", 8, 0, AM, List.of(MONDAY), false, clock);
        alarm.setIsAlarmGoingOff(true);
        clock.getListOfAlarms().add(alarm);

        final Timer timer = new Timer(0, 0, 30, "Timer1", clock);
        timer.setTimerGoingOff(true);
        clock.getListOfTimers().add(timer);

        when(g.getFont()).thenReturn(ClockFrame.font40);
        when(g.getFontMetrics(any())).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(any())).thenReturn(100);
        assertDoesNotThrow(() -> digitalClockPanel.drawStructure(g),
                "drawStructure should not throw when both alarms and timers are going off");
    }

    // ───────────────────────────────────────────────────
    // setupSettingsMenu
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("setupSettingsMenu populates the settings menu")
    void testSetupSettingsMenuPopulatesMenu()
    {
        digitalClockPanel.setupSettingsMenu();
        assertTrue(digitalClockPanel.getClockFrame().getClockMenuBar().getSettingsMenu().getItemCount() > 0,
                "Settings menu should have items after setupSettingsMenu");
    }
}
