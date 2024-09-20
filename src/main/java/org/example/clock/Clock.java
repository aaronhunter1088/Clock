package org.example.clock;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.time.Month.*;
import static java.time.DayOfWeek.*;
import static org.example.clock.ClockConstants.*;

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
public class Clock extends JFrame {
    private static final Logger logger = LogManager.getLogger(Clock.class);
    @Serial
    private static final long serialVersionUID = 2L;
    final static Dimension defaultSize = new Dimension(700, 300);
    final static Dimension panelSize = new Dimension(400, 300);
    final static Dimension alarmSize = new Dimension(200,100);
    final static Font font60 = new Font("Courier New", Font.BOLD, 60);
    final static Font font50 = new Font("Courier New", Font.BOLD, 50);
    final static Font font40 = new Font("Courier New", Font.BOLD, 40);
    final static Font font20 = new Font("Courier New", Font.BOLD, 20);
    final static Font font10 = new Font("Courier New", Font.BOLD, 10);

    PanelType currentPanel; // used to determine panel in use
    ClockMenuBar menuBar;
    DigitalClockPanel digitalClockPanel;
    AnalogueClockPanel analogueClockPanel;
    AlarmPanel alarmPanel;
    TimerPanel timerPanel;
    LocalDate beginDaylightSavingsTimeDate;
    LocalDate endDaylightSavingsTimeDate;
    private LocalDateTime currentTime;
    private ZoneId timezone;
    LocalDate date;
    LocalTime time;
    int seconds,minutes,hours,dayOfMonth,year;
    Time ampm;
    DayOfWeek dayOfWeek;
    Month month;
    String hoursAsStr=EMPTY, minutesAsStr=EMPTY, secondsAsStr=EMPTY;
    boolean leapYear,isDaylightSavingsTime,isDateChanged,isNewYear,
            alarm,timer,updateAlarm,showFullDate,showPartialDate,
            showMilitaryTime,showDigitalTimeOnAnalogueClock,testingClock,
            daylightSavingsTimeEnabled=true;
    List<Alarm> listOfAlarms;
    ImageIcon icon;
    private final ScheduledExecutorService timeUpdater;

