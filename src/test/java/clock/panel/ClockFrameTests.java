package clock.panel;

import clock.entity.Clock;
import clock.entity.Panel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import javax.swing.*;

import java.time.ZoneId;
import java.util.stream.Stream;

import static clock.entity.Panel.PANEL_ALARM;
import static clock.entity.Panel.PANEL_DIGITAL_CLOCK;
import static clock.util.Constants.*;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link ClockFrame} class
 *
 * @author Michael Ball
 * @version 2.9
 */
public class ClockFrameTests
{
    private static final Logger logger = LogManager.getLogger(ClockFrameTests.class);

    Clock clock;

    ClockFrame clockFrame;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting ClockFrameTests...");
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock(11, 30, 0, JANUARY, WEDNESDAY, 1, 2025, AM); // 11:30 AM
        clockFrame = new ClockFrame(clock);
    }

    @AfterEach
    void afterEach()
    {}

    @Test
    @DisplayName("Test ClockFrame no-args constructor")
    void testClockFrameNoArgsConstructor()
    {
        ClockFrame clockFrame = new ClockFrame();
        assertNotNull(clockFrame, "ClockFrame should not be null");
        assertEquals(PANEL_DIGITAL_CLOCK, clockFrame.getPanelType(), "Panel should be set to PANEL_DIGITAL_CLOCK");
        assertNotNull(clockFrame.getClock(), "Clock should not be null");
        assertNotNull(clockFrame.getScheduler(), "Scheduler should not be null");
        assertNotNull(clockFrame.getClockMenuBar(), "ClockMenuBar should not be null");
        assertNotNull(clockFrame.getDigitalClockPanel(), "DigitalClockPanel should not be null");
        assertNotNull(clockFrame.getAnalogueClockPanel(), "AnalogueClockPanel should not be null");
        assertNotNull(clockFrame.getAlarmPanel(), "AlarmPanel should not be null");
        assertNotNull(clockFrame.getTimerPanel(), "TimerPanel should not be null");
        assertNotNull(clockFrame.getStopwatchPanel(), "StopwatchPanel should not be null");
        assertTrue(clockFrame.isVisible(), "ClockFrame should be visible by default");
    }

    @ParameterizedTest
    @DisplayName("Test ClockFrame constructor with Specific Panel")
    @CsvSource({
        "PANEL_DIGITAL_CLOCK, PANEL_DIGITAL_CLOCK",
        "PANEL_ANALOGUE_CLOCK, PANEL_ANALOGUE_CLOCK",
        "PANEL_ALARM, PANEL_ALARM",
        "PANEL_TIMER, PANEL_TIMER",
        "PANEL_STOPWATCH, PANEL_STOPWATCH"
    })
    void testClockFrameConstructorWithSpecificPanel(String panelType, Panel expectedPanelType)
    {
        ClockFrame clockFrame = new ClockFrame(Panel.valueOf(panelType));
        assertNotNull(clockFrame, "ClockFrame should not be null");
        assertSame(expectedPanelType, clockFrame.getPanelType(), "Panel type should match expected value");
    }

    @Test
    @DisplayName("Test ClockFrame with Clock")
    void testClockFrameWithClock()
    {
        assertNotNull(clockFrame, "ClockFrame should not be null");
        assertEquals(clock, clockFrame.getClock(), "Clock in ClockFrame should match the provided clock");
        assertEquals(PANEL_DIGITAL_CLOCK, clockFrame.getPanelType(), "Panel type should be PANEL_DIGITAL_CLOCK by default");
    }

    @Test
    @DisplayName("Test createAndShowGUI")
    void testCreateAndShowGUI()
    {
        ClockFrame.createAndShowGUI();
    }

    @Test
    @DisplayName("Test createAndShowGUI with a clock")
    void testCreateAndShowGUIWithAClock()
    {
        ClockFrame.createAndShowGUI(clock);
    }

    @Test
    @DisplayName("Test createAndShowGUI with PANEL_DIGITAL_CLOCK")
    void testCreateAndShowGUIWithPanelDigitalClock()
    {
        ClockFrame.createAndShowGUI(PANEL_DIGITAL_CLOCK);
    }

    @Test
    @DisplayName("Test createAndShowGUI with PANEL_ANALOGUE_CLOCK")
    void testCreateAndShowGUIWithPanelAnalogueClock()
    {
        ClockFrame.createAndShowGUI(Panel.PANEL_ANALOGUE_CLOCK);
    }

    @Test
    @DisplayName("Test createAndShowGUI with PANEL_ALARM")
    void testCreateAndShowGUIWithPanelAlarm()
    {
        ClockFrame.createAndShowGUI(PANEL_ALARM);
    }

    @Test
    @DisplayName("Test createAndShowGUI with PANEL_TIMER")
    void testCreateAndShowGUIWithPanelTimer()
    {
        ClockFrame.createAndShowGUI(Panel.PANEL_TIMER);
    }

    @Test
    @DisplayName("Test createAndShowGUI with PANEL_STOPWATCH")
    void testCreateAndShowGUIWithPanelStopwatch()
    {
        ClockFrame.createAndShowGUI(Panel.PANEL_STOPWATCH);
    }

    @ParameterizedTest
    @DisplayName("Test change panels")
    @CsvSource({
        "PANEL_DIGITAL_CLOCK, PANEL_DIGITAL_CLOCK",
        "PANEL_ANALOGUE_CLOCK, PANEL_ANALOGUE_CLOCK",
        "PANEL_ALARM, PANEL_ALARM",
        "PANEL_TIMER, PANEL_TIMER",
        //"PANEL_STOPWATCH, PANEL_STOPWATCH",
    })
    void testChangePanels(String changeType, Panel expectedPanelType)
    {
        clockFrame.changePanels(Panel.valueOf(changeType));
        assertEquals(expectedPanelType, clockFrame.getPanelType(), "Panel type should match expected value after change");
        assertTrue(clockFrame.isVisible(), "ClockFrame should now be visible");
    }

    @Test
    @DisplayName("Test change panels to same panel does not do anything")
    void testChangePanelsToSamePanel()
    {
        clockFrame.changePanels(PANEL_DIGITAL_CLOCK);
        assertEquals(clockFrame.getCurrentPanel(), clockFrame.getDigitalClockPanel(), "Panel type should remain the same after changing to the same panel");
        assertTrue(clockFrame.isVisible(), "ClockFrame should still be visible");
    }

    @ParameterizedTest
    @DisplayName("Test updating the clock time")
    @MethodSource("clockTimeProvider")
    void testUpdatingClockTime(JMenuItem menuItemTimeZone, ZoneId timezone)
    {
        clockFrame.updateClockTimezone(menuItemTimeZone); // Update to 12:45:30
        assertEquals(timezone, clockFrame.getClock().getTimezone(), "Timezone should match the provided timezone");
    }
    private static Stream<Arguments> clockTimeProvider() {
        return Stream.of(
                Arguments.of(new JMenuItem(HAWAII), ZoneId.of(PACIFIC_HONOLULU)),
                Arguments.of(new JMenuItem(ALASKA), ZoneId.of(AMERICA_ANCHORAGE)),
                Arguments.of(new JMenuItem(PACIFIC), ZoneId.of(AMERICA_LOS_ANGELES)),
                Arguments.of(new JMenuItem(CENTRAL), ZoneId.of(AMERICA_CHICAGO)),
                Arguments.of(new JMenuItem(EASTERN), ZoneId.of(AMERICA_NEW_YORK)),
                Arguments.of(new JMenuItem(MOUNTAIN), ZoneId.of(AMERICA_DENVER))
        );
    }
}
