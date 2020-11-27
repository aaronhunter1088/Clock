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
    private final int lbl1 = 1;
    private final int lbl2 = 2;
    // For clock
    private JLabel jlbl1 = new JLabel("", SwingConstants.CENTER);
    private JLabel jlbl2 = new JLabel("", SwingConstants.CENTER);
    // For alarm
    private JLabel jalarmLbl1 = new JLabel("", SwingConstants.CENTER);
    private JLabel jalarmLbl2 = new JLabel("", SwingConstants.CENTER);
    private JLabel jalarmLbl3 = new JLabel("", SwingConstants.CENTER);
    private JLabel jalarmLbl4 = new JLabel("", SwingConstants.CENTER);
    private JTextField jtextField1 = new JTextField(2); // Hour
    private JTextField jtextField2 = new JTextField(2); // Min
    private JTextField jtextField3 = new JTextField(2); // Sec
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
    public Time.AMPM getAmpm() { return ampm; }
    public Time.Day getDay() { return day; }
    public int getDate() { return date; }
    public Time.Month getMonth() { return month; }
    public int getYear() { return year; }
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
            if (this.hours == 12) setHours(0);
            else setHours(this.hours);
        }
        else if (time == Time.AMPM.AM && !showMilitaryTime) // DayTime and we do not show Military v2.Time
        {
            if (this.hours == 0) setHours(12);
            else setHours(this.hours);
        }
        else if (time == Time.AMPM.PM && showMilitaryTime) // NightTime and we show Military v2.Time
        {
            if (this.hours == 24) setHours(0);
            else if (this.hours < 12) setHours(this.hours + 12);
            else setHours(this.hours);
        }
        else if (time == Time.AMPM.PM && !showMilitaryTime) // NightTime and we do not show Military v2.Time
        {
            if (this.hours > 12) setHours(this.hours - 12);
        }
    }
    public static boolean isALeapYear(int year) {
        boolean leap = false;
        if (year % 4 == 0) {
            leap = true;
            if (Integer.toString(year).substring(2).equals("00")) {
                if (year % 400 == 0) {
                    leap = true;
                } else {
                    leap = false;
                }
            }
        }
        return leap;
    }
    public void setDaylightSavingsTimeDates() throws ParseException {
        DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        beginDaylightSavingsTimeDate = sdf.parse("03-01-"+Integer.toString(year));
        endDaylightSavingsTimeDate = sdf.parse("11-01-"+Integer.toString(year));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDaylightSavingsTimeDate);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1: beginDaylightSavingsTimeDate = sdf.parse("03-08-"+Integer.toString(year));
                // endDaylightSavingsTimeDate already set
                break;
            case 2: beginDaylightSavingsTimeDate = sdf.parse("03-14-"+Integer.toString(year));
                endDaylightSavingsTimeDate = sdf.parse("11-07-"+Integer.toString(year)); break;
            case 3: beginDaylightSavingsTimeDate = sdf.parse("03-13-"+Integer.toString(year));
                endDaylightSavingsTimeDate = sdf.parse("11-06-"+Integer.toString(year)); break;
            case 4: beginDaylightSavingsTimeDate = sdf.parse("03-12-"+Integer.toString(year));
                endDaylightSavingsTimeDate = sdf.parse("11-05-"+Integer.toString(year)); break;
            case 5: beginDaylightSavingsTimeDate = sdf.parse("03-11-"+Integer.toString(year));
                endDaylightSavingsTimeDate = sdf.parse("11-04-"+Integer.toString(year)); break;
            case 6: beginDaylightSavingsTimeDate = sdf.parse("03-10-"+Integer.toString(year));
                endDaylightSavingsTimeDate = sdf.parse("11-03-"+Integer.toString(year)); break;
            case 7: beginDaylightSavingsTimeDate = sdf.parse("03-09-"+Integer.toString(year));
                endDaylightSavingsTimeDate = sdf.parse("11-02-"+Integer.toString(year)); break;
        }
    }
    public boolean isTodayDaylightSavingsTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDaylightSavingsTimeDate);
        if (getDateAsStr().equals(
                Integer.toString(calendar.get(Calendar.MONTH) + 1)+"/"+
                        Integer.toString(calendar.get(Calendar.DATE))+"/"+
                        Integer.toString(calendar.get(Calendar.YEAR)) ) )  {
            daylightSavingsTime = true;
            return true;
        } else {
            calendar.setTime(endDaylightSavingsTimeDate);
            if (getDateAsStr().equals(
                    Integer.toString(calendar.get(Calendar.MONTH) +1)+"/"+
                            Integer.toString(calendar.get(Calendar.DATE))+"/"+
                            Integer.toString(calendar.get(Calendar.YEAR)) )
                    && !this.daylightSavingsTime) {
                daylightSavingsTime = true;
                return true;
            }
        }
        daylightSavingsTime = false;
        return false;
    }
    private void setMonthAsTime() throws ParseException {
        month = convertMonthToStr(calendar.get(Calendar.MONTH)+1);
        this.day = convertDayFromIntToTimeDay(this.calendar.get(Calendar.DAY_OF_WEEK));
        this.date = this.calendar.get(Calendar.DAY_OF_MONTH);
        this.year = this.calendar.get(Calendar.YEAR);
        setHours(this.calendar.get(Calendar.HOUR));
        setMinutes(this.calendar.get(Calendar.MINUTE));
        setSeconds(this.calendar.get(Calendar.SECOND));
        this.ampm = this.calendar.get(Calendar.AM_PM) == Calendar.PM ? Time.AMPM.PM : Time.AMPM.AM;
        setDaylightSavingsTimeDates();
        this.leapYear = isALeapYear(year);
        this.daylightSavingsTime = isTodayDaylightSavingsTime();
    }
    /*
     *
     * Method to update clock because of lost seconds
     */
    public void updateAllClockValues() throws ParseException {
        this.calendar.setTime(new Date());
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
    public void updateJLabels(int seconds, int minutes, int hours) {
        this.seconds += seconds;
//        this.seconds += 1;
        this.secondsAsStr = this.seconds <= 9 ? "0"+Integer.toString(this.seconds) : Integer.toString(this.seconds);
        if (this.seconds == 60) {
            this.seconds = 0;
            this.secondsAsStr = "00";
            this.minutes += minutes;
//            this.minutes += 1;
            this.minutesAsStr = this.minutes <= 9 ? "0"+Integer.toString(this.minutes) : Integer.toString(this.minutes);
            if (this.minutes == 60) {
                this.minutes = 0;
                this.minutesAsStr = "00";
                this.hours += hours;
//                this.hours += 1;
                if (this.hours == 12 && this.minutes == 0 && this.seconds == 0 && !showMilitaryTime) {
                    this.hours = 12;
                    this.hoursAsStr = "12";
                    if (ampm == Time.AMPM.PM) {
                        this.ampm = Time.AMPM.AM;
                        this.dateChanged = true;
                    } else {
                        this.dateChanged = false;
                        this.ampm = Time.AMPM.PM;
                    }
                } else if (this.hours == 13 && !showMilitaryTime) {
                    this.hours = 1;
                    this.hoursAsStr = "01";
                    this.dateChanged = false;
                } else if (this.hours == 24 && this.minutes == 0 && this.seconds == 0 && showMilitaryTime) {
                    this.hours = 0;
                    this.hoursAsStr = "00";
                    this.ampm = Time.AMPM.AM;
                    this.dateChanged = true;
                } else if (this.hours >= 13 && showMilitaryTime) {
                    this.hoursAsStr = Integer.toString(this.hours);
                    this.dateChanged = false;
                } else {
                    setHours(this.hours);
                }
            }
        }
        else { this.dateChanged = false; }

        if (this.dateChanged) {
            this.date += 1;
            this.daylightSavingsTime = isTodayDaylightSavingsTime();
        }
        switch (month) {
            case JANUARY: {
                if (this.date == 31 && dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.FEBRUARY;
                }
                break;
            }
            case FEBRUARY: {
                if ((this.date == 28 || this.date == 30) && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.MARCH;
                } else if (this.date == 28 && this.leapYear && this.dateChanged) {
                    this.date = 29;
                    this.month = Time.Month.FEBRUARY;
                }
                break;
            }
            case MARCH: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.APRIL;
                }
                break;
            }
            case APRIL: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.MAY;
                }
                break;
            }
            case MAY: {
                if (this.date == 32 && this.dateChanged) {
                    this.date= 1;
                    this.month = Time.Month.JUNE;
                }
                break;
            }
            case JUNE: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.JULY;
                }
                break;
            }
            case JULY: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.AUGUST;
                }
                break;
            }
            case AUGUST: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.SEPTEMBER;
                }
                break;
            }
            case SEPTEMBER: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.OCTOBER;
                }
                break;
            }
            case OCTOBER: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.NOVEMBER;
                }
                break;
            }
            case NOVEMBER: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.DECEMBER;
                }
                break;
            }
            case DECEMBER: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = Time.Month.JANUARY;
                    this.year += 1;
                }
                break;
            }
            default : {

            }
        }
        setClockValues(this.ampm, this.showMilitaryTime);
        if (this.dateChanged) {
            //System.out.println("day was: " + convertDayToStr(this.calendar.get(Calendar.DAY_OF_WEEK)));
            DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            int month = 0;
            try {
                month = convertMonthToInt(this.month);
            } catch (InvalidInputException iie) {
                System.out.println(iie.getMessage());
            }
            int date = this.date;
            String monthStr = month <= 9 ? "0"+Integer.toString(month) : Integer.toString(month);
            String dateStr = date <= 9 ? "0"+Integer.toString(date) : Integer.toString(date);
            Date updatedDate = null;
            try {
                updatedDate = sdf.parse(monthStr+"-"+dateStr+"-"+Integer.toString(this.year));
            } catch (ParseException e) {}
            this.calendar.setTime(updatedDate);
            this.day = convertDayFromIntToTimeDay(this.calendar.get(Calendar.DAY_OF_WEEK));
            //System.out.println("day is now: " + convertDayToStr(this.calendar.get(Calendar.DAY_OF_WEEK)));
        }
        if (this.daylightSavingsTime) {
            if (calendar.get(Calendar.MONTH) == 2 && this.ampm.getStrValue().equals("AM")) {
                setHours(3);
                this.daylightSavingsTime = false;
            } else if (calendar.get(Calendar.MONTH) == 10 && this.ampm.getStrValue().equals("AM")) { // && daylightSavingsTime
                setHours(1);
                this.daylightSavingsTime = false;
            }
        }
        //System.out.println(defaultText(lbl2, isALeapYear(this.year)));
    } // update jLabels
    public String defaultText(int labelVersion, boolean isALeapYear) {
        String defaultText = "";
        if (labelVersion == 1) {
            if (showFullDate && !showPartialDate) defaultText += getFullDateAsStr();
            else if (showPartialDate && !showFullDate) defaultText += getPartialDateAsStr();
            else defaultText += getDateAsStr();
        } else if (labelVersion == 2) {
            if (!showMilitaryTime) defaultText += getTimeAsStr();
            else if (showMilitaryTime) defaultText += getMilitaryTimeAsStr();
        } else if (labelVersion == 3 || labelVersion == 5) {
            defaultText = labelVersion == 3 ? "H" : "M";
        } else if (labelVersion == 6) {
            defaultText = "T";
        }   else if (labelVersion == 4) {
            defaultText = "All Alarms";
        }
        return defaultText;
    }
    public Time.Day convertDayFromIntToTimeDay(int thisDay) {
        switch(thisDay) {
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
    public int convertMonthToInt(Time.Month month) throws InvalidInputException {
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
            default: throw new InvalidInputException("Unknown month");
        }
    }
    public Time.Month convertMonthToStr(int thisMonth) {
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
     * The purpose of tick is to start the clock normally.
     */
    public void tick() {
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
    public void tick(int seconds, int minutes, int hours) {
        try {
            updateJLabels(seconds, minutes, hours);
            //Updates the clock daily to keep time current
            if (!isShowMilitaryTime()) {
                if (getTimeAsStr().equals("04:20:00 AM")) {
                    updateAllClockValues();
                    updateClockFace(false);
                }
            } else {
                if (getMilitaryTimeAsStr().equals("0420 hours 00")) {
                    updateAllClockValues();
                    updateClockFace(false);
                }
            }
            updateClockFace(true);
        } catch (Exception e) {

        }
    }
    /**
     * Constructor for objects of class v2.Clockv2
     */
    public Clock() throws ParseException {
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
    public Clock(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int date, int year, Time.AMPM time) throws ParseException {
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
    public void setGUI(int hours, int minutes, int seconds, Time.Month month, Time.Day day, int  date, int year, Time.AMPM time) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Date definedDate = null;
        try {
            if (date < 10)
                definedDate = sdf.parse(convertMonthToInt(month)+"-0"+date+"-"+year);
            else
                definedDate = sdf.parse(convertMonthToInt(month)+"-"+date+"-"+year);
        } catch (ParseException | InvalidInputException e) {
        }
        calendar = Calendar.getInstance();
        calendar.setTime(definedDate);
        this.month = month;
        this.day = day;
        this.date = date;
        this.year =year;
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
        this.ampm = time;
        setDaylightSavingsTimeDates();
        this.daylightSavingsTime = isTodayDaylightSavingsTime();
        jlbl1.setFont(new Font("Courier New", Font.BOLD, 50));
        jlbl2.setFont(new Font("Courier New", Font.BOLD, 60));
        jlbl1.setForeground(Color.WHITE);
        jlbl2.setForeground(Color.WHITE);
        jlbl1.setText(defaultText(lbl1, isALeapYear(year)));
        jlbl2.setText(defaultText(lbl2, isALeapYear(year)));
        //addComponent(jlbl1, 0,0,1,1, 0,0);
        //addComponent(jlbl2, 1,0,1,1, 0,0);
    }
    public void setGUI() throws ParseException {
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(new Date());
        setMonthAsTime();
        this.dateChanged = false;
        setShowFullDate(false);
        setShowPartialDate(false);
        setShowMilitaryTime(false);
    }
    public void setMenuBar() {
        UIManager.put("MenuItem.background", Color.BLACK);
        class BackgroundMenuBar extends JMenuBar {
            private static final long serialVersionUID = 1L;
            Color bgColor=Color.BLACK;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(bgColor);
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
            if (this.showFullDate) {
                this.showFullDate = false;
                this.showPartialDate = false;
                fullDateSetting.setText(SHOW + SPACE + FULL_TIME_SETTING);
                partialDateSetting.setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            else {
                this.showFullDate = true;
                this.showPartialDate = false;
                fullDateSetting.setText(HIDE + SPACE + FULL_TIME_SETTING);
                partialDateSetting.setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            updateClockFace(true);
            pack();
        });
        fullDateSetting.setForeground(Color.WHITE); // added on mac

        // new: added on Raspberry PI
        partialDateSetting.addActionListener(action -> {
            if (this.showPartialDate) {
                this.showPartialDate = false;
                partialDateSetting.setText("Show partial date");
            }
            else {
                this.showPartialDate = true;
                this.showFullDate = false;
                partialDateSetting.setText("Hide partial date");
                fullDateSetting.setText("Show full date");
            }
            updateClockFace(true);
            pack();
        });
        partialDateSetting.setForeground(Color.WHITE); // added on Raspberry PI

        // Menu Items for Features
        JMenuItem clockFeature = new JMenuItem("View Clock");
        JMenuItem alarmFeature = new JMenuItem("View Alarms");

        clockFeature.addActionListener(action -> {
            if (this.clockFace != ClockFace.StartFace) {
                this.clockFace = ClockFace.StartFace;
            }
            updateClockFace(true);
        });
        clockFeature.setForeground(Color.WHITE);

        alarmFeature.addActionListener(action -> {
            if (this.clockFace != ClockFace.AlarmFace) {
                this.clockFace = ClockFace.AlarmFace;
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
    public void updateClockFace(boolean dothis) {
        if (dothis) {
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
        if (this.showFullDate && !this.showPartialDate) {
            jlbl1.setFont(font50);
            jlbl2.setFont(font50);
        } else if (!this.showFullDate && this.showPartialDate) {
            jlbl1.setFont(font60);
            jlbl2.setFont(font60);
        } else {
            jlbl1.setFont(font60);
            jlbl2.setFont(font60);
        }
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
        if (this.clockFace == ClockFace.StartFace) {
            jlbl1.setText(defaultText(lbl1, isALeapYear(year)));
            jlbl2.setText(defaultText(lbl2, isALeapYear(year)));
            addComponent(jlbl1, 0,0,1,1, 0,0);
            addComponent(jlbl2, 1,0,1,1, 0,0);
        } else if (this.clockFace == ClockFace.AlarmFace) {
            jalarmLbl1.setText(defaultText(3, isALeapYear(year))); // H
            addComponent(jalarmLbl1, 0,0,1,1, 0,0);
            addComponent(jtextField1, 0,1,1,1, 0,0); // Textfield
            //do { jtextField1.requestFocusInWindow(); } while (jtextField1.getText().length() <= 2);
            jalarmLbl2.setText(defaultText(5, isALeapYear(year))); // M
            addComponent(jalarmLbl2, 0,2,1,1, 0,0);
            addComponent(jtextField2, 0,3,1,1, 0,0); // Textfield
            //do { jtextField2.requestFocusInWindow(); } while (jtextField2.getText().length() <= 2);
            jalarmLbl4.setText(defaultText(6, isALeapYear(year))); // T
            addComponent(jalarmLbl4, 0,4,1,1, 0,0);
            addComponent(jtextField3, 0,5,1,1, 0,0); // Textfield
            addComponent(jsetAlarmBtn, 0,6,1,1, 0,0); // Set v2.Alarm button
            jalarmLbl3.setText(defaultText(4, isALeapYear(year))); // All Alarms ...
            addComponent(jalarmLbl3, 1,0,7,1, 0,0);
            // TODO: add new alarm and continue to display all alarms created
        }
        this.repaint();
    }
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady) {
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