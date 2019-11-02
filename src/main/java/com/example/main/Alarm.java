package com.example.main;

/**
 * Write a description of class Alarm here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Alarm
{
    private int hours;
    private int minutes;
    private int seconds;
    private Time time;
    private boolean showMilitaryTime;
    
    private String hoursAsStr;
    private String minutesAsStr;
    private String secondsAsStr;
    private String name;

    /**
     * Constructor for objects of class Alarm
     */
    public Alarm() {}
    
    public Alarm(int hours, int minutes, int seconds, Time time, String name) {
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
        setTime(time);
        setName(name);
        this.showMilitaryTime = false;
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
        if (this.hours <= 9) this.hoursAsStr = "0"+Integer.toString(this.hours);
        else this.hoursAsStr = Integer.toString(this.hours);
    }
    private void setMinutes(int minutes) {
        this.minutes = minutes;
        if (this.minutes <= 9) this.minutesAsStr = "0"+Integer.toString(this.minutes);
        else this.minutesAsStr = Integer.toString(this.minutes);
    }
    private void setSeconds(int seconds) {
        this.seconds = seconds;
        if (this.seconds <= 9) this.secondsAsStr = "0"+Integer.toString(this.seconds);
        else this.secondsAsStr = Integer.toString(this.seconds);
    }
    private void setTime(Time time) { this.time = time; }
    private void setName(String name) { this.name = name; }
    public void setAlarmTime(int hours, int minutes, int seconds, Time time) {
        // perform logic for setting an alarm
    }
    
    public int getHours() { return this.hours; }
    public int getMinutes() { return this.minutes; }
    public int getSeconds() { return this.seconds; }
    public Time getTime() { return this.time; }
    public String getName() { return this.name; }
    
    public String getHoursAsStr() { return this.hoursAsStr; }
    public String getMinutesAsStr() { return this.minutesAsStr; }
    public String getSecondsAsStr() { return this.secondsAsStr; }
    public String getTimeAsStr() { 
        if (this.time == Time.AM) return "AM";
        else  return "PM";
    }
    
    public String getAlarmTime() {
        return getHoursAsStr() + ":" + getMinutesAsStr() + ":" + getSecondsAsStr() + " " + getTimeAsStr();
    }
}
