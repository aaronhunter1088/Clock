package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockitoAnnotations;

import static clock.entity.Panel.PANEL_ANALOGUE_CLOCK;
import static clock.util.Constants.AM;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests for the {@link ClockPanel} class
 *
 * @author Michael Ball
 * @version 2.9
 */
public class ClockPanelTests
{
    private static final Logger logger = LogManager.getLogger(ClockPanelTests.class);

    Clock clock;

    ClockPanel clockPanel;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting AnalogueClockPanelTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        clock = new Clock(11, 30, 0, JANUARY, WEDNESDAY, 1, 2025, AM); // 11:30 AM
        clockPanel = new AlarmPanel(new ClockFrame(clock));
    }

    @AfterEach
    void afterEach()
    {}

    @ParameterizedTest
    @DisplayName("Test Display Popup Message")
    @CsvSource({
            "Test Error,This is a test error message.,0", // ERROR
            "Test Title,This is a test message.,1", // INFO
            "Warning Title,This is a warning message.,2", // WARNING
            "Question Title,Is this a question?,3", // QUESTION
            "Plain Title,This is a plain message.,4" // PLAIN
    })
    void testDisplayPopupMessage(String title, String message, int optionPane)
    {
        assertDoesNotThrow(() -> clockPanel.displayPopupMessage(title, message, optionPane));
        logger.info("Popup message displayed successfully with title: {}", title);
    }
}
