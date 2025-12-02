package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

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
}
