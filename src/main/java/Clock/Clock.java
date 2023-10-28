package Clock;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.ArrayList;

import static java.time.Month.*;
import static java.time.DayOfWeek.*;
import static Clock.ClockConstants.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The clock object to set a time and date. The time
 * can be view in military time or not, and the date fully expressed,
 * partially expressed or standard expression.
 * 
 * @author Michael Ball 
 * @version 2.5
 */
public class Clock extends JFrame
{
    private static final Logger logger = LogManager.getLogger(Clock.class);
    private static final long serialVersionUID = 1L;
    protected static final Dimension defaultSize = new Dimension(700, 300);
    protected static final Dimension panelSize = new Dimension(400, 300);
    protected static final Font font60 = new Font("Courier New", Font.BOLD, 60);
    protected static final Font font50 = new Font("Courier New", Font.BOLD, 50);
    protected static final Font font40 = new Font("Courier New", Font.BOLD, 40);
    protected static final Font font20 = new Font("Courier New", Font.BOLD, 20);
    protected static final Font font10 = new Font("Courier New", Font.BOLD, 10);

    protected ClockFace facePanel; // used to determine panel in use
    protected ClockMenuBar menuBar;
    protected ClockPanel clockPanel;
    protected AlarmPanel alarmPanel;
    protected TimerPanel timerPanel;
    protected LocalDate beginDaylightSavingsTimeDate;
    protected LocalDate endDaylightSavingsTimeDate;
    protected LocalDate date;
    protected int seconds;
    protected int minutes;
    protected int hours;
    protected Time ampm;
    protected DayOfWeek dayOfWeek;
    protected int dayOfMonth;
    protected Month month;
    protected int year;
    protected String hoursAsStr = "";
    protected String minutesAsStr = "";
    protected String secondsAsStr = "";
    protected boolean leapYear;
    protected boolean isDaylightSavingsTime;
    protected boolean isDateChanged;
    protected boolean isNewYear;
    protected boolean alarm = false;
    protected boolean timer = false;
    protected boolean updateAlarm = false;
    protected boolean showFullDate = false;
    protected boolean showPartialDate = false;
    protected boolean showMilitaryTime = false;
    protected ArrayList<Alarm> listOfAlarms;

