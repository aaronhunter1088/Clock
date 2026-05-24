package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.stream.Stream;

import static clock.util.Constants.STOPWATCH_READING_FORMAT;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Stopwatch} class
 *
 * @author michael ball
 * @version since 2.9
 */
public class StopwatchTest {

    private static final Logger logger = LogManager.getLogger(StopwatchTest.class);

    private static Clock clock;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting {}...", StopwatchTest.class.getSimpleName());
        Stopwatch.stopwatchCounter = 0L;
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
    }

    @AfterEach
    void afterEach()
    {}

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", StopwatchTest.class.getSimpleName()); }

    @Test
    @DisplayName("Test Stopwatch Creation")
    void testStopwatchCreation()
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);
        assertNotNull(stopwatch);
        assertEquals("Test Stopwatch", stopwatch.getName());
        assertFalse(stopwatch.isStarted());
        assertFalse(stopwatch.isPaused());
        assertNotNull(stopwatch.getLaps());
    }

    @Test
    @DisplayName("Test Stopwatch Starts")
    void testStopwatchStarts()
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);
        stopwatch.startStopwatch();

        assertTrue(stopwatch.isStarted());
        assertNotNull(stopwatch.getSelfThread());

        assertNotNull(stopwatch.elapsedFormatted(stopwatch.getAccumMilli(), STOPWATCH_READING_FORMAT)); // used for parsing in analogue panel
    }

    @Test
    @DisplayName("Test Starting a Stopwatch That Is Already Started")
    void testStartingAStopwatchThatIsAlreadyStarted() throws InterruptedException
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);
        stopwatch.startStopwatch();
        sleep(100); // Ensure some time passes
        stopwatch.startStopwatch();

        assertTrue(stopwatch.isStarted());
        assertNotNull(stopwatch.getSelfThread());
    }

    @Test
    @DisplayName("Test Stopping a Stopwatch")
    void testStoppingAStopwatch() throws InterruptedException
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);
        stopwatch.startStopwatch();
        sleep(100); // Ensure some time passes
        stopwatch.stopStopwatch();
    }

    @Test
    @DisplayName("Test Pause a Stopwatch")
    void testPauseAStopwatch() throws InterruptedException
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);
        stopwatch.startStopwatch();
        sleep(100); // Ensure some time passes
        stopwatch.pauseStopwatch();

        assertTrue(stopwatch.isPaused());
        assertNotEquals(0L, stopwatch.getPausedMilli());
    }

    @Test
    @DisplayName("Test Resuming a Paused Stopwatch")
    void testResumeAPausedStopwatch() throws InterruptedException
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);

        stopwatch.startStopwatch();

        long startDeadline = System.currentTimeMillis() + 1000;
        while (!stopwatch.isStarted() && System.currentTimeMillis() < startDeadline) {
            Thread.sleep(10);
        }
        assertTrue(stopwatch.isStarted(), "Stopwatch did not start in time");

        stopwatch.pauseStopwatch();

        long pauseDeadline = System.currentTimeMillis() + 1000;
        while (!stopwatch.isPaused() && System.currentTimeMillis() < pauseDeadline) {
            Thread.sleep(10);
        }
        assertTrue(stopwatch.isPaused(), "Stopwatch did not pause in time");
        assertNotEquals(0L, stopwatch.getPausedMilli());

        stopwatch.resumeStopwatch();
        assertFalse(stopwatch.isPaused());
        assertEquals(0L, stopwatch.getPausedAccumMilli());
    }

    @Test
    @DisplayName("Test Recording a Lap")
    void testRecordingALap() throws InterruptedException
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);
        stopwatch.startStopwatch();
        sleep(100); // Ensure some time passes

        stopwatch.recordLap();

        assertEquals(1, stopwatch.getLaps().size());
        assertNotEquals(0L, stopwatch.getLastLapMarkMilli());
    }

    @ParameterizedTest
    @DisplayName("Test Comparing Two Stopwatches")
    @MethodSource("stopwatches")
    void testComparingTwoStopwatches(Stopwatch sw1, Stopwatch sw2, int value) throws InterruptedException
    {
        int comparison = sw1.compareTo(sw2);
        assertEquals(value, comparison);
    }
    private static Stream<Arguments> stopwatches() {
        return Stream.of(
                Arguments.of(new Stopwatch("Stopwatch 1", false, false, clock), new Stopwatch("Stopwatch 2", false, false, clock), -1),
                Arguments.of(new Stopwatch("Stopwatch 2", false, false, clock), new Stopwatch("Stopwatch 2", false, false, clock), 0),
                Arguments.of(new Stopwatch("Stopwatch 2", false, false, clock), new Stopwatch("Stopwatch 1", false, false, clock), 1)
        );
    }

    @Test
    @DisplayName("Test compareTo when both stopwatches have null names")
    void testCompareToWithBothNullNames()
    {
        final Stopwatch sw1 = new Stopwatch(null, false, false, clock);
        final Stopwatch sw2 = new Stopwatch(null, false, false, clock);
        assertEquals(0, sw1.compareTo(sw2), "Two null-named stopwatches should be equal");
    }

    @Test
    @DisplayName("Test compareTo when left stopwatch has null name")
    void testCompareToWithLeftNullName()
    {
        final Stopwatch sw1 = new Stopwatch(null, false, false, clock);
        final Stopwatch sw2 = new Stopwatch("Stopwatch 2", false, false, clock);
        assertEquals(-1, sw1.compareTo(sw2), "Null-named stopwatch should sort before named one");
    }

    @Test
    @DisplayName("Test compareTo when right stopwatch has null name")
    void testCompareToWithRightNullName()
    {
        final Stopwatch sw1 = new Stopwatch("Stopwatch 1", false, false, clock);
        final Stopwatch sw2 = new Stopwatch(null, false, false, clock);
        assertEquals(1, sw1.compareTo(sw2), "Named stopwatch should sort after null-named one");
    }

    @Test
    @DisplayName("Test Stopwatch equals - same name are equal")
    void testStopwatchEqualsSameName()
    {
        final Stopwatch sw1 = new Stopwatch("My Stopwatch", false, false, clock);
        final Stopwatch sw2 = new Stopwatch("My Stopwatch", false, false, clock);
        assertEquals(sw1, sw2);
    }

    @Test
    @DisplayName("Test Stopwatch equals - different names are not equal")
    void testStopwatchEqualsDifferentName()
    {
        final Stopwatch sw1 = new Stopwatch("Stopwatch A", false, false, clock);
        final Stopwatch sw2 = new Stopwatch("Stopwatch B", false, false, clock);
        assertNotEquals(sw1, sw2);
    }

    @Test
    @DisplayName("Test Stopwatch equals - not equal to null")
    void testStopwatchEqualsNull()
    {
        final Stopwatch sw = new Stopwatch("My Stopwatch", false, false, clock);
        assertNotEquals(null, sw);
    }

    @Test
    @DisplayName("Test Stopwatch equals - not equal to different type")
    void testStopwatchEqualsDifferentType()
    {
        final Stopwatch sw = new Stopwatch("My Stopwatch", false, false, clock);
        assertNotEquals("not a stopwatch", sw);
    }

    @Test
    @DisplayName("Test hashCode - equal stopwatches share the same hash code")
    void testHashCodeEqualStopwatches()
    {
        final Stopwatch sw1 = new Stopwatch("My Stopwatch", false, false, clock);
        final Stopwatch sw2 = new Stopwatch("My Stopwatch", false, false, clock);
        assertEquals(sw1.hashCode(), sw2.hashCode());
    }

    @Test
    @DisplayName("Test hashCode - different stopwatches have different hash codes")
    void testHashCodeDifferentStopwatches()
    {
        final Stopwatch sw1 = new Stopwatch("Stopwatch A", false, false, clock);
        final Stopwatch sw2 = new Stopwatch("Stopwatch B", false, false, clock);
        assertNotEquals(sw1.hashCode(), sw2.hashCode());
    }

    @Test
    @DisplayName("Test toString contains expected content")
    void testToStringContent()
    {
        final Stopwatch sw = new Stopwatch("My Stopwatch", false, false, clock);
        final String result = sw.toString();
        assertTrue(result.contains("My Stopwatch"), "toString should contain the name");
        assertTrue(result.contains("started=false"), "toString should show started state");
        assertTrue(result.contains("paused=false"), "toString should show paused state");
        assertTrue(result.contains("laps=0"), "toString should show lap count");
    }

    @Test
    @DisplayName("Test toString shows pausedAccumMilli when paused")
    void testToStringWhenPaused() throws InterruptedException
    {
        final Stopwatch sw = new Stopwatch("My Stopwatch", false, false, clock);
        sw.startStopwatch();
        sleep(100);
        sw.pauseStopwatch();

        final String result = sw.toString();
        assertTrue(result.contains("paused=true"), "toString should show paused=true");
        assertTrue(result.contains("pausedAccumMilli="), "toString should include pausedAccumMilli when paused");

        sw.stopStopwatch();
    }

    @Test
    @DisplayName("Test stopStopwatch resets all fields")
    void testStopStopwatchResetsAllFields() throws InterruptedException
    {
        final Stopwatch sw = new Stopwatch("My Stopwatch", false, false, clock);
        sw.startStopwatch();
        sleep(100);
        sw.stopStopwatch();

        assertNull(sw.getName(), "Name should be null after stop");
        assertFalse(sw.isPaused(), "isPaused should be false after stop");
        assertFalse(sw.isStarted(), "isStarted should be false after stop");
        assertNull(sw.getClock(), "Clock should be null after stop");
        assertNull(sw.getSelfThread(), "SelfThread should be null after stop");
        assertNull(sw.getLaps(), "Laps should be null after stop");
        assertEquals(0L, sw.getStartMilli(), "startMilli should be 0 after stop");
        assertEquals(0L, sw.getAccumMilli(), "accumMilli should be 0 after stop");
        assertEquals(0L, sw.getLastLapMarkMilli(), "lastLapMarkMilli should be 0 after stop");
        assertEquals(0L, sw.getPausedAccumMilli(), "pausedAccumMilli should be 0 after stop");
        assertEquals(0L, sw.getTotalPausedMilli(), "totalPausedMilli should be 0 after stop");
        assertEquals(0L, sw.getPausedMilli(), "pausedMilli should be 0 after stop");
    }

    @Test
    @DisplayName("Test elapsedFormatted produces correct output for known values")
    void testElapsedFormattedKnownValues()
    {
        final Stopwatch sw = new Stopwatch("Test", false, false, clock);
        assertEquals("00:00.000", sw.elapsedFormatted(0L, STOPWATCH_READING_FORMAT));
        assertEquals("00:01.000", sw.elapsedFormatted(1000L, STOPWATCH_READING_FORMAT));
        assertEquals("01:00.000", sw.elapsedFormatted(60000L, STOPWATCH_READING_FORMAT));
        assertEquals("02:03.456", sw.elapsedFormatted(123456L, STOPWATCH_READING_FORMAT));
    }

    @Test
    @DisplayName("Test recording multiple laps")
    void testRecordingMultipleLaps() throws InterruptedException
    {
        final Stopwatch sw = new Stopwatch("Lap Stopwatch", false, false, clock);
        sw.startStopwatch();
        sleep(50);
        sw.recordLap();
        sleep(50);
        sw.recordLap();
        sleep(50);
        sw.recordLap();

        assertEquals(3, sw.getLaps().size(), "Should have 3 laps recorded");
        assertEquals(1, sw.getLaps().get(0).getLapNumber());
        assertEquals(2, sw.getLaps().get(1).getLapNumber());
        assertEquals(3, sw.getLaps().get(2).getLapNumber());

        sw.stopStopwatch();
    }

    @Test
    @DisplayName("Test resumeStopwatch when not paused does nothing")
    void testResumeWhenNotPausedDoesNothing()
    {
        final Stopwatch sw = new Stopwatch("Test", false, false, clock);
        sw.startStopwatch();
        assertFalse(sw.isPaused(), "Stopwatch should not be paused initially");

        // resuming when not paused should be a no-op
        sw.resumeStopwatch();

        assertFalse(sw.isPaused(), "Stopwatch should still not be paused");
        assertEquals(0L, sw.getPausedAccumMilli(), "pausedAccumMilli should remain 0");

        sw.stopStopwatch();
    }

    @Test
    @DisplayName("Test printStackTrace with message logs without exception")
    void testPrintStackTraceWithMessage()
    {
        final Stopwatch sw = new Stopwatch("Test", false, false, clock);
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> sw.printStackTrace(e, "custom message"));
    }

    @Test
    @DisplayName("Test printStackTrace with null message logs without exception")
    void testPrintStackTraceWithNullMessage()
    {
        final Stopwatch sw = new Stopwatch("Test", false, false, clock);
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> sw.printStackTrace(e, null));
    }

    @Test
    @DisplayName("Test setters update stopwatch state correctly")
    void testSettersUpdateState()
    {
        final Stopwatch sw = new Stopwatch("Test", false, false, clock);

        sw.setName("Renamed");
        assertEquals("Renamed", sw.getName());

        sw.setPaused(true);
        assertTrue(sw.isPaused());
        sw.setPaused(false);
        assertFalse(sw.isPaused());

        sw.setStarted(true);
        assertTrue(sw.isStarted());
        sw.setStarted(false);
        assertFalse(sw.isStarted());

        final Clock newClock = new Clock();
        sw.setClock(newClock);
        assertEquals(newClock, sw.getClock());

        final Duration d = Duration.ofSeconds(30);
        sw.setDuration(d);
        assertEquals(d, sw.getDuration());

        sw.setPausedAccumMilli(5000L);
        assertEquals(5000L, sw.getPausedAccumMilli());

        final var newLaps = new ArrayList<Lap>();
        sw.setLaps(newLaps);
        assertSame(newLaps, sw.getLaps());

        sw.setLaps(null);
        assertNull(sw.getLaps());
    }
}
