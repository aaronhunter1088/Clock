package clock.entity;

import clock.exception.InvalidInputException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static clock.util.Constants.*;
import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link Alarm} class
 *
 * @author Michael Ball
 * @version 2.9
 */
class AlarmTest {

    private static final Logger logger = LogManager.getLogger(AlarmTest.class);

    private Clock clock;
    private final List<DayOfWeek> weekDays = List.of(DayOfWeek.MONDAY, TUESDAY, WEDNESDAY, THURSDAY, DayOfWeek.FRIDAY),
            weekendDays = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    @InjectMocks
    private Alarm alarm1, alarm2, weekDays730AmAlarm, weekend10AmAlarm;

    @Mock
    private AdvancedPlayer musicPlayerMock;

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting {}...", AlarmTest.class.getSimpleName()); }

    @BeforeEach
    void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        clock = new Clock();
        weekDays730AmAlarm = new Alarm("Weekdays Alarm", 7, 30, AM, weekDays, false, clock);
        weekend10AmAlarm = new Alarm("Weekends Alarm", 10, 0, AM, weekendDays, false, clock);
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
        assertFalse(alarm1.isActivatedToday(), "Alarm should not yet be triggered today");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 25})
    @DisplayName("Test Creating Alarm with Invalid Hours")
    void testCreateAlarmWithInvalidHours(int hours)
    {
        final var exception = assertThrows(InvalidInputException.class, () -> new Alarm("Invalid", hours, 0, AM, List.of(DayOfWeek.MONDAY), false, clock),
                "Creating an alarm with invalid hours should throw an exception");
        assertEquals("Hours must be between 0 and 12", exception.getMessage(), "Exception message should indicate invalid hours");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 60})
    @DisplayName("Test Creating Alarm with Invalid Minutes")
    void testCreateAlarmWithInvalidMinutes(int minutes)
    {
        final var exception = assertThrows(InvalidInputException.class, () -> new Alarm("Invalid", 1, minutes, AM, List.of(DayOfWeek.MONDAY), false, clock),
                "Creating an alarm with invalid hours should throw an exception");
        assertEquals("Minutes must be between 0 and 59", exception.getMessage(), "Exception message should indicate invalid hours");
    }

    @Test
    @DisplayName("Activate An Alarm")
    void testActivateAnAlarm()
    {
        alarm1 = weekDays730AmAlarm;
        alarm1.startAlarm();

        sleep(1000);

        assertNotNull(alarm1.getSelfThread(), "Alarm should be active");
        assertFalse(alarm1.isUpdatingAlarm(), "Alarm should not be in update mode");
        assertFalse(alarm1.isAlarmGoingOff(), "Alarm should not be going off");
    }

    @Test
    @DisplayName("Activate An Alarm that is already active does nothing")
    void testActivateAnAlarmThatIsAlreadyActive()
    {
        alarm1 = weekDays730AmAlarm;
        alarm1.startAlarm();

        sleep(1000);

        assertNotNull(alarm1.getSelfThread(), "Alarm should be active");
        assertFalse(alarm1.isUpdatingAlarm(), "Alarm should not be in update mode");
        assertFalse(alarm1.isAlarmGoingOff(), "Alarm should not be going off");

        // Attempt to start the alarm again
        alarm1.startAlarm();

        assertNotNull(alarm1.getSelfThread(), "Alarm should still be active");
        assertFalse(alarm1.isUpdatingAlarm(), "Alarm should not be in update mode");
        assertFalse(alarm1.isAlarmGoingOff(), "Alarm should not be going off");
    }

    @Test
    @DisplayName("Deactivate An Alarm")
    void testDeactivateAnAlarm()
    {
        alarm1 = weekDays730AmAlarm;
        alarm1.startAlarm();

        sleep(1000);

        assertNotNull(alarm1.getSelfThread(), "Alarm should be active");
        assertFalse(alarm1.isUpdatingAlarm(), "Alarm should not be in update mode");
        assertFalse(alarm1.isAlarmGoingOff(), "Alarm should not be going off");

        alarm1.stopAlarm();

        assertNull(alarm1.getSelfThread(), "Alarm should be inactive");
        assertFalse(alarm1.isUpdatingAlarm(), "Alarm should not be in update mode");
        assertFalse(alarm1.isAlarmGoingOff(), "Alarm should not be going off");
    }

    @Test
    @DisplayName("Test Alarm Is Triggered")
    void testTriggerAnAlarm()
    {
        alarm1 = weekDays730AmAlarm;

        // Simulate the clock reaching the alarm time
        LocalDate date = LocalDateTime.now().toLocalDate().with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        LocalTime time = LocalDateTime.now().toLocalTime().withHour(7).withMinute(30);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        clock.setTheTime(dateTime);

        alarm1.startAlarm();
        sleep(1000); // Allow time for the alarm to trigger

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(alarm1.isAlarmGoingOff(), "Alarm should be going off");
            assertTrue(alarm1.isActivatedToday(), "Alarm should be triggered today");
            assertNotNull(alarm1.getMusicPlayer(), "Music player should be set");
        });
    }

    @Test
    @DisplayName("Test Pausing An Alarm")
    void testPausingAnAlarm()
    {
        alarm1 = weekDays730AmAlarm;

        // Simulate the clock reaching the alarm time
        LocalDate date = LocalDateTime.now().toLocalDate().with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        LocalTime time = LocalDateTime.now().toLocalTime().withHour(7).withMinute(30);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        clock.setTheTime(dateTime);

        alarm1.startAlarm();
        sleep(1000); // Allow time for the alarm to trigger

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(alarm1.isAlarmGoingOff(), "Alarm should be going off");
            assertTrue(alarm1.isActivatedToday(), "Alarm should be triggered today");
            alarm1.pauseAlarm();
            assertTrue(alarm1.isPaused(), "Alarm should be paused");
        });
    }

    @Test
    @DisplayName("Test Resuming A Paused Alarm")
    void testResumingAPausedAlarm()
    {
        alarm1 = weekDays730AmAlarm;

        // Simulate the clock reaching the alarm time
        LocalDate date = LocalDateTime.now().toLocalDate().with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        LocalTime time = LocalDateTime.now().toLocalTime().withHour(7).withMinute(30);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        clock.setTheTime(dateTime);

        alarm1.startAlarm();
        sleep(1000); // Allow time for the alarm to trigger

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(alarm1.isAlarmGoingOff(), "Alarm should be going off");
            assertTrue(alarm1.isActivatedToday(), "Alarm should be triggered today");
            alarm1.pauseAlarm();
            assertTrue(alarm1.isPaused(), "Alarm should be paused");
            sleep(2000); // Simulate some time passing while paused
            alarm1.resumeAlarm();
            assertFalse(alarm1.isPaused(), "Alarm should not be paused anymore");
        });
    }

    @Test
    @DisplayName("Checking An Alarm when not equal to current time does nothing")
    void testCheckingAnAlarmWhenNotEqualToCurrentTimeDoesNothing()
    {
        alarm1 = weekDays730AmAlarm;

        // Simulate the clock not reaching the alarm time
        LocalDate date = LocalDateTime.now().toLocalDate().with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        LocalTime time = LocalDateTime.now().toLocalTime().withHour(6).withMinute(30);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        clock.setTheTime(dateTime);

        alarm1.startAlarm();
        sleep(1000);

        javax.swing.SwingUtilities.invokeLater(() -> assertNull(alarm1.getMusicPlayer(), "Music player should not be set yet"));
    }

    @Test
    @DisplayName("Trigger An Alarm Throws An Exception But Is Handled")
    void testTriggerAnAlarmThrowsExceptionButIsHandled() throws JavaLayerException
    {
        alarm1 = spy(weekDays730AmAlarm);
        alarm1.setMusicPlayer(musicPlayerMock);

        // Simulate the clock reaching the alarm time
        LocalDate date = LocalDateTime.now().toLocalDate();
        LocalTime time = LocalDateTime.now().toLocalTime().withHour(7).withMinute(30);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        clock.setTheTime(dateTime);

        doNothing().when(alarm1).setupMusicPlayer();
        doThrow(new RuntimeException("Mocked Music player error"))
            .when(musicPlayerMock).play();

        assertDoesNotThrow(() -> alarm1.triggerAlarm(), "An exception was thrown");
    }

    @Test
    @DisplayName("Test Get Shortened Days For Weekdays Alarm")
    void testGetShortenedDaysForWeekdaysAlarm()
    {
        alarm1 = weekDays730AmAlarm;
        List<String> expectedShortenedDays = new ArrayList<>() {{
            add(WEEKDAYS);
        }};
        List<String> actualShortenedDays = alarm1.getDaysShortened();

        assertIterableEquals(expectedShortenedDays, actualShortenedDays, "Shortened days should match the expected format for weekdays");
    }

    @Test
    @DisplayName("Test Get Shortened Days For Weekend Alarm")
    void testGetShortenedDaysForWeekendAlarm()
    {
        alarm1 = weekend10AmAlarm;
        List<String> expectedShortenedDays = new ArrayList<>() {{
            add(WEEKENDS);
        }};
        List<String> actualShortenedDays = alarm1.getDaysShortened();

        assertIterableEquals(expectedShortenedDays, actualShortenedDays, "Shortened days should match the expected format for weekdays");
    }

    @Test
    @DisplayName("Test Get Shortened Days For Random Days Alarm")
    void testGetShortenedDaysForRandomDaysAlarm()
    {
        alarm1 = new Alarm("Random Days Alarm", 8, 0, AM,
                List.of(DayOfWeek.MONDAY,
                        WEDNESDAY,
                        DayOfWeek.FRIDAY,
                        DayOfWeek.SUNDAY
                ), false, clock);
        List<String> expectedShortenedDays = new ArrayList<>() {{
            add(M); add(W);
            add(F); add(SU);
        }};
        List<String> actualShortenedDays = alarm1.getDaysShortened();

        assertIterableEquals(expectedShortenedDays, actualShortenedDays, "Shortened days should match the expected format for weekdays");
    }

    @Test
    @DisplayName("Test Get Shortened Days For All Days Alarm")
    void testGetShortenedDaysForAllDaysAlarm()
    {
        alarm1 = new Alarm("All Days Alarm", 8, 0, AM,
                List.of(DayOfWeek.MONDAY,
                        TUESDAY,
                        WEDNESDAY,
                        THURSDAY,
                        DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY,
                        DayOfWeek.SUNDAY
                ), false, clock);
        List<String> expectedShortenedDays = new ArrayList<>() {{
            add(EVERY_DAY);
        }};
        List<String> actualShortenedDays = alarm1.getDaysShortened();

        assertIterableEquals(expectedShortenedDays, actualShortenedDays, "Shortened days should match the expected format for weekdays");
    }

    @Test
    @DisplayName("Alarm Names Are As Expected")
    void testAlarmNamesAreAsExpected()
    {
        alarm1 = new Alarm("Alarm3", 7, 0, AM, weekDays, false, clock);
        alarm2 = new Alarm("", 6, 0, PM, weekendDays, false, clock);

        long currentAlarmsCounter = Alarm.alarmsCounter-1; // Subtract 1
        assertEquals("Alarm3", alarm1.getName(), "Alarm1 name should match");
        assertEquals("Alarm"+currentAlarmsCounter, alarm2.getName(), "Alarm2 name should match");
    }

    @ParameterizedTest
    @DisplayName("Test Alarm Equals Method")
    @MethodSource("checkForEquality")
    void testAlarmEqualsMethod(Object testAlarm, boolean expected)
    {
        alarm1 = new Alarm("Weekdays Alarm", 7, 30, AM, weekDays, false, clock);

        assertEquals(expected, alarm1.equals(testAlarm), "Expected " + expected + " but got " + testAlarm.equals(alarm1));
    }
    private static Stream<Arguments> checkForEquality()
    {
        // Return a stream of arguments, one being an object to compare against the alarm, and the expected result of the comparison
        // Updating an alarm and the clock is not compared against the alarm
        return Stream.of(
                /* Different name, days */ Arguments.of(new Alarm("Monday Alarm", 7, 30, AM, List.of(MONDAY), false, new Clock()), false),
                Arguments.of(new Alarm("Saturday Alarm", 10, 0, AM, List.of(SATURDAY), false, new Clock()), false),
                Arguments.of(new Clock(), false),
                // Compare each option
                /* Name different */ Arguments.of(new Alarm("Different Alarm", 7, 30, AM, List.of(MONDAY), false, new Clock()), false),
                /* Name same */ Arguments.of(new Alarm("Weekdays Alarm", 7, 30, AM, List.of(MONDAY), false, new Clock()), false),
                /* Name same, different hour */ Arguments.of(new Alarm("Weekdays Alarm", 6, 30, AM, List.of(MONDAY), false, new Clock()), false),
                /* Name same, same hour */ Arguments.of(new Alarm("Weekdays Alarm", 7, 30, AM, List.of(MONDAY), false, new Clock()), false),
                /* Name same, same hour, different minute */ Arguments.of(new Alarm("Weekdays Alarm", 7, 0, AM, List.of(MONDAY), false, new Clock()), false),
                /* Name same, same hour, same minute */ Arguments.of(new Alarm("Weekdays Alarm", 7, 30, AM, List.of(MONDAY), false, new Clock()), false),
                /* Name same, same hour, same minute, different AMPM */ Arguments.of(new Alarm("Weekdays Alarm", 7, 30, PM, List.of(MONDAY), false, new Clock()), false),
                /* Name same, same hour, same minute, same AMPM */ Arguments.of(new Alarm("Weekdays Alarm", 7, 30, AM, List.of(MONDAY), false, new Clock()), false),
                /* Name same, same hour, same minute, same AMPM, different days */ Arguments.of(new Alarm("Weekdays Alarm", 7, 30, AM, List.of(MONDAY, TUESDAY), false, new Clock()), false),
                /* Same everything */ Arguments.of(new Alarm("Weekdays Alarm", 7, 30, AM, List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY), false, new Clock()), true)
        );
    }

    @Test
    @DisplayName("Test Alarms compared against another")
    void testAlarmsComparedAgainstAnother()
    {
        alarm1 = new Alarm("Alarm1", 7, 30, AM, weekDays, false, clock);
        alarm2 = new Alarm("Work", 5, 0, AM, weekDays, false, clock);

        List<Alarm> expectedAlarms = List.of(alarm2, alarm1, weekDays730AmAlarm, weekend10AmAlarm);

        // test that the alarms are sorted by time
        List<Alarm> alarms = new ArrayList<>(4);
        alarms.add(alarm1);
        alarms.add(alarm2);
        alarms.add(weekDays730AmAlarm);
        alarms.add(weekend10AmAlarm);
        Collections.sort(alarms);

        assertIterableEquals(expectedAlarms, alarms, "Alarms should match");
    }

    // Helper methods
    @SuppressWarnings("SameParameterValue")
    private void sleep(int time)
    {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
