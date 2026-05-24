package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Lap} class
 *
 * @author michael ball
 * @version since 2.9
 */
class LapTest {

    private static final Logger logger = LogManager.getLogger(LapTest.class);

    private static Clock clock;
    private static Stopwatch stopwatch;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting {}...", LapTest.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        stopwatch = new Stopwatch("Test Stopwatch", false, false, clock);
    }

    @AfterEach
    void afterEach()
    {}

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", LapTest.class.getSimpleName()); }

    @Test
    @DisplayName("Test Lap Creation")
    void testLapCreation()
    {
        Lap lap = new Lap(1, 123456, 123456, stopwatch);
        assertNotNull(lap);
        assertEquals(1, lap.getLapNumber());
        assertNotNull(lap.getFormattedDuration());
        assertNotNull(lap.getFormattedLapTime());
        assertEquals(stopwatch, lap.getStopwatch());
        assertEquals("02:03.456", lap.getFormattedDuration());
        assertEquals("02:03.456", lap.getFormattedLapTime());
        assertNotNull(lap.toString());

        Lap lap2 = new Lap(2, 123987, 123987, stopwatch);
        assertEquals(-1, lap.compareTo(lap2));

        assertEquals(0, lap.compareTo(lap.clone()));
        assertEquals(0, lap2.compareTo(lap2.clone()));

        assertEquals(1, lap2.compareTo(lap));
    }

    @Test
    @DisplayName("Test Lap equals - same values are equal")
    void testLapEqualsSameValues()
    {
        final Lap lap1 = new Lap(1, 123456, 123456, stopwatch);
        final Lap lap2 = new Lap(1, 123456, 123456, stopwatch);
        assertEquals(lap1, lap2);
    }

    @Test
    @DisplayName("Test Lap equals - different lapNumber are not equal")
    void testLapEqualsDifferentLapNumber()
    {
        final Lap lap1 = new Lap(1, 123456, 123456, stopwatch);
        final Lap lap2 = new Lap(2, 123456, 123456, stopwatch);
        assertNotEquals(lap1, lap2);
    }

    @Test
    @DisplayName("Test Lap equals - different duration are not equal")
    void testLapEqualsDifferentDuration()
    {
        final Lap lap1 = new Lap(1, 1000, 123456, stopwatch);
        final Lap lap2 = new Lap(1, 2000, 123456, stopwatch);
        assertNotEquals(lap1, lap2);
    }

    @Test
    @DisplayName("Test Lap equals - different lapTime are not equal")
    void testLapEqualsDifferentLapTime()
    {
        final Lap lap1 = new Lap(1, 123456, 1000, stopwatch);
        final Lap lap2 = new Lap(1, 123456, 2000, stopwatch);
        assertNotEquals(lap1, lap2);
    }

    @Test
    @DisplayName("Test Lap equals - not equal to null")
    void testLapEqualsNull()
    {
        final Lap lap = new Lap(1, 123456, 123456, stopwatch);
        assertNotEquals(null, lap);
    }

    @Test
    @DisplayName("Test Lap equals - not equal to different type")
    void testLapEqualsDifferentType()
    {
        final Lap lap = new Lap(1, 123456, 123456, stopwatch);
        assertNotEquals("not a lap", lap);
    }

    @Test
    @DisplayName("Test Lap hashCode - equal laps have the same hash code")
    void testLapHashCodeEqual()
    {
        final Lap lap1 = new Lap(1, 123456, 123456, stopwatch);
        final Lap lap2 = new Lap(1, 123456, 123456, stopwatch);
        assertEquals(lap1.hashCode(), lap2.hashCode());
    }

    @Test
    @DisplayName("Test Lap hashCode - different laps have different hash codes")
    void testLapHashCodeDifferent()
    {
        final Lap lap1 = new Lap(1, 123456, 123456, stopwatch);
        final Lap lap2 = new Lap(2, 999999, 999999, stopwatch);
        assertNotEquals(lap1.hashCode(), lap2.hashCode());
    }

    @Test
    @DisplayName("Test Lap setters update values correctly")
    void testLapSetters()
    {
        final Lap lap = new Lap(1, 123456, 123456, stopwatch);
        final Stopwatch newStopwatch = new Stopwatch("New Stopwatch", false, false, clock);

        lap.setLapNumber(5);
        lap.setDuration(99999L);
        lap.setLapTime(88888L);
        lap.setStopwatch(newStopwatch);

        assertEquals(5, lap.getLapNumber());
        assertEquals(99999L, lap.getDuration());
        assertEquals(88888L, lap.getLapTime());
        assertEquals(newStopwatch, lap.getStopwatch());
    }

    @Test
    @DisplayName("Test Lap toString contains expected content")
    void testLapToStringContent()
    {
        final Lap lap = new Lap(1, 123456, 123456, stopwatch);
        final String result = lap.toString();

        assertTrue(result.contains("Test Stopwatch"), "toString should contain stopwatch name");
        assertTrue(result.contains("lapNumber=1"), "toString should contain lapNumber");
    }

    @Test
    @DisplayName("Test Lap clone is independent of original")
    void testLapCloneIndependence()
    {
        final Lap original = new Lap(1, 123456, 123456, stopwatch);
        final Lap clone = original.clone();

        assertEquals(original, clone);

        clone.setLapNumber(99);
        clone.setDuration(999L);

        assertEquals(1, original.getLapNumber());
        assertEquals(123456L, original.getDuration());
    }

    @ParameterizedTest
    @DisplayName("Test Lap formatted duration for various millisecond values")
    @MethodSource("durationFormatScenarios")
    void testGetFormattedDuration(long millis, String expected)
    {
        final Lap lap = new Lap(1, millis, millis, stopwatch);
        assertEquals(expected, lap.getFormattedDuration());
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> durationFormatScenarios()
    {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(0L,      "00:00.000"),
            org.junit.jupiter.params.provider.Arguments.of(1000L,   "00:01.000"),
            org.junit.jupiter.params.provider.Arguments.of(60000L,  "01:00.000"),
            org.junit.jupiter.params.provider.Arguments.of(61500L,  "01:01.500"),
            org.junit.jupiter.params.provider.Arguments.of(123456L, "02:03.456")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Lap formatted lap time for various millisecond values")
    @MethodSource("lapTimeFormatScenarios")
    void testGetFormattedLapTime(long millis, String expected)
    {
        final Lap lap = new Lap(1, millis, millis, stopwatch);
        assertEquals(expected, lap.getFormattedLapTime());
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> lapTimeFormatScenarios()
    {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(0L,      "00:00.000"),
            org.junit.jupiter.params.provider.Arguments.of(999L,    "00:00.999"),
            org.junit.jupiter.params.provider.Arguments.of(59999L,  "00:59.999"),
            org.junit.jupiter.params.provider.Arguments.of(120000L, "02:00.000"),
            org.junit.jupiter.params.provider.Arguments.of(123456L, "02:03.456")
        );
    }

    @Test
    @DisplayName("Test printStackTrace with a message logs without exception")
    void testPrintStackTraceWithMessage()
    {
        final Lap lap = new Lap(1, 123456, 123456, stopwatch);
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> lap.printStackTrace(e, "custom message"));
    }

    @Test
    @DisplayName("Test printStackTrace with null message logs without exception")
    void testPrintStackTraceWithNullMessage()
    {
        final Lap lap = new Lap(1, 123456, 123456, stopwatch);
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> lap.printStackTrace(e, null));
    }
}
