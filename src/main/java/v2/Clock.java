package v2;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

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
 * @version 2
 */
public class Clock extends JFrame {
    // Class attributes
    private static final long serialVersionUID = 1L;
    private static final Dimension defaultSize = new Dimension(700, 300);
    private static final String HIDE = "Hide";
    private static final String SHOW = "Show";
    private static final String SPACE = " ";
    private static final String STANDARD_TIME_SETTING = "standard time";
    private static final String MILITARY_TIME_SETTING = "military time";
    private static final String FULL_TIME_SETTING = "full date";
    private static final String PARTIAL_TIME_SETTING = "partial date";
    private static final DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    private final int lbl1 = 1;
    private final int lbl2 = 2;
    // For clock
    private JLabel jlbl1 = new JLabel("", SwingConstants.CENTER);
    private JLabel jlbl2 = new JLabel("", SwingConstants.CENTER);
    // For alarm
    private JLabel jalarmLbl1 = new JLabel("", SwingConstants.CENTER); // H
    private JLabel jalarmLbl2 = new JLabel("", SwingConstants.CENTER); // M
    private JLabel jalarmLbl3 = new JLabel("", SwingConstants.CENTER); // S
    private JLabel jalarmLbl4 = new JLabel("", SwingConstants.CENTER); // All Alarms
    private JTextField jtextField1 = new JTextField(2); // Hour textfield
    private JTextField jtextField2 = new JTextField(2); // Min textfield
    private JTextField jtextField3 = new JTextField(2); // Sec textfield
    private ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    private JButton jsetAlarmBtn = new JButton("Set");
    // Main GUI Components
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Font font60 = new Font("Courier New", Font.BOLD, 60);
    private Font font50 = new Font("Courier New", Font.BOLD, 50);
    // Clock object attributes
    private Date beginDaylightSavingsTimeDate;
    private Date endDaylightSavingsTimeDate;
    private Calendar calendar;
    private int seconds;
    private int minutes;
    private int hours;
    private Time.AMPM ampm;
    private Time.Day day;
    private int date;
    private Time.Month month;
    private int year;
    private String hoursAsStr = "", minutesAsStr = "", secondsAsStr = "";
    private ClockFace clockFace;
    private boolean leapYear;
    private boolean daylightSavingsTime;
    private boolean dateChanged;
    private boolean home = true, alarm= false, timer = false;
    private boolean showFullDate = false;
    private boolean showPartialDate = false;
    private boolean showMilitaryTime = false;
    // Getters/Issers
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
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
    public boolean isAlarm() { return this.alarm; }
    public boolean isTimer() { return this.timer; }
    public boolean isShowFullDate() { return this.showFullDate; }
    public boolean isShowPartialDate() { return this.showPartialDate; }
    public boolean isShowMilitaryTime() { return this.showMilitaryTime; }
    // Setters
    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    private void setBeginDaylightSavingsTimeDate(Date beginDaylightSavingsTimeDate) { this.beginDaylightSavingsTimeDate = beginDaylightSavingsTimeDate; }
    private void setEndDaylightSavingsTimeDate(Date endDaylightSavingsTimeDate) { this.endDaylightSavingsTimeDate = endDaylightSavingsTimeDate; }
    private void setCalendar(Calendar calendar) { this.calendar = calendar; }
    private void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) this.secondsAsStr = "0"+this.seconds;
        else this.secondsAsStr = Integer.toString(this.seconds);
    }
    private void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) this.minutesAsStr = "0"+this.minutes;
        else this.minutesAsStr = Integer.toString(this.minutes);
    }
    private void setHours(int hours) {
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
    private void setAMPM(Time.AMPM ampm) { this.ampm = ampm; }
    private void setDay(Time.Day day) { this.day = day; }
    private void setDate(int date) { this.date = date; }
    private void setMonth(Time.Month month) { this.month = month; }
    private void setYear(int year) { this.year = year; }
    private void setHoursAsStr(String hoursAsStr) { this.hoursAsStr = hoursAsStr; }
    private void setMinutesAsStr(String minutesAsStr) { this.minutesAsStr = minutesAsStr; }
    private void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; }
    // setDateAsStr()
    // setFullDateAsStr()
    // setMilitaryTimeAsStr()
    // setPartialTimeAsStr()
    private void setClockFace(ClockFace clockFace) { this.clockFace = clockFace; }
    private void setLeapYear(boolean leapYear) { this.leapYear = leapYear; }
    private void setDaylightSavingsTime(boolean daylightSavingsTime) { this.daylightSavingsTime = daylightSavingsTime; }
    private void setDateChanged(boolean dateChanged) { this.dateChanged = dateChanged; }
    private void setHome(boolean home) { this.home = home; }
    private void setAlarm(boolean alarm) { this.alarm = alarm; }
    private void setTimer(boolean timer) { this.timer = timer; }
    private void setShowFullDate(boolean showFullDate) { this.showFullDate = showFullDate; }
    private void setShowPartialDate(boolean showPartialDate) { this.showPartialDate = showPartialDate; }
    private void setShowMilitaryTime(boolean showMilitaryTime) { this.showMilitaryTime = showMilitaryTime; }
    // Helper methods
    public void setClockValues(Time.AMPM time, boolean showMilitaryTime) {
        if (time == null && showMilitaryTime == false) {}

        if (time == Time.AMPM.AM && showMilitaryTime) // Daytime and we show Military v2.Time
        {
            if (getHours() == 12) setHours(0);
            else setHours(getHours());
        }
        else if (time == Time.AMPM.AM && !showMilitaryTime) // DayTime and we do not show Military v2.Time
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
        else if (time == Time.AMPM.PM && !showMilitaryTime) // NightTime and we do not show Military v2.Time
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
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
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
    private void setMonthAsTime() throws ParseException
    {
        setMonth(convertMonthFromIntToTimeMonth(getCalendar().get(Calendar.MONTH)+1));
        setDay(convertIntToTimeDay(getCalendar().get(Calendar.DAY_OF_WEEK)));
        setDate(getCalendar().get(Calendar.DAY_OF_MONTH));
        setYear(getCalendar().get(Calendar.YEAR));
        setHours(getCalendar().get(Calendar.HOUR));
        setMinutes(getCalendar().get(Calendar.MINUTE));
        setSeconds(getCalendar().get(Calendar.SECOND));
        setAMPM(getCalendar().get(Calendar.AM_PM) == Calendar.PM ? Time.AMPM.PM : Time.AMPM.AM);
        setDaylightSavingsTimeDates();
        setLeapYear(isALeapYear(getYear()));
        setDaylightSavingsTime(isTodayDaylightSavingsTime());
    }
    /*
     *
     * Method to update clock because of lost seconds
     */
    public void updateAllClockValues() throws ParseException
    {
        getCalendar().setTime(new Date());
        setMonthAsTime();
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
    public void updateJLabels(int seconds, int minutes, int hours)
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
        setClockValues(getAMPM(), isShowMilitaryTime());
        if (isDateChanged())
        {
            int month = 0;
            try {
                month = convertFromTimeMonthToInt(getMonth());
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
            getCalendar().setTime(updatedDate);
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
    } // update jLabels
    public void updateJLabels()
    {
        setSeconds(getSeconds()+1);
        setSecondsAsStr(getSeconds() <= 9 ? "0"+getSeconds() : Integer.toString(getSeconds()));
        if (getSeconds() == 60)
        {
            setSeconds(0);
            setSecondsAsStr("00");
            setMinutes(getMinutes()+1);
            setMinutesAsStr(getMinutes() <= 9 ? "0"+getMinutes() : Integer.toString(getMinutes()));
            if (getMinutes() == 60)
            {
                setMinutes(0);
                setMinutesAsStr("00");
                setHours(getHours()+1);
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
                    setHoursAsStr(Integer.toString(getHours()));
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
        setClockValues(getAMPM(), isShowMilitaryTime());
        if (isDateChanged())
        {
            int month = 0;
            try {
                month = convertFromTimeMonthToInt(getMonth());
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
            getCalendar().setTime(updatedDate);
            setDay(convertIntToTimeDay(getCalendar().get(Calendar.DAY_OF_WEEK)));
        }
        if (isDaylightSavingsTime())
        {
            if (getMonth() == Time.Month.MARCH && getAMPM().getStrValue().equals(Time.AMPM.AM.strValue))
            {
                setHours(3);
                setDaylightSavingsTime(false);
            }
            else if (getMonth() == Time.Month.NOVEMBER && getAMPM().getStrValue().equals(Time.AMPM.AM.strValue))
            { // && daylightSavingsTime
                setHours(1);
                setDaylightSavingsTime(false);
            }
        }
    }
    // TODO: refactor and organize code
    public String defaultText(int labelVersion, boolean isALeapYear)
    {
        String defaultText = "";
        if (labelVersion == 1)
        {
            if (isShowFullDate() && !isShowPartialDate()) defaultText += getFullDateAsStr();
            else if (isShowPartialDate() && !isShowFullDate()) defaultText += getPartialDateAsStr();
            else defaultText += getDateAsStr();
        }
        else if (labelVersion == 2) {
            if (!isShowMilitaryTime()) defaultText += getTimeAsStr();
            else if (isShowMilitaryTime()) defaultText += getMilitaryTimeAsStr();
        }
        else if (labelVersion == 3 || labelVersion == 5) {
            defaultText = labelVersion == 3 ? "H" : "M";
        }
        else if (labelVersion == 6) {
            defaultText = "T";
        }
        else if (labelVersion == 4) {
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
    public int convertFromTimeMonthToInt(Time.Month month) throws InvalidInputException
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
            default: throw new InvalidInputException(Time.Month.ERR.strValue);
        }
    }
    public Time.Month convertMonthFromIntToTimeMonth(int thisMonth)
    {
        switch (thisMonth) {
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
            default: return Time.Month.ERR;
        }
    }
    /**
     * Constructor for objects of class v2.Clockv2
     */
    public Clock() throws ParseException
    {
        super();
        setResizable(true);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setGUI();
        setMenuBar();
        setClockFace(ClockFace.StartFace);
        updateClockFace(false);
    }
    public Clock(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int date, int year, Time.AMPM time) throws ParseException
    {
        super();
        setResizable(true);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setGUI(hours, minutes, seconds, month, day, date, year, time);
        setMenuBar();
        setClockFace(ClockFace.StartFace);
        pack();
    }
    // Constructor methods
    public void setGUI(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int  date, int year, Time.AMPM ampm) throws ParseException
    {
        Date definedDate = null;
        try {
            if (date < 10)
                definedDate = sdf.parse(convertFromTimeMonthToInt(month)+"-0"+date+"-"+year);
            else
                definedDate = sdf.parse(convertFromTimeMonthToInt(month)+"-"+date+"-"+year);
        }
        catch (ParseException | InvalidInputException e)
        {
            String dateStr = date <= 9 ? "0"+date : Integer.toString(date);
            System.err.println("Error! Couldn't create the date using month=["+month+"], date=["+dateStr+
                    "], year=["+year+"]");
        }
        setCalendar(Calendar.getInstance());
        getCalendar().setTime(definedDate);
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
        jlbl1.setFont(new Font("Courier New", Font.BOLD, 50));
        jlbl2.setFont(new Font("Courier New", Font.BOLD, 60));
        jlbl1.setForeground(Color.WHITE);
        jlbl2.setForeground(Color.WHITE);
        jlbl1.setText(defaultText(lbl1, isALeapYear(getYear())));
        jlbl2.setText(defaultText(lbl2, isALeapYear(getYear())));
        //addComponent(jlbl1, 0,0,1,1, 0,0);
        //addComponent(jlbl2, 1,0,1,1, 0,0);
    }
    public void setGUI() throws ParseException
    {
        setCalendar(Calendar.getInstance());
        getCalendar().setTime(new Date());
        setMonthAsTime();
        setDateChanged(false);
        setShowFullDate(false);
        setShowPartialDate(false);
        setShowMilitaryTime(false);
    }
    public void setMenuBar()
    {
        UIManager.put("MenuItem.background", Color.BLACK);
        class BackgroundMenuBar extends JMenuBar {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        }
        BackgroundMenuBar menuBar = new BackgroundMenuBar();
        menuBar.setForeground(Color.WHITE);
        menuBar.setBackground(Color.BLACK); // added on mac

        // Menu Options
        JMenu settings = new JMenu("Settings");
        settings.setOpaque(false);
        settings.setForeground(Color.WHITE);

        JMenu features = new JMenu("Features");
        features.setOpaque(false);
        features.setForeground(Color.WHITE);

        // Menu Items for Settings
        JMenuItem militaryTimeSetting = new JMenuItem(SHOW + SPACE + MILITARY_TIME_SETTING);
        JMenuItem partialDateSetting = new JMenuItem(SHOW + SPACE + PARTIAL_TIME_SETTING);
        JMenuItem fullDateSetting = new JMenuItem(SHOW + SPACE + FULL_TIME_SETTING);

        militaryTimeSetting.addActionListener(action -> {
            if (isShowMilitaryTime() == true) {
                setShowMilitaryTime(false);
                militaryTimeSetting.setText(HIDE + SPACE + MILITARY_TIME_SETTING);
            } else {
                setShowMilitaryTime(true);
                militaryTimeSetting.setText(SHOW + SPACE + STANDARD_TIME_SETTING);
            }
            updateClockFace(true);
            pack();
        });
        militaryTimeSetting.setForeground(Color.WHITE); // added on mac

        fullDateSetting.addActionListener(action -> {
            if (isShowFullDate()) {
                setShowFullDate(false);
                setShowPartialDate(false);
                fullDateSetting.setText(SHOW + SPACE + FULL_TIME_SETTING);
                partialDateSetting.setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            else {
                setShowFullDate(true);
                setShowPartialDate(false);
                fullDateSetting.setText(HIDE + SPACE + FULL_TIME_SETTING);
                partialDateSetting.setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            updateClockFace(true);
            pack();
        });
        fullDateSetting.setForeground(Color.WHITE); // added on mac

        // new: added on Raspberry PI
        partialDateSetting.addActionListener(action -> {
            if (isShowPartialDate()) {
                setShowPartialDate(false);
                setShowFullDate(false);
                partialDateSetting.setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
                fullDateSetting.setText(SHOW + SPACE + FULL_TIME_SETTING);
            }
            else {
                setShowPartialDate(true);
                setShowFullDate(false);
                partialDateSetting.setText(HIDE + SPACE + PARTIAL_TIME_SETTING);
                fullDateSetting.setText(SHOW + SPACE + FULL_TIME_SETTING);
            }
            updateClockFace(true);
            pack();
        });
        partialDateSetting.setForeground(Color.WHITE); // added on Raspberry PI

        // Menu Items for Features
        JMenuItem clockFeature = new JMenuItem("View Clock");
        JMenuItem alarmFeature = new JMenuItem("View Alarms");

        clockFeature.addActionListener(action -> {
            if (getClockFace() != ClockFace.StartFace) {
                setClockFace(ClockFace.StartFace);
            }
            updateClockFace(true);
        });
        clockFeature.setForeground(Color.WHITE);

        alarmFeature.addActionListener(action -> {
            if (getClockFace() != ClockFace.AlarmFace) {
                setClockFace(ClockFace.AlarmFace);
            }
            updateClockFace(true);
        });
        alarmFeature.setForeground(Color.WHITE);

        // Add menu items to menu
        settings.add(militaryTimeSetting);
        settings.add(fullDateSetting);
        settings.add(partialDateSetting);

        features.add(clockFeature);
        features.add(alarmFeature);

        // Add menu to menuBar
        menuBar.add(settings);
        menuBar.add(features);

        this.setJMenuBar(menuBar);
    }
    public void updateClockFace(boolean dothis)
    {
        if (dothis)
        {
            this.getContentPane().remove(jlbl1);
            this.getContentPane().remove(jlbl2);
            this.getContentPane().remove(jalarmLbl1);
            this.getContentPane().remove(jalarmLbl2);
            this.getContentPane().remove(jalarmLbl3);
            this.getContentPane().remove(jalarmLbl4);
            this.getContentPane().remove(jsetAlarmBtn);
            this.getContentPane().remove(jtextField1);
            this.getContentPane().remove(jtextField2);
            this.getContentPane().remove(jtextField3);
            this.getContentPane().remove(scrollPane);
        }
        // logic should be done in menu
        if (isShowFullDate() && !isShowPartialDate())
        {
            jlbl1.setFont(font50);
            jlbl2.setFont(font50);
        }
        else if (!isShowFullDate() && isShowPartialDate())
        {
            jlbl1.setFont(font60);
            jlbl2.setFont(font60);
        }
        else
        {
            jlbl1.setFont(font60);
            jlbl2.setFont(font60);
        }
        // end in menu
        jalarmLbl1.setFont(font60);
        jalarmLbl2.setFont(font60);
        jalarmLbl3.setFont(font60);
        jalarmLbl4.setFont(font60);
        jlbl1.setForeground(Color.WHITE);
        jlbl2.setForeground(Color.WHITE);
        jalarmLbl1.setForeground(Color.WHITE);
        jalarmLbl2.setForeground(Color.WHITE);
        jalarmLbl3.setForeground(Color.WHITE);
        jalarmLbl4.setForeground(Color.WHITE);
        jtextField1.setVisible(true);
        jtextField1.setEnabled(true);
        jtextField2.setEnabled(false);
        jtextField3.setEnabled(false);
        jtextField1.setFocusable(true);
        if (getClockFace() == ClockFace.StartFace)
        {
            jlbl1.setText(defaultText(lbl1, isALeapYear(year)));
            jlbl2.setText(defaultText(lbl2, isALeapYear(year)));
            addComponent(jlbl1, 0,0,1,1, 0,0);
            addComponent(jlbl2, 1,0,1,1, 0,0);
        }
        else if (getClockFace() == ClockFace.AlarmFace)
        {
            jalarmLbl1.setText(defaultText(3, isALeapYear(getYear()))); // H
            addComponent(jalarmLbl1, 0,0,1,1, 0,0);
            addComponent(jtextField1, 0,1,1,1, 0,0); // Textfield
            //do { jtextField1.requestFocusInWindow(); } while (jtextField1.getText().length() <= 2);
            jalarmLbl2.setText(defaultText(5, isALeapYear(getYear()))); // M
            addComponent(jalarmLbl2, 0,2,1,1, 0,0);
            addComponent(jtextField2, 0,3,1,1, 0,0); // Textfield
            //do { jtextField2.requestFocusInWindow(); } while (jtextField2.getText().length() <= 2);
            jalarmLbl4.setText(defaultText(6, isALeapYear(getYear()))); // T
            addComponent(jalarmLbl4, 0,4,1,1, 0,0);
            addComponent(jtextField3, 0,5,1,1, 0,0); // Textfield
            addComponent(jsetAlarmBtn, 0,6,1,1, 0,0); // Set v2.Alarm button
            jalarmLbl3.setText(defaultText(4, isALeapYear(getYear()))); // All Alarms ...
            addComponent(jalarmLbl3, 1,0,7,1, 0,0);
            // TODO: add new alarm and continue to display all alarms created
        }
        this.repaint();
    }
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady)
    {
        this.constraints.gridx = gridx;
        this.constraints.gridy = gridy;
        this.constraints.gridwidth = (int)Math.ceil(gwidth);
        this.constraints.gridheight = (int)Math.ceil(gheight);
        this.constraints.ipadx = ipadx;
        this.constraints.ipady = ipady;
        this.constraints.fill = GridBagConstraints.NONE;
        this.constraints.insets = new Insets(0,0,0,0);
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }
    /**
     * The purpose of tick is to start the clock normally.
     */
    public void tick()
    {
        if (getCalendar() != null)
        {
            updateJLabels();
        }
        else
        {
            tick(1, 1, 1);
        }
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
        try {
            updateJLabels(seconds, minutes, hours);
            //Updates the clock daily to keep time current
            if (!isShowMilitaryTime())
            {
                if (getTimeAsStr().equals("04:20:00 " + Time.AMPM.AM.strValue))
                {
                    updateAllClockValues();
                    updateClockFace(false);
                }
            }
            else
            {
                if (getMilitaryTimeAsStr().equals("0420 hours 00"))
                {
                    updateAllClockValues();
                    updateClockFace(false);
                }
            }
            updateClockFace(true);
        } catch (Exception e) {
            System.err.println("Error! Clock had an exception when performing tick: " + e.getMessage());
        }
    }
    public static void main(String[] args) throws ParseException, InterruptedException {
        Clock clock = new Clock();
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        clock.setSize(defaultSize);
        clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clock.setBounds(200, 200, 700, 300);
        while (true) {
            clock.tick();
            Thread.sleep(1000);
        }
    }
}