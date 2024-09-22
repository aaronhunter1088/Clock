package org.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import static org.example.clock.ClockConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TimerPanelTest
{
    static { System.setProperty("appName", TimerPanelTest.class.getSimpleName()); }
    private static final Logger logger = LogManager.getLogger(TimerPanelTest.class);

    Clock clock;

    TimerPanel timerPanel;
    @Mock
    ActionEvent action;

    @BeforeClass
    public static void beforeClass()
    { logger.info("Starting TimerPanelTest..."); }

    @Before
    public void beforeEach()
    {
        clock = new Clock(true);
        timerPanel = clock.getTimerPanel();
        //MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validateFirstTextField()
    {
        timerPanel.getHourField().setText(HOUR);
        assertTrue("Expected value to be Hour", timerPanel.validateFirstTextField());

        timerPanel.getHourField().setText("13");
        assertTrue("Expected value to be 13", timerPanel.validateFirstTextField());

        timerPanel.getHourField().setText("24");
        assertFalse("Expected value to be 24", timerPanel.validateFirstTextField());

        timerPanel.getHourField().setText("-1");
        assertFalse("Expected value to be invalid", timerPanel.validateFirstTextField());

        timerPanel.getHourField().setText(EMPTY);
        assertFalse("Expected value to be invalid", timerPanel.validateFirstTextField());
    }

    @Test
    public void validateSecondTextField()
    {
        timerPanel.getMinuteField().setText(MIN);
        assertTrue("Expected value to be Min", timerPanel.validateSecondTextField());

        timerPanel.getMinuteField().setText("13");
        assertTrue("Expected value to be 13", timerPanel.validateSecondTextField());

        timerPanel.getMinuteField().setText("60");
        assertFalse("Expected value to be invalid", timerPanel.validateSecondTextField());

        timerPanel.getMinuteField().setText("-1");
        assertFalse("Expected value to be invalid", timerPanel.validateSecondTextField());

        timerPanel.getMinuteField().setText(EMPTY);
        assertFalse("Expected value to be invalid", timerPanel.validateSecondTextField());
    }

    @Test
    public void validateThirdTextField()
    {
        timerPanel.getSecondField().setText(SEC);
        assertTrue("Expected value to be SEC", timerPanel.validateThirdTextField());

        timerPanel.getSecondField().setText("13");
        assertTrue("Expected value to be 13", timerPanel.validateThirdTextField());

        timerPanel.getSecondField().setText("59");
        assertTrue("Expected value to be 59", timerPanel.validateThirdTextField());

        timerPanel.getSecondField().setText("60");
        assertFalse("Expected value to be invalid", timerPanel.validateThirdTextField());

        timerPanel.getSecondField().setText(EMPTY);
        assertFalse("Expected value to be invalid", timerPanel.validateThirdTextField());
    }

    @Test
    public void resetTimerFields()
    {
    }

    @Test
    public void performCountDownWith5Seconds() throws InterruptedException
    {
        timerPanel.getHourField().setText(ZERO);
        timerPanel.getMinuteField().setText(ZERO);
        timerPanel.getSecondField().setText("5");
        timerPanel.getTimerButton().setText(SET);
        timerPanel.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        assertEquals("Expected timer to be done", 0, Integer.parseInt(timerPanel.getSecondField().getText()));
    }

    @Test
    public void pauseCountDown() throws InterruptedException
    {
        timerPanel.getHourField().setText(ZERO);
        timerPanel.getMinuteField().setText("1");
        timerPanel.getSecondField().setText("5");
        timerPanel.getTimerButton().setText(SET);
        timerPanel.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        timerPanel.run(mock(ActionEvent.class));

        assertEquals("After 5 seconds, 1 minute is shown", 1, Integer.parseInt(timerPanel.getMinuteField().getText()));
        assertEquals("After 5 seconds, 0 seconds is shown", 0, Integer.parseInt(timerPanel.getSecondField().getText()));
    }

    @Test
    public void testHittingSetButtonStartsTimer()
    {
        timerPanel.run(action);

        assertEquals("Expected timer button text to be "+PAUSE_TIMER, PAUSE_TIMER, timerPanel.getTimerButton().getText());
        assertTrue("Expected Reset Button to be enabled", timerPanel.getResetButton().isEnabled());
        assertFalse("Expected paused to be false", timerPanel.isPaused());
        assertNotNull("Expected scheduler to be set", timerPanel.getScheduler());
        assertNotNull("Expected future to be set", timerPanel.getCountdownFuture());
    }

    @Test
    public void testHittingSetButtonResumesTimer()
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
        assertFalse("Expected paused to be false", timerPanel.isPaused());
        assertNotNull("Expected scheduler to be set", timerPanel.getScheduler());
        assertNotNull("Expected future to be set", timerPanel.getCountdownFuture());
    }

    @Test
    public void testHittingSetButtonPausesTimer()
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
        assertTrue("Expected paused to be true", timerPanel.isPaused());
        assertNotNull("Expected scheduler to be set", timerPanel.getScheduler());
        assertNotNull("Expected future to be set", timerPanel.getCountdownFuture());
    }

    @Test
    public void testCheckIfTimerHasConcluded()
    {
        clock.setTimerActive(true);
        timerPanel.checkIfTimerHasConcluded();

        assertFalse("Expected clock timer to be inactive", clock.isTimerActive());
        assertNull("Expected music player to be null", timerPanel.getMusicPlayer());
        assertFalse("Expected timerGoingOff to be false", timerPanel.isTimerGoingOff());
    }

    @Test
    public void testClickOnTimerPanel()
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
    public void testClickedOnHourFieldFocusGained()
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
                if ("org.example.clock.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getHourField().getText());
    }

    @Test
    public void testClickedOnMinutesFieldFocusGained()
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
                if ("org.example.clock.TimerPanel$3".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getMinuteField().getText());
    }

    @Test
    public void testClickedOnSecondsFieldFocusGained()
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
                if ("org.example.clock.TimerPanel$4".equals(listener.getClass().getName()))
                { listener.focusGained(focusEvent); }
            }
        );

        assertEquals("Expected field to be emptied", EMPTY, timerPanel.getSecondField().getText());
    }

    @Test
    public void testClickedOffHourFieldNoInputFocusLost()
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
                if ("org.example.clock.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be "+HOUR, HOUR, timerPanel.getHourField().getText());
    }

    @Test
    public void testClickedOffHourFieldEmptyInputFocusLost()
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
                if ("org.example.clock.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be "+HOUR, HOUR, timerPanel.getHourField().getText());
    }

    @Test
    public void testClickedOffHourFieldValidNumberInputFocusLost()
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
                if ("org.example.clock.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be 1", "1", timerPanel.getHourField().getText());
        assertEquals("Expected set timer button text to be "+SET, SET, timerPanel.getTimerButton().getText());
        assertTrue("Expected set timer button to be enabled", timerPanel.getTimerButton().isEnabled());
    }

    @Test
    public void testClickedOffHourFieldInvalidNumberInputFocusLost()
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
                if ("org.example.clock.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be -1", "-1", timerPanel.getHourField().getText());
        assertEquals("Expected set timer button text to be "+TIMER_HOUR_ERROR, TIMER_HOUR_ERROR, timerPanel.getTimerButton().getText());
        assertFalse("Expected set timer button to be disabled", timerPanel.getTimerButton().isEnabled());
    }

    @Test
    public void testClickedOffHourFieldInvalidNumberAgainInputFocusLost()
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
                            if ("org.example.clock.TimerPanel$2".equals(listener.getClass().getName()))
                            { listener.focusLost(focusEvent); }
                        }
                );

        assertEquals("Expected field to be 24", "24", timerPanel.getHourField().getText());
        assertEquals("Expected set timer button text to be "+TIMER_HOUR_ERROR, TIMER_HOUR_ERROR, timerPanel.getTimerButton().getText());
        assertFalse("Expected set timer button to be disabled", timerPanel.getTimerButton().isEnabled());
    }

    @Test
    public void testClickedOffHourFieldValidNumberInputFocusLostMinutesIsInvalid()
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
                if ("org.example.clock.TimerPanel$2".equals(listener.getClass().getName()))
                { listener.focusLost(focusEvent); }
            }
        );

        assertEquals("Expected field to be 1", "1", timerPanel.getHourField().getText());
        // TODO: FIX THIS
        //assertTrue("Expected minutes field to have focus", timerPanel.getMinuteField().isFocusOwner());
        assertEquals("Expected set timer button text to be "+SET, SET, timerPanel.getTimerButton().getText());
        assertFalse("Expected set timer button to be disabled", timerPanel.getTimerButton().isEnabled());
    }

}