    public PanelType getPanelType() { return this.currentPanel; }
    public ClockMenuBar getClockMenuBar() { return this.menuBar; }
    public DigitalClockPanel getDigitalClockPanel() { return this.digitalClockPanel; }
    public AnalogueClockPanel getAnalogueClockPanel() { return this.analogueClockPanel; }
    public AlarmPanel getAlarmPanel() { return this.alarmPanel; }
    public TimerPanel getTimerPanel() { return this.timerPanel; }
    public LocalDate getDate() { return this.date; }
    public LocalTime getTime() { return time; }
    public LocalDate getBeginDaylightSavingsTimeDate() { return this.beginDaylightSavingsTimeDate; }
    public LocalDate getEndDaylightSavingsTimeDate() { return this.endDaylightSavingsTimeDate; }
    public int getSeconds() { return seconds; }
    public int getMinutes() { return minutes; }
    public int getHours() { return hours; }
    public Time getAMPM() { return ampm; }
    public ZoneId getTimezone() { return timezone; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public int getDayOfMonth() { return dayOfMonth; }
    public Month getMonth() { return month; }
    public int getYear() { return this.year; }
    public String getHoursAsStr() { return this.hoursAsStr; }
    public String getMinutesAsStr() { return this.minutesAsStr; }
    public String getSecondsAsStr() { return this.secondsAsStr; }
    /**
     * This method returns the clock's current hour, minute, second, and time.
     * It can also be used to get the alarm's time set value
     * @return 'HH:MM:SS TIME' ex: 05:15:24 PM
     */
    public String getTimeAsStr() { return this.hoursAsStr+":"+this.minutesAsStr+":"+this.secondsAsStr+" "+this.ampm; }
    public String getTimeAsStrAlarmRepresentation() { return getHoursAsStr()+":"+getMinutesAsStr()+" "+getAMPM(); }
    public String getDateAsStr() { return this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getFullDateAsStr() { return this.dayOfWeek+" "+this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getMilitaryTimeAsStr() { return hoursAsStr+minutesAsStr+SPACE+Hours.toLowerCase()+SPACE+secondsAsStr; }
    public String getPartialDateAsStr() { return dayOfWeek.toString().substring(0,3)+SPACE+this.month.toString().substring(0,3)+SPACE+this.dayOfMonth +COMMA+SPACE+this.year; }
    public boolean isLeapYear() { return this.leapYear; }
    public boolean isDaylightSavingsTime() { return this.isDaylightSavingsTime; }
    public boolean isDateChanged() { return this.isDateChanged; }
    public boolean isNewYear() { return this.isNewYear; }
    public boolean isAlarmGoingOff() { return this.alarm; }
    public boolean isUpdateAlarm() { return this.updateAlarm; }
    public boolean isTimerGoingOff() { return this.timer; }
    public boolean isShowFullDate() { return this.showFullDate; }
    public boolean isShowPartialDate() { return this.showPartialDate; }
    public boolean isShowMilitaryTime() { return this.showMilitaryTime; }
    public boolean isShowDigitalTimeOnAnalogueClock() { return this.showDigitalTimeOnAnalogueClock; }
    public boolean isTestingClock() { return this.testingClock; }
    public java.util.List<Alarm> getListOfAlarms() { return this.listOfAlarms; }
    public ScheduledExecutorService getTimeUpdater() { return this.timeUpdater; }
    public boolean isDaylightSavingsTimeEnabled() { return daylightSavingsTimeEnabled; }

    protected void setPanelType(PanelType currentPanel) { this.currentPanel = currentPanel; }
    protected void setClockMenuBar(ClockMenuBar menuBar) { this.menuBar = menuBar; }
    protected void setDigitalClockPanel(DigitalClockPanel digitalClockPanel) { this.digitalClockPanel = digitalClockPanel; }
    protected void setAnalogueClockPanel(AnalogueClockPanel analogueClockPanel) { this.analogueClockPanel = analogueClockPanel; }
    protected void setAlarmPanel(AlarmPanel alarmPanel) { this.alarmPanel = alarmPanel; }
    protected void setTimerPanel(TimerPanel timerPanel) { this.timerPanel = timerPanel; }
    protected void setBeginDaylightSavingsTimeDate(LocalDate beginDaylightSavingsTimeDate) { this.beginDaylightSavingsTimeDate = beginDaylightSavingsTimeDate; }
    protected void setEndDaylightSavingsTimeDate(LocalDate endDaylightSavingsTimeDate) { this.endDaylightSavingsTimeDate = endDaylightSavingsTimeDate; }
    protected void setDate(LocalDate theDate) { date = theDate; logger.debug("date: {}", date); }
    protected void setTime(LocalTime theTime) { time = theTime; logger.debug("time: {}", DateTimeFormatter.ofPattern("hh:mm:ss a").format(time)); }
    protected void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) setSecondsAsStr("0"+this.seconds);
        else setSecondsAsStr(Integer.toString(this.seconds));
        logger.debug("seconds: {}", seconds);
    }
    protected void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) setMinutesAsStr("0"+this.minutes);
        else setMinutesAsStr(Integer.toString(this.minutes));
        logger.debug("minutes: {}", minutes);
    }
    protected void setHours(int hours) {
        this.hours = hours;
        if (this.hours < 10) this.hoursAsStr = "0"+hours;
        else this.hoursAsStr = Integer.toString(this.hours);
    }
    protected void setAMPM(Time ampm) { this.ampm = ampm; }
    protected void setAMPM(LocalTime time) {
        if (time.getHour() < 12) this.ampm = Time.AM;
        else this.ampm = Time.PM;
        logger.debug("ampm: {}", this.ampm);
    }
    protected void setTimeZone(ZoneId timezone) { this.timezone = timezone; logger.debug("timezone: {}", timezone.getId()); }
    protected void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    protected void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; logger.debug("dayOfMonth: {}", dayOfMonth); }
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
    void setTestingClock(boolean testingClock) { this.testingClock = testingClock; }
    void setDaylightSavingsTimeEnabled(boolean daylightSavingsTimeEnabled) { this.daylightSavingsTimeEnabled = daylightSavingsTimeEnabled; }

    /**
     * The main constructor.
     * It creates a clock based on
     * the local clock. It determines daylight savings dates,
     * if it is a leap year, and more.
     */
    public Clock() throws InvalidInputException {
        super();
        setBounds(200, 200, 700, 300);
        setListOfAlarms(new ArrayList<>());
        setShowMilitaryTime(false);
        setTheTime(LocalDateTime.now());
        setupMenuBar();
        setDaylightSavingsTimeDates();
        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
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
        setPanel(getDigitalClockPanel());
        setVisible(true);
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(getDigitalClockPanel());
        timeUpdater = Executors.newScheduledThreadPool(1);
        timeUpdater.scheduleAtFixedRate(updateOutdatedTime(this), 1, 1, TimeUnit.SECONDS);
    }

    /**
     * This constructor takes in values for all Clock parameters
     * and sets them based on those inputs. Expects non-military
     * time values.
     * @param hours the hours to set
     * @param minutes the minutes to set
     * @param seconds the seconds to set
     * @param month the month to set
     * @param dayOfWeek the day of the week to set
     * @param dayOfMonth the date of the month to set
     * @param year the year to set
     * @param ampm Am or PM to set
     * @throws InvalidInputException when an InvalidInput has been given
     */
    public Clock(int hours, int minutes, int seconds, Month month, DayOfWeek dayOfWeek, int dayOfMonth, int year, Time ampm) throws InvalidInputException {
        this();
        setSeconds(seconds);
        setMinutes(minutes);
        setHours(hours);
        setMonth(month);
        setDayOfWeek(dayOfWeek);
        setDayOfMonth(dayOfMonth);
        setYear(year);
        setAMPM(ampm);
        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
        setDaylightSavingsTimeDates();
        setDaylightSavingsTime(isTodayDaylightSavingsTime());
        setLeapYear(getDate().isLeapYear());
    }


    public void setImageIcon(ImageIcon icon) { this.icon = icon; }
    void removePanel() {
        logger.debug("removing panel " + getPanelType());
        if (getPanelType() == PanelType.DIGITAL_CLOCK)
            remove(getDigitalClockPanel());
        else if (getPanelType() == PanelType.ANALOGUE_CLOCK) {
            getAnalogueClockPanel().stop();
            remove(getAnalogueClockPanel());
        }
        else if (getPanelType() == PanelType.ALARM)
            remove(getAlarmPanel());
        else
            remove(getTimerPanel());
    }
    public void setPanel(IClockPanel panelFace) {
        if (panelFace instanceof DigitalClockPanel digitalPanel) {
            setDigitalClockPanel(digitalPanel);
            setPanelType(PanelType.DIGITAL_CLOCK);
            add(getDigitalClockPanel());
            digitalClockPanel.setupSettingsMenu();
        }
        else if (panelFace instanceof AnalogueClockPanel analoguePanel) {
            setAnalogueClockPanel(analoguePanel);
            setPanelType(PanelType.ANALOGUE_CLOCK);
            add(getAnalogueClockPanel());
            analogueClockPanel.setupSettingsMenu();
        }
        else if (panelFace instanceof AlarmPanel alarmpanel) {
            setAlarmPanel(alarmpanel);
            setPanelType(PanelType.ALARM);
            add(getAlarmPanel());
            alarmPanel.setupSettingsMenu();
        }
        else if (panelFace instanceof TimerPanel timerpanel) {
            setTimerPanel(timerpanel);
            setPanelType(PanelType.TIMER);
            add(getTimerPanel());
            timerPanel.setupSettingsMenu();
        }
    }
    public void setTheTime(LocalDateTime dateTime) {
        logger.info("Setting the time");
        setSeconds(dateTime.getSecond()); // sets secsAsStr
        setMinutes(dateTime.getMinute()); // sets minutesAsStr
        if (dateTime.getHour() > 12 && !isShowMilitaryTime()) { setHours(dateTime.getHour()-12);}
        else { setHours(dateTime.getHour()); } // sets hoursAsStr
        logger.debug("hours: {}", hours);
        setMonth(dateTime.getMonth());
        setDayOfWeek(dateTime.getDayOfWeek());
        setDayOfMonth(dateTime.getDayOfMonth());
        setYear(dateTime.getYear());
        setAMPM(LocalTime.from(dateTime));
        setTimeZone(getZoneIdFromTimezoneButtonText(""));
        setCurrentTime();
    }
    public void updateTheTime(JMenuItem timezone) {
        logger.info("updateTheTime");
        LocalDateTime ldt = determineNewTimeFromSelectedTimeZone(timezone.getText());
        setTheTime(ldt);
        setTimeZone(getZoneIdFromTimezoneButtonText(timezone.getText()));
        getClockMenuBar().setCurrentTimeZone();
    }
    public void setCurrentTime() {
        date = LocalDate.of(year, month, dayOfMonth);
        time = LocalTime.of(hours, minutes, seconds);
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

    private Runnable updateOutdatedTime(Clock clock) {
        return () -> {
            LocalDateTime currentTime = formatCurrentTimeToNonMilitaryTime();
            LocalDateTime clockTime = clock.getCurrentTime();
            logger.debug("current timezone: {}", clock.getTimezone().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            logger.debug("current time: {}", currentTime);
            logger.debug("clock time: {}", clockTime);
            long secondsBetween = Duration.between(currentTime, clockTime).getSeconds();
            logger.debug("seconds between: {}", secondsBetween);
            if (secondsBetween < -1) {
                logger.warn("ClockTime is not correct. Resetting the time.");
                clock.setTheTime(LocalDateTime.now());
                clock.getDigitalClockPanel().updateLabels();
            }
        };
    }

    /**
     * Creates a zoned datetime object and then subtracts 12
     * hours from the result if we are in PM, we want standard
     * time display versus military time, and the hour is greater
     * than 12.
     * @return LocalDateTime the current time in non-military time
     */
    protected LocalDateTime formatCurrentTimeToNonMilitaryTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(timezone);
        DateTimeFormatter ampmFormatter = DateTimeFormatter.ofPattern("a");
        String ampm = zonedDateTime.format(ampmFormatter);
        if (Time.PM.getStrValue().equals(ampm) && !showMilitaryTime && zonedDateTime.getHour() > 12) {
            zonedDateTime = zonedDateTime.minusHours(12);
        } else if (Time.AM.getStrValue().equals(ampm) && zonedDateTime.getHour() == 12) {}
        return zonedDateTime.toLocalDateTime();
    }

    public void updateHourValueAndHourString() {
        if (getAMPM() == Time.AM && isShowMilitaryTime()) { // Daytime and we show Military Time
            if (getHours() > 12) setHours(0);
            else setHours(getHours());
        }
        else if (getAMPM() == Time.AM) { // DayTime and we do not show Military Time
            if (getHours() == 0) setHours(12);
            else setHours(getHours());
        }
        else if (getAMPM() == Time.PM && isShowMilitaryTime()) { // NightTime and we show Military v2.Time
            if (getHours() == 24) setHours(0);
            else if (getHours() < 12 && getHours() >= 0) setHours(getHours() + 12);
            else setHours(getHours());
        }
        else if (getAMPM() == Time.PM) { // NightTime and we do not show Military Time
            if (getHours() > 12) setHours(getHours() - 12);
        }
    }
    /**
     * Beginning date is always the second Sunday
     * Ending date is always the first Sunday
     */
    public void setDaylightSavingsTimeDates() {
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
            setDaylightSavingsTime(true);
            return isDaylightSavingsTime();
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
     * @param seconds, the amount of time to increase or decrease seconds
     * @param minutes, the amount of time to increase or decrease seconds
     * @param hours,   the amount of time to increase or decrease seconds
     */
    public void performTick(int seconds, int minutes, int hours) throws InvalidInputException {
        logger.info("performTick...");
        setSeconds(getSeconds()+seconds);
        if (getSeconds() == 60) {
            logger.info("updating minute");
            setSeconds(0);
            setMinutes(getMinutes()+1);
            if (getMinutes() == 60) {
                logger.info("updating hour");
                setMinutes(0);
                setHours(getHours()+1);
                logger.info("time: " + getTimeAsStr());
                if (getHours() == 12 && getMinutes() == 0 && getSeconds() == 0 && !isShowMilitaryTime()) {
                    setHours(12);
                    setHoursAsStr("12");
                    if (getAMPM() == Time.PM) {
                        logger.info("changing to AM");
                        setAMPM(Time.AM);
                        setIsDateChanged(true);
                    }
                    else {
                        logger.info("changing to PM");
                        setIsDateChanged(false);
                        setAMPM(Time.PM);
                    }
                }
                else if (getHours() == 13 && !isShowMilitaryTime()) {
                    setHours(1);
                    setHoursAsStr("01");
                    setIsDateChanged(false);
                }
                else if (getHours() == 24 && getMinutes() == 0 && getSeconds() == 0 && isShowMilitaryTime()) {
                    setHours(0);
                    setHoursAsStr("00");
                    setAMPM(Time.AM);
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

        if (isDateChanged()) {
            logger.info("date has changed");
            setDayOfMonth(getDayOfMonth()+1);
            setDaylightSavingsTime(isTodayDaylightSavingsTime());
            switch(getDayOfWeek()) {
                case SUNDAY: setDayOfWeek(MONDAY); break;
                case MONDAY: setDayOfWeek(TUESDAY); break;
                case TUESDAY: setDayOfWeek(WEDNESDAY); break;
                case WEDNESDAY: setDayOfWeek(THURSDAY); break;
                case THURSDAY: setDayOfWeek(FRIDAY); break;
                case FRIDAY: setDayOfWeek(SATURDAY); break;
                case SATURDAY: setDayOfWeek(SUNDAY); break;
                default: throw new InvalidInputException("Unknown DayOfWeek: " + getDayOfWeek());
            }
            switch (getMonth()) {
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
            if (month == MARCH && getAMPM() == Time.AM) {
                logger.info("spring forward");
                setHours(3);
                setDaylightSavingsTime(false);
                logger.info("hour set to {}", getHours());
            }
            else if (month == NOVEMBER && getAMPM() == Time.AM) {
                logger.info("fall back");
                setHours(1);
                setDaylightSavingsTime(false);
                logger.info("hour set to {}", getHours());
            }
            logger.info("setting isDaylightSavingsTime to {}", isDaylightSavingsTime);
        } else {
            logger.info("daylight savings time not enabled");
            logger.info("not adjusting time");
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
                if (getAMPM() == Time.PM && getHours() > 12) { setHours(getHours()-12); }
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

    public void setupMenuBar() {
        logger.info("setupMenuBar");
        UIManager.put("MenuItem.background", Color.BLACK);
        setClockMenuBar(new ClockMenuBar(this));
        setJMenuBar(getClockMenuBar());
    }

    public void changeToDigitalClockPanel() {
        logger.info("changeToDigitalClockPanel");
        removePanel();
        setPanel(getDigitalClockPanel());
        this.setSize(Clock.defaultSize);
        getDigitalClockPanel().updateLabels();
        this.repaint();
    }

    public void changeToAnalogueClockPanel() {
        logger.info("changeToAnalogueClockPanel");
        removePanel();
        setPanel(getAnalogueClockPanel());
        getAnalogueClockPanel().setupDefaultActions(this);
        this.setSize(getAnalogueClockPanel().getMaximumSize());
        this.setBackground(Color.BLACK);
        this.repaint();
        this.setVisible(true);
    }

    public void changeToAlarmPanel(boolean resetValues) {
        logger.info("changeToAlarmPanel");
        removePanel();
        setPanel(getAlarmPanel());
        if (resetValues) {
            getAlarmPanel().getJTextField1().setText("");
            getAlarmPanel().getJTextField2().setText("");
            getAlarmPanel().getJTextField3().setText("");
            getAlarmPanel().resetJCheckBoxes();
            getAlarmPanel().resetJTextArea(); // so error alarms don't show up after navigating out and back in
            getAlarmPanel().getJAlarmLbl4().setText("Current Alarms");
        }
        this.setSize(Clock.defaultSize);
        this.repaint();
        this.setVisible(true);
    }

    public void changeToTimerPanel() {
        logger.info("changeToTimerPanel");
        removePanel();
        setPanel(getTimerPanel());
        this.setSize(Clock.defaultSize);
        this.repaint();
        this.setVisible(true);
    }

    /**
     * The purpose of tick is to start the clock.
     */
    public void tick() { tick(1,0,0); }// default

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
    public void tick(int seconds, int minutes, int hours) {
        logger.info("tick...");
        try {
            performTick(seconds, minutes, hours);
            getDigitalClockPanel().updateLabels();
            logger.debug("date: " + getDateAsStr());
            logger.debug("time: " + (!isShowMilitaryTime() ? getTimeAsStr() : getMilitaryTimeAsStr()));
            //Updates the clock daily to keep time current
            if (((MIDNIGHT_STANDARD_TIME).equals(getTimeAsStr()) ||
                (MIDNIGHT_MILITARY_TIME).equals(getMilitaryTimeAsStr())) && !isTestingClock()) {
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

}