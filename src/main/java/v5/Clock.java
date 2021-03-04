package v5;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import static java.time.Month.*;
import static java.time.DayOfWeek.*;
import static v5.ClockConstants.*;
import static v5.Time.AMPM.AM;
import static v5.Time.AMPM.PM;

@SuppressWarnings({"unused", "ConstantConditions"})
/** A simple application which displays the time and date. The time
 * can be view in military time or not, and the date fully expressed,
 * partially expressed or standard expression.
 * 
 * You can also set Alarms and create a single Timer.
 * 
 * @author Michael Ball 
 * @version 2.5
 */
public class Clock extends JFrame
{
    // Class attributes
    protected static final long serialVersionUID = 1L;
    protected static final Dimension defaultSize = new Dimension(700, 300);
    protected static final Dimension alarmSize = new Dimension(701, 301);

    protected static final DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    protected static final Font font60 = new Font("Courier New", Font.BOLD, 60);
    protected static final Font font50 = new Font("Courier New", Font.BOLD, 50);
    protected static final Font font40 = new Font("Courier New", Font.PLAIN, 40);
    protected static final Font font30 = new Font("Courier New", Font.PLAIN, 30);
    protected static final Font font20 = new Font("Courier New", Font.PLAIN, 20);
    protected static final Font font15 = new Font("Courier New", Font.BOLD, 15);
    protected static final Font font10 = new Font("Courier New", Font.BOLD, 10);
    // Main GUI Components
    // For displaying faces on main display
    protected ClockFace facePanel;
    protected ClockMenuBar menuBar;
    protected ClockPanel clockPanel;
    protected AlarmPanel alarmPanel;
    protected TimerPanel timerPanel;
    protected Date beginDaylightSavingsTimeDate;
    protected Date endDaylightSavingsTimeDate;
    //protected Calendar calendar;
    protected int seconds;
    protected int minutes;
    protected int hours;
    protected Time.AMPM ampm;
    protected DayOfWeek dayOfWeek;
    protected int dayOfMonth;
    protected Month month;
    protected int year;
    protected String hoursAsStr = "", minutesAsStr = "", secondsAsStr = "";
    protected boolean leapYear;
    protected boolean isDaylightSavingsTime;
    protected boolean isDateChanged;
    protected boolean home = true;
    protected boolean alarm = false;
    protected boolean timer = false;
    protected boolean updateAlarm = false;
    protected boolean showFullDate = false;
    protected boolean showPartialDate = false;
    protected boolean showMilitaryTime = false;
    protected ArrayList<Alarm> listOfAlarms;
    // Getters/Issers
    public ClockFace getFacePanel() { return this.facePanel; }
    public ClockMenuBar getClockMenuBar() { return this.menuBar; }
    public ClockPanel getClockPanel() { return this.clockPanel; }
    public AlarmPanel getAlarmPanel() { return this.alarmPanel; }
    public TimerPanel getTimerPanel() { return this.timerPanel; }
    public Date getBeginDaylightSavingsTimeDate() { return this.beginDaylightSavingsTimeDate; }
    public Date getEndDaylightSavingsTimeDate() { return this.endDaylightSavingsTimeDate; }
    public int getSeconds() { return seconds; }
    public int getMinutes() { return minutes; }
    public int getHours() { return hours; }
    public Time.AMPM getAMPM() { return ampm; }
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
    public String getTimeAsStr() { return this.hoursAsStr+":"+this.minutesAsStr+":"+this.secondsAsStr+" "+this.ampm.getStrValue(); }
    public String getDateAsStr() { return this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getFullDateAsStr() { return this.dayOfWeek+" "+this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getMilitaryTimeAsStr() {
        return this.hoursAsStr + this.minutesAsStr + " hours " + this.secondsAsStr;
    }
    public String getPartialDateAsStr() { return this.dayOfWeek.toString().substring(0,3)+" "+this.month.toString().substring(0,3)+" "+this.dayOfMonth +", "+this.year; }
    public boolean isLeapYear() { return this.leapYear; }
    public boolean isDaylightSavingsTime() { return this.isDaylightSavingsTime; }
    public boolean isDateChanged() { return this.isDateChanged; }
    public boolean isHome() { return this.home; }
    public boolean isAlarmGoingOff() { return this.alarm; }
    public boolean isUpdateAlarm() { return this.updateAlarm; }
    public boolean isTimerGoingOff() { return this.timer; }
    public boolean isShowFullDate() { return this.showFullDate; }
    public boolean isShowPartialDate() { return this.showPartialDate; }
    public boolean isShowMilitaryTime() { return this.showMilitaryTime; }
    public ArrayList<Alarm> getListOfAlarms() { return this.listOfAlarms; }
    // Setters
    protected void setFacePanel(ClockFace facePanel) { this.facePanel = facePanel; }
    protected void setClockMenuBar(ClockMenuBar menuBar) { this.menuBar = menuBar; }
    protected void setClockPanel(ClockPanel clockPanel) { this.clockPanel = clockPanel; }
    protected void setAlarmPanel(AlarmPanel alarmPanel) { this.alarmPanel = alarmPanel; }
    protected void setTimerPanel(TimerPanel timerPanel) { this.timerPanel = timerPanel; }
    protected void setBeginDaylightSavingsTimeDate(Date beginDaylightSavingsTimeDate) { this.beginDaylightSavingsTimeDate = beginDaylightSavingsTimeDate; }
    protected void setEndDaylightSavingsTimeDate(Date endDaylightSavingsTimeDate) { this.endDaylightSavingsTimeDate = endDaylightSavingsTimeDate; }
    protected void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) this.secondsAsStr = "0"+this.seconds;
        else this.secondsAsStr = Integer.toString(this.seconds);
    }
    protected void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) this.minutesAsStr = "0"+this.minutes;
        else this.minutesAsStr = Integer.toString(this.minutes);
    }
    protected void setHours(int hours) {
        if (hours == 0 && !isShowMilitaryTime()) {
            this.hours = 12;
        }
        else if (hours == 0 && isShowMilitaryTime()) {
            this.hours = 0;
        }
        else
            this.hours = hours;
        if (this.hours <= 9) this.hoursAsStr = "0"+hours;
        else this.hoursAsStr = Integer.toString(this.hours);
    }
    protected void setHours(int hours, boolean hardSetHour) {
        if (hardSetHour)
        {
            this.hours = hours;
        }
        else
        {
            if (hours == 0 && !isShowMilitaryTime()) {
                this.hours = 12;
            }
            else if (hours == 0 && isShowMilitaryTime()) {
                this.hours = 0;
            }
            else
                this.hours = hours;
        }
        if (this.hours <= 9) this.hoursAsStr = "0"+hours;
        else this.hoursAsStr = Integer.toString(this.hours);
    }
    protected void setAMPM(Time.AMPM ampm) { this.ampm = ampm; }
    protected void setAMPM(LocalTime time) {
        if (time.getHour() < 12) this.ampm = AM;
        else this.ampm = PM;
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
    protected void setDateChanged(boolean dateChanged) { this.isDateChanged = dateChanged; }
    protected void setHome(boolean home) { this.home = home; }
    protected void setAlarmGoingOff(boolean alarm) { this.alarm = alarm; }
    protected void setTimerGoingOff(boolean timer) { this.timer = timer; }
    protected void setShowFullDate(boolean showFullDate) { this.showFullDate = showFullDate; }
    protected void setShowPartialDate(boolean showPartialDate) { this.showPartialDate = showPartialDate; }
    protected void setShowMilitaryTime(boolean showMilitaryTime) { this.showMilitaryTime = showMilitaryTime; }

    /**
     * This clock creates a new clock based on another
     * Clocks values. DBL CHECK: used for creating Alarms
     * @param clock
     * @throws ParseException
     */
    public Clock(Clock clock) throws ParseException
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
        setFacePanel(ClockFace.ClockFace);
        setClockPanel(new ClockPanel(this));
        pack();
        add(getClockPanel());
    }

    /**
     * This constructor is the main constructor.
     * It creates a clock based on current values of the system.
     */
    public Clock() throws ParseException
    {
        super();
        setResizable(true);
        setListOfAlarms(new ArrayList<>());
        setupMenuBar();
        setShowMilitaryTime(false);
        setSeconds(LocalTime.now().getSecond()); // sets secsAsStr
        setMinutes(LocalTime.now().getMinute()); // sets minsAsStr
        setHours(LocalTime.now().getHour(), true); // sets hoursAsStr
        setMonth(LocalDate.now().getMonth());
        setDayOfWeek(LocalDate.now().getDayOfWeek());
        setDayOfMonth(LocalDate.now().getDayOfMonth());
        setYear(LocalDate.now().getYear());
        setAMPM(LocalTime.now());
        setFacePanel(ClockFace.ClockFace);
        setClockPanel(new ClockPanel(this));
        setAlarmPanel(new AlarmPanel(this));
        setTimerPanel(new TimerPanel(this));
        setRemainingDefaultValues();
        add(getClockPanel());
        pack();
    }

    /**
     * This constructor takes in values for all Clock parameters
     * and sets them based on those inputs.
     * @param hours
     * @param minutes
     * @param seconds
     * @param month
     * @param dayOfWeek
     * @param dayOfMonth
     * @param year
     * @param ampm
     * @throws ParseException
     * @throws InvalidInputException
     */
    public Clock(int hours, int minutes, int seconds, Month month, DayOfWeek dayOfWeek, int dayOfMonth, int year, Time.AMPM ampm) throws ParseException, InvalidInputException
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
        setHours(hours, true);
        setMonth(month);
        setDayOfWeek(dayOfWeek);
        setDayOfMonth(dayOfMonth);
        setYear(year);
        setAMPM(ampm);
        setFacePanel(ClockFace.ClockFace);
        setClockPanel(new ClockPanel(this));
        pack();
        add(getClockPanel());
    }

    // Helper methods
    public void setRemainingDefaultValues()
    {
        setLeapYear(false);
        setDateChanged(false);
        setHome(false);
        //setAlarm(false); method exists as below
        setAlarmGoingOff(false);
        //setTimer(false); functionality doesn't exist
        setListOfAlarms(new ArrayList<Alarm>());
    }
    public void updateHourValueAndHourString(Time.AMPM time, boolean showMilitaryTime)
    {
        if (time == AM && showMilitaryTime) // Daytime and we show Military v2.Time
        {
            if (getHours() == 12) setHours(0);
            else setHours(getHours());
        }
        else if (time == AM) // DayTime and we do not show Military v2.Time
        {
            if (getHours() == 0) setHours(12);
            else setHours(getHours());
        }
        else if (time == PM && showMilitaryTime) // NightTime and we show Military v2.Time
        {
            if (getHours() == 24) setHours(0);
            else if (getHours() < 12) setHours(getHours() + 12);
            else setHours(getHours());
        }
        else if (time == PM) // NightTime and we do not show Military v2.Time
        {
            if (getHours() > 12) setHours(getHours() - 12);
        }
    }
    public boolean isALeapYear(int year)
    {
        boolean leap = false;
        if (year % 4 == 0)
        {
            leap = true;
            if (Integer.toString(year).substring(2).equals("00"))
            {
                leap = year % 400 == 0;
            }
        }
        return leap;
    }
    public void setDaylightSavingsTimeDates() throws ParseException
    {
        setBeginDaylightSavingsTimeDate(sdf.parse("03-01-"+year));
        setEndDaylightSavingsTimeDate(sdf.parse("11-01-"+year));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getBeginDaylightSavingsTimeDate());
        switch (calendar.get(Calendar.DAY_OF_WEEK))
        {
            case 1: setBeginDaylightSavingsTimeDate(sdf.parse("03-08-"+year));
                // endDaylightSavingsTimeDate already set
                break;
            case 2: setBeginDaylightSavingsTimeDate(sdf.parse("03-14-"+(year)));
                setEndDaylightSavingsTimeDate(sdf.parse("11-07-"+year)); break;
            case 3: setBeginDaylightSavingsTimeDate(sdf.parse("03-13-"+year));
                setEndDaylightSavingsTimeDate(sdf.parse("11-06-"+year)); break;
            case 4: setBeginDaylightSavingsTimeDate(sdf.parse("03-12-"+year));
                setEndDaylightSavingsTimeDate(sdf.parse("11-05-"+year)); break;
            case 5: setBeginDaylightSavingsTimeDate(sdf.parse("03-11-"+year));
                setEndDaylightSavingsTimeDate(sdf.parse("11-04-"+year)); break;
            case 6: setBeginDaylightSavingsTimeDate(sdf.parse("03-10-"+year));
                setEndDaylightSavingsTimeDate(sdf.parse("11-03-"+year)); break;
            case 7: setBeginDaylightSavingsTimeDate(sdf.parse("03-09-"+year));
                setEndDaylightSavingsTimeDate(sdf.parse("11-02-"+year)); break;
            default: setBeginDaylightSavingsTimeDate(new Date());
                setEndDaylightSavingsTimeDate(new Date()); break;
        }
    }
    // TODO: change to compare one date to another
    public boolean isTodayDaylightSavingsTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getBeginDaylightSavingsTimeDate());
        if (getMonth() == MARCH &&
                getDayOfMonth() == calendar.get(Calendar.DATE) &&
                getYear() == calendar.get(Calendar.YEAR))
        {
            setDaylightSavingsTime(true);
            return isDaylightSavingsTime();
        }
        else
        {
            calendar.setTime(getEndDaylightSavingsTimeDate());
            if (getMonth() == NOVEMBER &&
                    getDayOfMonth() == calendar.get(Calendar.DATE) &&
                    getYear() == calendar.get(Calendar.YEAR) &&
                    !isDaylightSavingsTime())
            {
                setDaylightSavingsTime(true);
                return isDaylightSavingsTime();
            }
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
    public void performTick(int seconds, int minutes, int hours)
    {
        setSeconds(getSeconds()+seconds);
        //setSecondsAsStr(getSeconds() <= 9 ? "0"+getSeconds() : Integer.toString(getSeconds()));
        if (getSeconds() == 60)
        {
            setSeconds(0);
            //setSecondsAsStr("00");
            setMinutes(getMinutes()+1);
            //setMinutesAsStr(getMinutes() <= 9 ? "0"+getMinutes() : Integer.toString(getMinutes()));
            if (getMinutes() == 60)
            {
                setMinutes(0);
                //setMinutesAsStr("00");
                setHours(getHours()+1, true);
                if (getHours() == 12 && getMinutes() == 0 && getSeconds() == 0 && !isShowMilitaryTime())
                {
                    setHours(12);
                    setHoursAsStr("12");
                    if (getAMPM() == PM)
                    {
                        setAMPM(AM);
                        setDateChanged(true);
                    }
                    else
                    {
                        setDateChanged(false);
                        setAMPM(PM);
                    }
                }
                else if (getHours() == 13 && !isShowMilitaryTime())
                {
                    setHours(1);
                    setHoursAsStr("01");
                    setDateChanged(false);
                }
                else if (getHours() == 24 && getMinutes() == 0 && getSeconds() == 0 && isShowMilitaryTime())
                {
                    setHours(0);
                    setHoursAsStr("00");
                    setAMPM(AM);
                    setDateChanged(true);
                }
                else if (getHours() >= 13 && isShowMilitaryTime())
                {
                    setHoursAsStr(Integer.toString(this.hours));
                    setDateChanged(false);
                }
                else
                {
                    setHours(getHours());
                }
            }
        }
        else
        {
            setDateChanged(false);
        }

        if (isDateChanged())
        {
            setDayOfMonth(getDayOfMonth()+1);
            setDaylightSavingsTime(isTodayDaylightSavingsTime());
        }
        switch (getMonth()) {
            case JANUARY: {
                if (getDayOfMonth() == 31 && isDateChanged()) {
                    setDayOfMonth(1);
                    setMonth(FEBRUARY);
                }
                break;
            }
            case FEBRUARY: {
                if ((getDayOfMonth() == 28 || getDayOfMonth() == 30) && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(MARCH);
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
                }
                break;
            }
            case APRIL: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(MAY);
                }
                break;
            }
            case MAY: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(JUNE);
                }
                break;
            }
            case JUNE: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(JULY);
                }
                break;
            }
            case JULY: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(AUGUST);
                }
                break;
            }
            case AUGUST: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(SEPTEMBER);
                }
                break;
            }
            case SEPTEMBER: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(OCTOBER);
                }
                break;
            }
            case OCTOBER: {
                if (getDayOfMonth() == 32 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(NOVEMBER);
                }
                break;
            }
            case NOVEMBER: {
                if (getDayOfMonth() == 31 && isDateChanged())
                {
                    setDayOfMonth(1);
                    setMonth(DECEMBER);
                }
                break;
            }
            case DECEMBER: {
                if (getDayOfMonth() == 32 && isDateChanged()) {
                    setDayOfMonth(1);
                    setMonth(JANUARY);
                    setYear(getYear()+1);
                }
                break;
            }
            default : {}
        }
        updateHourValueAndHourString(getAMPM(), isShowMilitaryTime());
        if (isDateChanged())
        {
            setDayOfWeek(LocalDate.now().getDayOfWeek());
        }
        if (isDaylightSavingsTime())
        {
            if (getMonth() == MARCH && getAMPM() == AM)
            {
                setHours(3);
                setDaylightSavingsTime(false);
            }
            else if (getMonth() == NOVEMBER && getAMPM() == AM)
            { // && daylightSavingsTime
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
            if (isShowFullDate() && !isShowPartialDate()) defaultText += getFullDateAsStr();
            else if (isShowPartialDate() && !isShowFullDate()) defaultText += getPartialDateAsStr();
            else defaultText += getDateAsStr();
        }
        else if (labelVersion == 2)
        {
            if (!isShowMilitaryTime()) defaultText += getTimeAsStr();
            else if (isShowMilitaryTime()) defaultText += getMilitaryTimeAsStr();
        }
        else if (labelVersion == 3)
        {
            defaultText = "H";
        }
        else if (labelVersion == 4)
        {
            defaultText = "M";
        }
        else if (labelVersion == 5)
        {
            defaultText = "T";
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
    public DayOfWeek convertIntToTimeDay(int thisDay) throws InvalidInputException
    {
        switch(thisDay)
        {
            case 1: return SUNDAY;
            case 2: return MONDAY;
            case 3: return TUESDAY;
            case 4: return WEDNESDAY;
            case 5: return THURSDAY;
            case 6: return FRIDAY;
            case 7: return SATURDAY;
            default: throw new InvalidInputException("Unknown day value: " + thisDay);
        }
    }
    public int convertTimeMonthToInt(Month month) throws InvalidInputException
    {
        switch (month) {
            case JANUARY: return 1;
            case FEBRUARY: return 2;
            case MARCH: return 3;
            case APRIL: return 4;
            case MAY: return 5;
            case JUNE: return 6;
            case JULY: return 7;
            case AUGUST: return 8;
            case SEPTEMBER: return 9;
            case OCTOBER: return 10;
            case NOVEMBER: return 11;
            case DECEMBER: return 12;
            default: throw new InvalidInputException("Unknown Month: " + month);
        }
    }
    public Month convertIntToTimeMonth(int thisMonth) throws InvalidInputException
    {
        switch (thisMonth)
        {
            case 1: return JANUARY;
            case 2: return FEBRUARY;
            case 3: return MARCH;
            case 4: return APRIL;
            case 5: return MAY;
            case 6: return JUNE;
            case 7: return JULY;
            case 8: return AUGUST;
            case 9: return SEPTEMBER;
            case 10: return OCTOBER;
            case 11: return NOVEMBER;
            case 12: return DECEMBER;
            default: throw new InvalidInputException("Unknown month value: " + thisMonth);
        }
    }
    public int convertTimeAMPMToInt(Time.AMPM ampm)
    {
        if (ampm == AM) return 0;
        else return 1;
    }
    public void setupMenuBar()
    {
        UIManager.put("MenuItem.background", Color.BLACK);
        setClockMenuBar(new ClockMenuBar());
        // Settings Actions for Settings menu
        getClockMenuBar().getMilitaryTimeSetting().addActionListener(action -> {
            if (isShowMilitaryTime())
            {
                setShowMilitaryTime(false);
                menuBar.getMilitaryTimeSetting().setText(HIDE + SPACE + MILITARY_TIME_SETTING);
            }
            else
            {
                setShowMilitaryTime(true);
                menuBar.getMilitaryTimeSetting().setText(SHOW + SPACE + STANDARD_TIME_SETTING);
            }
            //updateClockFace(true);
            // updatePanel
            //pack();
        });
        getClockMenuBar().getFullTimeSetting().addActionListener(action -> {
            if (isShowFullDate())
            {
                setShowFullDate(false);
                setShowPartialDate(false);
                menuBar.getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
            }
            else
            {
                setShowFullDate(true);
                setShowPartialDate(false);
                menuBar.getFullTimeSetting().setText(HIDE + SPACE + FULL_TIME_SETTING);
            }
            menuBar.getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            //updateClockFace(true);
            // updatePanel
            //pack();
        });
        getClockMenuBar().getPartialTimeSetting().addActionListener(action -> {
            if (isShowPartialDate())
            {
                setShowPartialDate(false);
                setShowFullDate(false);
                menuBar.getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            else
            {
                setShowPartialDate(true);
                setShowFullDate(false);
                menuBar.getPartialTimeSetting().setText(HIDE + SPACE + PARTIAL_TIME_SETTING);
            }
            menuBar.getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
            //updateClockFace(true);
            // updatePanel
            //pack();
        });
        // Features Actions for Features menu
        getClockMenuBar().getClockFeature().addActionListener(action -> {
            changeToClockPanel();
        });
        getClockMenuBar().getSetAlarms().addActionListener(action -> {
            changeToAlarmPanel();
        });
        getClockMenuBar().getTimerFeature().addActionListener(action -> {
            changeToTimerPanel();
        });
        // Add both menus to main menu
        getClockMenuBar().add(getClockMenuBar().getSettingsMenu());
        getClockMenuBar().add(getClockMenuBar().getFeaturesMenu());
        // Set the Clock's menu to ClockMenuBar
        setJMenuBar(getClockMenuBar());
    }
    public void changeToClockPanel()
    {
        if (getFacePanel() == ClockFace.TimerFace)
            remove(getTimerPanel());
        else if (getFacePanel() == ClockFace.AlarmFace)
            remove(getAlarmPanel());
        setFacePanel(ClockFace.ClockFace);
        add(getClockPanel());
        this.repaint();
        this.setVisible(true);
    }
    public void changeToAlarmPanel()
    {
        if (getFacePanel() == ClockFace.ClockFace)
            remove(getClockPanel());
        else if (getFacePanel() == ClockFace.TimerFace)
            remove(getTimerPanel());
        setFacePanel(ClockFace.AlarmFace);
        if (getAlarmPanel().getMusicPlayer() != null) { getAlarmPanel().setMusicPlayer(null); }
        add(getAlarmPanel());
        this.repaint();
        this.setVisible(true);
    }
    public void changeToTimerPanel()
    {
        if (getFacePanel() == ClockFace.ClockFace)
            remove(getClockPanel());
        else if (getFacePanel() == ClockFace.AlarmFace)
            remove(getAlarmPanel());
        setFacePanel(ClockFace.TimerFace);
        add(getTimerPanel());
        this.repaint();
        this.setVisible(true);
    }
    public void tick()
    {
        tick(1,0,0); // default
    }
    /**
     * The purpose of tick is to start the clock but it should increase
     * the clocks time given the values of seconds, minutes, and seconds
     * with each tick.
     *
     * @param seconds, the amount of seconds to tick forward or backwards with each tick
     * @param minutes, the amount of minutes to tick forward of backwards with each tick
     * @param hours,   the amount of hours   to tick forward or backwards with each tick
     */
    public void tick(int seconds, int minutes, int hours)
    {
        try
        {
            performTick(seconds, minutes, hours);
            //Updates the clock daily to keep time current
            getClockPanel().updateLabels();
            if (getTimeAsStr().equals("04:20:00" + SPACE + AM.getStrValue()) ||
                getMilitaryTimeAsStr().equals("0420 hours 00"))
            {
                setSeconds(LocalTime.now().getSecond());
                setMinutes(LocalTime.now().getMinute());
                setHours(LocalTime.now().getHour(), true);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error! Clock had an exception when performing tick: " + e.getMessage());
        }
    }
    public void printClockStatus()
    { printClockStatus(this.getClass(), ""); }
    public void printClockStatus(Class clazz, String status)
    {
        if (clazz == Alarm.class)
        {
            System.out.println("Alarm Status: " + status);
        }
        else
        {
            System.out.println("Clock Status: " + status);
        }
        System.out.println("clockPanel: " + getClockPanel().getName());
        System.out.println("alarmPanel: " + getAlarmPanel().getName());
        System.out.println("beginDST Date: " + getBeginDaylightSavingsTimeDate());
        System.out.println("endDST Date: " + getEndDaylightSavingsTimeDate());
        //System.out.println("calendar: " + getCalendar());
        System.out.println("seconds: " + getSeconds());
        System.out.println("secondsAtStr: " + getSecondsAsStr());
        System.out.println("minutes: " + getMinutes());
        System.out.println("minutesAsStr: " + getMinutesAsStr());
        System.out.println("hours: " + getHours());
        System.out.println("hoursAsStr: " + getHoursAsStr());
        System.out.println("AMPM: " + getAMPM());
        System.out.println("day: " + getDayOfWeek());
        System.out.println("date: " + getDayOfMonth());
        System.out.println("month: " + getMonth());
        System.out.println("year: " + getYear());
        System.out.println("clockFaceVisible: " + getFacePanel());
        System.out.println("leapYear: " + isLeapYear());
        System.out.println("DST: " + isDaylightSavingsTime());
        System.out.println("dateChanged: " + isDateChanged());
        System.out.println("alarm: " + isAlarmGoingOff());
        System.out.println("timer: " + isTimerGoingOff());
        System.out.println("updateAlarm: " + isUpdateAlarm());
        System.out.println("showFullDate: " + isShowFullDate());
        System.out.println("showPartialDate: " + isShowPartialDate());
        System.out.println("showMilitaryTime: " + isShowMilitaryTime());
        System.out.println("listOfAlarms size: " + this.getListOfAlarms().size());
        for(int i = 0; i < getListOfAlarms().size(); i++)
        {
            System.out.println("\talarm: " + getListOfAlarms().get(i).getTimeAsStr());
        }
    }
    protected void printStackTrace(Exception e)
    {
        System.err.println(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        {
            System.out.println(ste.toString());
        }
    }
}