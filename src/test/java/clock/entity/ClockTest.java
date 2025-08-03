package clock.entity;

import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;
import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Clock class
 *
 * @author Michael Ball
 * @version 1.0
 */
class ClockTest
{
    private static final Logger logger = LogManager.getLogger(ClockTest.class);

    private Clock clock;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting ClockTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock(true);
    }

    @AfterEach
    void afterEach()
    {}

    @Test
    void testDefaultClock()
    {
        clock = new Clock();

        assertNotNull(clock.getCurrentDateTime(), "The current time should not be null");
    }

    @Test
    void testProvidedValuesClockStandardTime()
    {
        int hours = 10;
        int minutes = 30;
        int seconds = 45;
        Month month = AUGUST;
        DayOfWeek dayOfWeek = SATURDAY;
        int dayOfMonth = 15;
        int year = 2023;
        String ampm = AM;
        clock = new Clock(hours, minutes, seconds, month, dayOfWeek, dayOfMonth, year, ampm);

        assertNotNull(clock.getCurrentDateTime(), "The current time should not be null");
        assertEquals(hours, clock.getHours(), "Hours should match");
        assertEquals(minutes, clock.getMinutes(), "Minutes should match");
        assertEquals(seconds, clock.getSeconds(), "Seconds should match");
        assertEquals(month, clock.getMonth(), "Month should match");
        assertEquals(dayOfWeek, clock.getDayOfWeek(), "Day of week should match");
        assertEquals(dayOfMonth, clock.getDayOfMonth(), "Day of month should match");
        assertEquals(year, clock.getYear(), "Year should match");
        assertEquals(ampm, clock.getAMPM(), "AM/PM should match");
        assertFalse(clock.isShowMilitaryTime(), "Military time should be off");
        assertTrue(clock.isDaylightSavingsTimeEnabled(), "Daylight savings time should be on");
    }

    @Test
    void testProvidedValuesClockMilitaryTime()
    {
        int hours = 13; // 1 PM in military time
        int minutes = 30;
        int seconds = 45;
        Month month = AUGUST;
        DayOfWeek dayOfWeek = SATURDAY;
        int dayOfMonth = 15;
        int year = 2023;
        String ampm = PM;
        clock = new Clock(hours, minutes, seconds, month, dayOfWeek, dayOfMonth, year, ampm);

        assertNotNull(clock.getCurrentDateTime(), "The current time should not be null");
        assertEquals(hours, clock.getHours(), "Hours should match");
        assertEquals(minutes, clock.getMinutes(), "Minutes should match");
        assertEquals(seconds, clock.getSeconds(), "Seconds should match");
        assertEquals(month, clock.getMonth(), "Month should match");
        assertEquals(dayOfWeek, clock.getDayOfWeek(), "Day of week should match");
        assertEquals(dayOfMonth, clock.getDayOfMonth(), "Day of month should match");
        assertEquals(year, clock.getYear(), "Year should match");
        assertEquals(ampm, clock.getAMPM(), "AM/PM should match");
        assertTrue(clock.isShowMilitaryTime(), "Military time should be on");
        assertTrue(clock.isDaylightSavingsTimeEnabled(), "Daylight savings time should be on");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 24})
    void testProvidedInvalidHoursThrowsIllegalArgumentException(int hours)
    {
        final var exception = assertThrows(IllegalArgumentException.class, () -> new Clock(hours, 0, 0, JANUARY, MONDAY, 1, 2022, AM),
                "Expected IllegalArgumentException for invalid hours: " + hours);
        if (hours < 0) {
            assertEquals("Hours must be between 0 and 12", exception.getMessage(), "Expected message for hours < 0");
        } else {
            assertEquals("Hours must be between 0 and 23", exception.getMessage(), "Expected message for hours > 23");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 60})
    void testProvidedInvalidMinutesThrowsIllegalArgumentException(int minutes)
    {
        final var exception = assertThrows(IllegalArgumentException.class, () -> new Clock(10, minutes, 0, JANUARY, MONDAY, 1, 2022, AM),
                "Expected IllegalArgumentException for invalid minutes: " + minutes);
        if (minutes < 0) {
            assertEquals("Minutes must be between 0 and 59", exception.getMessage(), "Expected message for minutes < 0");
        } else {
            assertEquals("Minutes must be between 0 and 59", exception.getMessage(), "Expected message for minutes > 59");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 60})
    void testProvidedInvalidSecondsThrowsIllegalArgumentException(int seconds)
    {
        final var exception = assertThrows(IllegalArgumentException.class, () -> new Clock(10, 0, seconds, JANUARY, MONDAY, 1, 2022, AM),
                "Expected IllegalArgumentException for invalid seconds: " + seconds);
        if (seconds < 0) {
            assertEquals("Seconds must be between 0 and 59", exception.getMessage(), "Expected message for seconds < 0");
        } else {
            assertEquals("Seconds must be between 0 and 59", exception.getMessage(), "Expected message for seconds > 59");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 32})
    void testProvidedInvalidDayOfMonthThrowsIllegalArgumentException(int dayOfMonth)
    {
        final var exception = assertThrows(IllegalArgumentException.class, () -> new Clock(10, 0, 0, JANUARY, MONDAY, dayOfMonth, 2022, AM),
                "Expected IllegalArgumentException for invalid dayOfMonth: " + dayOfMonth);
        assertEquals("The day of month must be between 1 and 31", exception.getMessage(), "Expected message for invalid dayOfMonth");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 999})
    void testProvidedInvalidYearThrowsIllegalArgumentException(int year)
    {
        final var exception = assertThrows(IllegalArgumentException.class, () -> new Clock(10, 0, 0, JANUARY, MONDAY, 1, year, AM),
                "Expected IllegalArgumentException for invalid year: " + year);
        assertEquals("Year must be greater than 1000", exception.getMessage(), "Expected message for invalid year");
    }

    @Test
    void testBeginningDayLightSavingsTimeIsProperlySet()
    {
        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(MARCH);
        clock.setDayOfWeek(THURSDAY);
        clock.setDayOfMonth(2);
        clock.setYear(2021);
        clock.setAMPM(PM);
        clock.setTheCurrentTime();
        assertEquals(14, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth(), "For 2021, Beginning DST Day should be 14th");
        assertEquals(7, clock.getEndDaylightSavingsTimeDate().getDayOfMonth(), "For 2021, Ending DST Day should be 7th");

        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(MARCH);
        clock.setDayOfWeek(THURSDAY);
        clock.setDayOfMonth(3);
        clock.setYear(2022);
        clock.setAMPM(PM);
        clock.setTheCurrentTime();
        assertEquals(13, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth(), "For 2022, Beginning DST Day should be 13th");
        assertEquals(6, clock.getEndDaylightSavingsTimeDate().getDayOfMonth(), "For 2022, Ending DST Day should be 6th");
    }

    @Test
    void testIsTodayDaylightSavingsDayReturnsFalseWhenNotBeginningDST()
    {
        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(MARCH);
        clock.setDayOfWeek(THURSDAY);
        clock.setDayOfMonth(3);
        clock.setYear(2022);
        clock.setAMPM(PM);
        clock.setTheCurrentTime();
        assertFalse(clock.isTodayDaylightSavingsTime());
    }

    @Test
    void testLocalDatesAreCompared()
    {
        LocalDate today = LocalDate.now();
        assertEquals(clock.getDate(), today, "Dates should be equal");

        LocalDate anotherDay = LocalDate.of(2022, 8, 29);
        assertNotEquals(clock.getDate(), anotherDay, "Date should not be equal");
    }

    @Test
    void testIsDateDaylightSavingsDayReturnsFalseWhenNotEndingDST()
    {
        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(NOVEMBER);
        clock.setDayOfWeek(SATURDAY);
        clock.setDayOfMonth(5);
        clock.setYear(2022);
        clock.setAMPM(PM);
        // needed to update certain values now that we reset clock time and date
        clock.setTheCurrentTime();
        assertFalse(clock.isTodayDaylightSavingsTime());
    }

    @Test
    void testIsTodayDaylightSavingsDayReturnsTrueWhenIsBeginningDST()
    {
        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(MARCH);
        clock.setDayOfWeek(SUNDAY);
        clock.setDayOfMonth(13);
        clock.setYear(2022);
        clock.setAMPM(PM);
        clock.setTheCurrentTime();
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    void testIsTodayDaylightSavingsDayReturnsTrueWhenIsEndingDST()
    {
        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(NOVEMBER);
        clock.setDayOfWeek(SUNDAY);
        clock.setDayOfMonth(6);
        clock.setYear(2022);
        clock.setAMPM(PM);
        clock.setTheCurrentTime();
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    void testIsTodayDaylightSavingsDay()
    {
        LocalDate today = LocalDate.now();
        clock.setHours(12);
        clock.setMinutes(0);
        clock.setSeconds(0);
        clock.setMonth(today.getMonth());
        clock.setDayOfWeek(today.getDayOfWeek());
        clock.setDayOfMonth(today.getDayOfMonth());
        clock.setYear(today.getYear());
        clock.setAMPM(AM);
        clock.setTheCurrentTime();
        boolean actual = clock.isTodayMatchesDSTDate();
        boolean expected = clock.getBeginDaylightSavingsTimeDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")).equals(today.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))) ||
                clock.getEndDaylightSavingsTimeDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")).equals(today.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        if (actual) {
            if (expected) logger.info("Today is DST day. This is expected.");
            else logger.error("Today should be DST but clock thinks it's not");
            assertTrue(expected, "Expected today to be DST day");
        }
        else {
            if (!expected) logger.info("Today is not DST day. This is expected.");
            else logger.error("Today should not be DST but clock thinks it is");
            assertFalse(expected, "Expected today to not be DST");
        }
    }

    @Test
    void testClockBecomesAMWhenMidnightStarts()
    {
        clock.setHours(11);
        clock.setMinutes(59);
        clock.setSeconds(57);
        clock.setMonth(FEBRUARY);
        clock.setDayOfWeek(SUNDAY);
        clock.setDayOfMonth(21);
        clock.setYear(2021);
        clock.setAMPM(PM);
        clock.setTheCurrentTime();
        tick(3);
        assertEquals(12, clock.getHours(), "Hours should be 12");
        assertEquals(0, clock.getMinutes(), "Minutes should be 0");
        assertEquals(0, clock.getSeconds(), "Seconds should be 0");
        assertEquals(FEBRUARY, clock.getMonth(), "Month should be February");
        assertEquals(MONDAY, clock.getDayOfWeek(), "Day should be Monday");
        assertEquals(22, clock.getDayOfMonth(), "Date should be 22");
        assertEquals(2021, clock.getYear(), "Year should be 2021");
        assertEquals(AM, clock.getAMPM(), "AMPM should be AM");
    }

    @Test
    void testWhenClockInMilitaryTimeAlarmStillTriggers() throws InvalidInputException
    {
        clock.setShowMilitaryTime(true);
        clock.setHours(13);
        clock.setMinutes(0);
        clock.setSeconds(59);
        clock.setMonth(MARCH);
        clock.setDayOfWeek(SATURDAY);
        clock.setDayOfMonth(6);
        clock.setYear(2021);
        clock.setAMPM(PM);
        clock.setTheCurrentTime();
        Alarm alarm = new Alarm("Test", 1, 1, PM, false, new ArrayList<>(){{add(SATURDAY);}}, clock);
        clock.getListOfAlarms().add(alarm);

        assertEquals(1, clock.getListOfAlarms().size());

        tick(4);
        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(clock.getListOfAlarms().getFirst().isAlarmGoingOff());
        });
    }

    @Test
    @DisplayName("Test turn off dst setting")
    void testTurnOffDSTSetting()
    {
        LocalDate endDSTDate = clock.getEndDaylightSavingsTimeDate();
        clock.setHours(1);
        clock.setMinutes(59);
        clock.setSeconds(50);
        clock.setMonth(endDSTDate.getMonth());
        clock.setDayOfWeek(endDSTDate.getDayOfWeek());
        clock.setDayOfMonth(endDSTDate.getDayOfMonth());
        clock.setYear(endDSTDate.getYear());
        clock.setAMPM(AM);
        clock.setTheCurrentTime();
        clock.getClockFrame().getClockMenuBar().getToggleDSTSetting().doClick(); // DST turning off; enabled:false
        javax.swing.SwingUtilities.invokeLater(() -> {
            var expectedValue = Turn+SPACE+on+SPACE+DST_SETTING;
            assertEquals(expectedValue, clock.getClockFrame().getClockMenuBar().getToggleDSTSetting().getText(), "Expected setting to be off");
            assertFalse(clock.isDaylightSavingsTimeEnabled());
            assertEquals(2, clock.getHours(), "Expected hours to be 2");
        });

        tick(10);
    }

    @Test
    void testDSTSettingWhenSettingIsFalse()
    {
        clock.setDaylightSavingsTimeEnabled(false);
        clock.getClockFrame().getClockMenuBar().getToggleDSTSetting().setText(Turn+SPACE+on+SPACE+DST_SETTING);

        LocalDate endDSTDate = clock.getEndDaylightSavingsTimeDate();
        clock.setHours(1);
        clock.setMinutes(59);
        clock.setSeconds(50);
        clock.setMonth(endDSTDate.getMonth());
        clock.setDayOfWeek(endDSTDate.getDayOfWeek());
        clock.setDayOfMonth(endDSTDate.getDayOfMonth());
        clock.setYear(endDSTDate.getYear());
        clock.setAMPM(AM);
        clock.setTheCurrentTime();
        var expectedValue = Turn+SPACE+on+SPACE+DST_SETTING;

        tick(10);

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertEquals(expectedValue, clock.getClockFrame().getClockMenuBar().getToggleDSTSetting().getText(), "Expected setting to be off");
            assertEquals(1, clock.getHours(), "Expected hours to be 1");
        });
    }

