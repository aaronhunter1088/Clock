package clock.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import clock.exception.InvalidInputException;
import clock.panel.ClockFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.time.Month.*;
import static java.time.DayOfWeek.*;
import static clock.util.Constants.*;

/**
 * The clock object is capable of showing the date and time.
 * The time can be viewed in standard form (HH:MM:SS AM/PM),
 * military time (HHMM hours SS), and analogue.
 * The date can be viewed in standard form (MONTH DATE, YEAR),
 * partial (DAY_OF_WEEK MONTH DATE, YEAR), and fully expressed
 * (DAY_OF_WEEK MONTH DATE, YEAR).
 * 
 * @author Michael Ball 
*  @version since 1.0
 */
public class Clock implements Serializable, Comparable<Clock>
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Clock.class);

    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private Month month;

    private LocalTime time;
    private int seconds,minutes,hours,dayOfMonth,year;
    private String ampm;
    private ZoneId timezone;
    private String hoursAsStr=EMPTY, minutesAsStr=EMPTY, secondsAsStr=EMPTY;

    private LocalDate beginDaylightSavingsTimeDate,
                      endDaylightSavingsTimeDate;
    private LocalDateTime currentDateTime;
    private List<Alarm> listOfAlarms;
    private List<Timer> listOfTimers;
    public ClockFrame clockFrame;
    private boolean isLeapYear, todayMatchesDSTDate,
            dateChanged, isNewYear,
            showFullDate,
            showPartialDate, showMilitaryTime,
            testingClock,
            daylightSavingsTimeEnabled;


    /**
     * Default constructor for the Clock class.
     */
    public Clock()
    {
        this(false);
    }

    /**
     * Creates a new Clock object with the testing flag set.
     * @param testing if the clock is for testing purposes
     */
    public Clock(boolean testing)
    {
        super();
        setTestingClock(testing);
        initialize();
    }

    /**
     * Custom constructor which takes in values for all Clock
     * parameters and sets them based on those inputs.
     * If the hours provided is greater than 12, the clock
     * will assume military time.
     * @param hours      the hours to set, 0-12/23, depending
     * @param minutes    the minutes to set
     * @param seconds    the seconds to set
     * @param month      the month to set
     * @param dayOfWeek  the day of the week to set
     * @param dayOfMonth the date of the month to set
     * @param year       the year to set
     * @param ampm       the AM or PM to set
     * @throws InvalidInputException when an InvalidInput has been given
     * @see InvalidInputException
     */
    public Clock(int hours, int minutes, int seconds, Month month, DayOfWeek dayOfWeek,
                 int dayOfMonth, int year, String ampm) throws InvalidInputException
    {
        logger.info("Initializing Test Clock");
        setTestingClock(true);
        // Validate the inputs
        if (hours < 0 || hours > 23) {
            if (hours > 12) throw new IllegalArgumentException("Hours must be between 0 and 23");
            else throw new IllegalArgumentException("Hours must be between 0 and 12");
        }
        if (minutes < 0 || minutes > 59) throw new IllegalArgumentException("Minutes must be between 0 and 59");
        if (seconds < 0 || seconds > 59) throw new IllegalArgumentException("Seconds must be between 0 and 59");
        // TODO: Enhance by first checking what month it is. Then determine exactly what values are acceptable for that month and display proper IllegalArgumentException message. Ex: Feb would say between 1 and 28 or even 29 if it is a leap year
        if (dayOfMonth < 1 || dayOfMonth > 31) throw new IllegalArgumentException("The day of month must be between 1 and 31");
        // TODO: May want to think about but for now, the year must be 4 digits long and at least 1000 or more
        if (year < 1000) throw new IllegalArgumentException("Year must be greater than 1000");
        if (hours > 12) setShowMilitaryTime(true);
        setSeconds(seconds);
        setMinutes(minutes);
        setHours(hours);
        setAMPM(ampm);
        setMonth(month);
        setDayOfWeek(dayOfWeek);
        setDayOfMonth(dayOfMonth);
        setYear(year);
        setTimeZone(getZoneIdFromTimezoneButtonText(EMPTY));
        setTheCurrentTime();
        setDaylightSavingsTimeDates();
        if (isTodayDaylightSavingsTime()) { todayMatchesDSTDate = true; }
        isLeapYear = date.isLeapYear();
        listOfAlarms = new ArrayList<>();
        listOfTimers = new ArrayList<>();
        daylightSavingsTimeEnabled = true;
    }

    /**
     * Initializes the clock with default settings, including setting the initial time,
     * configuring the menu bar, setting up daylight savings time dates, and creating
     * various clock panels. It also sets the clock's size, location, and icon.
     */
    private void initialize()
    {
        logger.info("Initializing {} Clock", testingClock ? "Test" : "");
        setTheTime(LocalDateTime.now());
        setDaylightSavingsTimeDates();
        if (isTodayDaylightSavingsTime()) { todayMatchesDSTDate = true; }
        isLeapYear = date.isLeapYear();
        listOfAlarms = new ArrayList<>();
        listOfTimers = new ArrayList<>();
        daylightSavingsTimeEnabled = true;
    }

    /**
     * Sets the seconds, minutes, hours, month,
     * dayOfWeek, dayOfMonth, year, and timezone
     * Those values are used to set the date and
     * time and last the AMPM value. The inner
     * methods logs the setting of each value.
     * @param dateTime the dateTime values to use
     */
    public void setTheTime(LocalDateTime dateTime)
    {
        logger.info("Setting the time");
        setSeconds(dateTime.getSecond());
        setMinutes(dateTime.getMinute());
        setHours(dateTime.getHour()==0 && !showMilitaryTime ? 12 : dateTime.getHour());
        setAMPM(dateTime.getHour()<12?AM:PM);
        setMonth(dateTime.getMonth());
        setDayOfWeek(dateTime.getDayOfWeek());
        setDayOfMonth(dateTime.getDayOfMonth());
        setYear(dateTime.getYear());
        setTimeZone(getZoneIdFromTimezoneButtonText(EMPTY));
        setTheCurrentTime();
    }

    /**
     * Sets and logs the new date value from the year, month, and dayOfMonth
     * Sets and logs the new time value from the hours, minutes, and seconds
     * Sets the currentTime value from the LocalDate and LocalTime
     */
    protected void setTheCurrentTime()
    {
        setTimeZone(ZoneId.systemDefault());
        setDate(LocalDate.of(year, month, dayOfMonth));
        setTime(LocalTime.of(hours, minutes, seconds));
        setCurrentDateTime(LocalDateTime.of(date, time));
        setDaylightSavingsTimeDates();
        if (isTodayDaylightSavingsTime()) { setTodayMatchesDSTDate(true); }
        setLeapYear(date.isLeapYear());
        setDateChanged(false);
    }

    /**
     * Sets the dates for the beginning and ending of
     * daylight savings time.
     * Beginning date is always the second Sunday
     * Ending date is always the first Sunday
     */
    private void setDaylightSavingsTimeDates()
    {
        logger.debug("setting begin and end daylight savings dates");
        int sundayCount = 0;
        int firstOfMonth = 1;
        LocalDate beginDate = LocalDate.of(year, MARCH, firstOfMonth); // 3
        while (sundayCount != 2) {
            DayOfWeek day = beginDate.getDayOfWeek();
            if (day == SUNDAY) {
                sundayCount++;
                if (sundayCount == 2) firstOfMonth -= 1;
            }
            beginDate = LocalDate.of(year, MARCH, firstOfMonth++);
        }
        setBeginDaylightSavingsTimeDate(beginDate);

        sundayCount = 0;
        firstOfMonth = 1;
        LocalDate endDate = LocalDate.of(getYear(), NOVEMBER, firstOfMonth); // 11
        while (sundayCount != 1) {
            DayOfWeek day = endDate.getDayOfWeek();
            if (day == SUNDAY) {
                sundayCount++;
                firstOfMonth -= 1;
            }
            endDate = LocalDate.of(year, 11, ++firstOfMonth);
        }
        setEndDaylightSavingsTimeDate(endDate);
        logger.info("daylight savings dates set");
    }

    /**
     * Returns the String associated to the selected ZoneId
     * Example log: timezone: America/Chicago or Central Time
     * @param timezone the selected ZoneId
     * @return String the plain timezone from the selected ZoneId
     */
    protected String getPlainTimezoneFromZoneId(ZoneId timezone)
    {
        logger.debug("timezone: {} or {}", timezone, timezone.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        return switch (timezone.getId()) {
            case PACIFIC_HONOLULU -> HAWAII;
            case AMERICA_ANCHORAGE -> ALASKA;
            case AMERICA_LOS_ANGELES -> PACIFIC;
            case AMERICA_CHICAGO -> CENTRAL;
            case AMERICA_NEW_YORK -> EASTERN;
            default -> ZoneId.systemDefault().getId();
        };
    }

    /**
     * Returns a Runnable that will update the time if the current time
     * does not match the clock time. This is executed once a second.
     * @return Runnable the runnable to update the time
     */
    public Runnable updateOutdatedTime()
    { return () -> shouldUpdateTime(LocalDateTime.now(timezone)); }

    /**
     * Determines if the expected currentTime now is
     * the same as the clock time. If it is not, the
     * clock time is updated.
     * @param rightNow the current time
     * @return boolean if the time was updated
     */
    public boolean shouldUpdateTime(LocalDateTime rightNow)
    {
        LocalDateTime nowUpdated = formatCurrentTimeToNonMilitaryTime(rightNow);
        nowUpdated = nowUpdated.minusNanos(nowUpdated.getNano());
        LocalDateTime clockTime = getCurrentDateTime();
        logger.debug("current time: {}", nowUpdated);
        logger.debug("clock time:   {}", clockTime);
        boolean timesAreTheSame = nowUpdated.equals(clockTime);
        logger.debug("{}", timesAreTheSame ? "times are the same" : "times are not the same");
        if (!timesAreTheSame) {
            logger.warn("clock time is incorrect. updating time");
            setTheTime(nowUpdated);
            if (null != rightNow) {
                setAMPM(rightNow.getHour() < 12 ? AM : PM);
            } else {
                String ampm = getAMPMFromTime(null);
                setAMPM(ampm);
            }
            //digitalClockPanel.updateLabels();
            return true;
        }
        return false;
    }

    /**
     * Returns the AMPM from the current time
     * @return String the AMPM from the current time
     */
    protected String getAMPMFromTime(LocalDateTime now)
    {
        ZonedDateTime zonedDateTime = getZonedDateTimeFromLocalDateTime(now);
        String ampm = zonedDateTime.format(DateTimeFormatter.ofPattern("a"));
        logger.debug("zdt: {} ampm: {}", zonedDateTime, ampm);
        return ampm;
    }

    /**
     * Returns a ZonedDateTime from a LocalDateTime
     * @param now the local date time
     * @return ZonedDateTime the zoned date time
     */
    protected ZonedDateTime getZonedDateTimeFromLocalDateTime(LocalDateTime now)
    { return now == null ? ZonedDateTime.now(timezone) : ZonedDateTime.of(now, timezone); }

    /**
     * Creates a zoned datetime object and then subtracts 12
     * hours from the result if we are in PM, we want standard
     * time display versus military time, and the hour is greater
     * than 12.
     * @return LocalDateTime the current time in non-military time
     */
    protected LocalDateTime formatCurrentTimeToNonMilitaryTime(LocalDateTime now)
    {
        logger.debug("now: {}", now);
        ZonedDateTime zonedDateTime = getZonedDateTimeFromLocalDateTime(now);
        String ampm = zonedDateTime.format(DateTimeFormatter.ofPattern("a"));
        logger.debug("zdt: {}", zonedDateTime);
        logger.debug("formatted ampm: {}", ampm);
        if (PM.equals(ampm) && !showMilitaryTime && zonedDateTime.getHour() > 12)
        { zonedDateTime = zonedDateTime.minusHours(12); }
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * Updates the hours and hoursAsStr values based
     * upon the current AMPM value and if we should
     * display time using military hours or not
     */
    public void updateHourValueAndHourString()
    {
        if (AM.equals(ampm)) {
            // Daytime and we show Military Time
            if (showMilitaryTime) {
                if (hours > 12) setHours(0);
                else setHours(hours);
            }
            // DayTime and we do not show Military Time
            else {
                if (hours == 0) setHours(12);
                else setHours(hours);
            }
        }
        else {
            // NightTime and we show Military Time
            if (showMilitaryTime) {
                if (hours == 24) setHours(0);
                else if (hours < 12 && hours >= 0) setHours(hours+12);
                else setHours(hours);
            }
            // NightTime and we do not show Military Time
            else {
                if (hours > 12) setHours(hours-12);
            }
        }
    }

    /**
     * Determines if today's date is equal to one
     * of the predetermined daylight savings time
     * @return boolean if today is daylight savings time
     */
    protected boolean isTodayDaylightSavingsTime()
    {
        return date.isEqual(beginDaylightSavingsTimeDate) ||
                date.isEqual(endDaylightSavingsTimeDate);
    }

    /**
     * Returns the default text for the clock label
     * @param labelVersion the value of the label to sue
     * @return String the default text for a label
     */
    public String defaultText(int labelVersion)
    {
        String defaultText = EMPTY;
        if (labelVersion == 1) {
            if (showFullDate && !showPartialDate) defaultText = getFullDateAsStr();
            else if (showPartialDate && !showFullDate) defaultText = getPartialDateAsStr();
            else defaultText = getDateAsStr();
        }
        else if (labelVersion == 2) {
            if (!showMilitaryTime) {
                if (PM.equals(ampm) && hours > 12) { setHours(hours-12); }
                defaultText = getTimeAsStr();
            }
            else {
                defaultText = getMilitaryTimeAsStr();
            }
        }
        else if (labelVersion == 3) { defaultText = Hours; }
        else if (labelVersion == 4) { defaultText = Minutes; }
        else if (labelVersion == 5) { defaultText = AM+SLASH+PM; }
        else if (labelVersion == 6) { defaultText = No_Alarms; }
        else if (labelVersion == 7) { defaultText = S; }
        //else if (labelVersion == 8) { defaultText = alarmPanel.getActiveAlarm().toString(); }
        else if (labelVersion == 9) { defaultText = is+SPACE+going_off; }
        else if (labelVersion == 10) { defaultText = No_Timers; }
        return defaultText;
    }

    /**
     * A default tick of the clock
     */
    public void tick()
    { tick(1,1,1); }

    /**
     * The purpose of tick is to update the clock.
     * Given the values of seconds, minutes, and hours, with each tick,
     * when one of these values rolls over, it will update its respective
     * value by that amount.
     * Then updates the clock labels to display the changes
     * in time or date.
     *
     * @param seconds, the amount of seconds to tick forward or backwards with each tick
     * @param minutes, the amount of minutes to tick forward or backwards with each tick
     * @param hours,   the amount of hours   to tick forward or backwards with each tick
     */
    public void tick(int seconds, int minutes, int hours)
    {
        logger.info("tick rate: sec: {} min: {} hrs: {}", seconds, minutes, hours);
        performTick(seconds, minutes, hours);
        checkIfItIsNewYears();
        updateTimeIfMidnight();
        setActiveAlarms();
        setActiveTimers();
        setTriggeredAlarms();
        setTheCurrentTime();
    }

    /**
     * updateJLabels performs the logic to update the time, date, month,
     * and many other values. it also updates the values we see on the
     * clock face.
     *
     * @param seconds, the amount of time to increase or decrease seconds when time to roll over
     * @param minutes, the amount of time to increase or decrease minutes when time to roll over
     * @param hours,   the amount of time to increase or decrease hours when time to roll over
     */
    private void performTick(Integer seconds, Integer minutes, Integer hours)
    {
        logger.info("performing tick...");
        setSeconds(this.seconds+seconds);
        if (this.seconds >= 60)
        {
            logger.debug("updating second");
            setSeconds(this.seconds-60);
            setMinutes(this.minutes+minutes);
            if (this.minutes >= 60)
            {
                logger.debug("updating minute");
                setMinutes(this.minutes-60);
                setHours(this.hours+hours);
                logger.debug("time: " + getTimeAsStr());
                if (this.hours >= 12 && this.minutes == 0 && this.seconds == 0 && !showMilitaryTime)
                {
                    logger.debug("updating hour");
                    setHours(12);
                    if (PM.equals(ampm))
                    {
                        logger.debug("changing to AM");
                        setAMPM(AM);
                        setDateChanged(true);
                    }
                    else
                    {
                        logger.debug("changing to PM");
                        setAMPM(PM);
                        setDateChanged(false);
                    }
                }
                else if (this.hours >= 24 && this.minutes == 0 && this.seconds == 0)
                {
                    setHours(0);
                    setAMPM(AM);
                    setDateChanged(true);
                }
                else if (this.hours >= 13 && !showMilitaryTime)
                {
                    setHours(this.hours-12);
                    setDateChanged(false);
                }
                else
                {
                    setHours(this.hours);
                    setDateChanged(false);
                }
            }
        }
        else { setDateChanged(false); }
        //updateHourValueAndHourString();
        if (!showMilitaryTime) { logger.info(getTimeAsStr()); }
        else { logger.info(getMilitaryTimeAsStr()); }

        if (dateChanged) {
            logger.info("date has changed");
            setDayOfMonth(dayOfMonth+1);
            setTodayMatchesDSTDate(isTodayDaylightSavingsTime());
            switch(dayOfWeek) {
                case SUNDAY -> setDayOfWeek(MONDAY);
                case MONDAY -> setDayOfWeek(TUESDAY);
                case TUESDAY -> setDayOfWeek(WEDNESDAY);
                case WEDNESDAY -> setDayOfWeek(THURSDAY);
                case THURSDAY -> setDayOfWeek(FRIDAY);
                case FRIDAY -> setDayOfWeek(SATURDAY);
                case SATURDAY -> setDayOfWeek(SUNDAY);
                default -> logger.error("Unknown DayOfWeek: " + getDayOfWeek());
            }
            switch (month)
            {
                case JANUARY ->
                {
                    if (dayOfMonth == 31)
                    {
                        setDayOfMonth(1);
                        setMonth(FEBRUARY);
                    }
                }
                case FEBRUARY ->
                {
                    if (!isLeapYear && dayOfMonth == 29 || isLeapYear && dayOfMonth == 30)
                    {
                        setDayOfMonth(1);
                        setMonth(MARCH);
                    }
                    else if (isLeapYear && dayOfMonth == 29)
                    { logger.info("happy leap day"); }
                }
                case MARCH ->
                {
                    if (dayOfMonth == 32)
                    {
                        setDayOfMonth(1);
                        setMonth(APRIL);
                    }
                }
                case APRIL ->
                {
                    if (dayOfMonth == 31)
                    {
                        setDayOfMonth(1);
                        setMonth(MAY);
                    }
                }
                case MAY ->
                {
                    if (dayOfMonth == 32)
                    {
                        setDayOfMonth(1);
                        setMonth(JUNE);
                    }
                }
                case JUNE ->
                {
                    if (dayOfMonth == 31)
                    {
                        setDayOfMonth(1);
                        setMonth(JULY);
                    }
                }
                case JULY ->
                {
                    if (dayOfMonth == 32)
                    {
                        setDayOfMonth(1);
                        setMonth(AUGUST);
                    }
                }
                case AUGUST ->
                {
                    if (dayOfMonth == 32)
                    {
                        setDayOfMonth(1);
                        setMonth(SEPTEMBER);
                    }
                }
                case SEPTEMBER ->
                {
                    if (dayOfMonth == 31)
                    {
                        setDayOfMonth(1);
                        setMonth(OCTOBER);
                    }
                }
                case OCTOBER ->
                {
                    if (dayOfMonth == 32)
                    {
                        setDayOfMonth(1);
                        setMonth(NOVEMBER);
                    }
                }
                case NOVEMBER ->
                {
                    if (dayOfMonth == 31)
                    {
                        setDayOfMonth(1);
                        setMonth(DECEMBER);
                    }
                }
                case DECEMBER ->
                {
                    if (dayOfMonth == 32)
                    {
                        setDayOfMonth(1);
                        setMonth(JANUARY);
                        setYear(year+1);
                        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
                        setLeapYear(date.isLeapYear());
                        setIsNewYear(true);
                        setDaylightSavingsTimeDates();
                        logger.info("new year!");
                    }
                }
                default -> logger.error("Unknown Month: {}", month);
            }
            setDate(LocalDate.of(year, month, dayOfMonth));
            if (isTodayDaylightSavingsTime()) { setTodayMatchesDSTDate(true); }
        }
        if (daylightSavingsTimeEnabled && todayMatchesDSTDate && this.hours == 2)
        {
            logger.debug("!! daylight savings time now !!");
            if (month == MARCH && AM.equals(ampm))
            {
                logger.debug("spring forward");
                setHours(3);
                setTodayMatchesDSTDate(false);
            }
            else if (month == NOVEMBER && AM.equals(ampm))
            {
                logger.debug("fall back");
                setHours(1);
                setTodayMatchesDSTDate(false);
            }
            logger.debug("setting doesTodayMatchDSTDate to {}", todayMatchesDSTDate);
        }
        else {
            if (!daylightSavingsTimeEnabled)
            {
                logger.debug("daylight savings time not enabled");
                logger.debug("not adjusting time");
            }
            else
            { logger.debug("!! today is not dst !!"); }
        }
    }

    /**
     * Updates the time if the clock is at midnight. This helps
     * to ensure that the clock is always up to date and accurate
     * by chance that it has gotten out of sync.
     */
    private void updateTimeIfMidnight()
    {
        if ((MIDNIGHT_STANDARD_TIME.equals(getTimeAsStr()) || MIDNIGHT_MILITARY_TIME.equals(getMilitaryTimeAsStr()))) {
            logger.info("midnight daily clock update");
            setTheTime(LocalDateTime.of(
                    LocalDate.of(getYear(), getMonth(), getDayOfMonth()),
                    LocalTime.of(0, 0, 0))
            );
        }
    }

    private void checkIfItIsNewYears()
    {
        logger.info("is today new years: {}", isNewYear);
        if (isNewYear())
        {
            logger.info("Happy New Year. Here's wishing you a healthy, productive {}.", year);
            setIsNewYear(false);
        }
    }

    private void setActiveAlarms()
    {
        getListOfAlarms().forEach(Alarm::startAlarm);
        getListOfAlarms().stream().filter(Alarm::isAlarmGoingOff).forEach(Alarm::triggerAlarm);
    }

    private void setActiveTimers()
    {
        List<Timer> timersGoingOff = getListOfTimers().stream()
                .filter(Timer::isTimerGoingOff)
                .toList();
        logger.debug("triggering {} timers", timersGoingOff.size());
        timersGoingOff.forEach(Timer::triggerTimer);
        getListOfTimers().forEach(Timer::startTimer);
        clockFrame.getTimerPanel2().setupTimersTableDefaults(false);
    }

    /**
     * Resets the alarms that were triggered today.
     * This will allow alarms to be reset and not triggered
     * again while the current time is still the same as
     * when the alarm should triggered.
     */
    public void setTriggeredAlarms()
    {
        if (dateChanged) {
            AtomicInteger total = new AtomicInteger();
            getListOfAlarms().forEach(alarm -> {
                alarm.setTriggeredToday(false);
                total.getAndIncrement();
            });
            logger.info("setting {} alarms to not triggered today", total.get());
        }
    }

    /**
     * Triggers all alarms that are currently going off.
     */
    public void triggerAlarms()
    {
        List<Alarm> alarmsToTrigger = getListOfAlarms().stream()
            .filter(Alarm::isAlarmGoingOff)
                .toList();
        if (alarmsToTrigger.isEmpty()) {
            logger.debug("no alarms are going off");
        } else {
            logger.debug("triggering {} alarms", alarmsToTrigger.size());
            alarmsToTrigger.forEach(Alarm::triggerAlarm);
        }
    }

    /**
     * Checks if the timer has concluded
     */
    public void triggerTimers()
    {

    }

    /* Getters */
    public ClockFrame getClockFrame() { return clockFrame; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public LocalDate getBeginDaylightSavingsTimeDate() { return this.beginDaylightSavingsTimeDate; }
    public LocalDate getEndDaylightSavingsTimeDate() { return this.endDaylightSavingsTimeDate; }
    public int getSeconds() { return seconds; }
    public int getMinutes() { return minutes; }
    public int getHours() { return hours; }
    public String getAMPM() { return ampm; }
    public ZoneId getTimezone() { return timezone; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public int getDayOfMonth() { return dayOfMonth; }
    public Month getMonth() { return month; }
    public int getYear() { return year; }
    public String getHoursAsStr() { return hoursAsStr; }
    public String getMinutesAsStr() { return minutesAsStr; }
    public String getSecondsAsStr() { return secondsAsStr; }
    /**
     * Returns the current time in the selected timezone
     * @return LocalDateTime the currentTime in the selected timezone
     */
    public LocalDateTime getCurrentDateTime() { return currentDateTime; }
    /**
     * This method returns the clock's current hour, minute, second, and time.
     * It can also be used to get the alarm's time set value
     * @return 'HH:MM:SS AMPM' ex: 05:15:24 PM
     */
    public String getTimeAsStr() { return hoursAsStr+COLON+minutesAsStr+COLON+secondsAsStr+SPACE+ampm; }
    public String getClockTimeAsAlarmString() {
        if (showMilitaryTime) {
            if (hours > 12) {
                var hours = this.hours - 12;
                if (hours <= 9) {
                    return "0"+hours+COLON+minutesAsStr+SPACE+ampm;
                } else {
                    return hours+COLON+minutesAsStr+SPACE+ampm;
                }
            } else {
                return hoursAsStr+COLON+minutesAsStr+SPACE+ampm;
            }
        } else {
            return hoursAsStr+COLON+minutesAsStr+SPACE+ampm;
        }
    }
    /**
     * Returns the date like: MAY 4, 2000
     * @return the date as a formatted string
     */
    public String getDateAsStr() { return month+SPACE+dayOfMonth+COMMA+SPACE+year; }
    public String getFullDateAsStr() { return dayOfWeek+SPACE+month+SPACE+dayOfMonth+COMMA+SPACE+year; }
    public String getMilitaryTimeAsStr() { return hoursAsStr+minutesAsStr+SPACE+Hours.toLowerCase()+SPACE+secondsAsStr; }
    public String getPartialDateAsStr() { return dayOfWeek.toString().substring(0,3)+SPACE+month.toString().substring(0,3)+SPACE+dayOfMonth+COMMA+SPACE+year; }
    public boolean isLeapYear() { return isLeapYear; }
    public boolean isTodayMatchesDSTDate() { return todayMatchesDSTDate; }
    public boolean isDateChanged() { return dateChanged; }
    public boolean isNewYear() { return isNewYear; }
    public boolean isShowFullDate() { return showFullDate; }
    public boolean isShowPartialDate() { return showPartialDate; }
    public boolean isShowMilitaryTime() { return showMilitaryTime; }
    public boolean isTestingClock() { return testingClock; }
    public boolean isDaylightSavingsTimeEnabled() { return daylightSavingsTimeEnabled; }
    public List<Alarm> getListOfAlarms() { return listOfAlarms; }
    public List<Timer> getListOfTimers() { return listOfTimers; }
    /**
     * Returns the timezone from the selected timezone button text
     * @param btnText the text from the timezone button
     * @return ZoneId the timezone from the selected timezone button text
     */
    public ZoneId getZoneIdFromTimezoneButtonText(String btnText) {
        logger.debug("btnText: {}", btnText);
        return switch (btnText) {
            case HAWAII -> ZoneId.of(PACIFIC_HONOLULU);
            case ALASKA -> ZoneId.of(AMERICA_ANCHORAGE);
            case PACIFIC -> ZoneId.of(AMERICA_LOS_ANGELES);
            case CENTRAL -> ZoneId.of(AMERICA_CHICAGO);
            case EASTERN -> ZoneId.of(AMERICA_NEW_YORK);
            default -> ZoneId.systemDefault();
        };
    }

    /* Setters */
    protected void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; }
    /**
     * Sets and logs the new second value
     * Also sets secondsAsStr
     * @param seconds the new seconds value
     */
    protected void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) secondsAsStr = "0"+this.seconds;
        else secondsAsStr = Integer.toString(this.seconds);
        logger.debug("seconds: {} asStr: {}", this.seconds, secondsAsStr);
    }
    /**
     * Sets and logs the new minute value
     * Also sets minutesAsStr
     * @param minutes the new minutes value
     */
    protected void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) minutesAsStr = "0"+this.minutes;
        else minutesAsStr = Integer.toString(this.minutes);
        logger.debug("minutes: {} asStr: {}", this.minutes, minutesAsStr);
    }
    /**
     * Sets and logs the new hour value
     * Also sets hoursAsStr
     * @param hours the new hours value
     */
    protected void setHours(int hours) {
        if (hours > 12 && !showMilitaryTime) { hours-=12; }
        else if (showMilitaryTime && hours == 24) { hours = 0; }
        this.hours = hours;
        if (this.hours < 10) this.hoursAsStr = "0"+hours;
        else this.hoursAsStr = Integer.toString(this.hours);
        logger.debug("hours: {} asStr: {}", this.hours, hoursAsStr);
    }
    /**
     * Sets and logs the new AMPM value
     * @param ampm the new AMPM value
     */
    protected void setAMPM(String ampm) { this.ampm = ampm; logger.debug("ampm: {}", this.ampm); }
    /**
     * Sets and logs the new timezone value
     * @param timezone the new timezone value
     */
    public void setTimeZone(ZoneId timezone) { this.timezone = timezone; logger.debug("timezone: {}", getPlainTimezoneFromZoneId(timezone)); }
    /** Sets and logs the new time value
     * @param time the new time value
     */
    private void setTime(LocalTime time) { this.time = time; logger.debug("time: {}", getTimeAsStr()); }
    /**
     * Sets and logs the new dayOfWeek value
     * @param dayOfWeek the new dayOfWeek value
     */
    protected void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; logger.debug("dayOfWeek: {}", dayOfWeek); }
    /**
     * Sets and logs the new dayOfMonth value
     * @param dayOfMonth the new dayOfMonth value
     */
    protected void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; logger.debug("dayOfMonth: {}", dayOfMonth); }
    /**
     * Sets and logs the new month value
     * @param month the new month value
     */
    protected void setMonth(Month month) { this.month = month; logger.debug("month: {}", month); }
    /**
     * Sets and logs the new year value
     * @param year the new year value
     */
    protected void setYear(int year) { this.year = year; logger.debug("year: {}", year); }
    /**
     * Sets and logs the new date value
     * Example log: FRIDAY MAY 4, 2000
     * @param date the new date value
     */
    protected void setDate(LocalDate date) { this.date = date; logger.debug("date: {} {}", dayOfWeek!=null?dayOfWeek.toString():"DayOfWeekUnset", getDateAsStr()); }
    /**
     * Sets and logs the new current time
     * @param currentDateTime the new current time
     */
    protected void setCurrentDateTime(LocalDateTime currentDateTime) { this.currentDateTime = currentDateTime; logger.debug("currentTime: {}", DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a").format(currentDateTime)); }
    /**
     * Sets and logs the new begin dst date value
     * @param beginDaylightSavingsTimeDate the new begin dst date value
     */
    protected void setBeginDaylightSavingsTimeDate(LocalDate beginDaylightSavingsTimeDate) { this.beginDaylightSavingsTimeDate = beginDaylightSavingsTimeDate; logger.debug("begin dst: {} {} {}, {}",beginDaylightSavingsTimeDate.getDayOfWeek(), beginDaylightSavingsTimeDate.getMonth(), beginDaylightSavingsTimeDate.getDayOfMonth(), beginDaylightSavingsTimeDate.getYear()); }
    /**
     * Sets and logs the new end dst date value
     * @param endDaylightSavingsTimeDate the new end dst date value
     */
    protected void setEndDaylightSavingsTimeDate(LocalDate endDaylightSavingsTimeDate) { this.endDaylightSavingsTimeDate = endDaylightSavingsTimeDate; logger.debug("end dst: {} {} {}, {}", endDaylightSavingsTimeDate.getDayOfWeek(), endDaylightSavingsTimeDate.getMonth(), endDaylightSavingsTimeDate.getDayOfMonth(), endDaylightSavingsTimeDate.getYear()); }
    protected void setLeapYear(boolean leapYear) { this.isLeapYear = leapYear; }
    /**
     * When the clock starts and the date matches a daylight savings
     * date, this value is set. It is also set after the date updates,
     * and that new date matches a daylight savings date.
     * @param todayMatchesDSTDate if today is daylight savings day
     */
    protected void setTodayMatchesDSTDate(boolean todayMatchesDSTDate) { this.todayMatchesDSTDate = todayMatchesDSTDate; logger.debug("today is dst? {}", todayMatchesDSTDate); }
    /**
     * Sets and logs the new dateChanged value
     * @param isDateChanged the dateChanged value to set
     */
    protected void setDateChanged(boolean isDateChanged) { this.dateChanged = isDateChanged; logger.debug("dateChanged: {}", dateChanged); }
    /**
     * Sets and logs the new isNewYear value
     * @param isNewYear the isNewYear value to set
     */
    protected void setIsNewYear(boolean isNewYear) { this.isNewYear = isNewYear; logger.debug("isNewYear: {}", isNewYear); }
    protected void setShowFullDate(boolean showFullDate) { this.showFullDate = showFullDate; }
    protected void setShowPartialDate(boolean showPartialDate) { this.showPartialDate = showPartialDate; }
    protected void setShowMilitaryTime(boolean showMilitaryTime) { this.showMilitaryTime = showMilitaryTime; }
    private void setTestingClock(boolean testingClock) { this.testingClock = testingClock; }
    protected void setDaylightSavingsTimeEnabled(boolean daylightSavingsTimeEnabled) { this.daylightSavingsTimeEnabled = daylightSavingsTimeEnabled; }
    protected void setListOfAlarms(List<Alarm> listOfAlarms) { this.listOfAlarms = listOfAlarms; }
    protected void setListOfTimers(List<Timer> listOfTimers) { this.listOfTimers = listOfTimers; }

    @Override
    public int compareTo(Clock o) {
        return this.getCurrentDateTime().compareTo(o.getCurrentDateTime());
    }
}