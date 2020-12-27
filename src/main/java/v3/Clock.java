package v3;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple application which displays the time and date. The time
 * can be view in military time or not, and the date fully expressed,
 * partially expressed or standard expression.
 * 
 * Alarms are the next feature and are a work in progress.
 *
 * Timers are coming soon!!
 * 
 * @author Michael Ball 
 * @version 3
 */
public class Clock extends JFrame {
    // Class attributes
    protected static final long serialVersionUID = 1L;
    protected static final Dimension defaultSize = new Dimension(700, 300);
    protected static final Dimension alarmSize = new Dimension(701, 301);
    protected static final String HIDE = "Hide";
    protected static final String SHOW = "Show";
    protected static final String SPACE = " ";
    protected static final String STANDARD_TIME_SETTING = "standard time";
    protected static final String MILITARY_TIME_SETTING = "military time";
    protected static final String FULL_TIME_SETTING = "full date";
    protected static final String PARTIAL_TIME_SETTING = "partial date";
    protected static final DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    protected static final Font font60 = new Font("Courier New", Font.BOLD, 60);
    protected static final Font font50 = new Font("Courier New", Font.BOLD, 50);
    protected static final Font font40 = new Font("Courier New", Font.PLAIN, 20);
    protected static final Font font20 = new Font("Courier New", Font.PLAIN, 20);
    // Main GUI Components
    // For displaying faces on main display
    protected Panels facePanel;
    protected ClockMenuBar menuBar;
    protected ClockPanel clockPanel;
    protected AlarmPanel alarmPanel;
    protected Date beginDaylightSavingsTimeDate;
    protected Date endDaylightSavingsTimeDate;
    protected Calendar calendar;
    protected int seconds;
    protected int minutes;
    protected int hours;
    protected Time.AMPM ampm;
    protected Time.Day day;
    protected int date;
    protected Time.Month month;
    protected int year;
    protected String hoursAsStr = "", minutesAsStr = "", secondsAsStr = "";
    protected ClockFace clockFace;
    protected boolean leapYear;
    protected boolean daylightSavingsTime;
    protected boolean dateChanged;
    protected boolean home = true, alarm = false, timer = false, updateAlarm = false;
    protected boolean showFullDate = false;
    protected boolean showPartialDate = false;
    protected boolean showMilitaryTime = false;
    protected ArrayList<Clock.Alarm> listOfAlarms;
    // Getters/Issers
    //public GridBagLayout getGridBagLayout() { return this.layout; }
    //public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Panels getFacePanel() { return this.facePanel; }
    public ClockMenuBar getClockMenuBar() { return this.menuBar; }
    public ClockPanel getClockPanel() { return this.clockPanel; }
    public AlarmPanel getAlarmPanel() { return this.alarmPanel; }
    public Date getBeginDaylightSavingsTimeDate() { return this.beginDaylightSavingsTimeDate; }
    public Date getEndDaylightSavingsTimeDate() { return this.endDaylightSavingsTimeDate; }
    public Calendar getCalendar() { return this.calendar; }
    public int getSeconds() { return seconds; }
    public int getMinutes() { return minutes; }
    public int getHours() { return hours; }
    public Time.AMPM getAMPM() { return ampm; }
    public Time.Day getDay() { return day; }
    public int getDate() { return date; }
    public Time.Month getMonth() { return month; }
    public int getYear() { return this.year; }
    public String getHoursAsStr() { return this.hoursAsStr; }
    public String getMinutesAsStr() { return this.minutesAsStr; }
    public String getSecondsAsStr() { return this.secondsAsStr; }

