package v5;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;

import static java.time.Month.*;
import static java.time.DayOfWeek.*;
import static v5.ClockConstants.*;
import static v5.Time.AMPM.*;

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
    protected static final long serialVersionUID = 1L;
    protected static final Dimension defaultSize = new Dimension(700, 300);
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
    protected Time.AMPM ampm;
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
    protected boolean alarm = false;
    protected boolean timer = false;
    protected boolean updateAlarm = false;
    protected boolean showFullDate = false;
    protected boolean showPartialDate = false;
    protected boolean showMilitaryTime = false;
    protected ArrayList<Alarm> listOfAlarms;

    public ClockFace getFacePanel() { return this.facePanel; }
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
    public String getTimeAsStr() { return getHoursAsStr()+":"+getMinutesAsStr()+":"+getSecondsAsStr()+" "+getAMPM().getStrValue(); }
    public String getDateAsStr() { return this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getFullDateAsStr() { return this.dayOfWeek+" "+this.month+" "+this.dayOfMonth +", "+this.year; }
    public String getMilitaryTimeAsStr() {
        return this.hoursAsStr + this.minutesAsStr + " hours " + this.secondsAsStr;
    }
    public String getPartialDateAsStr() { return this.dayOfWeek.toString().substring(0,3)+" "+this.month.toString().substring(0,3)+" "+this.dayOfMonth +", "+this.year; }
    public boolean isLeapYear() { return this.leapYear; }
    public boolean isDaylightSavingsTime() { return this.isDaylightSavingsTime; }
    public boolean isDateChanged() { return this.isDateChanged; }
    public boolean isAlarmGoingOff() { return this.alarm; }
    public boolean isUpdateAlarm() { return this.updateAlarm; }
    public boolean isTimerGoingOff() { return this.timer; }
    public boolean isShowFullDate() { return this.showFullDate; }
    public boolean isShowPartialDate() { return this.showPartialDate; }
    public boolean isShowMilitaryTime() { return this.showMilitaryTime; }
    public ArrayList<Alarm> getListOfAlarms() { return this.listOfAlarms; }

    protected void setFacePanel(ClockFace facePanel) { this.facePanel = facePanel; }
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
    protected void setAlarmGoingOff(boolean alarm) { this.alarm = alarm; }
    protected void setTimerGoingOff(boolean timer) { this.timer = timer; }
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
        setSeconds(LocalTime.now().getSecond()); // sets secsAsStr
        setMinutes(LocalTime.now().getMinute()); // sets minutesAsStr
        setHours(LocalTime.now().getHour(), true); // sets hoursAsStr
        setMonth(LocalDate.now().getMonth());
        setDayOfWeek(LocalDate.now().getDayOfWeek());
        setDayOfMonth(LocalDate.now().getDayOfMonth());
        setYear(LocalDate.now().getYear());
        setAMPM(LocalTime.now());
        setDaylightSavingsTimeDates();
        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
        setFacePanel(ClockFace.ClockFace);
        setClockPanel(new ClockPanel(this));
        setAlarmPanel(new AlarmPanel(this));
        setTimerPanel(new TimerPanel(this));
        setLeapYear(getDate().isLeapYear());
        setDateChanged(false);
        setAlarmGoingOff(false);
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
    public Clock(int hours, int minutes, int seconds, Month month, DayOfWeek dayOfWeek, int dayOfMonth, int year, Time.AMPM ampm) throws InvalidInputException
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
        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
        setDaylightSavingsTimeDates();
        setFacePanel(ClockFace.ClockFace);
        setClockPanel(new ClockPanel(this));
        setLeapYear(getDate().isLeapYear());
        setDateChanged(false);
        setAlarmGoingOff(false);
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
        setHours(clock.getHours(), true);
        setAMPM(clock.getAMPM());
        setMonth(clock.getMonth());
        setDayOfMonth(clock.getDayOfMonth());
        setYear(clock.getYear());
        setDate(LocalDate.of(getYear(), getMonth(), getDayOfMonth()));
        setDaylightSavingsTimeDates();
        setFacePanel(ClockFace.ClockFace);
        setClockPanel(new ClockPanel(this));
        pack();
        add(getClockPanel());
    }
    public void updateHourValueAndHourString(Time.AMPM time, boolean showMilitaryTime)
    {
        if (time == AM && showMilitaryTime) // Daytime and we show Military v2.Time
        {
            if (getHours() == 12) setHours(0, true);
            else setHours(getHours(), true);
        }
        else if (time == AM) // DayTime and we do not show Military v2.Time
        {
            if (getHours() == 0) setHours(12, true);
            else setHours(getHours(), true);
        }
        else if (time == PM && showMilitaryTime) // NightTime and we show Military v2.Time
        {
            if (getHours() == 24) setHours(0, true);
            else if (getHours() < 12) setHours(getHours() + 12, true);
            else setHours(getHours(), true);
        }
        else if (time == PM) // NightTime and we do not show Military v2.Time
        {
            if (getHours() > 12) setHours(getHours() - 12, true);
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
        setSeconds(getSeconds()+seconds);
        if (getSeconds() == 60)
        {
            setSeconds(0);
            setMinutes(getMinutes()+1);
            if (getMinutes() == 60)
            {
                setMinutes(0);
                setHours(getHours()+1, true);
                if (getHours() == 12 && getMinutes() == 0 && getSeconds() == 0 && !isShowMilitaryTime())
                {
                    setHours(12, true);
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
                    setHours(1, true);
                    setHoursAsStr("01");
                    setDateChanged(false);
                }
                else if (getHours() == 24 && getMinutes() == 0 && getSeconds() == 0 && isShowMilitaryTime())
                {
                    setHours(0, true);
                    setHoursAsStr("00");
                    setAMPM(AM);
                    setDateChanged(true);
                }
                else if (getHours() >= 13 && isShowMilitaryTime())
                {
                    setHoursAsStr(Integer.toString(getHours()));
                    setDateChanged(false);
                }
                else
                {
                    setHours(getHours(), true);
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
        if (isDaylightSavingsTime())
        {
            if (getMonth() == MARCH && getAMPM() == AM)
            {
                setHours(3, true);
                setDaylightSavingsTime(false);
            }
            else if (getMonth() == NOVEMBER && getAMPM() == AM)
            { // && daylightSavingsTime
                setHours(1, true);
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
                defaultText = getTimeAsStr();
                // change alarms to reflect normal time

            }
            else if (isShowMilitaryTime()) {
                defaultText = getMilitaryTimeAsStr();
                // change alarms to reflect military time
            }
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
    public void setupMenuBar()
    {
        UIManager.put("MenuItem.background", Color.BLACK);
        setClockMenuBar(new ClockMenuBar(this));
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
    protected void printStackTrace(Exception e)
    {
        System.err.println(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        {
            System.out.println(ste.toString());
        }
    }
}