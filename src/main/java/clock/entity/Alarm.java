package clock.entity;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import clock.exception.InvalidInputException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang3.StringUtils;
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
public class Alarm implements Serializable, Comparable<Alarm>
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Alarm.class);
    private static long alarmsCounter = 0L;
    private int hours, minutes;
    private String name;
    private String minutesAsStr,hoursAsStr,ampm;
    private List<DayOfWeek> days;
    boolean alarmGoingOff,updatingAlarm;
    private Clock clock;
    private AdvancedPlayer musicPlayer;

    /**
     * Creates a new Alarm object with default values
     * @throws InvalidInputException thrown when invalid input is given
     */
    public Alarm() throws InvalidInputException
    {
        this("Alarm", 0, 0, AM, false, new ArrayList<>(), null);
        logger.debug("Alarm created");
    }

    /**
     * @param clock the clock object
     * @param isUpdateAlarm if the alarm is being updated
     * @throws InvalidInputException thrown when invalid input is given
     */
    public Alarm(Clock clock, boolean isUpdateAlarm) throws InvalidInputException
    {
        this("Alarm", clock.getHours(), 0, clock.getAMPM(), isUpdateAlarm, List.of(clock.getDayOfWeek()), clock);
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
    public Alarm(String name, int hours, int minutes, String ampm,
                 boolean updatingAlarm, List<DayOfWeek> days, Clock clock) throws InvalidInputException
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
        this.name = StringUtils.isBlank(name) ? null : name;
        setupMusicPlayer();
        alarmsCounter++;
        logger.info("Alarm created with specific times");
    }

    /**
     * Defines the music player object
     */
    public void setupMusicPlayer()
    {
        logger.info("setup music player");
        InputStream inputStream = null;
        try
        {
            inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");
            if (null != inputStream) { musicPlayer = new AdvancedPlayer(inputStream); }
            else throw new NullPointerException();
        }
        catch (NullPointerException | JavaLayerException e)
        {
            logger.error("Music Player not set!");
            if (null == inputStream) printStackTrace(e, "An issue occurred while reading the alarm file.");
            else printStackTrace(e, "A JavaLayerException occurred: " + e.getMessage());
        }
    }

    /**
     * Stops an actively going off alarm
     */
    public void stopAlarm()
    {
        logger.info("stop alarm");
        musicPlayer = null;
        alarmGoingOff = false;
        logger.info("{} alarm turned off", this);
    }

    /**
     * Sets an alarm to go off
     */
    public void triggerAlarm()
    {
        logger.info("trigger alarm");
        try
        {
            logger.debug("while alarm is going off, play sound");
            setupMusicPlayer();
            getMusicPlayer().play();
            //musicPlayer.close();
        }
        catch (Exception e)
        {
            logger.error(e.getCause().getClass().getName() + " - " + e.getMessage());
            printStackTrace(e, "message");
            //setupMusicPlayer();
            //getMusicPlayer().play(50);
        }

    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     * @param message the message to print
     */
    public void printStackTrace(Exception e, String message)
    {
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        { logger.error(ste.toString()); }
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     */
    void printStackTrace(Exception e)
    { printStackTrace(e, ""); }


    public List<String> getDaysShortened()
    {
        logger.info("getDaysShortened");
        List<String> shortenedDays = new ArrayList<>();
        shortenedDays.add("Days: ");
        if (days.contains(MONDAY) && days.contains(TUESDAY) &&
            days.contains(WEDNESDAY) && days.contains(THURSDAY) && days.contains(FRIDAY))
        { shortenedDays.add("Weekdays "); }
        else if (days.contains(SATURDAY) && days.contains(SUNDAY))
        { shortenedDays.add("Weekends "); }
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

    @Override
    public String toString()
    {
        if (name == null || name.isBlank())
        { return hoursAsStr+COLON+minutesAsStr+SPACE+ampm; }
        else
        { return name + SPACE + "(" + hoursAsStr+COLON+minutesAsStr+SPACE+ampm + ")"; }
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
    public String getName() { return this.name; }
    public AdvancedPlayer getMusicPlayer() { return this.musicPlayer; }
    public String getAlarmAsString() {
        return hoursAsStr+COLON+minutesAsStr+SPACE+ampm;
    }

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
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = "Alarm" + alarmsCounter;
        } else {
            this.name = name;
        }
    }
    public void setMusicPlayer(AdvancedPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    @Override
    public int compareTo(Alarm o) {
        return this.getAlarmAsString().compareTo(o.getAlarmAsString());
    }
}