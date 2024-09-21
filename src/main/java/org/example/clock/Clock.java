package org.example.clock;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.time.Month.*;
import static java.time.DayOfWeek.*;
import static org.example.clock.ClockConstants.*;
import static org.example.clock.PanelType.*;

/**
 * The clock object is capable of showing the date and time.
 * The time can be viewed in standard form (HH:MM:SS AM/PM),
 * military time (HHMM hours SS), and analogue.
 * The date can be viewed in standard form (MONTH DATE, YEAR),
 * partial (DAY_OF_WEEK MONTH DATE, YEAR), and fully expressed
 * (DAY_OF_WEEK MONTH DATE, YEAR).
 * 
 * @author Michael Ball 
 * @version 2.7
 */
public class Clock extends JFrame 
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Clock.class);

    final static Dimension defaultSize = new Dimension(700, 300);
    final static Dimension panelSize = new Dimension(400, 300);
    final static Dimension alarmSize = new Dimension(200,100);
    final static Font font60 = new Font("Courier New", Font.BOLD, 60);
    final static Font font50 = new Font("Courier New", Font.BOLD, 50);
    final static Font font40 = new Font("Courier New", Font.BOLD, 40);
    final static Font font20 = new Font("Courier New", Font.BOLD, 20);
    final static Font font10 = new Font("Courier New", Font.BOLD, 10);

    private PanelType panelType; // used to determine panel in use
    private IClockPanel currentPanel;
    private ClockMenuBar menuBar;
    private DigitalClockPanel digitalClockPanel;
    private AnalogueClockPanel analogueClockPanel;
    private AlarmPanel alarmPanel;
    private TimerPanel timerPanel;
    private LocalDate beginDaylightSavingsTimeDate;
    private LocalDate endDaylightSavingsTimeDate;
    private LocalDateTime currentTime;
    private ZoneId timezone;
    private LocalDate date;
    private LocalTime time;
    private int seconds,minutes,hours,dayOfMonth,year;
    private String ampm;
    private DayOfWeek dayOfWeek;
    private Month month;
    private String hoursAsStr=EMPTY, minutesAsStr=EMPTY, secondsAsStr=EMPTY;
    private boolean leapYear,isDaylightSavingsTime,isDateChanged,isNewYear,
            alarm,timer,updateAlarm,showFullDate,showPartialDate,
            showMilitaryTime,showDigitalTimeOnAnalogueClock,testingClock,
            daylightSavingsTimeEnabled=true;
    private List<Alarm> listOfAlarms;
    private ImageIcon icon;
    private ScheduledExecutorService timeUpdater;

    public PanelType getPanelType() { return panelType; }
    public ClockMenuBar getClockMenuBar() { return menuBar; }
    public DigitalClockPanel getDigitalClockPanel() { return digitalClockPanel; }
    public AnalogueClockPanel getAnalogueClockPanel() { return analogueClockPanel; }
    public AlarmPanel getAlarmPanel() { return alarmPanel; }
    public TimerPanel getTimerPanel() { return timerPanel; }
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
     * This method returns the clock's current hour, minute, second, and time.
     * It can also be used to get the alarm's time set value
     * @return 'HH:MM:SS AMPM' ex: 05:15:24 PM
     */
    public String getTimeAsStr() { return hoursAsStr+COLON+minutesAsStr+COLON+secondsAsStr+SPACE+ampm; }
    public String getAlarmTimeAsStr() { return hoursAsStr+COLON+minutesAsStr+SPACE+ampm; }
    public String getDateAsStr() { return month+SPACE+dayOfMonth+COMMA+SPACE+year; }
    public String getFullDateAsStr() { return dayOfWeek+SPACE+month+SPACE+dayOfMonth+COMMA+SPACE+year; }
    public String getMilitaryTimeAsStr() { return hoursAsStr+minutesAsStr+SPACE+Hours.toLowerCase()+SPACE+secondsAsStr; }
    public String getPartialDateAsStr() { return dayOfWeek.toString().substring(0,3)+SPACE+month.toString().substring(0,3)+SPACE+dayOfMonth+COMMA+SPACE+year; }
    public boolean isLeapYear() { return leapYear; }
    public boolean isDaylightSavingsTime() { return isDaylightSavingsTime; }
    public boolean isDateChanged() { return isDateChanged; }
    public boolean isNewYear() { return isNewYear; }
    public boolean isAlarmGoingOff() { return alarm; }
    public boolean isUpdateAlarm() { return updateAlarm; }
    public boolean isTimerGoingOff() { return timer; }
    public boolean isShowFullDate() { return showFullDate; }
    public boolean isShowPartialDate() { return showPartialDate; }
    public boolean isShowMilitaryTime() { return showMilitaryTime; }
    public boolean isShowDigitalTimeOnAnalogueClock() { return showDigitalTimeOnAnalogueClock; }
    public boolean isTestingClock() { return testingClock; }
    public List<Alarm> getListOfAlarms() { return listOfAlarms; }
    public ScheduledExecutorService getTimeUpdater() { return timeUpdater; }
    public boolean isDaylightSavingsTimeEnabled() { return daylightSavingsTimeEnabled; }

    protected void setPanelType(PanelType panelType) { this.panelType = panelType; }
    protected void setClockMenuBar(ClockMenuBar menuBar) { this.menuBar = menuBar; }
    protected void setDigitalClockPanel(DigitalClockPanel digitalClockPanel) { this.digitalClockPanel = digitalClockPanel; }
    protected void setAnalogueClockPanel(AnalogueClockPanel analogueClockPanel) { this.analogueClockPanel = analogueClockPanel; }
    protected void setAlarmPanel(AlarmPanel alarmPanel) { this.alarmPanel = alarmPanel; }
    protected void setTimerPanel(TimerPanel timerPanel) { this.timerPanel = timerPanel; }
    protected void setBeginDaylightSavingsTimeDate(LocalDate beginDaylightSavingsTimeDate) { this.beginDaylightSavingsTimeDate = beginDaylightSavingsTimeDate; }
    protected void setEndDaylightSavingsTimeDate(LocalDate endDaylightSavingsTimeDate) { this.endDaylightSavingsTimeDate = endDaylightSavingsTimeDate; }
    protected void setDate(LocalDate date) { this.date = date; logger.debug("date: {} {}", date, dayOfWeek!=null?dayOfWeek.toString():"DayOfWeekUnset"); }
    protected void setTime(LocalTime time) { this.time = time; logger.debug("time: {} {}", DateTimeFormatter.ofPattern("hh:mm:ss").format(time), ampm); }
    /**
     * Sets and logs the new second value
     * Also sets secondsAsStr
     * @param seconds the new seconds value
     */
    protected void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) setSecondsAsStr("0"+this.seconds);
        else setSecondsAsStr(Integer.toString(this.seconds));
        logger.debug("seconds: {} asStr: {}", this.seconds, secondsAsStr);
    }
    /**
     * Sets and logs the new minute value
     * Also sets minutesAsStr
     * @param minutes the new minutes value
     */
    protected void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) setMinutesAsStr("0"+this.minutes);
        else setMinutesAsStr(Integer.toString(this.minutes));
        logger.debug("minutes: {} asStr: {}", this.minutes, minutesAsStr);
    }
    /**
     * Sets and logs the new hour value
     * Also sets hoursAsStr
     * @param hours the new hours value
     */
    protected void setHours(int hours) {
        this.hours = hours;
        if (this.hours < 10) this.hoursAsStr = "0"+hours;
        else this.hoursAsStr = Integer.toString(this.hours);
        logger.debug("hours: {} asStr: {}", this.hours, hoursAsStr);
    }
    protected void setAMPM(String ampm) {
        if (!List.of(AM,PM).contains(ampm)) throw new IllegalArgumentException("AMPM must be 'AM' or 'PM'");
        this.ampm = ampm; logger.debug("ampm: {}", this.ampm);
    }
    /**
     * Sets and logs the new timezone value
     * @param timezone the new timezone value
     */
    protected void setTimeZone(ZoneId timezone) { this.timezone = timezone; logger.debug("timezone: {}", timezone.getId()); }
    /**
     * Sets and logs the new dayOfWeek value
     * @param dayOfWeek the new dayOfWeek value
     */
    protected void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
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
    protected void setYear(int year) { this.year = year; logger.debug("year: {}", year); }
    protected void setHoursAsStr(String hoursAsStr) { this.hoursAsStr = hoursAsStr; }
    protected void setMinutesAsStr(String minutesAsStr) { this.minutesAsStr = minutesAsStr; }
    protected void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; }
    protected void setListOfAlarms(List<Alarm> listOfAlarms) { this.listOfAlarms = listOfAlarms; }
    protected void setUpdateAlarm(boolean updateAlarm) { this.updateAlarm = updateAlarm;}
    protected void setLeapYear(boolean leapYear) { this.leapYear = leapYear; }
    /**
     * When the clock starts and the date matches a daylight savings
     * date, this value is set. It is also set after the date updates,
     * and that new date matches a daylight savings date.
     * @param daylightSavingsTime if today is daylight savings day
     */
    protected void setDaylightSavingsTime(boolean daylightSavingsTime) { this.isDaylightSavingsTime = daylightSavingsTime; }
    protected void setIsDateChanged(boolean isDateChanged) { this.isDateChanged = isDateChanged; }
    protected void setIsNewYear(boolean isNewYear) { this.isNewYear = isNewYear; }
    protected void setIsAlarmGoingOff(boolean alarm) { this.alarm = alarm; }
    protected void setIsTimerGoingOff(boolean timer) { this.timer = timer; }
    protected void setShowFullDate(boolean showFullDate) { this.showFullDate = showFullDate; }
    protected void setShowPartialDate(boolean showPartialDate) { this.showPartialDate = showPartialDate; }
    protected void setShowMilitaryTime(boolean showMilitaryTime) { this.showMilitaryTime = showMilitaryTime; }
    protected void setShowDigitalTimeOnAnalogueClock(boolean showDigitalTimeOnAnalogueClock) { this.showDigitalTimeOnAnalogueClock = showDigitalTimeOnAnalogueClock; }
    protected void setTestingClock(boolean testingClock) { this.testingClock = testingClock; }
    protected void setDaylightSavingsTimeEnabled(boolean daylightSavingsTimeEnabled) { this.daylightSavingsTimeEnabled = daylightSavingsTimeEnabled; }

    /**
     * Default constructor for the Clock class.
     */
    public Clock() {
        super();
    }

    /**
     * Main constructor for the Clock class.
     * Initializes the clock with default settings, including setting the initial time,
     * configuring the menu bar, setting up daylight savings time dates, and creating
     * various clock panels. It also sets the clock's size, location, and icon.
     */
    public Clock(boolean initialize) {
        super();
        if (initialize) { initialize(); }
    }

    /**
     * Custom constructor which takes in values for all Clock
     * parameters and sets them based on those inputs. Expects
     * non-military time values. Also is the default constructor
     * for a test clock.
     * @param hours      the hours to set
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
    public Clock(int hours, int minutes, int seconds, Month month, DayOfWeek dayOfWeek, int dayOfMonth, int year, String ampm) throws InvalidInputException {
        this();
        testingClock = true;
        initialize();
        if (seconds < 0 || seconds > 59 && seconds != 60) throw new IllegalArgumentException("Seconds must be between 0 and 59");
        else setSeconds(seconds);
        if (minutes < 0 || minutes > 59 && minutes != 60) throw new IllegalArgumentException("Minutes must be between 0 and 59");
        else setMinutes(minutes);
        if (!showMilitaryTime) {
            if (hours < 0 || hours > 12) throw new IllegalArgumentException("Hours must be between 0 and 12");
            else setHours(hours);
        } else {
            if (hours < 0 || hours > 23) throw new IllegalArgumentException("Hours must be between 0 and 23");
            else setHours(hours);
        }
        if (List.of(Month.values()).contains(month)) setMonth(month);
        else throw new InvalidInputException("Invalid month '"+month+"'");
        if (List.of(DayOfWeek.values()).contains(dayOfWeek)) setDayOfWeek(dayOfWeek);
        else throw new InvalidInputException("Invalid day of week '"+dayOfWeek+"'");
        // TODO: Enhance by first checking what month it is. Then determine exactly what values are acceptable for that month and display proper IllegalArgumentException message. Ex: Feb would say between 1 and 28 or even 29 if it is a leap year
        if (dayOfMonth < 1 || dayOfMonth > 31) throw new IllegalArgumentException("The day of month must be between 1 and 31");
        else setDayOfMonth(dayOfMonth);
        // TODO: May want to think about but for now, the year must be 4 digits and at least 1000 or more
        if (year < 1000) throw new IllegalArgumentException("Year must be greater than 1000");
        else setYear(year);
        setTheTime(LocalDateTime.of(LocalDate.of(year,month,dayOfMonth), LocalTime.of(hours,minutes,seconds)));
        if (List.of(AM,PM).contains(ampm)) setAMPM(ampm);
        else throw new InvalidInputException("Invalid AM/PM value '"+ampm+"'");
        setDaylightSavingsTimeDates();
        setDaylightSavingsTime(isTodayDaylightSavingsTime());
        setLeapYear(getDate().isLeapYear());
    }

    public Clock initialize() {
        setBounds(200, 200, 700, 300);
        setListOfAlarms(new ArrayList<>());
        setShowMilitaryTime(false);
        setTheTime(LocalDateTime.now());
        setDaylightSavingsTimeDates();
        setupMenuBar();
        if (isTodayDaylightSavingsTime()) { setDaylightSavingsTime(true); }
        setDigitalClockPanel(new DigitalClockPanel(this));
        setAnalogueClockPanel(new AnalogueClockPanel(this));
        setAlarmPanel(new AlarmPanel(this));
        setTimerPanel(new TimerPanel(this));
        setLeapYear(getDate().isLeapYear());
        setIsDateChanged(false);
        setIsAlarmGoingOff(false);
        setIsTimerGoingOff(false);
        setSize(Clock.defaultSize);
        setImageIcon(createImageIcon("src/main/resources/images/clockImageIcon.png"));
        final Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(icon.getImage());
        setIconImage(icon.getImage());
        updatePanel(DIGITAL_CLOCK);
        setVisible(true);
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (!testingClock) {
            timeUpdater = Executors.newScheduledThreadPool(1);
            timeUpdater.scheduleAtFixedRate(updateOutdatedTime(), 10, 1, TimeUnit.SECONDS);
        }
        return this;
    }

    void setImageIcon(ImageIcon icon) { this.icon = icon; }

    void removePanel() {
        logger.debug("removing panel {}", getPanelType());
        switch (panelType) {
            case DIGITAL_CLOCK -> remove(digitalClockPanel);
            case ANALOGUE_CLOCK -> {
                getAnalogueClockPanel().stop();
                remove(analogueClockPanel);
            }
            case ALARM -> remove(alarmPanel);
            case TIMER -> remove(timerPanel);
        }
    }

    void updatePanel(PanelType panelType) {
        logger.debug("removing panel {}", panelType);
        switch (panelType) {
            case DIGITAL_CLOCK -> {
                changeToDigitalClockPanel();
                setPanelType(DIGITAL_CLOCK);
                digitalClockPanel.setupSettingsMenu();
            }
            case ANALOGUE_CLOCK -> {
                changeToAnalogueClockPanel();
                setPanelType(ANALOGUE_CLOCK);
                analogueClockPanel.setupSettingsMenu();
            }
            case ALARM -> {
                changeToAlarmPanel(true);
                setPanelType(ALARM);
                alarmPanel.setupSettingsMenu();
            }
            case TIMER -> {
                changeToTimerPanel();
                setPanelType(TIMER);
                timerPanel.setupSettingsMenu();
            }
        }
    }

    void setTheTime(LocalDateTime dateTime) {
        logger.info("Setting the time");
        setSeconds(dateTime.getSecond()); // sets secsAsStr
        setMinutes(dateTime.getMinute()); // sets minutesAsStr
        if (dateTime.getHour() > 12 && !showMilitaryTime) { setHours(dateTime.getHour()-12);}
        else { setHours(dateTime.getHour()); } // sets hoursAsStr
        setMonth(dateTime.getMonth());
        setDayOfWeek(dateTime.getDayOfWeek());
        setDayOfMonth(dateTime.getDayOfMonth());
        setYear(dateTime.getYear());
        setTimeZone(getZoneIdFromTimezoneButtonText(EMPTY));
        setCurrentTime();
        setAMPM(dateTime.getHour()<12?AM:PM);
    }

    void updateTheTime(JMenuItem timezone) {
        logger.info("updateTheTime");
        LocalDateTime ldt = determineNewTimeFromSelectedTimeZone(timezone.getText());
        setTheTime(ldt);
        setTimeZone(getZoneIdFromTimezoneButtonText(timezone.getText()));
        getClockMenuBar().setCurrentTimeZone();
    }

    void setCurrentTime() {
        setDate(LocalDate.of(year, month, dayOfMonth));
        setTime(LocalTime.of(hours, minutes, seconds));
        currentTime = LocalDateTime.of(date, time);
    }

    public LocalDateTime getCurrentTime() { return currentTime; }

    public LocalDateTime determineNewTimeFromSelectedTimeZone(String timezone) {
        return switch (timezone) {
            case HAWAII -> LocalDateTime.now(ZoneId.of(PACIFIC_HONOLULU));
            case ALASKA -> LocalDateTime.now(ZoneId.of(AMERICA_ANCHORAGE));
            case PACIFIC -> LocalDateTime.now(ZoneId.of(AMERICA_LOS_ANGELES));
            case CENTRAL -> LocalDateTime.now(ZoneId.of(AMERICA_CHICAGO));
            case EASTERN -> LocalDateTime.now(ZoneId.of(AMERICA_NEW_YORK));
            default -> LocalDateTime.now();
        };
    }

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

    public String getPlainTimezoneFromZoneId(ZoneId timezone) {
        logger.debug("timezone: {}", timezone.getId());
        return switch (timezone.getId()) {
            case PACIFIC_HONOLULU -> HAWAII;
            case AMERICA_ANCHORAGE -> ALASKA;
            case AMERICA_LOS_ANGELES -> PACIFIC;
            case AMERICA_CHICAGO -> CENTRAL;
            case AMERICA_NEW_YORK -> EASTERN;
            default -> CENTRAL;
        };
    }

    protected Runnable updateOutdatedTime() {
        return () -> shouldUpdateTime(null);
    }

    protected boolean shouldUpdateTime(LocalDateTime now) {
        LocalDateTime nowUpdated = formatCurrentTimeToNonMilitaryTime(now);
        nowUpdated = nowUpdated.minusNanos(nowUpdated.getNano());
        LocalDateTime clockTime = getCurrentTime();
        //logger.debug("current timezone: {}", timezone.getDisplayName(TextStyle.FULL, Locale.ENGLISH)); Central Time
        logger.debug("current time: {}", nowUpdated);
        logger.debug("clock time:   {}", clockTime);
        boolean timesAreTheSame = nowUpdated.equals(clockTime);
        logger.debug("{}", timesAreTheSame ? "times are the same" : "times are not the same");
        if (!timesAreTheSame) {
            logger.warn("clock time is incorrect. updating time");
            setTheTime(LocalDateTime.now());
            getDigitalClockPanel().updateLabels();
            return true;
        }
        return false;
    }

    /**
     * Creates a zoned datetime object and then subtracts 12
     * hours from the result if we are in PM, we want standard
     * time display versus military time, and the hour is greater
     * than 12.
     * @return LocalDateTime the current time in non-military time
     */
    protected LocalDateTime formatCurrentTimeToNonMilitaryTime(LocalDateTime now) {
        ZonedDateTime zonedDateTime = now == null ? ZonedDateTime.now(timezone) : ZonedDateTime.of(now, timezone);
        DateTimeFormatter ampmFormatter = DateTimeFormatter.ofPattern("a");
        String ampm = zonedDateTime.format(ampmFormatter);
        if (PM.equals(ampm) && !showMilitaryTime && zonedDateTime.getHour() > 12) {
            zonedDateTime = zonedDateTime.minusHours(12);
        } //else if (AM.equals(ampm) && zonedDateTime.getHour() == 12) {}
        return zonedDateTime.toLocalDateTime();
    }

    void updateHourValueAndHourString() {
        if (AM.equals(ampm) && showMilitaryTime) { // Daytime and we show Military Time
            if (hours > 12) setHours(0);
            else setHours(hours);
        }
        else if (AM.equals(ampm)) { // DayTime and we do not show Military Time
            if (hours == 0) setHours(12);
            else setHours(hours);
        }
        else if (PM.equals(ampm) && showMilitaryTime) { // NightTime and we show Military v2.Time
            if (hours == 24) setHours(0);
            else if (hours < 12 && getHours() >= 0) setHours(hours+12);
            else setHours(getHours());
        }
        else if (PM.equals(ampm)) { // NightTime and we do not show Military Time
            if (hours > 12) setHours(hours - 12);
        }
    }
    /**
     * Beginning date is always the second Sunday
     * Ending date is always the first Sunday
     */
    void setDaylightSavingsTimeDates() {
        logger.info("setting begin and end daylight savings dates");
        int sundayCount = 0;
        int firstOfMonth = 1;
        LocalDate beginDate = LocalDate.of(getYear(), 3, firstOfMonth);
        while (sundayCount != 2) {
            DayOfWeek day = beginDate.getDayOfWeek();
            if (day == SUNDAY) {
                sundayCount++;
                if (sundayCount == 2) firstOfMonth -= 1;
            }
            beginDate = LocalDate.of(getYear(), 3, firstOfMonth++);
        }
        setBeginDaylightSavingsTimeDate(beginDate);
        firstOfMonth = 1;
        sundayCount = 0;
        LocalDate endDate = LocalDate.of(getYear(), 11, firstOfMonth);
        while (sundayCount != 1) {
            DayOfWeek day = endDate.getDayOfWeek();
            if (day == SUNDAY) {
                sundayCount++;
                if (sundayCount == 1) firstOfMonth -= 1;
            }
            endDate = LocalDate.of(year, 11, firstOfMonth++);
        }
        setEndDaylightSavingsTimeDate(endDate);
        logger.info("begin date: {}", beginDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        logger.info("end date: {}", endDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        logger.info("daylight savings dates set");
    }

    public boolean isTodayDaylightSavingsTime() {
        if (date.isEqual(getBeginDaylightSavingsTimeDate())) {
            isDaylightSavingsTime = true;
            return true;
        }
        else if (date.isEqual(getEndDaylightSavingsTimeDate())) {
            setDaylightSavingsTime(true);
            return isDaylightSavingsTime();
        }
        else {
            setDaylightSavingsTime(false);
            return isDaylightSavingsTime;
        }
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
    void performTick(Integer seconds, Integer minutes, Integer hours) throws InvalidInputException {
        logger.info("perform tick...");
        setSeconds(getSeconds()+seconds);
        if (this.seconds >= 60) {
            logger.info("updating minute");
            setSeconds(getSeconds()-60);
            setMinutes(getMinutes()+minutes);
            if (this.minutes >= 60) {
                logger.info("updating hour");
                setMinutes(getMinutes()-60);
                setHours(getHours()+hours); // 1
                logger.info("time: " + getTimeAsStr());
                if (getHours() >= 12 && getMinutes() == 0 && getSeconds() == 0 && !showMilitaryTime) {
                    setHours(12);
                    if (PM.equals(ampm)) {
                        logger.debug("changing to AM");
                        setAMPM(AM);
                        setIsDateChanged(true);
                    }
                    else {
                        logger.debug("changing to PM");
                        setIsDateChanged(false);
                        setAMPM(PM);
                    }
                }
                else if (getHours() >= 13 && !isShowMilitaryTime()) {
                    setHours(1);
                    setHoursAsStr("01");
                    setIsDateChanged(false);
                }
                else if (getHours() >= 24 && getMinutes() == 0 && getSeconds() == 0 && isShowMilitaryTime()) {
                    setHours(0);
                    setHoursAsStr("00");
                    setAMPM(AM);
                    setIsDateChanged(true);
                }
                else if (getHours() >= 13 && isShowMilitaryTime()) {
                    setHoursAsStr(Integer.toString(getHours()));
                    setIsDateChanged(false);
                }
                else { setHours(getHours()); }
            }
        }
        else { setIsDateChanged(false); }
        updateHourValueAndHourString();
        if (!isShowMilitaryTime()) { logger.info(getHoursAsStr()+COLON+getMinutesAsStr()+COLON+getSecondsAsStr()); }
        else { logger.info(getMilitaryTimeAsStr()); }

        if (isDateChanged) {
            logger.info("date has changed");
            setDayOfMonth(getDayOfMonth()+1);
            setDaylightSavingsTime(isTodayDaylightSavingsTime());
            switch(dayOfWeek) {
                case SUNDAY: setDayOfWeek(MONDAY); break;
                case MONDAY: setDayOfWeek(TUESDAY); break;
                case TUESDAY: setDayOfWeek(WEDNESDAY); break;
                case WEDNESDAY: setDayOfWeek(THURSDAY); break;
                case THURSDAY: setDayOfWeek(FRIDAY); break;
                case FRIDAY: setDayOfWeek(SATURDAY); break;
                case SATURDAY: setDayOfWeek(SUNDAY); break;
                default: throw new InvalidInputException("Unknown DayOfWeek: " + getDayOfWeek());
            }
            switch (month) {
                case JANUARY: {
                    if (getDayOfMonth() == 31) {
                        setDayOfMonth(1);
                        setMonth(FEBRUARY);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case FEBRUARY: {
                    if (isLeapYear() && getDayOfMonth() == 29) {
                        setDayOfMonth(29);
                        setMonth(FEBRUARY);
                        logger.info("month: " + getMonth());
                    } else if (isLeapYear() && getDayOfMonth() == 30) {
                        setDayOfMonth(1);
                        setMonth(MARCH);
                        logger.info("month: " + getMonth());
                    } else if (!isLeapYear() && getDayOfMonth() == 29) {
                        setDayOfMonth(1);
                        setMonth(MARCH);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case MARCH: {
                    if (getDayOfMonth() == 32) {
                        setDayOfMonth(1);
                        setMonth(APRIL);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case APRIL: {
                    if (getDayOfMonth() == 31) {
                        setDayOfMonth(1);
                        setMonth(MAY);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case MAY: {
                    if (getDayOfMonth() == 32) {
                        setDayOfMonth(1);
                        setMonth(JUNE);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case JUNE: {
                    if (getDayOfMonth() == 31) {
                        setDayOfMonth(1);
                        setMonth(JULY);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case JULY: {
                    if (getDayOfMonth() == 32) {
                        setDayOfMonth(1);
                        setMonth(AUGUST);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case AUGUST: {
                    if (getDayOfMonth() == 32) {
                        setDayOfMonth(1);
                        setMonth(SEPTEMBER);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case SEPTEMBER: {
                    if (getDayOfMonth() == 31) {
                        setDayOfMonth(1);
                        setMonth(OCTOBER);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case OCTOBER: {
                    if (getDayOfMonth() == 32) {
                        setDayOfMonth(1);
                        setMonth(NOVEMBER);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case NOVEMBER: {
                    if (getDayOfMonth() == 31) {
                        setDayOfMonth(1);
                        setMonth(DECEMBER);
                        logger.info("month: " + getMonth());
                    }
                    break;
                }
                case DECEMBER: {
                    if (getDayOfMonth() == 32) {
                        setDayOfMonth(1);
                        setMonth(JANUARY);
                        setYear(getYear() + 1);
                        setLeapYear(getDate().isLeapYear());
                        setIsNewYear(true);
                        setDaylightSavingsTimeDates(); // reset the daylight savings dates for new year
                        logger.info("date: " + getDateAsStr());
                        logger.info("new year!");
                    }
                    break;
                }
                default: throw new InvalidInputException("Unknown Month: " + getMonth());
            }
            setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
            if (isTodayDaylightSavingsTime()) { setDaylightSavingsTime(true); }
        }
        if (daylightSavingsTimeEnabled && isDaylightSavingsTime && getHours() == 2) {
            logger.info("!! daylight savings time now !!");
            if (month == MARCH && AM.equals(ampm)) {
                logger.info("spring forward");
                setHours(3);
                setDaylightSavingsTime(false);
                logger.info("hour set to {}", getHours());
            }
            else if (month == NOVEMBER && AM.equals(ampm)) {
                logger.info("fall back");
                setHours(1);
                setDaylightSavingsTime(false);
                logger.info("hour set to {}", getHours());
            }
            logger.info("setting isDaylightSavingsTime to {}", isDaylightSavingsTime);
        } else {
            if (!daylightSavingsTimeEnabled) {
                logger.info("daylight savings time not enabled");
                logger.info("not adjusting time");
            } else {
                logger.info("!! not daylight savings time !!");
            }
        }
    } // performTick

    public String defaultText(int labelVersion) {
        String defaultText = EMPTY;
        if (labelVersion == 1) {
            if (isShowFullDate() && !isShowPartialDate()) defaultText = getFullDateAsStr();
            else if (isShowPartialDate() && !isShowFullDate()) defaultText = getPartialDateAsStr();
            else defaultText = getDateAsStr();
        }
        else if (labelVersion == 2) {
            if (!showMilitaryTime) {
                if (PM.equals(ampm) && getHours() > 12) { setHours(getHours()-12); }
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
        else if (labelVersion == 8) { defaultText = getAlarmPanel().getCurrentAlarmGoingOff().getAlarmAsString(); }
        else if (labelVersion == 9) { defaultText = is_going_off; }
        return defaultText;
    }

    void setupMenuBar() {
        logger.info("setupMenuBar");
        UIManager.put("MenuItem.background", Color.BLACK);
        setClockMenuBar(new ClockMenuBar(this));
        setJMenuBar(getClockMenuBar());
    }

    void changeToDigitalClockPanel() {
        logger.info("change to digital clock");
        add(digitalClockPanel);
        setSize(Clock.defaultSize);
        digitalClockPanel.updateLabels();
    }

    void changeToAnalogueClockPanel() {
        logger.info("change to analogue clock");
        add(analogueClockPanel);
        analogueClockPanel.setupDefaultActions(this);
        setSize(analogueClockPanel.getMaximumSize());
        setBackground(Color.BLACK);
    }

    void changeToAlarmPanel(boolean resetValues) {
        logger.info("change to alarm panel. reset values: {}", resetValues);
        add(alarmPanel);
        if (resetValues) {
            alarmPanel.getJTextField1().setText(EMPTY);
            alarmPanel.getJTextField2().setText(EMPTY);
            alarmPanel.getJTextField3().setText(EMPTY);
            alarmPanel.resetJCheckBoxes();
            alarmPanel.resetJTextArea(); // so error alarms don't show up after navigating out and back in
            alarmPanel.getJAlarmLbl4().setText("Current Alarms");
        }
        setSize(Clock.defaultSize);
    }

    void changeToTimerPanel() {
        logger.info("change to timer panel");
        add(timerPanel);
        setSize(Clock.defaultSize);
    }

    void changePanels(PanelType panelType) {
        logger.info("change panels");
        removePanel();
        updatePanel(panelType);
        this.repaint();
        this.setVisible(true);
    }

    /**
     * The main purpose of the clock
     */
    void tick() { tick(1,1,1); }// default

    /**
     * The purpose of tick is to start the clock, but it should increase
     * the clocks time given the values of seconds, minutes, and seconds
     * with each tick. Then updates the clock labels to display the changes
     * in time or date.
     *
     * @param seconds, the amount of seconds to tick forward or backwards with each tick
     * @param minutes, the amount of minutes to tick forward of backwards with each tick
     * @param hours,   the amount of hours   to tick forward or backwards with each tick
     */
    void tick(int seconds, int minutes, int hours) {
        logger.info("tick; sec: {} min: {} hrs: {}", seconds, minutes, hours);
        try {
            performTick(seconds, minutes, hours);
            getDigitalClockPanel().updateLabels();
            logger.debug("date: {} {}", getDateAsStr(), getDayOfWeek() );
            logger.debug("time: " + (!isShowMilitaryTime() ? getTimeAsStr() : getMilitaryTimeAsStr()));
            //Updates the clock daily to keep time current. may no longer be req'd now that we have our timeUpdater
            if ((MIDNIGHT_STANDARD_TIME.equals(getTimeAsStr()) ||
                MIDNIGHT_MILITARY_TIME.equals(getMilitaryTimeAsStr())) && !testingClock) {
                logger.info("midnight daily clock update");
                setSeconds(0);
                setMinutes(0);
                if (!showMilitaryTime) setHours(12);
                else setHours(0);
            }
            setCurrentTime();
        }
        catch (Exception e)
        { logger.error("Error! Clock had an exception when performing tick: " + e.getMessage()); }
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path the path of the image
     */
    public ImageIcon createImageIcon(String path) {
        logger.info("createImageIcon");
        ImageIcon retImageIcon = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path.substring(19));
        if (resource != null) { retImageIcon = new ImageIcon(resource); }
        else {
            resource = classLoader.getResource(path.substring(19));
            if (resource != null) { retImageIcon = new ImageIcon(resource); }
            else { logger.error("The path '" + path + "' you provided cannot find a resource. Returning null"); }
        }
        return retImageIcon;
    }

    public void clearSettingsMenu() {
        menuBar.getSettingsMenu().removeAll(); // easier
    }
}