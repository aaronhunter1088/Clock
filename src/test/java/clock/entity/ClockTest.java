package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

import clock.exception.InvalidInputException;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;
import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Clock} class
 *
 * @author michael ball
 * @version since 1.0
 */
class ClockTest
{
    private static final Logger logger = LogManager.getLogger(ClockTest.class);

    private static final LocalDate DATE_NOW = LocalDate.now();
    private static final LocalTime AM_TIME = LocalTime.of(10, 30, 45); // 10:30:45 AM
    private static final LocalTime PM_TIME = LocalTime.of(15, 30, 45); // 3:30:45 PM
    private static Clock clockWEDJAN12025_103000AM, clockWEDJAN12025_103000PM;
    private Clock clock;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting {}...", ClockTest.class.getSimpleName());
        clockWEDJAN12025_103000AM = new Clock(10, 30, 0, JANUARY, WEDNESDAY, 1, 2025, AM);
        clockWEDJAN12025_103000PM = new Clock(10, 30, 0, JANUARY, WEDNESDAY, 1, 2025, PM);
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
    static void afterAll() { logger.info("Concluding {}", ClockTest.class.getSimpleName()); }

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
        final var exception = assertThrows(InvalidInputException.class, () -> new Clock(hours, 0, 0, JANUARY, MONDAY, 1, 2022, AM),
                "Expected InvalidInputException for invalid hours: " + hours);
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
        final var exception = assertThrows(InvalidInputException.class, () -> new Clock(10, minutes, 0, JANUARY, MONDAY, 1, 2022, AM),
                "Expected InvalidInputException for invalid minutes: " + minutes);
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
        final var exception = assertThrows(InvalidInputException.class, () -> new Clock(10, 0, seconds, JANUARY, MONDAY, 1, 2022, AM),
                "Expected InvalidInputException for invalid seconds: " + seconds);
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
        final var exception = assertThrows(InvalidInputException.class, () -> new Clock(10, 0, 0, JANUARY, MONDAY, dayOfMonth, 2022, AM),
                "Expected InvalidInputException for invalid dayOfMonth: " + dayOfMonth);
        assertEquals("The day of month for JANUARY must be between 1 and 31", exception.getMessage(), "Expected message for invalid dayOfMonth");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1581})
    void testProvidedInvalidYearThrowsIllegalArgumentException(int year)
    {
        final var exception = assertThrows(InvalidInputException.class, () -> new Clock(10, 0, 0, JANUARY, MONDAY, 1, year, AM),
                "Expected InvalidInputException for invalid year: " + year);
        assertEquals("Year must be 1582 or later (Gregorian calendar did not exist before this)", exception.getMessage(), "Expected message for invalid year");
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
        clock.setDaylightSavingsTimeDates();
        clock.setTheDateAndTime();
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
        clock.setDaylightSavingsTimeDates();
        clock.logIsNewYear();
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
        clock.logIsNewYear();
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
        clock.logIsNewYear();
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
        clock.setDaylightSavingsTimeDates();
        clock.setTheDateAndTime();
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
        clock.setDaylightSavingsTimeDates();
        clock.setTheDateAndTime();
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
        clock.logIsNewYear();
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
        clock.logIsNewYear();
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
    void testWhenClockInMilitaryTimeAlarmStillTriggers()
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
        clock.logIsNewYear();
        Alarm alarm = new Alarm("Test", 1, 1, PM, new ArrayList<>(){{add(SATURDAY);}}, false, clock);
        clock.getListOfAlarms().add(alarm);

        assertEquals(1, clock.getListOfAlarms().size());

        tick(4);
        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(clock.getListOfAlarms().getFirst().isAlarmGoingOff());
            alarm.stopAlarm();
        });
    }

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
    void testTickClockFaster() throws InterruptedException
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

    @Test
    void testGetAMPMFromTime()
    {
        LocalDate date = LocalDate.now();
        LocalTime time = AM_TIME; // 10:30:45 AM
        LocalDateTime now = LocalDateTime.of(date, time);
        String ampm = clock.getAMPMFromTime(now);

        assertEquals(AM, ampm, "AMPM should be AM");

        time = PM_TIME; // 3:30:45 PM
        now = LocalDateTime.of(date, time);
        ampm = clock.getAMPMFromTime(now);

        assertEquals(PM, ampm, "AMPM should be PM");
    }

    @ParameterizedTest
    @CsvSource({
            "Hawaii, Pacific/Honolulu",
            "Alaska, America/Anchorage",
            "Pacific, America/Los_Angeles",
            "Central, America/Chicago",
            "Eastern, America/New_York",
            "Unknown, America/Chicago", // your default system timezone
    })
    void testGetZoneIdFromTimezone(String timezone, ZoneId zoneId)
    {
        ZoneId zone = clock.getZoneIdFromTimezoneButtonText(timezone);
        assertEquals(zone, zoneId, "Expected ZoneId to match: " + timezone);
    }

    @ParameterizedTest
    @CsvSource({
            "Pacific/Honolulu, Hawaii",
            "America/Anchorage, Alaska",
            "America/Los_Angeles, Pacific",
            "America/Chicago, Central",
            "America/New_York, Eastern"
    })
    void testGetPlainTimezoneFromZoneId(ZoneId zoneId, String timezone)
    {
        String tz = clock.getPlainTimezoneFromZoneId(zoneId);
        assertEquals(tz, timezone, "Expected timezone to match: " + timezone);
    }

    @Test
    void testGetZoneDateTimeWithANewDate()
    {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Current LocalDateTime: {}", now);
        final var zonedTime = clock.getZonedDateTimeFromLocalDateTime(now);
        logger.info("ZonedDateTime: {}", zonedTime);
        assertNotNull(zonedTime, "ZonedDateTime should not be null");
    }

    @Test
    void testGetZoneDateTimeWithANoDate()
    {
        final var zonedTime = clock.getZonedDateTimeFromLocalDateTime(null);
        logger.info("ZonedDateTime: {}", zonedTime);
        assertNotNull(zonedTime, "ZonedDateTime should not be null");
    }

    @Test
    void testFormattingTimeToNonMilitaryTimeKeepsAMPMIntact()
    {
        LocalDateTime now = LocalDateTime.of(DATE_NOW, AM_TIME);
        var localDateTime = clock.formatCurrentTimeToNonMilitaryTime(now);

        assertEquals(10, localDateTime.getHour(), "Expected hour to be 10");

        now = LocalDateTime.of(DATE_NOW, PM_TIME);
        localDateTime = clock.formatCurrentTimeToNonMilitaryTime(now);

        assertEquals(3, localDateTime.getHour(), "Expected hour to be 3");

        clock.setShowMilitaryTime(true);
        localDateTime = clock.formatCurrentTimeToNonMilitaryTime(now);

        assertEquals(15, localDateTime.getHour(), "Expected hour to be 15 in military time");
    }

    @Test
    void testClockTimeUpdatesAtMidnight()
    {
        clock.setHours(11);
        clock.setMinutes(59);
        clock.setSeconds(55);
        clock.setAMPM(PM);
        clock.logIsNewYear();

        assertEquals(11, clock.getHours(), "Expected hours to be 11");
        assertEquals(59, clock.getMinutes(), "Expected minutes to be 59");
        assertEquals(55, clock.getSeconds(), "Expected seconds to be 55");

        tick(5); // Tick the clock to midnight

        assertEquals(12, clock.getHours(), "Expected hours to be 12 at midnight");
        assertEquals(0, clock.getMinutes(), "Expected minutes to be 0 at midnight");
        assertEquals(0, clock.getSeconds(), "Expected seconds to be 0 at midnight");
    }

    @Test
    void testClockIsNewYear()
    {
        int currentYear = clock.getYear();
        clock.setHours(11);
        clock.setMinutes(59);
        clock.setSeconds(55);
        clock.setAMPM(PM);
        clock.setMonth(DECEMBER);
        clock.setDayOfWeek(clock.getDayOfWeek());
        clock.setDayOfMonth(31);
        clock.logIsNewYear();

        tick(5); // Tick the clock to midnight

        assertEquals(12, clock.getHours(), "Expected hours to be 12 at midnight");
        assertEquals(0, clock.getMinutes(), "Expected minutes to be 0 at midnight");
        assertEquals(0, clock.getSeconds(), "Expected seconds to be 0 at midnight");
        assertEquals(JANUARY, clock.getMonth(), "Expected month to be January after New Year");
        assertEquals(1, clock.getDayOfMonth(), "Expected day of month to be 1 after New Year");
        assertEquals(currentYear+1, clock.getYear(), "Expected year to be 2023 after New Year");
    }

    @ParameterizedTest
    @DisplayName("Compare default clock with provided clocks")
    @MethodSource("provideClocksForComparison")
    void testCompareDefaultClock(Clock input)
    {
        int comparison = clock.compareTo(input);
        assertTrue(comparison >= 0, "Comparison result should match expected value");
    }
    public static Stream<Arguments> provideClocksForComparison() {
        return Stream.of(
            Arguments.of(new Clock()),
            Arguments.of(clockWEDJAN12025_103000AM),
            Arguments.of(clockWEDJAN12025_103000PM)
        );
    }

    @ParameterizedTest
    @MethodSource("provideClocksForEquality")
    @DisplayName("Test compareTo between two clocks with similar time")
    void testCompareToSimilarClocks(Clock input, boolean expected)
    {
        assertEquals(expected, clock.equals(input), "Clocks should not be equal");
    }
    public static Stream<Arguments> provideClocksForEquality() {
        return Stream.of(
                Arguments.of(new Clock(), true),
                Arguments.of(clockWEDJAN12025_103000AM, false),
                Arguments.of(clockWEDJAN12025_103000PM, false)
        );
    }

    // -------------------------------------------------------------------------
    // Day-of-month validation – per month
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("Invalid day of month for each non-February month throws InvalidInputException")
    @MethodSource("provideInvalidDaysPerMonth")
    void testInvalidDayOfMonthPerMonthThrowsIllegalArgumentException(Month month, int invalidDay, int year, int expectedMaxDay)
    {
        final var exception = assertThrows(InvalidInputException.class,
                () -> new Clock(10, 0, 0, month, MONDAY, invalidDay, year, AM),
                "Expected exception for " + month + " day " + invalidDay);
        assertEquals("The day of month for " + month + " must be between 1 and " + expectedMaxDay, exception.getMessage());
    }
    public static Stream<Arguments> provideInvalidDaysPerMonth() {
        return Stream.of(
                Arguments.of(JANUARY,   32, 2025, 31),
                Arguments.of(MARCH,     32, 2025, 31),
                Arguments.of(APRIL,     31, 2025, 30),
                Arguments.of(MAY,       32, 2025, 31),
                Arguments.of(JUNE,      31, 2025, 30),
                Arguments.of(JULY,      32, 2025, 31),
                Arguments.of(AUGUST,    32, 2025, 31),
                Arguments.of(SEPTEMBER, 31, 2025, 30),
                Arguments.of(OCTOBER,   32, 2025, 31),
                Arguments.of(NOVEMBER,  31, 2025, 30),
                Arguments.of(DECEMBER,  32, 2025, 31)
        );
    }

    @Test
    @DisplayName("February in a non-leap year rejects day 29")
    void testFebruaryNonLeapYearRejectsDay29()
    {
        final var exception = assertThrows(InvalidInputException.class,
                () -> new Clock(10, 0, 0, FEBRUARY, MONDAY, 29, 2025, AM));
        assertEquals("The day of month for FEBRUARY must be between 1 and 28 (non-leap year)", exception.getMessage());
    }

    @Test
    @DisplayName("February in a non-leap year accepts day 28")
    void testFebruaryNonLeapYearAcceptsDay28()
    {
        assertDoesNotThrow(() -> new Clock(10, 0, 0, FEBRUARY, FRIDAY, 28, 2025, AM));
    }

    @Test
    @DisplayName("February in a leap year accepts day 29")
    void testFebruaryLeapYearAcceptsDay29()
    {
        assertDoesNotThrow(() -> new Clock(10, 0, 0, FEBRUARY, THURSDAY, 29, 2024, AM));
    }

    @Test
    @DisplayName("February in a leap year rejects day 30")
    void testFebruaryLeapYearRejectsDay30()
    {
        final var exception = assertThrows(InvalidInputException.class,
                () -> new Clock(10, 0, 0, FEBRUARY, FRIDAY, 30, 2024, AM));
        assertEquals("The day of month for FEBRUARY must be between 1 and 29 (leap year)", exception.getMessage());
    }

    // -------------------------------------------------------------------------
    // Gregorian calendar minimum date enforcement
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("Year before 1582 throws InvalidInputException")
    @ValueSource(ints = {-1, 0, 1000, 1581})
    void testYearBeforeGregorianCalendarThrowsIllegalArgumentException(int year)
    {
        final var exception = assertThrows(InvalidInputException.class,
                () -> new Clock(10, 0, 0, JANUARY, MONDAY, 1, year, AM),
                "Expected exception for year: " + year);
        assertEquals("Year must be 1582 or later (Gregorian calendar did not exist before this)", exception.getMessage());
    }

    @Test
    @DisplayName("October 1-14 of 1582 throws InvalidInputException (Gregorian calendar starts October 15)")
    void testDatesIn1582BeforeOctober15ThrowsIllegalArgumentException()
    {
        final var oct14 = assertThrows(InvalidInputException.class,
                () -> new Clock(10, 0, 0, OCTOBER, THURSDAY, 14, 1582, AM));
        assertEquals("Date must be on or after October 15, 1582 (Gregorian calendar start date). Your date: October 14, 1582", oct14.getMessage());
    }

    @Test
    @DisplayName("October 15, 1582 is valid as the Gregorian calendar start date")
    void testInvalidNumericMonthThrowsDateTimeException()
    {
        assertThrows(DateTimeException.class, () -> Month.of(0),
                "Month.of(0) should throw DateTimeException — months are 1-12");
        assertThrows(DateTimeException.class, () -> Month.of(13),
                "Month.of(13) should throw DateTimeException — months are 1-12");
    }

    @Test
    @DisplayName("October 15, 1582 is valid as the Gregorian calendar start date")
    void testOctober15_1582IsValidGregorianStartDate()
    {
        assertDoesNotThrow(() -> new Clock(10, 0, 0, OCTOBER, FRIDAY, 15, 1582, AM));
    }

    @Test
    @DisplayName("Dates after October 15, 1582 are valid")
    void testDatesAfterOctober15_1582AreValid()
    {
        assertDoesNotThrow(() -> new Clock(10, 0, 0, OCTOBER, SATURDAY, 16, 1582, AM));
        assertDoesNotThrow(() -> new Clock(10, 0, 0, NOVEMBER, MONDAY, 1, 1582, AM));
        assertDoesNotThrow(() -> new Clock(10, 0, 0, DECEMBER, WEDNESDAY, 31, 1582, AM));
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
            Thread.currentThread().interrupt();
        }
    }

    // -------------------------------------------------------------------------
    // Format string methods
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getTimeAsStr returns correct HH:MM:SS AMPM format")
    void testGetTimeAsStr()
    {
        clock = new Clock(10, 30, 45, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals("10:30:45 AM", clock.getTimeAsStr());
    }

    @Test
    @DisplayName("getDateAsStr returns MONTH day, year format")
    void testGetDateAsStr()
    {
        clock = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals("AUGUST 15, 2023", clock.getDateAsStr());
    }

    @Test
    @DisplayName("getFullDateAsStr returns DAYOFWEEK MONTH day, year format")
    void testGetFullDateAsStr()
    {
        clock = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals("SATURDAY AUGUST 15, 2023", clock.getFullDateAsStr());
    }

    @Test
    @DisplayName("getPartialDateAsStr returns 3-letter day and month abbreviations")
    void testGetPartialDateAsStr()
    {
        clock = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals("SAT AUG 15, 2023", clock.getPartialDateAsStr());
    }

    @Test
    @DisplayName("getMilitaryTimeAsStr returns HHMMhrs SS format")
    void testGetMilitaryTimeAsStr()
    {
        clock = new Clock(13, 30, 45, AUGUST, SATURDAY, 15, 2023, PM); // 13 → military
        assertEquals("1330 hours 45", clock.getMilitaryTimeAsStr());
    }

    // -------------------------------------------------------------------------
    // getClockTimeAsAlarmString
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getClockTimeAsAlarmString returns standard format in non-military mode")
    void testGetClockTimeAsAlarmStringNonMilitary()
    {
        clock = new Clock(8, 5, 0, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals("08:05 AM", clock.getClockTimeAsAlarmString());
    }

    @Test
    @DisplayName("getClockTimeAsAlarmString converts hours > 12 in military mode (single digit)")
    void testGetClockTimeAsAlarmStringMilitaryHoursAbove12SingleDigit()
    {
        clock = new Clock(13, 30, 0, AUGUST, SATURDAY, 15, 2023, PM); // military
        assertEquals("01:30 PM", clock.getClockTimeAsAlarmString());
    }

    @Test
    @DisplayName("getClockTimeAsAlarmString converts hours > 12 in military mode (double digit)")
    void testGetClockTimeAsAlarmStringMilitaryHoursAbove12DoubleDigit()
    {
        clock = new Clock(22, 15, 0, AUGUST, SATURDAY, 15, 2023, PM); // military, 22-12=10
        assertEquals("10:15 PM", clock.getClockTimeAsAlarmString());
    }

    @Test
    @DisplayName("getClockTimeAsAlarmString uses hoursAsStr when hours <= 12 in military mode")
    void testGetClockTimeAsAlarmStringMilitaryHoursAtOrBelow12()
    {
        clock = new Clock(9, 0, 0, AUGUST, SATURDAY, 15, 2023, AM);
        clock.setShowMilitaryTime(true);
        assertEquals("09:00 AM", clock.getClockTimeAsAlarmString());
    }

    // -------------------------------------------------------------------------
    // defaultText
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("defaultText returns label string for each label version")
    @MethodSource("defaultTextScenarios")
    void testDefaultText(int labelVersion, String expected)
    {
        clock = new Clock(10, 30, 45, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals(expected, clock.defaultText(labelVersion));
    }
    static Stream<Arguments> defaultTextScenarios()
    {
        return Stream.of(
            Arguments.of(1,  "AUGUST 15, 2023"),      // no full/partial
            Arguments.of(3,  Hours),
            Arguments.of(4,  Minutes),
            Arguments.of(5,  AM+SLASH+PM),
            Arguments.of(6,  No_Alarms),
            Arguments.of(7,  S),
            Arguments.of(9,  is+SPACE+going_off),
            Arguments.of(10, No_Timers),
            Arguments.of(0,  EMPTY),                  // unknown version
            Arguments.of(99, EMPTY)
        );
    }

    @Test
    @DisplayName("defaultText label 1 returns full date when showFullDate is true")
    void testDefaultTextLabel1ShowFullDate()
    {
        clock = new Clock(10, 30, 45, AUGUST, SATURDAY, 15, 2023, AM);
        clock.setShowFullDate(true);
        assertEquals("SATURDAY AUGUST 15, 2023", clock.defaultText(1));
    }

    @Test
    @DisplayName("defaultText label 1 returns partial date when showPartialDate is true")
    void testDefaultTextLabel1ShowPartialDate()
    {
        clock = new Clock(10, 30, 45, AUGUST, SATURDAY, 15, 2023, AM);
        clock.setShowPartialDate(true);
        assertEquals("SAT AUG 15, 2023", clock.defaultText(1));
    }

    @Test
    @DisplayName("defaultText label 2 returns standard time string")
    void testDefaultTextLabel2StandardTime()
    {
        clock = new Clock(10, 30, 45, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals("10:30:45 AM", clock.defaultText(2));
    }

    @Test
    @DisplayName("defaultText label 2 returns military time string when showMilitaryTime is true")
    void testDefaultTextLabel2MilitaryTime()
    {
        clock = new Clock(13, 30, 45, AUGUST, SATURDAY, 15, 2023, PM);
        assertTrue(clock.isShowMilitaryTime());
        assertEquals("1330 hours 45", clock.defaultText(2));
    }

    // -------------------------------------------------------------------------
    // hashCode and toString
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("hashCode is consistent for two clocks with the same specific values")
    void testHashCodeSameValues()
    {
        final Clock c1 = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        final Clock c2 = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        assertEquals(c1.hashCode(), c2.hashCode(), "Equal clocks should have same hash code");
    }

    @Test
    @DisplayName("toString returns correctly formatted abbreviated clock string")
    void testToString()
    {
        clock = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        final String result = clock.toString();
        assertTrue(result.startsWith("Sat"), "toString should start with abbreviated day");
        assertTrue(result.contains("Aug"), "toString should contain abbreviated month");
        assertTrue(result.contains("2023"), "toString should contain year");
        assertTrue(result.contains("10:30:00"), "toString should contain time");
        assertTrue(result.contains("AM"), "toString should contain AM/PM");
    }

    // -------------------------------------------------------------------------
    // printStackTrace
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("printStackTrace with message logs without throwing")
    void testPrintStackTraceWithMessage()
    {
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> clock.printStackTrace(e, "custom message"));
    }

    @Test
    @DisplayName("printStackTrace with null message logs without throwing")
    void testPrintStackTraceWithNullMessage()
    {
        final Exception e = new Exception("test error");
        assertDoesNotThrow(() -> clock.printStackTrace(e, null));
    }

    // -------------------------------------------------------------------------
    // shouldUpdateTime / updateOutdatedTime
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("shouldUpdateTime returns false when times are the same")
    void testShouldUpdateTimeReturnsFalseWhenSameTime()
    {
        clock = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        // Pass the clock's own currentDateTime — no update needed
        final boolean updated = clock.shouldUpdateTime(clock.getCurrentDateTime());
        assertFalse(updated, "shouldUpdateTime should return false when time has not changed");
    }

    @Test
    @DisplayName("shouldUpdateTime returns true and updates clock when time differs")
    void testShouldUpdateTimeReturnsTrueWhenDifferentTime()
    {
        clock = new Clock(10, 30, 0, AUGUST, SATURDAY, 15, 2023, AM);
        final LocalDateTime different = LocalDateTime.of(2023, 8, 15, 11, 0, 0);
        final boolean updated = clock.shouldUpdateTime(different);
        assertTrue(updated, "shouldUpdateTime should return true when time differs");
        assertEquals(11, clock.getHours(), "Clock should have updated to the new hour");
    }

    @Test
    @DisplayName("updateOutdatedTime returns a non-null Runnable that executes without throwing")
    void testUpdateOutdatedTimeReturnsRunnable()
    {
        final Runnable r = clock.updateOutdatedTime();
        assertNotNull(r, "updateOutdatedTime should return a Runnable");
        assertDoesNotThrow(r::run, "Running updateOutdatedTime Runnable should not throw");
    }

    // -------------------------------------------------------------------------
    // logIsNewYear
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("logIsNewYear logs and resets the isNewYear flag when true")
    void testLogIsNewYearWhenFlagIsTrue()
    {
        clock.setIsNewYear(true);
        assertTrue(clock.isNewYear(), "isNewYear should be true before logIsNewYear");
        clock.logIsNewYear();
        assertFalse(clock.isNewYear(), "isNewYear should be false after logIsNewYear");
    }

    // -------------------------------------------------------------------------
    // DST spring forward / fall back
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DST spring forward: 2 AM in March advances to 3 AM")
    void testDSTSpringForward()
    {
        clock.setHours(2);
        clock.setMinutes(0);
        clock.setSeconds(0);
        clock.setMonth(MARCH);
        clock.setDayOfMonth(13);
        clock.setYear(2022);
        clock.setAMPM(AM);
        clock.setDaylightSavingsTimeEnabled(true);
        clock.setTodayMatchesDSTDate(true);
        clock.tick();
        assertEquals(3, clock.getHours(), "Should spring forward to 3 AM");
        assertFalse(clock.isTodayMatchesDSTDate(), "todayMatchesDSTDate should be cleared");
    }

    @Test
    @DisplayName("DST fall back: 2 AM in November falls back to 1 AM")
    void testDSTFallBack()
    {
        clock.setHours(2);
        clock.setMinutes(0);
        clock.setSeconds(0);
        clock.setMonth(NOVEMBER);
        clock.setDayOfMonth(6);
        clock.setYear(2022);
        clock.setAMPM(AM);
        clock.setDaylightSavingsTimeEnabled(true);
        clock.setTodayMatchesDSTDate(true);
        clock.tick();
        assertEquals(1, clock.getHours(), "Should fall back to 1 AM");
        assertFalse(clock.isTodayMatchesDSTDate(), "todayMatchesDSTDate should be cleared");
    }

    @Test
    @DisplayName("DST is not applied when daylightSavingsTimeEnabled is false")
    void testDSTNotAppliedWhenDisabled()
    {
        clock.setHours(2);
        clock.setMinutes(0);
        clock.setSeconds(0);
        clock.setMonth(MARCH);
        clock.setDayOfMonth(13);
        clock.setYear(2022);
        clock.setAMPM(AM);
        clock.setDaylightSavingsTimeEnabled(false);
        clock.setTodayMatchesDSTDate(true);
        clock.tick();
        assertEquals(2, clock.getHours(), "Hours should remain 2 when DST is disabled");
    }

    // -------------------------------------------------------------------------
    // JANUARY rollover fix
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("January 30 midnight does not roll over to February — stays January 31")
    void testJanuaryDoesNotRolloverOnDay30()
    {
        clock.setHours(11);
        clock.setMinutes(59);
        clock.setSeconds(59);
        clock.setMonth(JANUARY);
        clock.setDayOfWeek(THURSDAY);
        clock.setDayOfMonth(30);
        clock.setYear(2025);
        clock.setAMPM(PM);
        clock.tick();
        assertEquals(JANUARY, clock.getMonth(), "Should still be January after day 30 → 31");
        assertEquals(31, clock.getDayOfMonth(), "Should advance to day 31, not roll to February");
    }

    @Test
    @DisplayName("January 31 midnight rolls over to February 1")
    void testJanuaryRollovesToFebruaryAfterDay31()
    {
        clock.setHours(11);
        clock.setMinutes(59);
        clock.setSeconds(59);
        clock.setMonth(JANUARY);
        clock.setDayOfWeek(FRIDAY);
        clock.setDayOfMonth(31);
        clock.setYear(2025);
        clock.setAMPM(PM);
        clock.tick();
        assertEquals(FEBRUARY, clock.getMonth(), "Should roll to February after January 31");
        assertEquals(1, clock.getDayOfMonth(), "Should reset to day 1");
    }

    // -------------------------------------------------------------------------
    // Military time midnight
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Military time midnight (23:59:59) rolls to 00:00:00 and advances date")
    void testMilitaryTimeMidnight()
    {
        clock.setShowMilitaryTime(true);
        clock.setHours(23);
        clock.setMinutes(59);
        clock.setSeconds(59);
        clock.setMonth(JUNE);
        clock.setDayOfWeek(SUNDAY);
        clock.setDayOfMonth(15);
        clock.setYear(2025);
        clock.setAMPM(AM);
        clock.tick();
        assertEquals(0, clock.getHours(), "Hours should roll to 0 at military midnight");
        assertEquals(AM, clock.getAMPM(), "AMPM should be AM at midnight");
        assertEquals(16, clock.getDayOfMonth(), "Day should advance from 15 to 16");
        assertEquals(MONDAY, clock.getDayOfWeek(), "Day of week should advance from Sunday to Monday");
    }

    // -------------------------------------------------------------------------
    // Month rollovers
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("Month rollover at last day always advances to the 1st of the next month")
    @MethodSource("monthRolloverScenarios")
    void testMonthRollover(Month startMonth, int lastDay, DayOfWeek dayOfWeek, Month expectedMonth)
    {
        clock.setHours(11);
        clock.setMinutes(59);
        clock.setSeconds(59);
        clock.setMonth(startMonth);
        clock.setDayOfWeek(dayOfWeek);
        clock.setDayOfMonth(lastDay);
        clock.setYear(2025);
        clock.setAMPM(PM);
        clock.tick();
        assertEquals(expectedMonth, clock.getMonth(), startMonth + " should roll to " + expectedMonth);
        assertEquals(1, clock.getDayOfMonth(), "Day should reset to 1 after rollover");
    }
    static Stream<Arguments> monthRolloverScenarios()
    {
        return Stream.of(
            Arguments.of(MARCH,     31, MONDAY,    APRIL),
            Arguments.of(APRIL,     30, WEDNESDAY, MAY),
            Arguments.of(MAY,       31, FRIDAY,    JUNE),
            Arguments.of(JUNE,      30, MONDAY,    JULY),
            Arguments.of(JULY,      31, WEDNESDAY, AUGUST),
            Arguments.of(AUGUST,    31, SATURDAY,  SEPTEMBER),
            Arguments.of(SEPTEMBER, 30, TUESDAY,   OCTOBER),
            Arguments.of(OCTOBER,   31, THURSDAY,  NOVEMBER),
            Arguments.of(NOVEMBER,  30, SUNDAY,    DECEMBER)
        );
    }

    // -------------------------------------------------------------------------
    // List setters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("setListOfStopwatches stores and getListOfStopwatches retrieves the list")
    void testSetAndGetListOfStopwatches()
    {
        final var sw = new Stopwatch("Test SW", false, false, clock);
        final var list = new ArrayList<Stopwatch>();
        list.add(sw);
        clock.setListOfStopwatches(list);
        assertEquals(1, clock.getListOfStopwatches().size());
        assertSame(sw, clock.getListOfStopwatches().getFirst());
    }

    @Test
    @DisplayName("setListOfAlarms and setListOfTimers store and retrieve their lists")
    void testSetListOfAlarmsAndTimers()
    {
        final var alarmList = new ArrayList<Alarm>();
        clock.setListOfAlarms(alarmList);
        assertSame(alarmList, clock.getListOfAlarms());

        final var timerList = new ArrayList<Timer>();
        clock.setListOfTimers(timerList);
        assertSame(timerList, clock.getListOfTimers());
    }

    // -------------------------------------------------------------------------
    // Mountain timezone (previously uncovered in getZoneIdFromTimezoneButtonText)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getZoneIdFromTimezoneButtonText returns America/Denver for Mountain")
    void testGetZoneIdFromTimezoneMountain()
    {
        assertEquals(ZoneId.of(AMERICA_DENVER), clock.getZoneIdFromTimezoneButtonText(MOUNTAIN));
    }

    @Test
    @DisplayName("getPlainTimezoneFromZoneId returns Mountain for America/Denver")
    void testGetPlainTimezoneFromZoneIdMountain()
    {
        assertEquals(MOUNTAIN, clock.getPlainTimezoneFromZoneId(ZoneId.of(AMERICA_DENVER)));
    }

    @Test
    @DisplayName("getPlainTimezoneFromZoneId returns system default id for unknown zone")
    void testGetPlainTimezoneFromZoneIdUnknown()
    {
        final String result = clock.getPlainTimezoneFromZoneId(ZoneId.of("Europe/London"));
        assertEquals(ZoneId.systemDefault().getId(), result);
    }

}