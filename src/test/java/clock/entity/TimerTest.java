package clock.entity;

import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Timer} class
 *
 * @author michael ball
 * @version since 2.0
 */
class TimerTest
{
    private static final Logger logger = LogManager.getLogger(TimerTest.class);

    private Clock clock;
    private Timer timer1, timer2;

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting {}...", TimerTest.class.getSimpleName()); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock(true);
    }

    @AfterEach
    void afterEach()
    {
        if (timer1 != null) timer1.stopTimer();
        if (timer2 != null) timer2.stopTimer();
    }

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", TimerTest.class.getSimpleName()); }

    @Test
    @DisplayName("Create a Timer")
    void testCreatingATimer()
    {
        String expectedName = "(Timer"+(Timer.timersCounter+1)+") 00:00:00";
        assertEquals(expectedName, new clock.entity.Timer().toString());
    }

    @Test
    @DisplayName("Create a 1 hour Timer")
    void testCreateA1HourTimer()
    {
        timer1 = new clock.entity.Timer(1, 0, 0);

        String expectedName = "(Timer"+Timer.timersCounter+") 01:00:00";
        assertEquals(expectedName, timer1.toString(), "Strings don't match");
        assertSame(1, timer1.getHours());
        assertSame(0, timer1.getMinutes());
        assertSame(0, timer1.getSeconds());
        assertFalse(timer1.isTimerGoingOff());
        assertFalse(timer1.isPaused());
    }

    @Test
    @DisplayName("Create a 5 min Timer")
    void testCreatingA5MinuteTimer()
    {
        timer1 = new clock.entity.Timer(0, 5, 0, clock);
        timer2 = new clock.entity.Timer(0, 5, 0, "Two", clock);

        String expectedName = "(Timer"+(Timer.timersCounter-1)+") 00:05:00";
        assertEquals(expectedName, timer1.toString(), "Strings don't match");
        assertSame(0, timer1.getHours());
        assertSame(5, timer1.getMinutes());
        assertSame(0, timer1.getSeconds());
        assertFalse(timer1.isTimerGoingOff());
        assertFalse(timer1.isPaused());

        assertEquals("(Two) 00:05:00", timer2.toString(), "Strings don't match");
        assertSame(0, timer2.getHours());
        assertSame(5, timer2.getMinutes());
        assertSame(0, timer2.getSeconds());
        assertFalse(timer2.isTimerGoingOff());
        assertFalse(timer2.isPaused());
    }

    @Test
    @DisplayName("Create 2 Timers, Pause Both, Resume 1")
    void testCreateTwoTimers()
    {
        timer1 = new Timer(0, 4, 0, clock);
        timer2 = new Timer(0, 5, 0, clock);

        timer1.startTimer();
        sleep(1000); // timer1 now at 3:59, timer2 "doesn't exist yet"
        timer2.startTimer();
        sleep(1000); // timer1 now at 3:58, timer2 at 4:59

        timer1.pauseTimer(); // timer1 paused, timer2 at 4:58

        sleep(3000);

        timer2.pauseTimer();

        assertSame(3, timer1.getCountDown().getMinute());

        assertSame(4, timer2.getCountDown().getMinute());

        timer1.resumeTimer();

        assertFalse(timer1.isPaused(), "Timer1 should not be paused after resuming");
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 24, 25, 100 })
    @DisplayName("Create Invalid Timer by Hour")
    void testCreateInvalidTimerByHour(int hours)
    {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> new Timer(hours, 0, 0, clock));
        assertEquals("Hours must be between 0 and 12", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 100 })
    @DisplayName("Create Invalid Timer by Minute")
    void testCreateInvalidTimerByMinute(int minutes)
    {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> new Timer(1, minutes, 0, clock));
        assertEquals("Minutes must be between 0 and 59", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 100 })
    @DisplayName("Create Invalid Timer by Second")
    void testCreateInvalidTimerBySecond(int seconds)
    {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> new Timer(1, 1, seconds, clock));
        assertEquals("Seconds must be between 0 and 59", exception.getMessage());
    }

    @Test
    @DisplayName("Timer with name reaches zero")
    void testTimerReachesZero()
    {
        timer1 = new Timer(0, 0, 5, "Test Timer", clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");
        assertEquals("(Test Timer) 00:00:00", timer1.toString(), "Timer did not reach zero as expected");
    }

    @Test
    @DisplayName("Timer with 'no name' reaches zero")
    void testTimerWithNoNameReachesZero()
    {
        timer1 = new Timer(0, 0, 5, clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");
        String expectedName = "(Timer"+Timer.timersCounter+") 00:00:00";
        assertEquals(expectedName, timer1.toString(), "Timer did not reach zero as expected");
    }

    @Test
    @DisplayName("Timer with tabbed over 'no name' reaches zero")
    void testTimerWithTabbedOverNoNameReachesZero()
    {
        timer1 = new Timer(0, 0, 5, "Timer "+(Timer.timersCounter+1), clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");
        String expecting = "(Timer " + Timer.timersCounter + ") 00:00:00";
        assertEquals(expecting, timer1.toString(), "Timer did not match expecting");
    }

    @Test
    @DisplayName("Timer at zero is reset")
    void testTimerReachesZeroThenIsReset()
    {
        timer1 = new Timer(0, 0, 5, "Test Timer", clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");

        timer1.resetTimer();

        assertFalse(timer1.isTimerGoingOff(), "Timer should not be going off after reset");
        assertFalse(timer1.isStarted(), "Timer should not have been started");
        assertFalse(timer1.isTriggered(), "Timer should not have been triggered");
    }

    @Test
    @DisplayName("Timer is stopped midway")
    void testTimerStoppedMidway()
    {
        timer1 = new Timer(0, 0, 10, "Test Timer", clock);
        timer1.startTimer();
        sleep(3000); // wait for 3 seconds

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(timer1.isStarted(), "Timer should have been started");
            assertFalse(timer1.isTriggered(), "Timer should not have been triggered yet");
        });

        timer1.stopTimer();

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertFalse(timer1.isStarted(), "Timer should not be started after stopping");
            assertFalse(timer1.isTriggered(), "Timer should not have been triggered after stopping");
            assertEquals("(Test Timer) 00:00:07", timer1.toString(), "Timer did not stop at the expected time");
        });
    }

    @ParameterizedTest
    @DisplayName("Test Timer Equals Method")
    @MethodSource("checkForTimerEquality")
    void testTimerEqualsMethod(Object testTimer, boolean expected)
    {
        timer1 = new Timer(1, 2, 3, "Test Timer", clock);

        assertEquals(expected, timer1.equals(testTimer), "Expected " + expected + " but got " + timer1.equals(testTimer));
    }
    private static Stream<Arguments> checkForTimerEquality()
    {
        // Return a stream of arguments, one being an object to compare against the timer, and the expected result of the comparison
        // The clock is not compared against the timer
        return Stream.of(
                /* Not a Timer */ Arguments.of(new Clock(), false),
                // Compare each option
                /* Hours different */ Arguments.of(new Timer(0, 2, 3, "Test Timer", new Clock()), false),
                /* Hours same */ Arguments.of(new Timer(1, 0, 3, "Test Timer", new Clock()), false),
                /* Hours same, different minutes */ Arguments.of(new Timer(1, 0, 3, "Test Timer", new Clock()), false),
                /* Hours same, same minutes */ Arguments.of(new Timer(1, 2, 0, "Test Timer", new Clock()), false),
                /* Hours same, same minutes, different seconds */ Arguments.of(new Timer(1, 2, 4, "Test Timer", new Clock()), false),
                /* Hours same, same minutes, same seconds */ Arguments.of(new Timer(1, 2, 3, "Test1 Timer", new Clock()), false),
                /* Hours same, same minutes, same seconds, different name */ Arguments.of(new Timer(1, 2, 3, "Test1 Timer", new Clock()), false),
                /* Hours same, same minutes, same seconds, same name */ Arguments.of(new Timer(1, 2, 3, "Test Timer", new Clock()), true)
        );
    }

    @Test
    @DisplayName("Test Alarms compared against another")
    void testAlarmsComparedAgainstAnother()
    {
        timer1 = new Timer(0, 0, 5, "Timer1", clock);
        timer2 = new Timer(1, 2, 3, "Timer2", clock);

        List<Timer> expectedTimers = List.of(timer1, timer2);

        // test that the alarms are sorted by time
        List<Timer> timers = new ArrayList<>(2);
        timers.add(timer2);
        timers.add(timer1);
        Collections.sort(timers);

        assertIterableEquals(expectedTimers, timers, "Timers should match");
    }

    // Helper methods
    private void sleep(int time)
    {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
