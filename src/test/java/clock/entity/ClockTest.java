package clock.entity;

import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static clock.util.Constants.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.junit.jupiter.api.Assertions.*;

class ClockTest
{
    static { System.setProperty("appName", ClockTest.class.getSimpleName()); }
    private static final Logger logger = LogManager.getLogger(ClockTest.class);

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
    {
        if (clock != null) {
            logger.info("Test complete. Closing the clock...");
            // Create a WindowEvent with WINDOW_CLOSING event type
            WindowEvent windowClosing = new WindowEvent(clock, WindowEvent.WINDOW_CLOSING);

            // Dispatch the event to the JFrame instance
            clock.dispatchEvent(windowClosing);

            // Ensure the clock is no longer visible
            assertFalse(clock.isVisible());

            // Dispose of the JFrame to release resources
            clock.dispose();
        }
    }

    @Test
    void testBeginningDayLightSavingsTimeIsProperlySet() throws InvalidInputException
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
        clock.setDaylightSavingsTimeDates();
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
        clock.setDaylightSavingsTimeDates();
        assertEquals(13, clock.getBeginDaylightSavingsTimeDate().getDayOfMonth(), "For 2022, Beginning DST Day should be 13th");
        assertEquals(6, clock.getEndDaylightSavingsTimeDate().getDayOfMonth(), "For 2022, Ending DST Day should be 6th");
    }

    @Test
    void testIsTodayDaylightSavingsDayReturnsFalseWhenNotBeginningDST() throws InvalidInputException
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
        clock.setDaylightSavingsTimeDates();
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
    void testIsDateDaylightSavingsDayReturnsFalseWhenNotEndingDST() throws InvalidInputException
    {
        // reset clock time and date
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
        clock.setDaylightSavingsTimeDates();
        assertFalse(clock.isTodayDaylightSavingsTime());
    }

    @Test
    void testIsTodayDaylightSavingsDayReturnsTrueWhenIsBeginningDST() throws InvalidInputException
    {
        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(MARCH);
        clock.setDayOfWeek(SUNDAY);
        clock.setDayOfMonth(13);
        clock.setYear(2022);
        clock.setAMPM(PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    void testIsTodayDaylightSavingsDayReturnsTrueWhenIsEndingDST() throws InvalidInputException
    {
        clock.setHours(5);
        clock.setMinutes(42);
        clock.setSeconds(0);
        clock.setMonth(NOVEMBER);
        clock.setDayOfWeek(SUNDAY);
        clock.setDayOfMonth(6);
        clock.setYear(2022);
        clock.setAMPM(PM);
        assertTrue(clock.isTodayDaylightSavingsTime());
    }

    @Test
    void testIsTodayDaylightSavingsDay() throws InvalidInputException
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
        boolean actual = clock.isDoesTodayMatchDSTDate();
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
    void testClockBecomesAMWhenMidnightStarts() throws InvalidInputException
    {
        clock.setHours(11);
        clock.setMinutes(59);
        clock.setSeconds(57);
        clock.setMonth(FEBRUARY);
        clock.setDayOfWeek(SUNDAY);
        clock.setDayOfMonth(21);
        clock.setYear(2021);
        clock.setAMPM(PM);
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
        clock.setHours(1);
        clock.setMinutes(0);
        clock.setSeconds(59);
        clock.setMonth(MARCH);
        clock.setDayOfWeek(SATURDAY);
        clock.setDayOfMonth(6);
        clock.setYear(2021);
        clock.setAMPM(PM);
        clock.setShowMilitaryTime(true);
        Alarm alarm = new Alarm("Test", 1, 1, PM, false, new ArrayList<>(){{add(SATURDAY);}}, clock);
        clock.setListOfAlarms(new ArrayList<>(){{add(alarm);}});

        assertEquals(1, clock.getListOfAlarms().size());

        clock.tick();
        clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
        //assertTrue(clock.getAlarmPanel().isAlarmIsGoingOff());
    }

    @Test
    void testTurnOffDSTSetting() throws InvalidInputException
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
        clock.getClockMenuBar().getToggleDSTSetting().doClick(); // DST turned off; enabled:false
        var expectedValue = Turn+SPACE+on+SPACE+DST_SETTING;
        assertEquals("Expected setting to be on", expectedValue, clock.getClockMenuBar().getToggleDSTSetting().getText());

        tick(10);

        assertEquals(2, clock.getHours(), "Expected hours to be 2");
    }

    @Test
    void testKeepDSTSettingOn() throws InvalidInputException
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
        var expectedValue = Turn+SPACE+off+SPACE+DST_SETTING;
        assertEquals("Expected setting to be off", expectedValue, clock.getClockMenuBar().getToggleDSTSetting().getText());

        tick(10);

        assertEquals(1, clock.getHours(), "Expected hours to be 1");
    }

    @Test
    void testUpdateClockTimeSyncsClockTime() throws InvalidInputException
    {
        clock.setHours(7);
        clock.setMinutes(0);
        clock.setSeconds(6);
        clock.setMonth(SEPTEMBER);
        clock.setDayOfWeek(FRIDAY);
        clock.setDayOfMonth(20);
        clock.setYear(2024);
        clock.setAMPM(PM);
        LocalDateTime testNow = LocalDateTime.of(LocalDate.of(2024, AUGUST, 20), clock.getTime());
        assertTrue(clock.shouldUpdateTime(testNow));
    }

    @Test
    void testUpdateClockTimeDoesNotSyncClockTime() throws InvalidInputException
    {
        clock.setHours(7);
        clock.setMinutes(0);
        clock.setSeconds(6);
        clock.setMonth(SEPTEMBER);
        clock.setDayOfWeek(FRIDAY);
        clock.setDayOfMonth(20);
        clock.setYear(2024);
        clock.setAMPM(PM);
        LocalDateTime testNow = LocalDateTime.of(clock.getDate(), clock.getTime());
        assertFalse(clock.shouldUpdateTime(testNow));
    }

    @Test
    void testTickClockTwiceAsFast() throws InvalidInputException
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
        assertEquals("Expected hoursAsStr to be 07", "07", clock.getHoursAsStr());
        assertEquals("Expected minutesAsStr to be 00", "00", clock.getMinutesAsStr());
        assertEquals("Expected secondsAsStr to be 08", "08", clock.getSecondsAsStr());
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
        Thread.sleep(2000); // needed because it wasn't displaying immediately
        clock.tick(2,3,4);
        Thread.sleep(2000); // needed because it needed a second to refresh
        assertEquals("11", clock.getHoursAsStr(), "Expected hoursAsStr to be 11");
        assertEquals("02", clock.getMinutesAsStr(), "Expected minutesAsStr to be 02");
        assertEquals("00", clock.getSecondsAsStr(), "Expected secondsAsStr to be 00");
        assertEquals(11, clock.getHours(), "Expected hours to be 11");
        assertEquals(2, clock.getMinutes(), "Expected minutes to be 2");
        assertEquals(0, clock.getSeconds(), "Expected seconds to be 0");
    }


    // Helper methods
    private void tick(int times) {
        for (int i=0; i<times; i++) {
            clock.tick();
        }
    }

}