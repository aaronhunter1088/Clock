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
        clock = new Clock();
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

    @Test
    @DisplayName("Test hashCode - equal timers have the same hash code")
    void testHashCodeEqualTimers()
    {
        timer1 = new Timer(1, 2, 3, "Test Timer", clock);
        timer2 = new Timer(1, 2, 3, "Test Timer", clock);
        assertEquals(timer1.hashCode(), timer2.hashCode(), "Equal timers should have same hash code");
    }

    @Test
    @DisplayName("Test hashCode - different timers have different hash codes")
    void testHashCodeDifferentTimers()
    {
        timer1 = new Timer(1, 2, 3, "Timer A", clock);
        timer2 = new Timer(0, 5, 0, "Timer B", clock);
        assertNotEquals(timer1.hashCode(), timer2.hashCode(), "Different timers should have different hash codes");
    }

    @Test
    @DisplayName("Test printStackTrace with message logs without exception")
    void testPrintStackTraceWithMessage()
    {
        timer1 = new Timer(0, 1, 0, clock);
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> timer1.printStackTrace(e, "custom message"));
    }

    @Test
    @DisplayName("Test printStackTrace with null message logs without exception")
    void testPrintStackTraceWithNullMessage()
    {
        timer1 = new Timer(0, 1, 0, clock);
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> timer1.printStackTrace(e, null));
    }

    @ParameterizedTest
    @DisplayName("Test getHoursAsStr leading zero for single-digit hours")
    @MethodSource("hoursAsStrScenarios")
    void testGetHoursAsStrLeadingZero(int hours, String expected)
    {
        timer1 = new Timer(hours, 0, 0, clock);
        assertEquals(expected, timer1.getHoursAsStr());
    }
    static Stream<Arguments> hoursAsStrScenarios()
    {
        return Stream.of(
            Arguments.of(0,  "00"),
            Arguments.of(1,  "01"),
            Arguments.of(9,  "09"),
            Arguments.of(10, "10"),
            Arguments.of(12, "12")
        );
    }

    @ParameterizedTest
    @DisplayName("Test getMinutesAsStr leading zero for single-digit minutes")
    @MethodSource("minutesAsStrScenarios")
    void testGetMinutesAsStrLeadingZero(int minutes, String expected)
    {
        timer1 = new Timer(0, minutes, 0, clock);
        assertEquals(expected, timer1.getMinutesAsStr());
    }
    static Stream<Arguments> minutesAsStrScenarios()
    {
        return Stream.of(
            Arguments.of(0,  "00"),
            Arguments.of(5,  "05"),
            Arguments.of(9,  "09"),
            Arguments.of(10, "10"),
            Arguments.of(59, "59")
        );
    }

    @ParameterizedTest
    @DisplayName("Test getSecondsAsStr leading zero for single-digit seconds")
    @MethodSource("secondsAsStrScenarios")
    void testGetSecondsAsStrLeadingZero(int seconds, String expected)
    {
        timer1 = new Timer(0, 0, seconds, clock);
        assertEquals(expected, timer1.getSecondsAsStr());
    }
    static Stream<Arguments> secondsAsStrScenarios()
    {
        return Stream.of(
            Arguments.of(0,  "00"),
            Arguments.of(5,  "05"),
            Arguments.of(9,  "09"),
            Arguments.of(10, "10"),
            Arguments.of(59, "59")
        );
    }

    @Test
    @DisplayName("Test getClock returns the expected clock reference")
    void testGetClock()
    {
        timer1 = new Timer(0, 1, 0, clock);
        assertEquals(clock, timer1.getClock());
    }

    @Test
    @DisplayName("Test getCountDown returns the initial LocalTime")
    void testGetCountDown()
    {
        timer1 = new Timer(1, 30, 45, clock);
        assertNotNull(timer1.getCountDown());
        assertEquals(1, timer1.getCountDown().getHour());
        assertEquals(30, timer1.getCountDown().getMinute());
        assertEquals(45, timer1.getCountDown().getSecond());
    }

    @Test
    @DisplayName("Test setTriggered and isTriggered")
    void testSetAndGetTriggered()
    {
        timer1 = new Timer(0, 1, 0, clock);
        assertFalse(timer1.isTriggered(), "Timer should not be triggered initially");

        timer1.setTriggered(true);
        assertTrue(timer1.isTriggered(), "Timer should be triggered after setTriggered(true)");

        timer1.setTriggered(false);
        assertFalse(timer1.isTriggered(), "Timer should not be triggered after setTriggered(false)");
    }

    @Test
    @DisplayName("Test startTimer when already started does not create a new thread")
    void testStartTimerWhenAlreadyStartedDoesNothing()
    {
        timer1 = new Timer(0, 5, 0, clock);
        timer1.startTimer();
        final Thread firstThread = timer1.getSelfThread();

        timer1.startTimer();

        assertSame(firstThread, timer1.getSelfThread(), "Starting an already-started timer should not replace the thread");
    }

    @Test
    @DisplayName("Test resetTimer restores countdown to initial values")
    void testResetTimerRestoresCountdown()
    {
        timer1 = new Timer(0, 1, 0, "Reset Test", clock);
        timer1.startTimer();
        sleep(3000); // countdown ticks down a few seconds

        timer1.resetTimer();

        assertFalse(timer1.isTimerGoingOff(), "Timer should not be going off after reset");
        assertFalse(timer1.isStarted(), "Timer should not be started after reset");
        assertFalse(timer1.isPaused(), "Timer should not be paused after reset");
        assertFalse(timer1.isTriggered(), "Timer should not be triggered after reset");
        assertEquals(1, timer1.getCountDown().getMinute(), "Countdown minutes should be reset to 1");
        assertEquals(0, timer1.getCountDown().getSecond(), "Countdown seconds should be reset to 0");
    }

    @Test
    @DisplayName("Test setters update timer state correctly")
    void testSettersUpdateState()
    {
        timer1 = new Timer(0, 5, 0, clock);
        final Clock newClock = new Clock();

        timer1.setClock(newClock);
        assertEquals(newClock, timer1.getClock());

        timer1.setName("Renamed Timer");
        assertEquals("Renamed Timer", timer1.getName());

        timer1.setPaused(true);
        assertTrue(timer1.isPaused());
        timer1.setPaused(false);
        assertFalse(timer1.isPaused());

        timer1.setTimerGoingOff(true);
        assertTrue(timer1.isTimerGoingOff());
        timer1.setTimerGoingOff(false);
        assertFalse(timer1.isTimerGoingOff());

        timer1.setStarted(true);
        assertTrue(timer1.isStarted());
        timer1.setStarted(false);
        assertFalse(timer1.isStarted());

        timer1.setTriggered(true);
        assertTrue(timer1.isTriggered());
        timer1.setTriggered(false);
        assertFalse(timer1.isTriggered());

        timer1.setMusicPlayer(null);
        assertNull(timer1.getMusicPlayer());
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
