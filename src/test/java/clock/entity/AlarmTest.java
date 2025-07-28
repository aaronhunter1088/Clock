package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * Tests for the {@link Alarm} class
 *
 * @author Michael Ball
 * @version 2.9
 */
public class AlarmTest {

    private static final Logger logger = LogManager.getLogger();

    private Clock clock;

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting ClockTest..."); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        clock.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @AfterEach
    void afterEach()
    {}

    @Test
    void testAddingTwoEqualAlarmsToListIsNotAllowed() {
        Alarm a1 = new Alarm(clock, false);

    }
}
