package clock.panel;

import clock.entity.Clock;
import clock.exception.InvalidInputException;
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
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.Month.JANUARY;
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
    void validateHoursTextField()
    {
        timerPanel.getNameTextField().setText(NAME);
        timerPanel.getHoursTextField().setText(EMPTY);
        assertTrue(timerPanel.validateHoursTextField(), "Expected value to be Hour");

        timerPanel.getHoursTextField().setText("13");
        assertFalse(timerPanel.validateHoursTextField(), "Expected value to be invalid");

        timerPanel.getHoursTextField().setText("24");
        assertFalse(timerPanel.validateHoursTextField(), "Expected value to be invalid");

        timerPanel.getHoursTextField().setText("-1");
        assertFalse(timerPanel.validateHoursTextField(), "Expected value to be invalid");

        clock = new Clock(13, 30, 0, JANUARY, WEDNESDAY, 1, 2025, PM);
        timerPanel.getClockFrame().setClock(clock);
        timerPanel.getHoursTextField().setText("13");
        assertTrue(timerPanel.validateHoursTextField(), "Expected value to be 13");
    }

    @Test
    void validateMinutesTextField()
    {
        timerPanel.getNameTextField().setText(NAME);
        timerPanel.getMinutesTextField().setText(EMPTY);
        assertTrue(timerPanel.validateMinutesTextField(), "Expected value to be Min");

        timerPanel.getMinutesTextField().setText("13");
        assertTrue(timerPanel.validateMinutesTextField(), "Expected value to be 13");

        timerPanel.getMinutesTextField().setText("60");
        assertFalse(timerPanel.validateMinutesTextField(), "Expected value to be invalid");

        timerPanel.getMinutesTextField().setText("-1");
        assertFalse(timerPanel.validateMinutesTextField(), "Expected value to be invalid");
    }

    @Test
    void validateSecondsTextField()
    {
        timerPanel.getNameTextField().setText(NAME);
        timerPanel.getSecondsTextField().setText(EMPTY);
        assertTrue(timerPanel.validateSecondsTextField(), "Expected value to be SEC");

        timerPanel.getSecondsTextField().setText("13");
        assertTrue(timerPanel.validateSecondsTextField(), "Expected value to be 13");

        timerPanel.getSecondsTextField().setText("59");
        assertTrue(timerPanel.validateSecondsTextField(), "Expected value to be 59");

        timerPanel.getSecondsTextField().setText("60");
        assertFalse(timerPanel.validateSecondsTextField(), "Expected value to be invalid");

        timerPanel.getSecondsTextField().setText("-1");
        assertFalse(timerPanel.validateSecondsTextField(), "Expected value to be invalid");
    }

    @Test
    void resetTimerFields()
    {
        timerPanel.resetTimerPanel();

        assertTrue(timerPanel.getNameTextField().getText().isBlank(), "Expected name field to be blank");
        assertTrue(timerPanel.getHoursTextField().getText().isBlank(), "Expected hours field to be blank");
        assertTrue(timerPanel.getMinutesTextField().getText().isBlank(), "Expected minutes field to be blank");
        assertTrue(timerPanel.getSecondsTextField().getText().isBlank(), "Expected seconds field to be blank");
        assertTrue(timerPanel.getClock().getListOfTimers().isEmpty(), "Expected clock list to be empty");
    }

