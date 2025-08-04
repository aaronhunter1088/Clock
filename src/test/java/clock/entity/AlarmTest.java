package clock.entity;

import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Alarm} class
 *
 * @author Michael Ball
 * @version 2.9
 */
class AlarmTest {

    private static final Logger logger = LogManager.getLogger();

    private Clock clock;
    private List<DayOfWeek> weekDays = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            weekendDays = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private Alarm alarm1, alarm2,
            morningAlarm = new Alarm("Morning Alarm", 7, 30, AM, weekDays, false, clock);

    private List<Alarm> alarms = new ArrayList<>();

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting {}...", AlarmTest.class.getSimpleName()); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
    }

    @AfterEach
    void afterEach()
    {}

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", AlarmTest.class.getSimpleName()); }

    @Test
    @DisplayName("Create an Alarm")
    void testCreateAnAlarm()
    {
        alarm1 = new Alarm();

        String expectedName = ALARM + Alarm.alarmsCounter;
        assertEquals(expectedName, alarm1.getName(), "Alarm name should be empty");
    }

    @Test
    @DisplayName("Create an Alarm with a name")
    void testCreateAnAlarmWithName()
    {
        String name = "Work";
        alarm1 = new Alarm(name, 7, 0, AM, weekDays, false, clock);

        assertEquals(name, alarm1.getName(), "Alarm name should match the provided name");
        assertEquals(7, alarm1.getHours(), "Alarm hour should be 7");
        assertEquals(0, alarm1.getMinutes(), "Alarm minute should be 0");
        assertEquals(AM, alarm1.getAMPM(), "Alarm AM/PM should be AM");
        assertSame(weekDays, alarm1.getDays(), "Alarm days should be the same");
        assertFalse(alarm1.isUpdatingAlarm(), "Alarm should not be updating");
        assertFalse(alarm1.isAlarmGoingOff(), "Alarm should not be going off");
        assertFalse(alarm1.isTriggeredToday(), "Alarm should not yet be triggered today");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 25})
    @DisplayName("Test Creating Alarm with Invalid Hours")
    void testCreateAlarmWithInvalidHours(int hours) {
        final var exception = assertThrows(InvalidInputException.class, () -> new Alarm("Invalid", hours, 0, AM, List.of(DayOfWeek.MONDAY), false, clock),
                "Creating an alarm with invalid hours should throw an exception");
        assertEquals("Hours must be between 0 and 12", exception.getMessage(), "Exception message should indicate invalid hours");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 60})
    @DisplayName("Test Creating Alarm with Invalid Minutes")
    void testCreateAlarmWithInvalidMinutes(int minutes) {
        final var exception = assertThrows(InvalidInputException.class, () -> new Alarm("Invalid", 1, minutes, AM, List.of(DayOfWeek.MONDAY), false, clock),
                "Creating an alarm with invalid hours should throw an exception");
        assertEquals("Minutes must be between 0 and 59", exception.getMessage(), "Exception message should indicate invalid hours");
    }

    @Test
    @DisplayName("Activate An Alarm")
    void testActivateAnAlarm() {
        alarm1 = morningAlarm;
        alarm1.startAlarm();

        sleep(1000);

        assertNotNull(alarm1.getSelfThread(), "Alarm should be active after activation");
        assertFalse(alarm1.isUpdatingAlarm(), "Alarm should not be updating after activation");
    }

    // Helper methods
    private void sleep(int time)
    {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
