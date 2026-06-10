package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static clock.util.Constants.AM;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for the {@link ClockPanel} class
 *
 * @author michael ball
 * @version since 2.9
 */
public class ClockPanelTest
{
    private static final Logger logger = LogManager.getLogger(ClockPanelTest.class);

    ClockFrame clockFrame;
    Clock clock;

    AlarmPanel alarmPanel;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting ClockPanelTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock(11, 30, 0, JANUARY, WEDNESDAY, 1, 2025, AM); // 11:30 AM
        clockFrame = new ClockFrame(clock);
        alarmPanel = new AlarmPanel(clockFrame);
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        logger.info("Test complete. Closing the clock...");
        EventQueue.invokeAndWait(() -> {
            clockFrame.stop();
            clockFrame.dispose();
        });
        assertFalse(clockFrame.isVisible());
    }

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", ClockPanelTest.class.getSimpleName()); }

    @ParameterizedTest
    @DisplayName("Test Display Popup Message")
    @CsvSource({
            "Test Error,This is a test error message.,0", // ERROR
            "Test Title,This is a test message.,1", // INFO
            "Warning Title,This is a warning message.,2", // WARNING
            "Question Title,Is this a question?,3", // QUESTION
            "Plain Title,This is a plain message.,4" // PLAIN
    })
    @Disabled
    void testDisplayPopupMessage(String title, String message, int optionPane)
    {
        assertDoesNotThrow(() -> alarmPanel.displayPopupMessage(title, message, optionPane));
        logger.info("Popup message displayed successfully with title: {}", title);
    }

    @Test
    @DisplayName("printStackTrace logs exception details without throwing")
    void testPrintStackTraceDoesNotThrow()
    {
        final Exception e = new RuntimeException("test exception");
        assertDoesNotThrow(() -> alarmPanel.printStackTrace(e, "custom message"),
                "printStackTrace should not throw an exception");
    }

    @Test
    @DisplayName("printStackTrace with null message does not throw")
    void testPrintStackTraceWithNullMessageDoesNotThrow()
    {
        final Exception e = new RuntimeException("test exception with null message detail");
        assertDoesNotThrow(() -> alarmPanel.printStackTrace(e, null),
                "printStackTrace should handle a null custom message without throwing");
    }
}
