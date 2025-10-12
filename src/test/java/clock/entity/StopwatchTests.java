package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static clock.util.Constants.STOPWATCH_READING_FORMAT;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Stopwatch} class
 *
 * @author Michael Ball
 * @version 2.9
 */
public class StopwatchTests {

    private static final Logger logger = LogManager.getLogger(StopwatchTests.class);

    private static Clock clock;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting {}...", StopwatchTests.class.getSimpleName());
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
    static void afterAll() { logger.info("Concluding {}", StopwatchTests.class.getSimpleName()); }

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
        assertNotEquals(0L, stopwatch.getStartPauseMilli());
    }

    @Test
    @DisplayName("Test Resuming a Paused Stopwatch")
    void testResumeAPausedStopwatch() throws InterruptedException
    {
        Stopwatch stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);

        stopwatch.startStopwatch();
        sleep(100); // Ensure some time passes
        stopwatch.pauseStopwatch();
        sleep(100); // Ensure some time passes while paused
        stopwatch.resumeStopwatch();

        assertFalse(stopwatch.isPaused());
        assertEquals(0L, stopwatch.getStartPauseMilli());
        assertNotEquals(0L, stopwatch.getTotalPausedMilli());
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
}
