package v1;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
/**
 * A simple application which displays the time and date. The time
 * can be view in military time or not, and the date fully expressed
 * or not.
 * 
 * @author Michael Ball 
 * @version 1
 */
public class Clock extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    private final String JANUARY = "January";
    private final String FEBRUARY = "February";
    private final String MARCH = "March";
    private final String APRIL = "April";
    private final String MAY = "May";
    private final String JUNE = "June";
    private final String JULY = "July";
    private final String AUGUST = "August";
    private final String SEPTEMBER = "September";
    private final String OCTOBER = "October";
    private final String NOVEMBER = "November";
    private final String DECEMBER = "December";
    private final int lbl1 = 1;
    private final int lbl2 = 2;
    private JLabel jlbl1 = new JLabel("", SwingConstants.CENTER);
    private JLabel jlbl2 = new JLabel("", SwingConstants.CENTER);
    private JLabel jalarmLbl1 = new JLabel("", SwingConstants.CENTER);
    private JLabel jalarmLbl2 = new JLabel("", SwingConstants.CENTER);
    private JLabel jalarmLbl3 = new JLabel("", SwingConstants.CENTER);
    private JLabel jalarmLbl4 = new JLabel("", SwingConstants.CENTER);
    private ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    private JTextField jtextField1 = new JTextField(2);
    private JTextField jtextField2 = new JTextField(2);
    private JTextField jtextField3 = new JTextField(2);
    private JButton jsetAlarmBtn = new JButton("Set");
    
    private GridBagLayout layout = new GridBagLayout();
    private GridBagConstraints constraints;
    private Font font60 = new Font("Courier New", Font.BOLD, 60);
    
    private Date beginDaylightSavingsTimeDate;
    private Date endDaylightSavingsTimeDate;
    protected Calendar calendar;
    protected String month;
    protected String day;
    protected int date;
    protected int year;
    protected int hours;
    protected int minutes;
    protected int seconds;
    protected boolean leapYear;
    protected boolean daylightSavingsTime;
    protected boolean dateChanged;
    protected boolean home = true, alarm= false, timer = false;
    protected boolean showFullDate = false;
    protected boolean showMilitaryTime = false;
    protected ClockFace clockFace;
    protected Time time;
    protected String hoursAsStr = "", minutesAsStr = "", secondsAsStr = "";
   
    public int getHours() { return hours; }
    private void setHours(int hours) {
        if (hours == 0 && !showMilitaryTime) {
            this.hours = 12;
        }
        else if (hours == 0 && showMilitaryTime) {
            this.hours = 0;
        }
        else 
            this.hours = hours;
        if (this.hours <= 9) this.hoursAsStr = "0"+Integer.toString(hours);
        else this.hoursAsStr = Integer.toString(hours);
    }
    
    public int getMinutes() { return minutes; }
    private void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) this.minutesAsStr = "0"+Integer.toString(minutes);
        else this.minutesAsStr = Integer.toString(minutes);
    }
    
    public int getSeconds() { return seconds; }
    private void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) this.secondsAsStr = "0"+Integer.toString(seconds);
        else this.secondsAsStr = Integer.toString(seconds);
    }
    
    private void setShowMilitaryTime(boolean showMilitaryTime) { this.showMilitaryTime = showMilitaryTime; }
    
    private void setClockFace(ClockFace clockFace) { this.clockFace = clockFace; }
    
    /**
     * Constructor for objects of class Clock
     */
    public Clock() throws ParseException {
        super();
        setResizable(true);
        setLayout(layout);
        constraints = new GridBagConstraints();
        setGUI();
        setMenuBar();
        setClockFace(ClockFace.StartFace);
        updateClockFace(false);
        
    }
    
    public Clock(int hours, int minutes, int seconds, String month, String day, int date, int year, Time time) throws ParseException {
        super();
        setResizable(true);
        setLayout(layout);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setGUI(hours, minutes, seconds, month, day, date, year, time);
        setMenuBar();
        setClockFace(ClockFace.StartFace);
        pack();
    }
    
    public Clock(int hours, int minutes, int seconds, Date today, Time time) throws ParseException {
        super();
        setResizable(true);
        setLayout(layout);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        String month = convertMonthToStr(cal.get(Calendar.MONTH)+1);
        String day = convertDayToStr(cal.get(Calendar.DAY_OF_WEEK));
        int date = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        setGUI(hours, minutes, seconds, month, day, date, year, time);
        setMenuBar();
        setClockFace(ClockFace.StartFace);
        pack();
    }
    
    public String getTimeAsStr() {
        return this.hoursAsStr+":"+this.minutesAsStr+":"+this.secondsAsStr+" "+this.time.getStrValue(); 
    }
    
    public Time getTime() { return time; }
    public String getMonth() { return month; }
    public int getDate() { return date; }
    public int getYear() { return year; }
    public String getDay() { return day; }
    
    public boolean isShowMilitaryTime() { return this.showMilitaryTime; }

    public String getMilitaryTimeAsStr() {
        return this.hoursAsStr + this.minutesAsStr + " hours " + this.secondsAsStr;
    }
    
    public String getDateAsStr() {
        try {
        	return Integer.toString(convertMonthToInt(this.month))+"/"+this.date+"/"+this.year;
        } catch (InvalidInputException iie) { 
        	System.out.println(iie.getMessage());
        	return null; 
        }
    }
    
    public String getFullDateAsStr() {
        return this.day+" "+this.month+" "+Integer.toString(this.date)+", "+this.year;
    }
    
    public ClockFace getClockFace() { return this.clockFace; }

    public void setGUI(int hours, int minutes, int seconds, String month, String day, int  date, int year, Time time) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Date definedDate = null;
        try {
            definedDate = sdf.parse(Integer.toString(convertMonthToInt(month))+"-0"+Integer.toString(date)+"-"+Integer.toString(year));
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
        this.time = time;
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
        this.month = convertMonthToStr(calendar.get(Calendar.MONTH)+1);
        this.day = convertDayToStr(this.calendar.get(Calendar.DAY_OF_WEEK));
        this.date = this.calendar.get(Calendar.DAY_OF_MONTH);
        this.year = this.calendar.get(Calendar.YEAR);
        setHours(this.calendar.get(Calendar.HOUR));
        setMinutes(this.calendar.get(Calendar.MINUTE));
        setSeconds(this.calendar.get(Calendar.SECOND));
        this.time = this.calendar.get(Calendar.AM_PM) == Calendar.PM ? Time.PM : Time.AM;
        setDaylightSavingsTimeDates();
        this.leapYear = isALeapYear(year);
        this.daylightSavingsTime = isTodayDaylightSavingsTime();
        this.dateChanged = false;
        this.showFullDate = false;
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
        
        // Menu Items for Settings
        JMenuItem militaryTimeSetting = new JMenuItem("Show Military Time");
        militaryTimeSetting.addActionListener(action -> {
            if (isShowMilitaryTime() == true) {
            	setShowMilitaryTime(false);
                militaryTimeSetting.setText("Show military time");
            } else {
                setShowMilitaryTime(true);
                militaryTimeSetting.setText("Show standard time");
            }
            updateClockFace(true);
        });
        militaryTimeSetting.setForeground(Color.WHITE); // added on mac
        
        JMenuItem fullDateSetting = new JMenuItem("Show full date");
        fullDateSetting.addActionListener(action -> {
           if (this.showFullDate) {
               this.showFullDate = false;
               fullDateSetting.setText("Show full date");
           }
           else {
               this.showFullDate = true;
               fullDateSetting.setText("Hide full date");
           }
           updateClockFace(true);
        });
        fullDateSetting.setForeground(Color.WHITE); // added on mac
        
        // Add menu items to menu
        settings.add(militaryTimeSetting);
        settings.add(fullDateSetting);
        
        // Add menu to menuBar
        menuBar.add(settings);
        
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
    	jlbl1.setFont(font60);
		jlbl2.setFont(font60);
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
    		addComponent(jtextField1, 0,1,1,1, 0,0);
    		//do { jtextField1.requestFocusInWindow(); } while (jtextField1.getText().length() <= 2);
    		jalarmLbl2.setText(defaultText(5, isALeapYear(year))); // M
    		addComponent(jalarmLbl2, 0,2,1,1, 0,0);
    		addComponent(jtextField2, 0,3,1,1, 0,0);
    		//do { jtextField2.requestFocusInWindow(); } while (jtextField2.getText().length() <= 2);
    		jalarmLbl4.setText(defaultText(6, isALeapYear(year))); // T
    		addComponent(jalarmLbl4, 0,4,1,1, 0,0);
    		addComponent(jtextField3, 0,5,1,1, 0,0);
    		//do { jtextField3.requestFocusInWindow(); } while (jtextField3.getText().length() <= 2);
    		addComponent(jsetAlarmBtn, 0,6,1,1, 0,0);
            jalarmLbl3.setText(defaultText(4, isALeapYear(year)));
            addComponent(jalarmLbl3, 1,0,7,1, 0,0);
    		//addComponent(scrollPane, 2,0,1,1, 0,0);
            
    		
    	}
    	this.repaint();
    }

    /**
     * the purpose of tick is to start the clock normally.
     */
    public void tick() {
        Thread t = new Thread(this);
        t.start();
    }
    
    @Override
    public void run() {
    	try {
            updateJLabels();
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
            pack(); // Moved here to help facilitate not having to resize GUI for components to show up
        } catch (Exception e) {}
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
    
    /*
     * 
     * Method to update clock because of lost seconds
     */
    public void updateAllClockValues() throws ParseException {
        this.calendar.setTime(new Date());
        this.month = convertMonthToStr(calendar.get(Calendar.MONTH)+1);
        this.day = convertDayToStr(this.calendar.get(Calendar.DAY_OF_WEEK));
        this.date = this.calendar.get(Calendar.DAY_OF_MONTH);
        this.year = this.calendar.get(Calendar.YEAR);
        setHours(this.calendar.get(Calendar.HOUR));
        setMinutes(this.calendar.get(Calendar.MINUTE));
        setSeconds(this.calendar.get(Calendar.SECOND));
        this.time = this.calendar.get(Calendar.AM_PM) == Calendar.PM ? Time.PM : Time.AM;
        setDaylightSavingsTimeDates();
        this.leapYear = isALeapYear(year);
        this.daylightSavingsTime = isTodayDaylightSavingsTime();
    }
    
    public void updateJLabels() {
        this.seconds += 1;
        this.secondsAsStr = this.seconds <= 9 ? "0"+Integer.toString(this.seconds) : Integer.toString(this.seconds);
        if (this.seconds == 60) {
            this.seconds = 0;
            this.secondsAsStr = "00";
            this.minutes += 1;
            this.minutesAsStr = this.minutes <= 9 ? "0"+Integer.toString(this.minutes) : Integer.toString(this.minutes);
            if (this.minutes == 60) {
                this.minutes = 0;
                this.minutesAsStr = "00";
                this.hours += 1;
                if (this.hours == 12 && this.minutes == 0 && this.seconds == 0 && !showMilitaryTime) {
                    this.hours = 12;
                    this.hoursAsStr = "12";
                    if (time == Time.PM) {
                        this.time = Time.AM;
                        this.dateChanged = true;
                    } else {
                        this.dateChanged = false;
                        this.time = Time.PM;
                    }
                } else if (this.hours == 13 && !showMilitaryTime) {
                    this.hours = 1;
                    this.hoursAsStr = "01";
                    this.dateChanged = false;
                } else if (this.hours == 24 && this.minutes == 0 && this.seconds == 0 && showMilitaryTime) {
                    this.hours = 0;
                    this.hoursAsStr = "00";
                    this.time = Time.AM;
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
        switch (this.month) {
            case JANUARY: {
                if (this.date == 31 && dateChanged) {
                    this.date = 1;
                    this.month = FEBRUARY;
                }
                break;
            }
            case FEBRUARY: {
                if ((this.date == 28 || this.date == 30) && this.dateChanged) {
                    this.date = 1;
                    this.month = MARCH;
                } else if (this.date == 28 && this.leapYear && this.dateChanged) {
                    this.date = 29;
                }
                break;
            }
            case MARCH: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = APRIL;
                }
                break;
            }
            case APRIL: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1;
                    this.month = MAY;
                }
                break;
            }
            case MAY: {
                if (this.date == 32 && this.dateChanged) {
                    this.date= 1;
                    this.month = JUNE;
                }
                break;
            }
            case JUNE: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1;
                    this.month = JULY;
                }
                break;
            }
            case JULY: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = AUGUST;
                }
                break;
            }
            case AUGUST: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = SEPTEMBER;
                }
                break;
            }
            case SEPTEMBER: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1; 
                    this.month = OCTOBER;
                }
                break;
            }
            case OCTOBER: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = NOVEMBER;
                }
                break;
            }
            case NOVEMBER: {
                if (this.date == 31 && this.dateChanged) {
                    this.date = 1;
                    this.month = DECEMBER;
                }
                break;
            }
            case DECEMBER: {
                if (this.date == 32 && this.dateChanged) {
                    this.date = 1;
                    this.month = JANUARY;
                    this.year += 1;
                }
                break;
            }
            default : {
                
            }
        }
        setClockValues(this.time, this.showMilitaryTime);
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
            this.day = convertDayToStr(this.calendar.get(Calendar.DAY_OF_WEEK));
            //System.out.println("day is now: " + convertDayToStr(this.calendar.get(Calendar.DAY_OF_WEEK)));
        }
        if (this.daylightSavingsTime) {
            if (calendar.get(Calendar.MONTH) == 2 && this.time.getStrValue().equals("AM")) {
                setHours(3);
                this.daylightSavingsTime = false;
            } else if (calendar.get(Calendar.MONTH) == 10 && this.time.getStrValue().equals("AM")) { // && daylightSavingsTime
                setHours(1);
                this.daylightSavingsTime = false;
            }
        }
        //System.out.println(defaultText(lbl2, isALeapYear(this.year)));
    } // update jLabels
    
    public String defaultText(int labelVersion, boolean isALeapYear) {
        String defaultText = "";
        if (labelVersion == 1) {
            if (!showFullDate) defaultText += getDateAsStr();
            else if (showFullDate) defaultText += getFullDateAsStr();
        } else if (labelVersion == 2) {
            if (!showMilitaryTime) defaultText += getTimeAsStr();
            else if (showMilitaryTime) defaultText += getMilitaryTimeAsStr();
        } else if (labelVersion == 3 || labelVersion == 5) {
        	defaultText = labelVersion == 3 ? "H" : "M";
        } else if (labelVersion == 6) {
        	defaultText = "T";
        }	else if (labelVersion == 4) {
        	defaultText = "All Alarms";
        }
        return defaultText;
    }
    
    public String convertDayToStr(int thisDay) {
        String day = "";
        switch(thisDay) {
            case 1: day = "Sunday"; break;
            case 2: day = "Monday"; break;
            case 3: day = "Tuesday"; break;
            case 4: day = "Wednesday"; break;
            case 5: day = "Thursday"; break;
            case 6: day = "Friday"; break;
            case 7: day = "Saturday"; break;
            default: day = "Unknown day"; break;
        }
        return day;
    }
    
    public int convertMonthToInt(String thisMonth) throws InvalidInputException {
        int month = 0;
        switch (thisMonth) {
            case JANUARY: month = 1; break;
            case FEBRUARY: month = 2; break;
            case MARCH: month = 3; break;
            case APRIL: month = 4; break;
            case MAY: month = 5; break;
            case JUNE: month = 6; break;
            case JULY: month = 7; break;
            case AUGUST: month = 8; break;
            case SEPTEMBER: month = 9; break;
            case OCTOBER: month = 10; break;
            case NOVEMBER: month = 11; break;
            case DECEMBER: month = 12; break;
            default: throw new InvalidInputException("Unknown month");
        }
        return month;
    }
    
    public String convertMonthToStr(int thisMonth) {
        String month = "";
        switch (thisMonth) {
            case 1: month = JANUARY; break;
            case 2: month = FEBRUARY; break;
            case 3: month = MARCH; break;
            case 4: month = APRIL; break;
            case 5: month = MAY; break;
            case 6: month = JUNE; break;
            case 7: month = JULY; break;
            case 8: month = AUGUST; break;
            case 9: month = SEPTEMBER; break;
            case 10: month = OCTOBER; break;
            case 11: month = NOVEMBER; break;
            case 12: month = DECEMBER; break;
            default: month = "Invalid month"; break;
        }
        return month;
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
        this.layout.setConstraints(cpt, constraints);
        add(cpt);
    }
    
    
    public void setClockValues(Time time, boolean showMilitaryTime) {
    	if (time == null && showMilitaryTime == false) {}
    	
    	if (time == Time.AM && showMilitaryTime) // Daytime and we show Military Time
    	{
    		if (this.hours == 12) setHours(0);
    		else setHours(this.hours);
    	} 
    	else if (time == Time.AM && !showMilitaryTime) // DayTime and we do not show Military Time
    	{
    		if (this.hours == 0) setHours(12);
    		else setHours(this.hours);
    	} 
    	else if (time == Time.PM && showMilitaryTime) // NightTime and we show Military Time
    	{
    		if (this.hours == 24) setHours(0);
    		else if (this.hours < 12) setHours(this.hours + 12);
    		else setHours(this.hours);
    	} 
    	else if (time == Time.PM && !showMilitaryTime) // NightTime and we do not show Military Time
    	{
    		if (this.hours > 12) setHours(this.hours - 12);
    	}
    }
    
    

    public static void main(String[] args) throws ParseException, InterruptedException {
        Clock clock = new Clock();
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        clock.setSize(700, 300); // 500, 300
        clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clock.setBounds(200, 200, 700, 300);
        while (true) {
            clock.tick();
            Thread.sleep(1000); // main thread put to sleep
        }
    }

}