//    @Test
//    void testUpdateClockTimeSyncsClockTime() throws InvalidInputException
//    {
//        clock.setHours(7);
//        clock.setMinutes(0);
//        clock.setSeconds(6);
//        clock.setMonth(SEPTEMBER);
//        clock.setDayOfWeek(FRIDAY);
//        clock.setDayOfMonth(20);
//        clock.setYear(2024);
//        clock.setAMPM(PM);
//        clock.setTheCurrentTime();
//        LocalDateTime testNow = LocalDateTime.of(LocalDate.of(2024, AUGUST, 20), clock.getTime());
//        assertTrue(clock.shouldUpdateTime(testNow));
//    }

//    @Test
//    void testUpdateClockTimeDoesNotSyncClockTime() throws InvalidInputException
//    {
//        clock.setHours(7);
//        clock.setMinutes(0);
//        clock.setSeconds(6);
//        clock.setMonth(SEPTEMBER);
//        clock.setDayOfWeek(FRIDAY);
//        clock.setDayOfMonth(20);
//        clock.setYear(2024);
//        clock.setAMPM(PM);
//        clock.setTheCurrentTime();
//        LocalDateTime testNow = LocalDateTime.of(clock.getDate(), clock.getTime());
//        assertFalse(clock.shouldUpdateTime(testNow));
//    }

    @Test
    void testTickClockTwiceAsFast()
    {
        clock.setHours(7);
        clock.setMinutes(0);
        clock.setSeconds(6);
        clock.setMonth(SEPTEMBER);
        clock.setDayOfWeek(FRIDAY);
        clock.setDayOfMonth(20);
        clock.setYear(2024);
        clock.setAMPM(PM);
        clock.tick(2,1,1);
        assertEquals(ZERO+SEVEN, clock.getHoursAsStr(), "Expected hoursAsStr to be 07");
        assertEquals(ZERO+ZERO, clock.getMinutesAsStr(), "Expected minutesAsStr to be 00");
        assertEquals(ZERO+EIGHT, clock.getSecondsAsStr(), "Expected secondsAsStr to be 08");
        assertEquals(7, clock.getHours(), "Expected hours to be 7");
        assertEquals(0, clock.getMinutes(), "Expected minutes to be 0");
        assertEquals(8, clock.getSeconds(), "Expected seconds to be 8");
    }

    @Test
    void testTickClockFaster() throws InvalidInputException, InterruptedException
    {
        clock.setHours(7);
        clock.setMinutes(59);
        clock.setSeconds(58);
        clock.setMonth(SEPTEMBER);
        clock.setDayOfWeek(FRIDAY);
        clock.setDayOfMonth(20);
        clock.setYear(2024);
        clock.setAMPM(PM);
        sleep(2000); // needed because it wasn't displaying immediately
        clock.tick(2,3,4);
        sleep(2000); // needed because it needed a second to refresh
        assertEquals(ONE+ONE, clock.getHoursAsStr(), "Expected hoursAsStr to be 11");
        assertEquals(ZERO+TWO, clock.getMinutesAsStr(), "Expected minutesAsStr to be 02");
        assertEquals(ZERO+ZERO, clock.getSecondsAsStr(), "Expected secondsAsStr to be 00");
        assertEquals(11, clock.getHours(), "Expected hours to be 11");
        assertEquals(2, clock.getMinutes(), "Expected minutes to be 2");
        assertEquals(0, clock.getSeconds(), "Expected seconds to be 0");
    }

    // Helper methods
    private void tick(int times) {
        logger.info("Test ticking clock {} times", times);
        try {
            for (int i = 0; i < times; i++) {
                clock.tick();
                sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted while ticking clock", e);
        }
    }

}