    public ClockFace getPanelInUse() { return this.facePanel; }
    public ClockMenuBar getClockMenuBar() { return this.menuBar; }
    public ClockPanel getClockPanel() { return this.clockPanel; }
    public AlarmPanel getAlarmPanel() { return this.alarmPanel; }
    public TimerPanel getTimerPanel() { return this.timerPanel; }
    public LocalDate getDate() { return this.date; }
    public LocalDate getBeginDaylightSavingsTimeDate() { return this.beginDaylightSavingsTimeDate; }
    public LocalDate getEndDaylightSavingsTimeDate() { return this.endDaylightSavingsTimeDate; }
    public int getSeconds() { return seconds; }
    public int getMinutes() { return minutes; }
    public int getHours() { return hours; }
    public Time getAMPM() { return ampm; }
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
    public String getTimeAsStr() { return getHoursAsStr()+":"+getMinutesAsStr()+":"+getSecondsAsStr()+" "+getAMPM(); }
    public String getTimeAsStrAlarmRepresentation() { return getHoursAsStr()+":"+getMinutesAsStr()+" "+getAMPM(); }
    public String getDateAsStr() { return this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getFullDateAsStr() { return this.dayOfWeek+" "+this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getMilitaryTimeAsStr() {
        return this.hoursAsStr + this.minutesAsStr + " hours " + this.secondsAsStr;
    }
    public String getPartialDateAsStr() { return this.dayOfWeek.toString().substring(0,3)+" "+this.month.toString().substring(0,3)+" "+this.dayOfMonth +", "+this.year; }
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
    public ArrayList<Alarm> getListOfAlarms() { return this.listOfAlarms; }

    protected void setPanelInUse(ClockFace facePanel) { this.facePanel = facePanel; }
    protected void setClockMenuBar(ClockMenuBar menuBar) { this.menuBar = menuBar; }
    protected void setClockPanel(ClockPanel clockPanel) { this.clockPanel = clockPanel; }
    protected void setAlarmPanel(AlarmPanel alarmPanel) { this.alarmPanel = alarmPanel; }
    protected void setTimerPanel(TimerPanel timerPanel) { this.timerPanel = timerPanel; }
    protected void setBeginDaylightSavingsTimeDate(LocalDate beginDaylightSavingsTimeDate) { this.beginDaylightSavingsTimeDate = beginDaylightSavingsTimeDate; }
    protected void setEndDaylightSavingsTimeDate(LocalDate endDaylightSavingsTimeDate) { this.endDaylightSavingsTimeDate = endDaylightSavingsTimeDate; }
    protected void setDate(LocalDate theDate) { this.date = theDate; }
    protected void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) setSecondsAsStr("0"+this.seconds);
        else setSecondsAsStr(Integer.toString(this.seconds));
    }
    protected void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) setMinutesAsStr("0"+this.minutes);
        else setMinutesAsStr(Integer.toString(this.minutes));
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
    }
    protected void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    protected void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; }
    protected void setMonth(Month month) { this.month = month; }
    protected void setYear(int year) { this.year = year; }
    protected void setHoursAsStr(String hoursAsStr) { this.hoursAsStr = hoursAsStr; }
    protected void setMinutesAsStr(String minutesAsStr) { this.minutesAsStr = minutesAsStr; }
    protected void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; }
    protected void setListOfAlarms(ArrayList<Alarm> listOfAlarms) { this.listOfAlarms = listOfAlarms; }
    protected void setUpdateAlarm(boolean updateAlarm) { this.updateAlarm = updateAlarm;}
    protected void setLeapYear(boolean leapYear) { this.leapYear = leapYear; }
    protected void setDaylightSavingsTime(boolean daylightSavingsTime) { this.isDaylightSavingsTime = daylightSavingsTime; }
    protected void setIsDateChanged(boolean isDateChanged) { this.isDateChanged = isDateChanged; }
    protected void setIsNewYear(boolean isNewYear) { this.isNewYear = isNewYear; }
    protected void setIsAlarmGoingOff(boolean alarm) { this.alarm = alarm; }
    protected void setIsTimerGoingOff(boolean timer) { this.timer = timer; }
    protected void setShowFullDate(boolean showFullDate) { this.showFullDate = showFullDate; }
    protected void setShowPartialDate(boolean showPartialDate) { this.showPartialDate = showPartialDate; }
    protected void setShowMilitaryTime(boolean showMilitaryTime) { this.showMilitaryTime = showMilitaryTime; }

    /**
     * This constructor is the main constructor.
     * It creates a clock based on current values of the system.
     */
    public Clock() throws InvalidInputException
    {
        super();
        setResizable(true);
        setListOfAlarms(new ArrayList<>());
        setupMenuBar();
        setShowMilitaryTime(false);
        setTheTime();
        setDaylightSavingsTimeDates();
        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
        setPanelInUse(ClockFace.CLOCKPANEL);
        setClockPanel(new ClockPanel(this));
        setAlarmPanel(new AlarmPanel(this));
        setTimerPanel(new TimerPanel(this));
        setLeapYear(getDate().isLeapYear());
        setIsDateChanged(false);
        setIsAlarmGoingOff(false);
        add(getClockPanel());
        pack();
    }
    /**
     * This constructor takes in values for all Clock parameters
     * and sets them based on those inputs.
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
    public Clock(int hours, int minutes, int seconds, Month month, DayOfWeek dayOfWeek, int dayOfMonth, int year, Time ampm) throws InvalidInputException
    {
        super();
        setResizable(true);
        setListOfAlarms(new ArrayList<>());
        setupMenuBar();
        setShowMilitaryTime(false);
        setAlarmPanel(new AlarmPanel(this));
        setTimerPanel(new TimerPanel(this));
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
        setPanelInUse(ClockFace.CLOCKPANEL);
        setClockPanel(new ClockPanel(this));
        setLeapYear(getDate().isLeapYear());
        setIsDateChanged(false);
        setIsAlarmGoingOff(false);
        pack();
        add(getClockPanel());
    }
    /**
     * This clock creates a new clock based on another
     * Clocks values. DBL CHECK: used for creating Alarms
     * @param clock the Clock
     */
    public Clock(Clock clock)
    {
        super();
        setResizable(true);
        setListOfAlarms(clock.getListOfAlarms());
        setMenuBar(clock.getMenuBar());
        setAlarmPanel(new AlarmPanel(this));
        setSeconds(clock.getSeconds());
        setMinutes(clock.getMinutes());
        setHours(clock.getHours());
        setAMPM(clock.getAMPM());
        setMonth(clock.getMonth());
        setDayOfMonth(clock.getDayOfMonth());
        setYear(clock.getYear());
        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
        setDaylightSavingsTimeDates();
        setPanelInUse(ClockFace.CLOCKPANEL);
        setClockPanel(new ClockPanel(this));
        pack();
        add(getClockPanel());
    }

    public void setTheTime() {
        LocalDateTime dateTime = LocalDateTime.now(); //2022-09-29T22:08:23.701434
        setSeconds(dateTime.getSecond()); // sets secsAsStr
        setMinutes(dateTime.getMinute()); // sets minutesAsStr
        if (dateTime.getHour() > 12 && !isShowMilitaryTime()) { setHours(dateTime.getHour()-12);}
        else { setHours(dateTime.getHour()); } // sets hoursAsStr
        setMonth(dateTime.getMonth());
        setDayOfWeek(dateTime.getDayOfWeek());
        setDayOfMonth(dateTime.getDayOfMonth());
        setYear(dateTime.getYear());
        setAMPM(LocalTime.from(dateTime));
    }
    public void updateHourValueAndHourString()
    {
        if (getAMPM() == Time.AM && isShowMilitaryTime()) // Daytime and we show Military Time
        {
            if (getHours() > 12) setHours(0);
            else setHours(getHours());
        }
        else if (getAMPM() == Time.AM) // DayTime and we do not show Military Time
        {
            if (getHours() == 0) setHours(12);
            else setHours(getHours());
        }
        else if (getAMPM() == Time.PM && isShowMilitaryTime()) // NightTime and we show Military v2.Time
        {
            if (getHours() == 24) setHours(0);
            else if (getHours() < 12 && getHours() >= 0) setHours(getHours() + 12);
            else setHours(getHours());
        }
        else if (getAMPM() == Time.PM) // NightTime and we do not show Military Time
        {
            if (getHours() > 12) setHours(getHours() - 12);
        }
    }
    /**
     * Beginning date is always the second Sunday
     * Ending date is always the first Sunday
     */
    public void setDaylightSavingsTimeDates()
    {
        int sundayCount = 0;
        int firstOfMonth = 1;
        LocalDate beginDate = LocalDate.of(getYear(), 3, firstOfMonth);
        while (sundayCount != 2)
        {
            DayOfWeek day = beginDate.getDayOfWeek();
            if (day == SUNDAY)
            {
                sundayCount++;
                if (sundayCount == 2) firstOfMonth -= 1;
            }
            beginDate = LocalDate.of(year, 3, firstOfMonth++);
        }
        setBeginDaylightSavingsTimeDate(beginDate);
        firstOfMonth = 1;
        sundayCount = 0;
        LocalDate endDate = LocalDate.of(getYear(), 11, firstOfMonth);
        while (sundayCount != 1)
        {
            DayOfWeek day = endDate.getDayOfWeek();
            if (day == SUNDAY)
            {
                sundayCount++;
                if (sundayCount == 1) firstOfMonth -= 1;
            }
            endDate = LocalDate.of(year, 11, firstOfMonth++);
        }
        setEndDaylightSavingsTimeDate(endDate);
    }
    public boolean isTodayDaylightSavingsTime()
    {

        if (getDate().isEqual(getBeginDaylightSavingsTimeDate()))
        {
            setDaylightSavingsTime(true);
            return isDaylightSavingsTime();
        }
        else if (getDate().isEqual(getEndDaylightSavingsTimeDate()) && !isDaylightSavingsTime())
        {
            setDaylightSavingsTime(true);
            return isDaylightSavingsTime();
        }

        setDaylightSavingsTime(false);
        return isDaylightSavingsTime();
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
    public void performTick(int seconds, int minutes, int hours) throws InvalidInputException
    {
        logger.info("performTick...");
        setSeconds(getSeconds()+seconds);
        if (getSeconds() == 60)
        {
            logger.info("updating minute");
            setSeconds(0);
            setMinutes(getMinutes()+1);
            if (getMinutes() == 60)
            {
                logger.info("upddating hour");
                setMinutes(0);
                setHours(getHours()+1);
                if (getHours() == 12 && getMinutes() == 0 && getSeconds() == 0 && !isShowMilitaryTime())
                {
                    setHours(12);
                    setHoursAsStr("12");
                    if (getAMPM() == Time.PM)
                    {
                        logger.info("am");
                        setAMPM(Time.AM);
                        setIsDateChanged(true);
                    }
                    else
                    {
                        logger.info("pm");
                        setIsDateChanged(false);
                        setAMPM(Time.PM);
                    }
                }
                else if (getHours() == 13 && !isShowMilitaryTime())
                {
                    setHours(1);
                    setHoursAsStr("01");
                    setIsDateChanged(false);
                }
                else if (getHours() == 24 && getMinutes() == 0 && getSeconds() == 0 && isShowMilitaryTime())
                {
                    setHours(0);
                    setHoursAsStr("00");
                    setAMPM(Time.AM);
                    setIsDateChanged(true);
                }
                else if (getHours() >= 13 && isShowMilitaryTime())
                {
                    setHoursAsStr(Integer.toString(getHours()));
                    setIsDateChanged(false);
                }
                else
                {
                    setHours(getHours());
                }
            }
        }
        else
        {
            setIsDateChanged(false);
        }
        updateHourValueAndHourString();
        if (!isShowMilitaryTime()) {
            logger.info(getHoursAsStr()+":"+getMinutesAsStr()+":"+getSecondsAsStr());
        } else {
            logger.info(getMilitaryTimeAsStr());
        }

        if (isDateChanged())
        {
            logger.info("date has changed");
            setDayOfMonth(getDayOfMonth()+1);
            setDaylightSavingsTime(isTodayDaylightSavingsTime());
            switch(getDayOfWeek())
            {
                case SUNDAY: setDayOfWeek(MONDAY); break;
                case MONDAY: setDayOfWeek(TUESDAY); break;
                case TUESDAY: setDayOfWeek(WEDNESDAY); break;
                case WEDNESDAY: setDayOfWeek(THURSDAY); break;
                case THURSDAY: setDayOfWeek(FRIDAY); break;
                case FRIDAY: setDayOfWeek(SATURDAY); break;
                case SATURDAY: setDayOfWeek(SUNDAY); break;
                default: throw new InvalidInputException("Unknown DayOfWeek: " + getDayOfWeek());
            }
        }
        switch (getMonth()) {
            case JANUARY: {
                if (getDayOfMonth() == 31 && isDateChanged()) {
                    setDayOfMonth(1);
                    setMonth(FEBRUARY);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case FEBRUARY: {
                if ((getDayOfMonth() == 28 || getDayOfMonth() == 30) && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(MARCH);
                    logger.info("month: " + getMonth());
                }
                else if (getDayOfMonth() == 28 && isLeapYear() && isDateChanged())
                {
                    setDayOfMonth(29);
                    setMonth(FEBRUARY);
                }
                break;
            }
            case MARCH: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(APRIL);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case APRIL: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(MAY);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case MAY: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(JUNE);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case JUNE: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(JULY);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case JULY: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(AUGUST);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case AUGUST: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(SEPTEMBER);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case SEPTEMBER: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(OCTOBER);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case OCTOBER: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(NOVEMBER);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case NOVEMBER: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(DECEMBER);
                    logger.info("month: " + getMonth());
                }
                break;
            }
            case DECEMBER: {
                if (getDayOfMonth() == 32 && isDateChanged()) {
                    setDayOfMonth(1);
                    setMonth(JANUARY);
                    setYear(getYear()+1);
                    setIsNewYear(true);
                    logger.info("month: " + getMonth());
                    logger.info("new year!");
                }
                break;
            }
            default : {}
        }

        if (isDaylightSavingsTime())
        {
            logger.info("!! daylight savings !!");
            if (getMonth() == MARCH && getAMPM() == Time.AM)
            {
                logger.info("spring forward");
                setHours(3);
                setDaylightSavingsTime(false);
            }
            else if (getMonth() == NOVEMBER && getAMPM() == Time.AM)
            { // && daylightSavingsTime
                logger.info("fall back");
                setHours(1);
                setDaylightSavingsTime(false);
            }
        }
    } // performTick
    public String defaultText(int labelVersion)
    {
        String defaultText = "";
        if (labelVersion == 1)
        {
            if (isShowFullDate() && !isShowPartialDate()) defaultText = getFullDateAsStr();
            else if (isShowPartialDate() && !isShowFullDate()) defaultText = getPartialDateAsStr();
            else defaultText = getDateAsStr();
        }
        else if (labelVersion == 2)
        {
            if (!isShowMilitaryTime()) {
                if (getAMPM() == Time.PM && getHours() > 12) { setHours(getHours()-12); }
                defaultText = getTimeAsStr();
                // change alarms to reflect normal time
            }
            else if (isShowMilitaryTime()) {
                defaultText = getMilitaryTimeAsStr();
                // change alarms to reflect military time
                //setShowMilitaryTime(false);
            }
        }
        else if (labelVersion == 3)
        {
            defaultText = "Hours";
        }
        else if (labelVersion == 4)
        {
            defaultText = "Minutes";
        }
        else if (labelVersion == 5)
        {
            defaultText = "AM/PM";
        }
        else if (labelVersion == 6)
        {
            defaultText = "No Alarms";
        }
        else if (labelVersion == 7)
        {
            defaultText = "S";
        }
        return defaultText;
    }
    public void setupMenuBar()
    {
        logger.info("setupMenuBar");
        UIManager.put("MenuItem.background", Color.BLACK);
        setClockMenuBar(new ClockMenuBar(this));
        setJMenuBar(getClockMenuBar());
    }
    public void changeToClockPanel()
    {
        logger.info("changeToClockPanel");
        //logger.error("CurrentFace: " + getFacePanel().toString());
        if (getPanelInUse() == ClockFace.TIMERPANEL)
            remove(getTimerPanel());
        else if (getPanelInUse() == ClockFace.ALARMPANEL)
            remove(getAlarmPanel());
        setPanelInUse(ClockFace.CLOCKPANEL);
        add(getClockPanel());
        this.repaint();
        this.setVisible(true);
        //logger.error("ChangedToFace: " + getFacePanel().toString());
    }
    public void changeToAlarmPanel(boolean resetValues)
    {
        logger.info("changeToAlarmPanel");
        //logger.error("CurrentFace: " + getFacePanel().toString());
        if (getPanelInUse() == ClockFace.CLOCKPANEL)
            remove(getClockPanel());
        else if (getPanelInUse() == ClockFace.TIMERPANEL)
            remove(getTimerPanel());
        if (getAlarmPanel().getMusicPlayer() != null) { getAlarmPanel().setMusicPlayer(null); }
        if (getPanelInUse() != ClockFace.ALARMPANEL)
            add(getAlarmPanel());
        setPanelInUse(ClockFace.ALARMPANEL);
        if (resetValues) {
            getAlarmPanel().getJTextField1().setText("");
            getAlarmPanel().getJTextField2().setText("");
            getAlarmPanel().getJTextField3().setText("");
            getAlarmPanel().resetJCheckboxes();
            getAlarmPanel().resetJTextArea(); // so error alarms don't show up after navigating out and back in
            getAlarmPanel().getJAlarmLbl4().setText("Current Alarms");
        }
        this.repaint();
        this.setVisible(true);
        //logger.error("ChangedToFace: " + getFacePanel().toString());
    }
    public void changeToTimerPanel()
    {
        logger.info("changeToTimerPanel");
        //logger.error("CurrentFace: " + getFacePanel().toString());
        if (getPanelInUse() == ClockFace.CLOCKPANEL)
            remove(getClockPanel());
        else if (getPanelInUse() == ClockFace.ALARMPANEL)
            remove(getAlarmPanel());
        if (getPanelInUse() != ClockFace.TIMERPANEL)
            add(getTimerPanel());
        setPanelInUse(ClockFace.TIMERPANEL);
        this.repaint();
        this.setVisible(true);
        //logger.error("ChangedToFace: " + getFacePanel().toString());
    }
    /**
     * The purpose of tick is to start the clock.
     */
    public void tick()
    {
        tick(1,0,0); // default
    }

    /**
     * The purpose of tick is to start the clock, but it should increase
     * the clocks time given the values of seconds, minutes, and seconds
     * with each tick.
     *
     * @param seconds, the amount of seconds to tick forward or backwards with each tick
     * @param minutes, the amount of minutes to tick forward of backwards with each tick
     * @param hours,   the amount of hours   to tick forward or backwards with each tick
     */
    public void tick(int seconds, int minutes, int hours)
    {
        logger.info("tick...");
        try
        {
            performTick(seconds, minutes, hours);
            //Updates the clock daily to keep time current
            getClockPanel().updateLabels();
            if (getTimeAsStr().equals("12:00:00" + SPACE + Time.AM.getStrValue()) ||
                getMilitaryTimeAsStr().equals("0000 hours 00"))
            {
                logger.info("midnight daily clock update");
                setSeconds(LocalTime.now().getSecond());
                setMinutes(LocalTime.now().getMinute());
                setHours(LocalTime.now().getHour());
            }
        }
        catch (Exception e)
        {
            logger.error("Error! Clock had an exception when performing tick: " + e.getMessage());
        }
    }
    protected void printStackTrace(Exception e)
    {
        logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        {
            logger.error(ste.toString());
        }
    }
}