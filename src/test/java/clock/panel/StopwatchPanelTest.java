package clock.panel;

import clock.entity.Clock;
import clock.entity.Panel;
import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.event.ActionEvent;
import java.util.stream.Stream;

import static clock.entity.Panel.*;
import static clock.util.Constants.*;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link StopwatchPanel} class
 *
 * @author michael ball
 * @version since 2.9
 */
@ExtendWith(MockitoExtension.class)
class StopwatchPanelTest {

    private static final Logger logger = LogManager.getLogger(TimerPanelTest.class);

    Clock clock;

    StopwatchPanel stopwatchPanel;

    @Mock
    ActionEvent mockActionEvent;

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting TimerPanelTest..."); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        Stopwatch.stopwatchCounter = 0L;
        stopwatchPanel = new StopwatchPanel(new ClockFrame(clock));
        stopwatchPanel.getClockFrame().changePanels(PANEL_STOPWATCH);
    }

    @AfterEach
    void afterEach()
    {
        stopwatchPanel.getClockFrame().stop();
    }

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", StopwatchPanelTest.class.getSimpleName()); }

    @Test
    @DisplayName("Panel starts with default values")
    void testPanelStartsUp()
    {
        assertEquals(DisplayTimePanel.startText, stopwatchPanel.getDisplayTimePanel().getClockText());
        assertEquals(START, stopwatchPanel.getStartButton().getText());
        assertEquals(LAP, stopwatchPanel.getLapButton().getText());
        assertEquals("Sw1", stopwatchPanel.getStopwatchNameField().getText());
    }

    @Test
    @DisplayName("Panel starts a stopwatch")
    void testStartingStopwatchWorks() throws InterruptedException
    {
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getStartButton());
        stopwatchPanel.executeButtonAction(mockActionEvent);
        sleep(1000);
        assertEquals(PAUSE, stopwatchPanel.getStartButton().getText());
        assertEquals(LAP, stopwatchPanel.getLapButton().getText());
        assertNotNull(stopwatchPanel.getCurrentStopwatch());
        assertTrue(stopwatchPanel.getCurrentStopwatch().isStarted());
        assertTrue(stopwatchPanel.getClock().getListOfStopwatches()
                .contains(stopwatchPanel.getCurrentStopwatch()));
    }

    @Test
    @DisplayName("Panel pauses a stopwatch")
    void testPausingStopwatchWorks() throws InterruptedException
    {
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getStartButton());
        stopwatchPanel.executeButtonAction(mockActionEvent);
        sleep(3000);
        stopwatchPanel.executeButtonAction(mockActionEvent); // button will say PAUSE
        assertEquals(RESUME, stopwatchPanel.getStartButton().getText());
        assertEquals(RESET, stopwatchPanel.getLapButton().getText());
        assertNotNull(stopwatchPanel.getCurrentStopwatch());
        assertTrue(stopwatchPanel.getCurrentStopwatch().isPaused());
        assertFalse(stopwatchPanel.getDisplayTimePanel().isRunning());
    }

    @Test
    @DisplayName("Panel resumes a paused stopwatch")
    void testResumingStopwatchWorks() throws InterruptedException
    {
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getStartButton());
        stopwatchPanel.executeButtonAction(mockActionEvent); // starts the stopwatch
        sleep(3000);
        stopwatchPanel.executeButtonAction(mockActionEvent); // button will say PAUSE
        sleep(3000);
        stopwatchPanel.executeButtonAction(mockActionEvent); // button will say RESUME, resuming
        assertEquals(PAUSE, stopwatchPanel.getStartButton().getText());
        assertEquals(LAP, stopwatchPanel.getLapButton().getText());
        assertNotNull(stopwatchPanel.getCurrentStopwatch());
        assertFalse(stopwatchPanel.getCurrentStopwatch().isPaused());
        assertTrue(stopwatchPanel.getDisplayTimePanel().isRunning());
    }

    @Test
    @DisplayName("Click Lap button when stopwatch panel has not started")
    void testClickLapWhenNotStartedDoesNothing()
    {
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getLapButton());
        stopwatchPanel.executeButtonAction(mockActionEvent);
        assertEquals(DisplayTimePanel.startText, stopwatchPanel.getDisplayTimePanel().getClockText());
        assertEquals(START, stopwatchPanel.getStartButton().getText());
        assertEquals(LAP, stopwatchPanel.getLapButton().getText());
        assertNull(stopwatchPanel.getCurrentStopwatch());
    }

    @Test
    @DisplayName("Click Lap button when stopwatch panel is running")
    void testClickLapWhenRunningWorks() throws InterruptedException
    {
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getStartButton());
        stopwatchPanel.executeButtonAction(mockActionEvent); // starts the stopwatch
        sleep(2000);
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getLapButton());
        stopwatchPanel.executeButtonAction(mockActionEvent); // clicks lap button
        assertEquals(PAUSE, stopwatchPanel.getStartButton().getText());
        assertEquals(LAP, stopwatchPanel.getLapButton().getText());
        assertNotNull(stopwatchPanel.getCurrentStopwatch());
        assertTrue(stopwatchPanel.getCurrentStopwatch().isStarted());
        assertEquals(1, stopwatchPanel.getCurrentStopwatch().getLaps().size());
        assertTrue(stopwatchPanel.getDisplayTimePanel().isRunning());
    }

    @Test
    @DisplayName("Test that with 3 stopwatches, the panel is reset")
    void testResettingWith3StopwatchesWorks() throws InterruptedException
    {
        // Create 2 stopwatches and add them to the clock
        Stopwatch sw1 = new Stopwatch("Sw1", true, true, clock);
        Stopwatch sw2 = new Stopwatch("Sw2", true, true, clock);

        clock.getListOfStopwatches().add(sw1);
        clock.getListOfStopwatches().add(sw2);

        // Start a real stopwatch in the panel
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getStartButton());
        stopwatchPanel.executeButtonAction(mockActionEvent); // starts the stopwatch
        sleep(2000);

        // Pause the stopwatch
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getStartButton());
        stopwatchPanel.executeButtonAction(mockActionEvent); // button will say PAUSE
        assertEquals(RESUME, stopwatchPanel.getStartButton().getText());
        assertEquals(RESET, stopwatchPanel.getLapButton().getText());
        assertNotNull(stopwatchPanel.getCurrentStopwatch());
        assertTrue(stopwatchPanel.getCurrentStopwatch().isPaused());
        assertFalse(stopwatchPanel.getDisplayTimePanel().isRunning());

        // Now click reset - this should remove the current stopwatch and reset the panel
        when(mockActionEvent.getSource()).thenReturn(stopwatchPanel.getLapButton());
        stopwatchPanel.executeButtonAction(mockActionEvent); // clicks reset button
        assertEquals(DisplayTimePanel.startText, stopwatchPanel.getDisplayTimePanel().getClockText());
        assertEquals(0, clock.getListOfStopwatches().size());
        assertEquals("Sw" + (Stopwatch.stopwatchCounter + 1), stopwatchPanel.getStopwatchNameField().getText());
        assertEquals(START, stopwatchPanel.getStartButton().getText());
        assertEquals(LAP, stopwatchPanel.getLapButton().getText());
    }

    @ParameterizedTest
    @DisplayName("Panel switches to another panel")
    @MethodSource("switchPanelProvider")
    void testSwitchingPanelsWorks(Panel panel)
    {
        stopwatchPanel.getClockFrame().changePanels(panel);
        assertEquals(panel, stopwatchPanel.getClockFrame().getPanelType());
    }
    private static Stream<Arguments> switchPanelProvider() {
        return Stream.of(
            Arguments.of(PANEL_DIGITAL_CLOCK),
            Arguments.of(PANEL_ANALOGUE_CLOCK),
            Arguments.of(PANEL_ALARM),
            Arguments.of(PANEL_TIMER),
            Arguments.of(PANEL_STOPWATCH)
        );
    }
}
