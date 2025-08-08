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
import java.util.Arrays;

import static clock.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link TimerPanel} class
 *
 * @author Michael Ball
 * @version 2.9
 */
class TimerPanelTest
{
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
        timerPanel = new TimerPanel(new ClockFrame(clock));
    }

    @AfterEach
    void afterEach()
    {}

    @Test
    void validateFirstTextField()
    {
        timerPanel.getHourTextField().setText(HOUR);
        assertTrue(timerPanel.validateFirstTextField(), "Expected value to be Hour");

        timerPanel.getHourTextField().setText("13");
        assertTrue(timerPanel.validateFirstTextField(), "Expected value to be 13");

        timerPanel.getHourTextField().setText("24");
        assertFalse(timerPanel.validateFirstTextField(), "Expected value to be 24");

        timerPanel.getHourTextField().setText("-1");
        assertFalse(timerPanel.validateFirstTextField(), "Expected value to be invalid");

        timerPanel.getHourTextField().setText(EMPTY);
        assertFalse(timerPanel.validateFirstTextField(), "Expected value to be invalid");
    }

    @Test
    void validateSecondTextField()
    {
        timerPanel.getMinuteTextField().setText(MIN);
        assertTrue(timerPanel.validateSecondTextField(), "Expected value to be Min");

        timerPanel.getMinuteTextField().setText("13");
        assertTrue(timerPanel.validateSecondTextField(), "Expected value to be 13");

        timerPanel.getMinuteTextField().setText("60");
        assertFalse(timerPanel.validateSecondTextField(), "Expected value to be invalid");

        timerPanel.getMinuteTextField().setText("-1");
        assertFalse(timerPanel.validateSecondTextField(), "Expected value to be invalid");

        timerPanel.getMinuteTextField().setText(EMPTY);
        assertFalse(timerPanel.validateSecondTextField(), "Expected value to be invalid");
    }

    @Test
    void validateThirdTextField()
    {
        timerPanel.getSecondTextField().setText(SEC);
        assertTrue(timerPanel.validateThirdTextField(), "Expected value to be SEC");

        timerPanel.getSecondTextField().setText("13");
        assertTrue(timerPanel.validateThirdTextField(), "Expected value to be 13");

        timerPanel.getSecondTextField().setText("59");
        assertTrue(timerPanel.validateThirdTextField(), "Expected value to be 59");

        timerPanel.getSecondTextField().setText("60");
        assertFalse(timerPanel.validateThirdTextField(), "Expected value to be invalid");

        timerPanel.getSecondTextField().setText(EMPTY);
        assertFalse(timerPanel.validateThirdTextField(), "Expected value to be invalid");
    }

    @Test
    void resetTimerFields()
    {
    }

    @Test
    void performCountDownWith5Seconds() throws InterruptedException
    {
        timerPanel.getHourTextField().setText(ZERO);
        timerPanel.getMinuteTextField().setText(ZERO);
        timerPanel.getSecondTextField().setText("5");
        timerPanel.getSetTimerButton().setText(SET);
        //timerPanel.run(mock(ActionEvent.class));
        //Thread.sleep(5000);
        assertEquals(0, Integer.parseInt(timerPanel.getSecondTextField().getText()), "Expected timer to be done");
    }

    @Test
    void pauseCountDown() throws InterruptedException
    {
        timerPanel.getHourTextField().setText(ZERO);
        timerPanel.getMinuteTextField().setText("1");
        timerPanel.getSecondTextField().setText("5");
        timerPanel.getSetTimerButton().setText(SET);
        //timerPanel.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        //timerPanel.run(mock(ActionEvent.class));

        assertEquals(1, Integer.parseInt(timerPanel.getMinuteTextField().getText()), "After 5 seconds, 1 minute is shown");
        assertEquals(0, Integer.parseInt(timerPanel.getSecondTextField().getText()), "After 5 seconds, 0 seconds is shown");
    }

    @Test
    void testHittingSetButtonStartsTimer()
    {
        //timerPanel.run(action);

        assertEquals("Expected timer button text to be "+PAUSE_TIMER, PAUSE_TIMER, timerPanel.getSetTimerButton().getText());
        assertTrue(timerPanel.getResetButton().isEnabled(), "Expected Reset Button to be enabled");
        //assertFalse(timerPanel.isPaused(), "Expected paused to be false");
        //assertNotNull(timerPanel.getScheduler(), "Expected scheduler to be set");
        //assertNotNull(timerPanel.getCountdownFuture(), "Expected future to be set");
    }

    @Test
    void testHittingSetButtonResumesTimer()
    {
        // set up the timer
        timerPanel.getHourTextField().setText(ZERO);
        timerPanel.getMinuteTextField().setText(ZERO);
        timerPanel.getSecondTextField().setText("5");
        // test
        //timerPanel.run(action); // this will be run before a pause can happen
        timerPanel.getSetTimerButton().setText(RESUME_TIMER);
        //timerPanel.run(action);

        assertEquals("Expected timer button text to be "+PAUSE_TIMER, PAUSE_TIMER, timerPanel.getSetTimerButton().getText());
        //assertFalse(timerPanel.isPaused(), "Expected paused to be false");
        //assertNotNull(timerPanel.getScheduler(), "Expected scheduler to be set");
        //assertNotNull(timerPanel.getCountdownFuture(), "Expected future to be set");
    }

    @Test
    void testHittingSetButtonPausesTimer()
    {
        // set up the timer
        timerPanel.getHourTextField().setText(ZERO);
        timerPanel.getMinuteTextField().setText(ZERO);
        timerPanel.getSecondTextField().setText("5");
        // test
        //timerPanel.run(action); // this will be run before a pause can happen
        timerPanel.getSetTimerButton().setText(PAUSE_TIMER);
        //timerPanel.run(action);

        assertEquals("Expected timer button text to be "+RESUME_TIMER, RESUME_TIMER, timerPanel.getSetTimerButton().getText());
        //assertTrue(timerPanel.isPaused(), "Expected paused to be true");
        //assertNotNull(timerPanel.getScheduler(), "Expected scheduler to be set");
        //assertNotNull(timerPanel.getCountdownFuture(), "Expected future to be set");
    }

    @Test
    void testCheckIfTimerHasConcluded()
    {
        //clock.setTimerActive(true);
        //timerPanel.checkIfTimerHasConcluded();

        //assertFalse(clock.isTimerActive(), "Expected clock timer to be inactive");
        //assertNull(timerPanel.getMusicPlayer(), "Expected music player to be null");
        //assertFalse(timerPanel.isTimerGoingOff(), "Expected timerGoingOff to be false");
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
        timerPanel.getHourTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourTextField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getHourTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourTextField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getHourTextField().getText());
    }

    @Test
    void testClickedOnMinutesFieldFocusGained()
    {
        // user opens timer panel, and clicks on hour field
        timerPanel.getMinuteTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getMinuteTextField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getMinuteTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getMinuteTextField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$3".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getMinuteTextField().getText());
    }

    @Test
    void testClickedOnSecondsFieldFocusGained()
    {
        // user opens timer panel, and clicks on hour field
        timerPanel.getSecondTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getSecondTextField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getSecondTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getSecondTextField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$4".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getSecondTextField().getText());
    }

    @Test
    void testClickedOffHourFieldNoInputFocusLost()
    {
        timerPanel.getHourTextField().setText(EMPTY);
        // user opens timer panel, clicks on hour field, then clicks off hour field
        timerPanel.getHourTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );
        timerPanel.getHourTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourTextField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be "+HOUR, HOUR, timerPanel.getHourTextField().getText());
    }

    @Test
    void testClickedOffHourFieldEmptyInputFocusLost()
    {
        timerPanel.getHourTextField().setText(" ");
        // user opens timer panel, clicks space and clicks off hour field
        timerPanel.getHourTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourTextField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be "+HOUR, HOUR, timerPanel.getHourTextField().getText());
    }

    @Test
    void testClickedOffHourFieldValidNumberInputFocusLost()
    {
        timerPanel.getHourTextField().setText("1"); // start a 1 hour timer
        // user opens timer panel, enters number and clicks off hour field
        timerPanel.getHourTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourTextField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be 1", "1", timerPanel.getHourTextField().getText());
        assertEquals("Expected set timer button text to be "+SET, SET, timerPanel.getSetTimerButton().getText());
        assertTrue(timerPanel.getSetTimerButton().isEnabled(), "Expected set timer button to be enabled");
    }

    @Test
    void testClickedOffHourFieldInvalidNumberInputFocusLost()
    {
        timerPanel.getHourTextField().setText("-1"); // start a -1 hour timer
        // user opens timer panel, enters invalid number and clicks off hour field
        timerPanel.getHourTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourTextField().getFocusListeners())
            .forEach(listener -> {
                // anonymous inner class defined in TimerPanel
                if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("-1", timerPanel.getHourTextField().getText(), "Expected field to be -1");
        assertEquals(TIMER_HOUR_ERROR_12, timerPanel.getSetTimerButton().getText(), "Expected set timer button text to be: "+TIMER_HOUR_ERROR_12);
        assertFalse(timerPanel.getSetTimerButton().isEnabled(), "Expected set timer button to be disabled");
    }

    @Test
    void testClickedOffHourFieldInvalidNumberAgainInputFocusLost()
    {
        timerPanel.getHourTextField().setText("24"); // start a 24 hour timer
        // user opens timer panel, enters number and clicks off hour field
        timerPanel.getHourTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHourTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHourTextField().dispatchEvent(focusEvent);
        Arrays.stream(timerPanel.getHourTextField().getFocusListeners())
                .forEach(listener -> {
                            // anonymous inner class defined in TimerPanel
                            if ("clock.panel.TimerPanel$2".equals(listener.getClass().getName()))
                            { listener.focusLost(focusEvent); }
                        }
                );

        assertEquals("24", timerPanel.getHourTextField().getText(), "Expected field to be 24");
        assertEquals(TIMER_HOUR_ERROR_12, timerPanel.getSetTimerButton().getText(), "Expected set timer button text to be "+TIMER_HOUR_ERROR_12);
        assertFalse(timerPanel.getSetTimerButton().isEnabled(), "Expected set timer button to be disabled");
    }

    @Test
    void testClickedOffHourFieldValidNumberInputFocusLostMinutesIsInvalid()
    {
        timerPanel.getHourTextField().setText("1"); // start a 1 hour timer
        timerPanel.getMinuteTextField().setText("M"); // invalid minute
        //timerPanel.getHourField().setFocusable(true);
//        FocusEvent focusEvent = new FocusEvent(
//                timerPanel.getHourField(),
//                FocusEvent.FOCUS_LOST,
//                false,
//                timerPanel
//        );

//        timerPanel.getHourField().dispatchEvent(focusEvent);
//        Arrays.stream(timerPanel.getHourField().getFocusListeners())
//            .forEach(listener -> {
//                // anonymous inner class defined in TimerPanel
//                if (timerPanel.getClass().getName().equals(listener.getClass().getName()))
//                { listener.focusLost(focusEvent); }
//            }
//        );

        assertEquals("1", timerPanel.getHourTextField().getText(), "Expected field to be 1");
        assertTrue(timerPanel.getMinuteTextField().isFocusOwner(), "Expected minutes field to have focus");
        assertEquals(SET, timerPanel.getSetTimerButton().getText(), "Expected set timer button text to be "+SET);
        assertFalse(timerPanel.getSetTimerButton().isEnabled(), "Expected set timer button to be disabled");
    }

}