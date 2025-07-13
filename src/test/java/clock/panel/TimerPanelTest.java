package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import static clock.contract.ClockConstants.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TimerPanelTest
{
    static { System.setProperty("appName", TimerPanelTest.class.getSimpleName()); }
    private static final Logger logger = LogManager.getLogger(TimerPanelTest.class);

    Clock clock;

    TimerPanel timerPanel;
    @Mock
    ActionEvent action;

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting TimerPanelTest..."); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        clock.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        timerPanel = clock.getTimerPanel();
        //MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void afterEach() 
    {
        if (clock != null) {
            logger.info("Test complete. Closing the clock...");
            // Create a WindowEvent with WINDOW_CLOSING event type
            WindowEvent windowClosing = new WindowEvent(clock, WindowEvent.WINDOW_CLOSING);

            // Dispatch the event to the JFrame instance
            clock.dispatchEvent(windowClosing);

            // Ensure the clock is no longer visible
            assertFalse(clock.isVisible());

            // Dispose of the JFrame to release resources
            clock.dispose();
        }
    }

    @Test
    void validateFirstTextField()
    {
        timerPanel.getHourField().setText(HOUR);
        assertTrue(timerPanel.validateFirstTextField(), "Expected value to be Hour");

        timerPanel.getHourField().setText("13");
        assertTrue(timerPanel.validateFirstTextField(), "Expected value to be 13");

        timerPanel.getHourField().setText("24");
        assertFalse(timerPanel.validateFirstTextField(), "Expected value to be 24");

        timerPanel.getHourField().setText("-1");
        assertFalse(timerPanel.validateFirstTextField(), "Expected value to be invalid");

        timerPanel.getHourField().setText(EMPTY);
        assertFalse(timerPanel.validateFirstTextField(), "Expected value to be invalid");
    }

    @Test
    void validateSecondTextField()
    {
        timerPanel.getMinuteField().setText(MIN);
        assertTrue(timerPanel.validateSecondTextField(), "Expected value to be Min");

        timerPanel.getMinuteField().setText("13");
        assertTrue(timerPanel.validateSecondTextField(), "Expected value to be 13");

        timerPanel.getMinuteField().setText("60");
        assertFalse(timerPanel.validateSecondTextField(), "Expected value to be invalid");

        timerPanel.getMinuteField().setText("-1");
        assertFalse(timerPanel.validateSecondTextField(), "Expected value to be invalid");

        timerPanel.getMinuteField().setText(EMPTY);
        assertFalse(timerPanel.validateSecondTextField(), "Expected value to be invalid");
    }

    @Test
    void validateThirdTextField()
    {
        timerPanel.getSecondField().setText(SEC);
        assertTrue(timerPanel.validateThirdTextField(), "Expected value to be SEC");

        timerPanel.getSecondField().setText("13");
        assertTrue(timerPanel.validateThirdTextField(), "Expected value to be 13");

        timerPanel.getSecondField().setText("59");
        assertTrue(timerPanel.validateThirdTextField(), "Expected value to be 59");

        timerPanel.getSecondField().setText("60");
        assertFalse(timerPanel.validateThirdTextField(), "Expected value to be invalid");

        timerPanel.getSecondField().setText(EMPTY);
        assertFalse(timerPanel.validateThirdTextField(), "Expected value to be invalid");
    }

    @Test
    void resetTimerFields()
    {
    }

    @Test
    void performCountDownWith5Seconds() throws InterruptedException
    {
        timerPanel.getHourField().setText(ZERO);
        timerPanel.getMinuteField().setText(ZERO);
        timerPanel.getSecondField().setText("5");
        timerPanel.getTimerButton().setText(SET);
        timerPanel.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        assertEquals(0, Integer.parseInt(timerPanel.getSecondField().getText()), "Expected timer to be done");
    }

    @Test
    void pauseCountDown() throws InterruptedException
    {
        timerPanel.getHourField().setText(ZERO);
        timerPanel.getMinuteField().setText("1");
        timerPanel.getSecondField().setText("5");
        timerPanel.getTimerButton().setText(SET);
        timerPanel.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        timerPanel.run(mock(ActionEvent.class));

        assertEquals(1, Integer.parseInt(timerPanel.getMinuteField().getText()), "After 5 seconds, 1 minute is shown");
        assertEquals(0, Integer.parseInt(timerPanel.getSecondField().getText()), "After 5 seconds, 0 seconds is shown");
    }

    @Test
    void testHittingSetButtonStartsTimer()
    {
        timerPanel.run(action);

        assertEquals("Expected timer button text to be "+PAUSE_TIMER, PAUSE_TIMER, timerPanel.getTimerButton().getText());
        assertTrue(timerPanel.getResetButton().isEnabled(), "Expected Reset Button to be enabled");
        assertFalse(timerPanel.isPaused(), "Expected paused to be false");
        assertNotNull(timerPanel.getScheduler(), "Expected scheduler to be set");
        assertNotNull(timerPanel.getCountdownFuture(), "Expected future to be set");
    }

    @Test
    void testHittingSetButtonResumesTimer()
    {
        // set up the timer
        timerPanel.getHourField().setText(ZERO);
        timerPanel.getMinuteField().setText(ZERO);
        timerPanel.getSecondField().setText("5");
        // test
        timerPanel.run(action); // this will be run before a pause can happen
        timerPanel.getTimerButton().setText(RESUME_TIMER);
        timerPanel.run(action);

        assertEquals("Expected timer button text to be "+PAUSE_TIMER, PAUSE_TIMER, timerPanel.getTimerButton().getText());
        assertFalse(timerPanel.isPaused(), "Expected paused to be false");
        assertNotNull(timerPanel.getScheduler(), "Expected scheduler to be set");
        assertNotNull(timerPanel.getCountdownFuture(), "Expected future to be set");
    }

    @Test
    void testHittingSetButtonPausesTimer()
    {
        // set up the timer
        timerPanel.getHourField().setText(ZERO);
        timerPanel.getMinuteField().setText(ZERO);
        timerPanel.getSecondField().setText("5");
        // test
        timerPanel.run(action); // this will be run before a pause can happen
        timerPanel.getTimerButton().setText(PAUSE_TIMER);
        timerPanel.run(action);

        assertEquals("Expected timer button text to be "+RESUME_TIMER, RESUME_TIMER, timerPanel.getTimerButton().getText());
        assertTrue(timerPanel.isPaused(), "Expected paused to be true");
        assertNotNull(timerPanel.getScheduler(), "Expected scheduler to be set");
        assertNotNull(timerPanel.getCountdownFuture(), "Expected future to be set");
    }

    @Test
    void testCheckIfTimerHasConcluded()
    {
        clock.setTimerActive(true);
        timerPanel.checkIfTimerHasConcluded();

        assertFalse(clock.isTimerActive(), "Expected clock timer to be inactive");
        assertNull(timerPanel.getMusicPlayer(), "Expected music player to be null");
        assertFalse(timerPanel.isTimerGoingOff(), "Expected timerGoingOff to be false");
    }

    @Test
    void testClickOnTimerPanel()
    {
        // Create a MouseEvent
        MouseEvent clickEvent = new MouseEvent(
                timerPanel,
                MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(),
                0,
                10, 10,
                1,
                false
        );
        timerPanel.dispatchEvent(clickEvent);
    }

    @Test
    void testClickedOnHourFieldFocusGained()
    {
        // user opens timer panel, and clicks on hour field
        timerPanel.getHourField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getHourField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getHourField().getText());
    }

    @Test
    void testClickedOnMinutesFieldFocusGained()
    {
        // user opens timer panel, and clicks on hour field
        timerPanel.getMinuteField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getMinuteField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getMinuteField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getMinuteField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$3".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getMinuteField().getText());
    }

    @Test
    void testClickedOnSecondsFieldFocusGained()
    {
        // user opens timer panel, and clicks on hour field
        timerPanel.getSecondField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getSecondField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getSecondField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getSecondField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$4".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getSecondField().getText());
    }

    @Test
    void testClickedOffHourFieldNoInputFocusLost()
    {
        timerPanel.getHourField().setText(EMPTY);
        // user opens timer panel, clicks on hour field, then clicks off hour field
        timerPanel.getHourField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );
        timerPanel.getHourField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be "+HOUR, HOUR, timerPanel.getHourField().getText());
    }

    @Test
    void testClickedOffHourFieldEmptyInputFocusLost()
    {
        timerPanel.getHourField().setText(" ");
        // user opens timer panel, clicks space and clicks off hour field
        timerPanel.getHourField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be "+HOUR, HOUR, timerPanel.getHourField().getText());
    }

    @Test
    void testClickedOffHourFieldValidNumberInputFocusLost()
    {
        timerPanel.getHourField().setText("1"); // start a 1 hour timer
        // user opens timer panel, enters number and clicks off hour field
        timerPanel.getHourField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be 1", "1", timerPanel.getHourField().getText());
        assertEquals("Expected set timer button text to be "+SET, SET, timerPanel.getTimerButton().getText());
        assertTrue(timerPanel.getTimerButton().isEnabled(), "Expected set timer button to be enabled");
    }

    @Test
    void testClickedOffHourFieldInvalidNumberInputFocusLost()
    {
        timerPanel.getHourField().setText("-1"); // start a -1 hour timer
        // user opens timer panel, enters invalid number and clicks off hour field
        timerPanel.getHourField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be -1", "-1", timerPanel.getHourField().getText());
        assertEquals("Expected set timer button text to be "+TIMER_HOUR_ERROR, TIMER_HOUR_ERROR, timerPanel.getTimerButton().getText());
        assertFalse(timerPanel.getTimerButton().isEnabled(), "Expected set timer button to be disabled");
    }

    @Test
    void testClickedOffHourFieldInvalidNumberAgainInputFocusLost()
    {
        timerPanel.getHourField().setText("24"); // start a 24 hour timer
        // user opens timer panel, enters number and clicks off hour field
        timerPanel.getHourField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourField().getFocusListeners())
                .forEach(listener -> {
                            // anonymous inner class defined in TimerPanel
                            if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                            { listener.focusLost(focusEvent); }
                        }
                );

        assertEquals("Expected field to be 24", "24", timerPanel.getHourField().getText());
        assertEquals("Expected set timer button text to be "+TIMER_HOUR_ERROR, TIMER_HOUR_ERROR, timerPanel.getTimerButton().getText());
        assertFalse(timerPanel.getTimerButton().isEnabled(), "Expected set timer button to be disabled");
    }

    @Test
    void testClickedOffHourFieldValidNumberInputFocusLostMinutesIsInvalid()
    {
        timerPanel.getHourField().setText("1"); // start a 1 hour timer
        timerPanel.getMinuteField().setText("M"); // invalid minute
        timerPanel.getHourField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("1", timerPanel.getHourField().getText(), "Expected field to be 1");
        // TODO: FIX THIS
        //assertTrue("Expected minutes field to have focus", timerPanel.getMinuteField().isFocusOwner());
        assertEquals("Expected set timer button text to be "+SET, SET, timerPanel.getTimerButton().getText());
        assertFalse(timerPanel.getTimerButton().isEnabled(), "Expected set timer button to be disabled");
    }

}