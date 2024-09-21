package org.example.clock;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.time.DayOfWeek.*;
import static org.example.clock.ClockConstants.*;

/**
 * An Alarm is similar to a Clock.
 * The differences may continue to grow but
 * as for now, an Alarm knows all the days
 * it should go off, the time at which to
 * go off, and can set the current day based
 * on Clocks time if alarm is going off.
 *
 * @author michael ball
 * @version 2.7
 */
public class Alarm {
    private static final Logger logger = LogManager.getLogger(Alarm.class);
    private int minutes;
    private String minutesAsStr;
    private int hours;
    private String hoursAsStr;
    private String ampm;
    private List<DayOfWeek> days;
    boolean alarmGoingOff;
    boolean updatingAlarm;
    private Clock clock;

    public Alarm() throws InvalidInputException {
        this(0, 0, AM, false, new ArrayList<>(), null);
        logger.info("Finished creating an Alarm");
    }
    public Alarm(Clock clock, int hours, boolean isUpdateAlarm) throws InvalidInputException {
        this(hours, 0, clock.getAMPM(), isUpdateAlarm, new ArrayList<>(){{add(clock.getDayOfWeek());}}, clock);
        logger.info("Finished creating an Alarm from [Clock, hours, isUpdateAlarm]");
    }
    /**
     * Main constructor for creating alarms
     * @param hours the hour of the alarm
     * @param minutes the minutes of the alarm
     * @param time the AM or PM value
     * @param isUpdateAlarm updating an alarm
     * @param days the days to trigger alarm
     * @param clock reference to the clock
     * @throws InvalidInputException thrown when invalid input is given
     */
    public Alarm(int hours, int minutes, String time, boolean isUpdateAlarm,
                 List<DayOfWeek> days, Clock clock) throws InvalidInputException {
        setClock(clock);
        setHours(hours);
        setMinutes(minutes);
        setAMPM(time);
        setDays(days);
        setIsAlarmUpdating(isUpdateAlarm);
        logger.info("Finished creating an Alarm with specific times");
    }

    public Clock getClock() { return this.clock; }
    public boolean isAlarmGoingOff() { return alarmGoingOff; }
    public boolean isUpdatingAlarm() { return updatingAlarm; }
    public List<DayOfWeek> getDays() { return this.days; }
    public int getHours() { return this.hours; }
    public String getHoursAsStr() { return this.hoursAsStr; }
    public int getMinutes() { return this.minutes; }
    public String getMinutesAsStr() { return this.minutesAsStr; }
    public String getAMPM() { return this.ampm; }

    private void setClock(Clock clock) { this.clock = clock; }
    protected void setIsAlarmGoingOff(boolean alarmGoingOff) { this.alarmGoingOff = alarmGoingOff; }
    protected void setIsAlarmUpdating(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    private void setAlarmGoingOff(boolean alarmGoingOff) { this.alarmGoingOff = alarmGoingOff; }
    private void setUpdatingAlarm(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    private void setDays(List<DayOfWeek> days) { this.days = days; }
    private void setHours(int hours) {
        this.hours = hours;
        if (hours < 10) {
            this.hoursAsStr = "0" + this.hours;
        } else
            this.hoursAsStr = String.valueOf(this.hours);
    }
    private void setMinutes(int minutes) {
        this.minutes = minutes;
        if (minutes < 10) {
            this.minutesAsStr = "0" + this.minutes;
        } else {
            this.minutesAsStr = String.valueOf(this.minutes);
        }
    }
    private void setAMPM(String ampm) {
        this.ampm = ampm;
    }

    @Override
    public String toString() { return getAlarmAsString(); }

    protected String getAlarmAsString() {
        return hoursAsStr+COLON+minutesAsStr+SPACE+ampm;
    }
    public List<String> getDaysShortened()
    {
        logger.info("getDaysShortened");
        List<String> shortenedDays = new ArrayList<>();
        shortenedDays.add("Days: ");
        if (getDays().contains(MONDAY) && getDays().contains(TUESDAY) &&
                getDays().contains(WEDNESDAY) && getDays().contains(THURSDAY) &&
                getDays().contains(FRIDAY))
        { shortenedDays.add("Weekdays "); }
        else if (getDays().contains(SATURDAY) && getDays().contains(SUNDAY)) { shortenedDays.add("Weekend"); }
        else {
            for(DayOfWeek day : getDays())
            {
                if (day == MONDAY) { shortenedDays.add("M "); }
                if (day == TUESDAY) { shortenedDays.add("T "); }
                if (day == WEDNESDAY) { shortenedDays.add("W "); }
                if (day == THURSDAY) { shortenedDays.add("Th "); }
                if (day == FRIDAY) { shortenedDays.add("F "); }
                if (day == SATURDAY) { shortenedDays.add("S "); }
                if (day == SUNDAY) { shortenedDays.add("Su "); }
            }
        }
        shortenedDays.add("\n------");
        return shortenedDays;
    }
}