    /**
     * This method returns the clock's current hour, minute, second, and time.
     * It can also be used to get the alarm's time set value
     * @return
     */
    public String getTimeAsStr() { return this.hoursAsStr+":"+this.minutesAsStr+":"+this.secondsAsStr+" "+this.ampm.getStrValue(); }
    public String getDateAsStr() { return this.month.strValue+" "+this.date+", "+this.year; }
    public String getFullDateAsStr() { return this.day.strValue+" "+this.month.strValue+" "+this.date+", "+this.year; }
    public String getMilitaryTimeAsStr() {
        return this.hoursAsStr + this.minutesAsStr + " hours " + this.secondsAsStr;
    }
    public String getPartialDateAsStr() { return this.day.strValue.substring(0,3)+" "+this.month.strValue.substring(0,3)+" "+this.date+", "+this.year; }
    public ClockFace getClockFace() { return this.clockFace; }
    public boolean isLeapYear() { return this.leapYear; }
    public boolean isDaylightSavingsTime() { return this.daylightSavingsTime; }
    public boolean isDateChanged() { return this.dateChanged; }
    public boolean isHome() { return this.home; }
    public boolean isAlarmGoingOff() { return this.alarm; }
    public boolean isUpdateAlarm() { return this.updateAlarm; }
    public boolean isTimerGoingOff() { return this.timer; }
    public boolean isShowFullDate() { return this.showFullDate; }
    public boolean isShowPartialDate() { return this.showPartialDate; }
    public boolean isShowMilitaryTime() { return this.showMilitaryTime; }
    public ArrayList<Clock.Alarm> getListOfAlarms() { return this.listOfAlarms; }
    // Setters
    //private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    //private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    /* TODO: setFacePanel appears to be irrelevant now. remove*/
    protected void setFacePanel(Panels facePanel) { this.facePanel = facePanel; }
    protected void setClockMenuBar(ClockMenuBar menuBar) { this.menuBar = menuBar; }
    protected void setAlarmPanel(AlarmPanel alarmPanel) { this.alarmPanel = alarmPanel; }
    protected void setClockPanel(ClockPanel clockPanel) { this.clockPanel = clockPanel; }
    protected void setBeginDaylightSavingsTimeDate(Date beginDaylightSavingsTimeDate) { this.beginDaylightSavingsTimeDate = beginDaylightSavingsTimeDate; }
    protected void setEndDaylightSavingsTimeDate(Date endDaylightSavingsTimeDate) { this.endDaylightSavingsTimeDate = endDaylightSavingsTimeDate; }
    protected void setCalendar(Calendar calendar) { this.calendar = calendar; }
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
        if (hours == 0 && !showMilitaryTime) {
            this.hours = 12;
        }
        else if (hours == 0 && showMilitaryTime) {
            this.hours = 0;
        }
        else 
            this.hours = hours;
        if (this.hours <= 9) this.hoursAsStr = "0"+hours;
        else this.hoursAsStr = Integer.toString(this.hours);
    }
    protected void setAMPM(Time.AMPM ampm) { this.ampm = ampm; }
    protected void setDay(Time.Day day) { this.day = day; }
    protected void setDate(int date) { this.date = date; }
    protected void setMonth(Time.Month month) { this.month = month; }
    protected void setYear(int year) { this.year = year; }
    protected void setHoursAsStr(String hoursAsStr) { this.hoursAsStr = hoursAsStr; }
    protected void setMinutesAsStr(String minutesAsStr) { this.minutesAsStr = minutesAsStr; }
    protected void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; }
    protected void setListOfAlarms(ArrayList<Clock.Alarm> listOfAlarms) { this.listOfAlarms = listOfAlarms; }
    protected void setUpdateAlarm(boolean updateAlarm) { this.updateAlarm = updateAlarm;}
    // setDateAsStr()
    // setFullDateAsStr()
    // setMilitaryTimeAsStr()
    // setPartialTimeAsStr()
    protected void setClockFace(ClockFace clockFace) { this.clockFace = clockFace; }
    protected void setLeapYear(boolean leapYear) { this.leapYear = leapYear; }
    protected void setDaylightSavingsTime(boolean daylightSavingsTime) { this.daylightSavingsTime = daylightSavingsTime; }
    protected void setDateChanged(boolean dateChanged) { this.dateChanged = dateChanged; }
    protected void setHome(boolean home) { this.home = home; }
    protected void setAlarmGoingOff(boolean alarm) { this.alarm = alarm; }
    protected void setTimerGoingOff(boolean timer) { this.timer = timer; }
    protected void setShowFullDate(boolean showFullDate) { this.showFullDate = showFullDate; }
    protected void setShowPartialDate(boolean showPartialDate) { this.showPartialDate = showPartialDate; }
    protected void setShowMilitaryTime(boolean showMilitaryTime) { this.showMilitaryTime = showMilitaryTime; }
    protected void setCalendarTime(Date date) { getCalendar().setTime(date); }

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
        setDate(clock.getDate());
        setYear(clock.getYear());
        setFacePanel(new ClockPanel(this));
        setClockPanel((ClockPanel)getFacePanel());
        setClockFace(ClockFace.ClockFace);
        pack();
        add((Component) getFacePanel());
    }
    public Clock() throws ParseException
    {
        super();
        setResizable(true);
        setListOfAlarms(new ArrayList<>());
        setupMenuBar();
        setShowMilitaryTime(false);
        setSeconds(0);
        setMinutes(0);
        setHours(0);
        setSecondsAsStr("");
        setMinutesAsStr("");
        setDefaultClockValues(getHours(), getMinutes(), getSeconds(), getMonth(), getDay(), getDate(), getYear(), getAMPM());
        setFacePanel(new ClockPanel(this));
        setClockPanel((ClockPanel)getFacePanel());
        setAlarmPanel(new AlarmPanel(this));
        setClockFace(ClockFace.ClockFace);
        setRemainingDefaultValues();
        pack();
        add((Component) getFacePanel());
    }
    public Clock(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int date, int year, Time.AMPM ampm) throws ParseException
    {
        super();
        setResizable(true);
        setListOfAlarms(new ArrayList<>());
        setupMenuBar();
        setAlarmPanel(new AlarmPanel(this));
        setSeconds(seconds);
        setMinutes(minutes);
        setHours(hours);
        setDefaultClockValues(hours, minutes, seconds, month, day, date, year, ampm);
        setFacePanel(new ClockPanel(this));
        setClockPanel((ClockPanel)getFacePanel());
        setClockFace(ClockFace.ClockFace);
        pack();
        add((Component) getFacePanel());
    }
    static class Alarm extends Clock
    {
        public Alarm(Clock clock, int hours, boolean isUpdateAlarm) throws ParseException
        {
            super(clock);
            setHours(hours);
            setUpdateAlarm(isUpdateAlarm);
        }
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
        setListOfAlarms(new ArrayList<Clock.Alarm>());
    }
    public void setDefaultClockValues(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int  date, int year, Time.AMPM ampm) throws ParseException
    {
        setCalendar(Calendar.getInstance());
        setCalendarTime(new Date());
        Date definedDate = null;
        if (hours == 0 && hoursAsStr.equals("") ||
                hours == 12 && hoursAsStr.equals("12")) { hours = getCalendar().get(Calendar.HOUR); hoursAsStr = Integer.toString(hours); }
        if (minutes == 0 && minutesAsStr.equals("")) { minutes = getCalendar().get(Calendar.MINUTE); minutesAsStr = Integer.toString(minutes); }
        if (seconds == 0 && secondsAsStr.equals("")) { seconds = getCalendar().get(Calendar.SECOND); secondsAsStr = Integer.toString(seconds); }
        if (month == null) { month = convertIntToTimeMonth(getCalendar().get(Calendar.MONTH)+1); }
        if (day == null) { day = convertIntToTimeDay(getCalendar().get(Calendar.DAY_OF_WEEK)); }
        if (date == 0) { date = getCalendar().get(Calendar.DAY_OF_MONTH); }
        if (year == 0) { year = getCalendar().get(Calendar.YEAR); }
        if (ampm == null) { ampm = getCalendar().get(Calendar.AM_PM) == Calendar.PM ? Time.AMPM.PM : Time.AMPM.AM; }
        try {
            if (date < 10)
                definedDate = sdf.parse(convertTimeMonthToInt(month)+"-0"+date+"-"+year);
            else
                definedDate = sdf.parse(convertTimeMonthToInt(month)+"-"+date+"-"+year);
        }
        catch (ParseException | InvalidInputException e)
        {
            String dateStr = date <= 9 ? "0"+date : Integer.toString(date);
            System.err.println("Error! Couldn't create the date using month=["+month+"], date=["+dateStr+
                    "], year=["+year+"]");
        }
        setCalendarTime(definedDate);
        setMonth(month);
        setDay(day);
        setDate(date);
        setYear(year);
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
        setAMPM(ampm);
        setDaylightSavingsTimeDates();
        setDaylightSavingsTime(isTodayDaylightSavingsTime());
    }
    public void updateHourValueAndHourString(Time.AMPM time, boolean showMilitaryTime)
    {
        if (time == Time.AMPM.AM && showMilitaryTime) // Daytime and we show Military v2.Time
        {
            if (getHours() == 12) setHours(0);
            else setHours(getHours());
        }
        else if (time == Time.AMPM.AM) // DayTime and we do not show Military v2.Time
        {
            if (getHours() == 0) setHours(12);
            else setHours(getHours());
        }
        else if (time == Time.AMPM.PM && showMilitaryTime) // NightTime and we show Military v2.Time
        {
            if (getHours() == 24) setHours(0);
            else if (getHours() < 12) setHours(getHours() + 12);
            else setHours(getHours());
        }
        else if (time == Time.AMPM.PM) // NightTime and we do not show Military v2.Time
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
                if (year % 400 == 0)
                {
                    leap = true;
                }
                else
                {
                    leap = false;
                }
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
        if (getMonth() == Time.Month.MARCH &&
                getDate() == calendar.get(Calendar.DATE) &&
                getYear() == calendar.get(Calendar.YEAR))
        {
            setDaylightSavingsTime(true);
            return isDaylightSavingsTime();
        }
        else
        {
            calendar.setTime(getEndDaylightSavingsTimeDate());
            if (getMonth() == Time.Month.NOVEMBER &&
                    getDate() == calendar.get(Calendar.DATE) &&
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
        setSecondsAsStr(getSeconds() <= 9 ? "0"+getSeconds() : Integer.toString(getSeconds()));
        if (getSeconds() == 60)
        {
            setSeconds(0);
            setSecondsAsStr("00");
            setMinutes(getMinutes()+minutes);
            setMinutesAsStr(getMinutes() <= 9 ? "0"+getMinutes() : Integer.toString(getMinutes()));
            if (getMinutes() == 60)
            {
                setMinutes(0);
                setMinutesAsStr("00");
                setHours(getHours()+hours);
                if (getHours() == 12 && getMinutes() == 0 && getSeconds() == 0 && !isShowMilitaryTime())
                {
                    setHours(12);
                    setHoursAsStr("12");
                    if (getAMPM() == Time.AMPM.PM)
                    {
                        setAMPM(Time.AMPM.AM);
                        setDateChanged(true);
                    }
                    else
                    {
                        setDateChanged(false);
                        setAMPM(Time.AMPM.PM);
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
                    setAMPM(Time.AMPM.AM);
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
            setDate(getDate()+1);
            setDaylightSavingsTime(isTodayDaylightSavingsTime());
        }
        switch (getMonth()) {
            case ERROR:
                break;
            case JANUARY: {
                if (getDate() == 31 && isDateChanged()) {
                    setDate(1);
                    setMonth(Time.Month.FEBRUARY);
                }
                break;
            }
            case FEBRUARY: {
                if ((getDate() == 28 || getDate() == 30) && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.MARCH);
                }
                else if (getDate() == 28 && isLeapYear() && isDateChanged())
                {
                    setDate(29);
                    setMonth(Time.Month.FEBRUARY);
                }
                break;
            }
            case MARCH: {
                if (getDate() == 32 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.APRIL);
                }
                break;
            }
            case APRIL: {
                if (getDate() == 31 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.MAY);
                }
                break;
            }
            case MAY: {
                if (getDate() == 32 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.JUNE);
                }
                break;
            }
            case JUNE: {
                if (getDate() == 31 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.JULY);
                }
                break;
            }
            case JULY: {
                if (getDate() == 32 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.AUGUST);
                }
                break;
            }
            case AUGUST: {
                if (getDate() == 32 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.SEPTEMBER);
                }
                break;
            }
            case SEPTEMBER: {
                if (getDate() == 31 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.OCTOBER);
                }
                break;
            }
            case OCTOBER: {
                if (getDate() == 32 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.NOVEMBER);
                }
                break;
            }
            case NOVEMBER: {
                if (getDate() == 31 && isDateChanged())
                {
                    setDate(1);
                    setMonth(Time.Month.DECEMBER);
                }
                break;
            }
            case DECEMBER: {
                if (getDate() == 32 && isDateChanged()) {
                    setDate(1);
                    setMonth(Time.Month.JANUARY);
                    setYear(getYear()+1);
                }
                break;
            }
            default : {}
        }
        updateHourValueAndHourString(getAMPM(), isShowMilitaryTime());
        if (isDateChanged())
        {
            int month = 0;
            try {
                month = convertTimeMonthToInt(getMonth());
            } catch (InvalidInputException iie) {
                System.err.println(iie.getMessage());
            }
            int date = getDate();
            String monthStr = month <= 9 ? "0"+month : Integer.toString(month);
            String dateStr = date <= 9 ? "0"+date : Integer.toString(date);
            Date updatedDate = null;
            try {
                updatedDate = sdf.parse(monthStr+"-"+dateStr+"-"+getYear());
            } catch (ParseException e) { System.err.println(e.getMessage()); }
            setCalendarTime(updatedDate);
            setDay(convertIntToTimeDay(getCalendar().get(Calendar.DAY_OF_WEEK)));
        }
        if (isDaylightSavingsTime())
        {
            if (getCalendar().get(Calendar.MONTH) == 2 && getAMPM().getStrValue().equals(Time.AMPM.AM.strValue))
            {
                setHours(3);
                setDaylightSavingsTime(false);
            }
            else if (getCalendar().get(Calendar.MONTH) == 10 && getAMPM().getStrValue().equals(Time.AMPM.AM.strValue))
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
            defaultText = "All Alarms";
        }
        return defaultText;
    }
    public Time.Day convertIntToTimeDay(int thisDay)
    {
        switch(thisDay)
        {
            case 1: return Time.Day.SUNDAY;
            case 2: return Time.Day.MONDAY;
            case 3: return Time.Day.TUESDAY;
            case 4: return Time.Day.WEDNESDAY;
            case 5: return Time.Day.THURSDAY;
            case 6: return Time.Day.FRIDAY;
            case 7: return Time.Day.SATURDAY;
            default: return Time.Day.ERROR;
        }
    }
    public int convertTimeMonthToInt(Time.Month month) throws InvalidInputException
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
            default: throw new InvalidInputException(Time.Month.ERROR.strValue);
        }
    }
    public Time.Month convertIntToTimeMonth(int thisMonth)
    {
        switch (thisMonth)
        {
            case 1: return Time.Month.JANUARY;
            case 2: return Time.Month.FEBRUARY;
            case 3: return Time.Month.MARCH;
            case 4: return Time.Month.APRIL;
            case 5: return Time.Month.MAY;
            case 6: return Time.Month.JUNE;
            case 7: return Time.Month.JULY;
            case 8: return Time.Month.AUGUST;
            case 9: return Time.Month.SEPTEMBER;
            case 10: return Time.Month.OCTOBER;
            case 11: return Time.Month.NOVEMBER;
            case 12: return Time.Month.DECEMBER;
            default: return Time.Month.ERROR;
        }
    }
    public void setupMenuBar()
    {
        UIManager.put("MenuItem.background", Color.BLACK);
        setClockMenuBar(new ClockMenuBar());
        // Menu Options
        getClockMenuBar().getMilitaryTimeSetting().addActionListener(action -> {
            if (isShowMilitaryTime() == true)
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
            pack();
        });
        getClockMenuBar().getFullTimeSetting().addActionListener(action -> {
            if (isShowFullDate())
            {
                setShowFullDate(false);
                setShowPartialDate(false);
                menuBar.getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
                menuBar.getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            else
            {
                setShowFullDate(true);
                setShowPartialDate(false);
                menuBar.getFullTimeSetting().setText(HIDE + SPACE + FULL_TIME_SETTING);
                menuBar.getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            //updateClockFace(true);
            // updatePanel
            pack();
        });
        getClockMenuBar().getPartialTimeSetting().addActionListener(action -> {
            if (isShowPartialDate())
            {
                setShowPartialDate(false);
                setShowFullDate(false);
                menuBar.getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
                menuBar.getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
            }
            else
            {
                setShowPartialDate(true);
                setShowFullDate(false);
                menuBar.getPartialTimeSetting().setText(HIDE + SPACE + PARTIAL_TIME_SETTING);
                menuBar.getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
            }
            //updateClockFace(true);
            // updatePanel
            pack();
        });

        getClockMenuBar().getClockFeature().addActionListener(action -> {
            if (getClockFace() != ClockFace.ClockFace)
            {
                changeToClockPanel();
            }
            else
            {
                System.err.println("Trying to change to clock panel but already showing ClockFace");
            }
        });
        getClockMenuBar().getSetAlarms().addActionListener(action -> {
            changeToAlarmPanel();
            getAlarmPanel().getJTextField1().setText("");
            getAlarmPanel().getJTextField2().setText("");
            getAlarmPanel().getJTextField3().setText("");
        });

        getClockMenuBar().add(getClockMenuBar().getSettingsMenu());
        getClockMenuBar().add(getClockMenuBar().getFeaturesMenu());
        setJMenuBar(getClockMenuBar());
    }
    public void changeToClockPanel()
    {
        setAlarmPanel((AlarmPanel)getFacePanel()); // is this correct
        remove((Component) getFacePanel());
        setClockFace(ClockFace.ClockFace);
        setFacePanel(getClockPanel());
        setClockPanel((ClockPanel)getFacePanel());
        add((Component) getFacePanel());
        this.repaint();
        this.setVisible(true);
    }
    public void changeToAlarmPanel()
    {
        remove((Component) getFacePanel());
        setClockFace(ClockFace.AlarmFace);
        setFacePanel(getAlarmPanel());
        if (getAlarmPanel().getMusicPlayer() != null) { getAlarmPanel().setMusicPlayer(null); }
        add((Component) getFacePanel());
        this.repaint();
        this.setVisible(true);
    }
    /**
     * The purpose of tick is to start the clock normally.
     */
    public void tick()
    {
        tick(1, 1, 1);
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
            if (getTimeAsStr().equals("04:20:00" + SPACE + Time.AMPM.AM.strValue) ||
                getMilitaryTimeAsStr().equals("0420 hours 00"))
            {
                setSecondsAsStr("00");
                setMinutesAsStr("00");
                setHoursAsStr("00");
                setDefaultClockValues(0, 0, 0, null, null, 0, 0, null);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error! Clock had an exception when performing tick: " + e.getMessage());
        }
    }
    public void printClockStatus()
    {
        System.out.println("Clock Status:");
        //System.out.println("facePanel: " + getFacePanel());
        //System.out.println("menuBar: " + getClockMenuBar());
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
        System.out.println("day: " + getDay());
        System.out.println("date: " + getDate());
        System.out.println("month: " + getMonth());
        System.out.println("year: " + getYear());
        System.out.println("clockFace: " + getClockFace());
        System.out.println("leapYear: " + isLeapYear());
        System.out.println("DST: " + isDaylightSavingsTime());
        System.out.println("dateChanged: " + isDateChanged());
        //System.out.println("home: " + isHome());
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
    public static void main(String[] args) throws ParseException, InterruptedException
    {
        Clock clock = new Clock();
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        clock.setSize(defaultSize);
        clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clock.setBounds(200, 200, 700, 300);
        while (true)
        {
            clock.tick();
            // check alarms
            Thread.sleep(250);
            clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
            Thread.sleep(750);
        }
    }
}