package clock.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.time.DayOfWeek.*;
import static clock.util.Constants.*;

/**
 * An Alarm object that can be set to go off
 * at a specific time, on specific days.
 *
 * @author michael ball
*  @version 2.0
 */
public class Alarm implements Serializable
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Alarm.class);
    private int hours, minutes;
    private String minutesAsStr,hoursAsStr,ampm;
    private List<DayOfWeek> days;
    boolean alarmGoingOff,updatingAlarm;
    private Clock clock;

    /**
     * Creates a new Alarm object with default values
     * @throws InvalidInputException thrown when invalid input is given
     */
    public Alarm() throws InvalidInputException
    {
        this(0, 0, AM, false, new ArrayList<>(), null);
        logger.debug("Alarm created");
    }

    /**
     * @param clock the clock object
     * @param isUpdateAlarm if the alarm is being updated
     * @throws InvalidInputException thrown when invalid input is given
     */
    public Alarm(Clock clock, boolean isUpdateAlarm) throws InvalidInputException
    {
        this(clock.getHours(), 0, clock.getAMPM(), isUpdateAlarm, List.of(clock.getDayOfWeek()), clock);
        logger.debug("Alarm created from clock values");
    }

    /**
     * Main constructor for creating alarms
     * @param hours         the hour of the alarm
     * @param minutes       the minutes of the alarm
     * @param ampm          the AM or PM value
     * @param updatingAlarm updating an alarm
     * @param days          the days to trigger alarm
     * @param clock         reference to the clock that created this alarm
     * @throws InvalidInputException thrown when invalid input is given
     * @see InvalidInputException
     */
    public Alarm(int hours, int minutes, String ampm, boolean updatingAlarm,
                 List<DayOfWeek> days, Clock clock) throws InvalidInputException
    {
        this.clock = clock;
        if (hours < 0 || hours > 12) throw new IllegalArgumentException("Hours must be between 0 and 12");
        else setHours(hours);
        if (minutes < 0 || minutes > 59) throw new IllegalArgumentException("Minutes must be between 0 and 59");
        else setMinutes(minutes);
        if (List.of(AM,PM,AM.toLowerCase(),PM.toLowerCase()).contains(ampm)) setAMPM(ampm.toUpperCase());
        else throw new IllegalArgumentException("AMPM must be 'AM' or 'PM'");
        this.days = days;
        this.updatingAlarm = updatingAlarm;
        logger.info("Alarm created with specific times");
    }

    @Override
    public String toString()
    { return hoursAsStr+COLON+minutesAsStr+SPACE+ampm; }

    public List<String> getDaysShortened()
    {
        logger.info("getDaysShortened");
        List<String> shortenedDays = new ArrayList<>();
        shortenedDays.add("Days: ");
        if (days.contains(MONDAY) && days.contains(TUESDAY) &&
            days.contains(WEDNESDAY) && days.contains(THURSDAY) && days.contains(FRIDAY))
        { shortenedDays.add("Weekdays "); }
        else if (days.contains(SATURDAY) && days.contains(SUNDAY))
        { shortenedDays.add("Weekend"); }
        else
        {

            for(DayOfWeek day : days)
            {
                switch (day)
                {
                    case MONDAY -> shortenedDays.add("M ");
                    case TUESDAY -> shortenedDays.add("T ");
                    case WEDNESDAY -> shortenedDays.add("W ");
                    case THURSDAY -> shortenedDays.add("Th ");
                    case FRIDAY -> shortenedDays.add("F ");
                    case SATURDAY -> shortenedDays.add("S ");
                    case SUNDAY -> shortenedDays.add("Su ");
                }
            }
        }
        shortenedDays.add("\n------");
        return shortenedDays;
    }

    /* Getters */
    public Clock getClock() { return this.clock; }
    public boolean isAlarmGoingOff() { return alarmGoingOff; }
    public boolean isUpdatingAlarm() { return updatingAlarm; }
    public List<DayOfWeek> getDays() { return this.days; }
    public int getHours() { return this.hours; }
    public String getHoursAsStr() { return this.hoursAsStr; }
    public int getMinutes() { return this.minutes; }
    public String getMinutesAsStr() { return this.minutesAsStr; }
    public String getAMPM() { return this.ampm; }

    /* Setters */
    public void setClock(Clock clock) { this.clock = clock; }
    public void setIsAlarmGoingOff(boolean alarmGoingOff) { this.alarmGoingOff = alarmGoingOff; }
    public void setIsAlarmUpdating(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    public void setAlarmGoingOff(boolean alarmGoingOff) { this.alarmGoingOff = alarmGoingOff; }
    public void setUpdatingAlarm(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    public void setDays(List<DayOfWeek> days) { this.days = days; }
    public void setHours(int hours) {
        this.hours = hours;
        this.hoursAsStr = (hours < 10) ? "0"+this.hours : String.valueOf(this.hours);
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
        this.minutesAsStr = (minutes < 10) ? "0"+this.minutes : String.valueOf(this.minutes);
    }
    public void setAMPM(String ampm) { this.ampm = ampm; }
}