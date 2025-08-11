package clock.panel;

import clock.entity.Clock;
import clock.entity.Panel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link TimerPanel} class
 *
 * @author Michael Ball
 * @version 2.9
 */
class TimerPanelTests
{
    private static final Logger logger = LogManager.getLogger(TimerPanelTests.class);

    Clock clock;

    TimerPanel timerPanel;

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting TimerPanelTests..."); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        timerPanel = new TimerPanel(new ClockFrame(clock));
        timerPanel.getClockFrame().changePanels(Panel.PANEL_TIMER);
    }

    @AfterEach
    void afterEach()
    {}

    @ParameterizedTest
    @DisplayName("Test Validate Hours Text Field")
    @CsvSource({
            "0, true",
            "1, true",
            "2, true",
            "3, true",
            "4, true",
            "5, true",
            "6, true",
            "7, true",
            "8, true",
            "9, true",
            "10, true",
            "11, true",
            "12, true",
            "13, false",
            "24, false",
            "-1, false",
            "g, false",
    })
    void validateHoursTextField(String hours, boolean expected)
    {
        timerPanel.getNameTextField().setText(NAME);
        timerPanel.getHoursTextField().setText(EMPTY);
        timerPanel.getHoursTextField().setText(hours);
        assertEquals(expected, timerPanel.validateHoursTextField(), "Expected " + hours + " to be " + expected);
    }

    @ParameterizedTest
    @DisplayName("Test Validate Hours Text Field when isShowMilitaryTime is true")
    @CsvSource({
            "0, true", "1, true",
            "2, true", "3, true",
            "4, true", "5, true",
            "6, true", "7, true",
            "8, true", "9, true",
            "10, true", "11, true",
            "12, true", "13, true", // 1 PM
            "14, true", /* 2 PM */ "15, true", // 3 PM
            "16, true", /* 4 PM */ "17, true", // 5 PM
            "18, true", /* 6 PM */ "19, true", // 7 PM
            "20, true", /* 8 PM */ "21, true", // 9 PM
            "22, true", /* 10 PM */ "23, true", // 11 PM
            "24, false", /* Invalid hour */ "-1, false", // Invalid hour
            "g, false" // Invalid input
    })
    void validateHoursTextFieldWithMilitaryTime(String hours, boolean expected)
    {
        timerPanel.getClock().setShowMilitaryTime(true);
        timerPanel.getNameTextField().setText(NAME);
        timerPanel.getHoursTextField().setText(EMPTY);
        timerPanel.getHoursTextField().setText(hours);
        assertEquals(expected, timerPanel.validateHoursTextField(), "Expected " + hours + " to be " + expected);
    }

    @ParameterizedTest
    @DisplayName("Test Validate Minutes Text Field")
    @CsvSource({
            "0, true", "1, true", "2, true", "3, true",
            "4, true", "5, true", "6, true", "7, true",
            "8, true", "9, true", "10, true", "11, true",
            "12, true", "13, true", "14, true", "15, true",
            "16, true", "17, true", "18, true", "19, true",
            "20, true", "21, true", "22, true", "23, true",
            "24, true", "25, true", "26, true", "27, true",
            "28, true", "29, true", "30, true", "31, true",
            "32, true", "33, true", "34, true", "35, true",
            "36, true", "37, true", "38, true", "39, true",
            "40, true", "41, true", "42, true", "43, true",
            "44, true", "45, true", "46, true", "47, true",
            "48, true", "49, true", "50, true", "51, true",
            "52, true", "53, true", "54, true", "55, true",
            "56, true", "57, true", "58, true", "59, true",
            "60, false", "-1, false", "g, false", "@, false"
    })
    void validateMinutesTextField(String minutes, boolean expected)
    {
        timerPanel.getNameTextField().setText(NAME);
        timerPanel.getMinutesTextField().setText(EMPTY);
        timerPanel.getMinutesTextField().setText(minutes);
        assertEquals(expected, timerPanel.validateMinutesTextField(), "Expected " + minutes + " to be " + expected);
    }

    @ParameterizedTest
    @DisplayName("Test Validate Seconds Text Field")
    @CsvSource({
            "0, true", "1, true", "2, true", "3, true",
            "4, true", "5, true", "6, true", "7, true",
            "8, true", "9, true", "10, true", "11, true",
            "12, true", "13, true", "14, true", "15, true",
            "16, true", "17, true", "18, true", "19, true",
            "20, true", "21, true", "22, true", "23, true",
            "24, true", "25, true", "26, true", "27, true",
            "28, true", "29, true", "30, true", "31, true",
            "32, true", "33, true", "34, true", "35, true",
            "36, true", "37, true", "38, true", "39, true",
            "40, true", "41, true", "42, true", "43, true",
            "44, true", "45, true", "46, true", "47, true",
            "48, true", "49, true", "50, true", "51, true",
            "52, true", "53, true", "54, true", "55, true",
            "56, true", "57, true", "58, true", "59, true",
            "60, false", "-1, false", "g, false", "@, false"
    })
    void validateSecondsTextField(String seconds, boolean expected)
    {
        timerPanel.getNameTextField().setText(NAME);
        timerPanel.getSecondsTextField().setText(EMPTY);
        timerPanel.getSecondsTextField().setText(seconds);
        assertEquals(expected, timerPanel.validateSecondsTextField(), "Expected " + seconds + " to be " + expected);
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

    @Test
    @DisplayName("Create 2 Timers Using GUI")
    void testCreateTwoTimersUsingGUI() throws InterruptedException, InvocationTargetException
    {
        AtomicReference<clock.entity.Timer> timer1 = new AtomicReference<>(new clock.entity.Timer(0, 4, 0, clock));
        AtomicReference<clock.entity.Timer> timer2 = new AtomicReference<>(new clock.entity.Timer(0, 5, 0, clock));

        SwingUtilities.invokeLater(() -> {
            try {
                timerPanel.getHoursTextField().grabFocus();
                timerPanel.getHoursTextField().setText(Integer.toString(timer1.get().getHours()));
                //sleep(2000);
                timerPanel.getMinutesTextField().grabFocus();
                timerPanel.getMinutesTextField().setText(Integer.toString(timer1.get().getMinutes()));
                //sleep(2000);
                timerPanel.getSecondsTextField().grabFocus();
                timerPanel.getSetTimerButton().setEnabled(timerPanel.validTextFields());
                //sleep(2000);
                timerPanel.getSetTimerButton().doClick();

                timerPanel.getHoursTextField().grabFocus();
                timerPanel.getHoursTextField().setText(Integer.toString(timer2.get().getHours()));
                //sleep(2000);
                timerPanel.getMinutesTextField().grabFocus();
                timerPanel.getMinutesTextField().setText(Integer.toString(timer2.get().getMinutes()));
                //sleep(2000);
                timerPanel.getSecondsTextField().grabFocus();
                timerPanel.getSetTimerButton().setEnabled(timerPanel.validTextFields());
                //sleep(2000);
                timerPanel.getSetTimerButton().doClick();

                sleep(1000); // timer1 now at 3:58, timer2 at 4:59
                timerPanel.getClock().getListOfTimers().getFirst().pauseTimer(); // timer1 paused, timer2 at 4:58
                sleep(3000);

                timer1.set(timerPanel.getClock().getListOfTimers().get(0));
                timer2.set(timerPanel.getClock().getListOfTimers().get(1));

                assertSame(0, timer1.get().getHours());
                assertSame(3, timer1.get().getMinutes());
                assertSame(58, timer1.get().getSeconds());

                assertSame(0, timer2.get().getHours());
                assertSame(4, timer2.get().getMinutes());
                assertSame(55, timer2.get().getSeconds());
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}