//    @Test
//    void pauseCountDown() throws InterruptedException
//    {
//
//    }

    @Test
    void testHittingSetButtonCreates5SecondTimer()
    {
        // set up the timer
        timerPanel.getHoursTextField().setText(ZERO);
        timerPanel.getMinutesTextField().setText(ZERO);
        timerPanel.getSecondsTextField().setText("5");
        // click
        timerPanel.getSetTimerButton().doClick();

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(timerPanel.getSetTimerButton().isEnabled());
            assertTrue(timerPanel.areAllBlank());
            assertTrue(timerPanel.getNameTextField().getText().isBlank(), "Expected name field to be blank");
            assertSame(1, timerPanel.getClock().getListOfTimers().size(), "Expected clock to have 1 timer");
        });
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
        timerPanel.getHoursTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHoursTextField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getHoursTextField().dispatchEvent(focusEvent);

        assertEquals(EMPTY, timerPanel.getHoursTextField().getText(), "Expected field to be emptied");
    }

    @Test
    void testClickedOnMinutesFieldFocusGained()
    {
        // user opens timer panel, and clicks on hour field
        timerPanel.getMinutesTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getMinutesTextField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getMinutesTextField().dispatchEvent(focusEvent);

        assertEquals(EMPTY, timerPanel.getMinutesTextField().getText(), "Expected field to be emptied");
    }

    @Test
    void testClickedOnSecondsFieldFocusGained()
    {
        // user opens timer panel, and clicks on hour field
        timerPanel.getSecondsTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getSecondsTextField(),
                FocusEvent.FOCUS_GAINED,
                false,
                timerPanel
        );
        timerPanel.getSecondsTextField().dispatchEvent(focusEvent);

        assertEquals(EMPTY, timerPanel.getSecondsTextField().getText(), "Expected field to be emptied");
    }

    @Test
    void testClickedOffHourFieldNoInputFocusLost()
    {
        timerPanel.getHoursTextField().setText(EMPTY);
        // user opens timer panel, clicks on hour field, then clicks off hour field
        timerPanel.getHoursTextField().setFocusable(true);
        // Request focus on the hourField
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHoursTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );
        timerPanel.getHoursTextField().dispatchEvent(focusEvent);

        assertEquals(EMPTY, timerPanel.getHoursTextField().getText(), "Expected field to be "+EMPTY);
    }

    @Test
    void testClickedOffHourFieldEmptyInputFocusLost()
    {
        timerPanel.getHoursTextField().setText(SPACE);
        // user opens timer panel, clicks space and clicks off hour field
        timerPanel.getHoursTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHoursTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHoursTextField().dispatchEvent(focusEvent);

        assertEquals(SPACE, timerPanel.getHoursTextField().getText(), "Expected field to be "+ SPACE);
    }

    @Test
    void testClickedOffHourFieldValidNumberInputFocusLost()
    {
        timerPanel.getHoursTextField().setText("1"); // start a 1 hour timer
        // user opens timer panel, enters number and clicks off hour field
        timerPanel.getHoursTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHoursTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHoursTextField().dispatchEvent(focusEvent);

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertEquals("1", timerPanel.getHoursTextField().getText(), "Expected field to be 1");
            assertEquals(SET, timerPanel.getSetTimerButton().getText(), "Expected set timer button text to be "+SET);
            assertTrue(timerPanel.getSetTimerButton().isEnabled(), "Expected set timer button to be enabled");
        });
    }

    @Test
    void testClickedOffHourFieldInvalidNumberInputFocusLost()
    {
        timerPanel.getHoursTextField().setText("-1"); // start a -1 hour timer
        // user opens timer panel, enters invalid number and clicks off hour field
        timerPanel.getHoursTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHoursTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHoursTextField().dispatchEvent(focusEvent);

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertEquals("-1", timerPanel.getHoursTextField().getText(), "Expected field to be -1");
            assertEquals(TIMER_HOUR_ERROR_12, timerPanel.getSetTimerButton().getText(), "Expected set timer button text to be: "+TIMER_HOUR_ERROR_12);
            assertFalse(timerPanel.getSetTimerButton().isEnabled(), "Expected set timer button to be disabled");
        });
    }

    @Test
    void testClickedOffHourFieldInvalidNumberAgainInputFocusLost()
    {
        clock = new Clock(15, 30, 0, JANUARY, WEDNESDAY, 1, 2025, PM);
        timerPanel.getClockFrame().setClock(clock);
        timerPanel.getHoursTextField().setText("24"); // start a 24 hour timer
        // user opens timer panel, enters number and clicks off hour field
        timerPanel.getHoursTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getHoursTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getHoursTextField().dispatchEvent(focusEvent);

        assertEquals("24", timerPanel.getHoursTextField().getText(), "Expected field to be 24");
    }

    @Test
    void testClickedOffHourFieldValidNumberInputFocusLostMinutesIsInvalid()
    {
        timerPanel.getHoursTextField().setText("1"); // start a 1 hour timer
        timerPanel.getMinutesTextField().setText("M"); // invalid minute
        timerPanel.getMinutesTextField().setFocusable(true);
        FocusEvent focusEvent = new FocusEvent(
                timerPanel.getMinutesTextField(),
                FocusEvent.FOCUS_LOST,
                false,
                timerPanel
        );

        timerPanel.getMinutesTextField().dispatchEvent(focusEvent);

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertEquals("1", timerPanel.getHoursTextField().getText(), "Expected field to be 1");
            assertTrue(timerPanel.getMinutesTextField().isFocusOwner(), "Expected minutes field to have focus");
            assertEquals(SET, timerPanel.getSetTimerButton().getText(), "Expected set timer button text to be "+SET);
        });
    }

}