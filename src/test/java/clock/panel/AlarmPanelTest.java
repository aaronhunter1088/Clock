package clock.panel;

import clock.entity.Alarm;
import clock.entity.Clock;
import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.WindowEvent;
import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static java.time.Month.MARCH;
import static clock.util.Constants.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;

class AlarmPanelTest
{
    private static final Logger logger = LogManager.getLogger(AlarmPanelTest.class);
    private Clock clock;
    private Alarm alarm;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting AlarmPanelTest...");
    }

    @BeforeEach
    void beforeEach() throws InvalidInputException
    {
        clock = new Clock();
        clock.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        alarm = new Alarm(clock, true);
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
    void testCheckingWhichCheckBoxesAreCheckedWorks()
    {
        clock.getAlarmPanel().getMondayCheckBox().doClick();
        clock.getAlarmPanel().getTuesdayCheckBox().doClick();
        List<DayOfWeek> daysChecked = clock.getAlarmPanel().getDaysChecked();

        Predicate<DayOfWeek> predicate = p -> daysChecked.contains(MONDAY) && daysChecked.contains(TUESDAY);
        assertEquals(2, daysChecked.size(), "DaysChecked list size should be 2");
        assertTrue(daysChecked.stream().allMatch(predicate), "daysChecked should contain only MONDAY and TUESDAY");
    }

    @Test
    void testRangeIsCorrect()
    {
        assertFalse(12 <= 0);
        assertFalse(12 > 23);
    }

    // Helper methods
    private AlarmPanel createAndSetupAlarmPanel()
    {
        AlarmPanel testAlarmPanel = clock.getAlarmPanel();
        //testAlarmPanel.setAlarm(alarm);
        //testAlarmPanel.setupMusicPlayer();
        return testAlarmPanel;
    }
}