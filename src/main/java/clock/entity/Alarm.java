package clock.entity;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import clock.exception.InvalidInputException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;
import static java.time.DayOfWeek.*;
import static clock.util.Constants.*;

/**
 * An Alarm object that can be set to go off
 * at a specific time, on specific days.
 *
 * @author michael ball
*  @version 2.0
 */
public class Alarm implements Serializable, Comparable<Alarm>, Runnable
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Alarm.class);
    public static long alarmsCounter = 0L;
    private int hours, minutes;
    private String minutesAsStr,hoursAsStr,ampm, name;
    private List<DayOfWeek> days;
    private boolean alarmGoingOff,updatingAlarm, triggeredToday;
    private Clock clock;
    private Thread selfThread;
    private AdvancedPlayer musicPlayer;

    /**
     * Creates a new Alarm object with default values
     * Needed for testing purposes. When using
     * '@InjectMocks' in tests, a default constructor
     * is required. Otherwise you will see a
     * MockitoException: Cannot instantiate @InjectMocks
     * named 'some alarm' Cause: the type 'Alarm' has
     * not default constructor.
     * @throws InvalidInputException thrown when invalid input is given
     */
    public Alarm() throws InvalidInputException
    {
        this("Alarm"+(Alarm.alarmsCounter+1), 0, 0, AM, new ArrayList<>(), false, null);
        logger.debug("Default alarm created");
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
    public Alarm(String name, int hours, int minutes, String ampm, List<DayOfWeek> days,
                 boolean updatingAlarm, Clock clock)
    {
        if (hours < 0 || hours > 12) throw new InvalidInputException("Hours must be between 0 and 12");
        if (minutes < 0 || minutes > 59) throw new InvalidInputException("Minutes must be between 0 and 59");
        setClock(clock);
        setHours(hours);
        setMinutes(minutes);
        setAMPM(ampm.toUpperCase());
        setDays(days);
        setUpdatingAlarm(updatingAlarm);
        setName(name);
        //setupMusicPlayer();
        alarmsCounter++;
        logger.debug("Total alarms created: {}", alarmsCounter);
        if (alarmsCounter == 100L) {
            logger.info("Restarting counter for alarms");
            alarmsCounter = 0L;
        }
        logger.info("Alarm created");
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
        selfThread = null;
        logger.info("{} alarm turned off", this);
    }

    /**
     * Sets an alarm to go off
     */
    public void triggerAlarm()
    {
        logger.debug("trigger {}", this);
        try
        {
            logger.debug("playing sound");
            setupMusicPlayer();
            musicPlayer.play();
        }
        catch (Exception e)
        {
            printStackTrace(e, e.getMessage());
        }
    }

    /**
     * This method begins the thread
     * that runs the alarm.
     */
    public void startAlarm()
    {
        if (selfThread == null)
        {
            selfThread = new Thread(this);
            selfThread.start();
        }
    }

    /**
     * This method starts the alarm
     */
    @Override
    public void run()
    {
        while (selfThread != null)
        {
            try {
                if (!alarmGoingOff) {
                    activateAlarm();
                } else { // if (alarmGoingOff) {
                    triggerAlarm();
                }
                sleep(1000);
            }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
        }
    }

    /**
     * Scheduled to run once every second.
     * For each alarm, check if the alarm's
     * time and day matches the clocks current
     * time and day. And, if the alarm is not
     * already going off, set it to going off.
     */
    private void activateAlarm()
    {
        if (getAlarmAsString().equals(clock.getClockTimeAsAlarmString())
                && this.getDays().contains(clock.getDayOfWeek()))
        {
            setIsAlarmGoingOff(true);
            setTriggeredToday(true);
            logger.info("Alarm {} matches clock's time. Activating alarm", this);
        }
        // alarm has reference to time
        // check all alarms
        // if any alarm matches clock's time, an alarm should be going off
    }

    /**
     * Snoozing.... TODO: Implement this method
     */
    public void snooze()
    {
        logger.warn("IMPLEMENT");
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
     * Returns a shortened version of the day
     * @return a string representing the day
     */
    public List<String> getDaysShortened()
    {
        logger.info("getDaysShortened");
        List<String> shortenedDays = new ArrayList<>();
        if (days.contains(MONDAY) && days.contains(TUESDAY) &&
            days.contains(WEDNESDAY) && days.contains(THURSDAY) && days.contains(FRIDAY)
            && !days.contains(SATURDAY) && !days.contains(SUNDAY))
        { shortenedDays.add(WEEKDAYS); }
        else if (!days.contains(MONDAY) && !days.contains(TUESDAY) &&
                !days.contains(WEDNESDAY) && !days.contains(THURSDAY) && !days.contains(FRIDAY)
                && days.contains(SATURDAY) && days.contains(SUNDAY))
        { shortenedDays.add(WEEKENDS); }
        else if (days.contains(MONDAY) && days.contains(TUESDAY) &&
                days.contains(WEDNESDAY) && days.contains(THURSDAY) && days.contains(FRIDAY)
                && days.contains(SATURDAY) && days.contains(SUNDAY))
        { shortenedDays.add(EVERY_DAY); }
        else
        {
            for(DayOfWeek day : days)
            {
                switch (day)
                {
                    case MONDAY -> shortenedDays.add(M);
                    case TUESDAY -> shortenedDays.add(T);
                    case WEDNESDAY -> shortenedDays.add(W);
                    case THURSDAY -> shortenedDays.add(TH);
                    case FRIDAY -> shortenedDays.add(F);
                    case SATURDAY -> shortenedDays.add(S);
                    case SUNDAY -> shortenedDays.add(SU);
                }
            }
        }
        return shortenedDays;
    }

    @Override
    public String toString()
    {
        if (name == null || name.isBlank())
        { return getAlarmAsString(); }
        else
        { return "(" + name + ")" + SPACE + getAlarmAsString(); }
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
    public String getAlarmAsString() { return hoursAsStr+COLON+minutesAsStr+SPACE+ampm; }
    public boolean isTriggeredToday() { return triggeredToday; }
    public Thread getSelfThread() { return selfThread; }

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
    public void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    public void setTriggeredToday(boolean triggeredToday) {
        logger.debug("{} triggered today set to {}", this, triggeredToday);
        this.triggeredToday = triggeredToday;
    }
    public void setSelfThread(Thread selfThread) { this.selfThread = selfThread; }

    @Override
    public int compareTo(Alarm o) {
        return this.getAlarmAsString().compareTo(o.getAlarmAsString());
    }

    /**
     * Checks if two alarms are equals
     * If they have a name, it will check
     * against the lowercase version of the name.
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Alarm alarm)) return false;
        if (getName() != null) {
            return getHours() == alarm.getHours() &&
                   getMinutes() == alarm.getMinutes() &&
                   Objects.equals(getName().toLowerCase(), alarm.getName().toLowerCase()) &&
                   Objects.equals(ampm, alarm.ampm) &&
                   Objects.equals(getDays(), alarm.getDays());
        } else {
            return getHours() == alarm.getHours() &&
                   getMinutes() == alarm.getMinutes() &&
                   Objects.equals(ampm, alarm.ampm) &&
                   Objects.equals(getDays(), alarm.getDays());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHours(), getMinutes(), getName(), getMinutesAsStr(), getHoursAsStr(), ampm, getDays(), isAlarmGoingOff(), isUpdatingAlarm(), getClock(), getMusicPlayer());
    }
}