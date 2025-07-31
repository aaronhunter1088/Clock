package clock.entity;

import clock.exception.InvalidInputException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * A Timer object that can be set to go off
 * after a specific amount of time
 *
 * @author michael ball
 *  @version 2.0
 */
public class Timer  implements Serializable, Comparable<Timer>, Runnable
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Timer.class);
    private static long timersCounter = 0L;
    private int hours, minutes, seconds;
    private String hoursAsStr, minutesAsStr, secondsAsStr, name;
    private boolean timerGoingOff, paused,
            hasBeenStarted, hasBeenTriggered,
            stopTimer;
    private Clock clock;
    private LocalTime countDown;
    private AdvancedPlayer musicPlayer;

    /**
     * Creates a new Timer object with default values
     */
    public Timer() throws InvalidInputException
    {
        this(0, 0, 0, null, false, false, false, null);
    }

    /**
     * Creates a new Timer object with minimum values
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @throws InvalidInputException if the input values are invalid
     */
    public Timer(int hours, int minutes, int seconds) throws InvalidInputException
    {
        this(hours, minutes, seconds, null, false, false, false, null);
    }

    /**
     * Creates a new Timer with time values
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @param clock the clock object associated with this timer
     * @throws InvalidInputException if the input values are invalid
     */
    public Timer(int hours, int minutes, int seconds, Clock clock) throws InvalidInputException
    {
        this(hours, minutes, seconds, null, false, false, false, clock);
    }

    /**
     * Creates a new Timer with time values and name
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @param name the name of the timer
     * @param clock the clock object associated with this timer
     * @throws InvalidInputException if the input values are invalid
     */
    public Timer(int hours, int minutes, int seconds, String name, Clock clock) throws InvalidInputException
    {
        this(hours, minutes, seconds, name, false, false, false, clock);
    }

    /**
     * The main constructor for creating a Timer
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @param name the name of the timer
     * @param timerGoingOff whether the timer is going off
     * @param paused whether the timer is paused
     * @param hasBeenStarted whether the timer has been started
     * @param clock the clock object associated with this timer
     */
    public Timer(int hours, int minutes, int seconds, String name,
                 boolean timerGoingOff, boolean paused, boolean hasBeenStarted, Clock clock) throws InvalidInputException
    {
        if (hours < 0 || hours > 12) throw new IllegalArgumentException("Hours must be between 0 and 12");
        if (minutes < 0 || minutes > 59) throw new IllegalArgumentException("Minutes must be between 0 and 59");
        if (seconds < 0 || seconds > 59 && seconds != 60) throw new IllegalArgumentException("Seconds must be between 0 and 59");
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
        countDown = LocalTime.of(getHours(), getMinutes(), getSeconds());
        this.timerGoingOff = timerGoingOff;
        this.paused = paused;
        this.hasBeenStarted = hasBeenStarted;
        this.clock = clock;
        this.name = StringUtils.isBlank(name) ? null : name;
        setupMusicPlayer();
        timersCounter++;
        logger.debug("Timer {} created", timersCounter);
        logger.info("Timer created");
    }

    @Override
    public String toString()
    {
        if (name == null || name.isBlank())
        { return hoursAsStr +":"+ minutesAsStr +":"+ secondsAsStr; }
        else
        { return name + SPACE + "(" + hoursAsStr +":"+ minutesAsStr +":"+ secondsAsStr + ")"; }
    }

    /**
     * Defines the music player object
     */
    void setupMusicPlayer()
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
     * Stop the timer
     */
    public void stopTimer()
    {
        logger.info("stop timer");
        musicPlayer = null;
        timerGoingOff = false;
        logger.info("{} timer turned off", this);
    }

    /**
     * Sets a timer to go off
     */
    public void triggerTimer()
    {
        logger.info("trigger timer");
        try
        {
            logger.debug("playing sound");
            setupMusicPlayer();
            musicPlayer.play();
        }
        catch (Exception e)
        {
            logger.error(e.getCause().getClass().getName() + " - " + e.getMessage());
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
     * This method performs the countdown for the timer
     */
    public void performCountDown()
    {
        if (paused && !hasBeenStarted) {
            hasBeenStarted = true;
            paused = false;
            run();
        } else if (!paused) {
            run();
        }
    }

    public void run()
    {
        logger.info("timer ticking down...");
        countDown.minusSeconds(1);
        logger.debug("CountDown: {}:{}:{}", countDown.getHour(), countDown.getMinute(), countDown.getSecond());
    }

    /**
     * Pauses the timer
     */
    void pauseTimer()
    {
        logger.info("pausing timer");
        paused = true;
    }

    /* Getters */
    public Clock getClock() { return clock; }
    public int getHours() { return hours; }
    public String getHoursAsStr() { return hoursAsStr; }
    public int getMinutes() { return minutes; }
    public String getMinutesAsStr() { return minutesAsStr; }
    public boolean isPaused() { return paused; }
    public int getSeconds() { return seconds; }
    public String getSecondsAsStr() { return secondsAsStr; }
    public String getName() { return name; }
    public boolean isTimerGoingOff() { return timerGoingOff; }
    public boolean isHasBeenStarted() { return hasBeenStarted; }
    public boolean isHasBeenTriggered() { return hasBeenTriggered; }
    public boolean isStopTimer() { return stopTimer; }

    /* Setters */
    public void setClock(Clock clock) { this.clock = clock; }
    public void setHours(int hour) {
        this.hours = hour;
        if (hour < 10) setHoursAsStr(ZERO+hour);
        else setHoursAsStr(EMPTY+hour);
    }
    public void setHoursAsStr(String hoursAsStr) { this.hoursAsStr = hoursAsStr; }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
        if (minutes < 10) setMinutesAsStr(ZERO+ minutes);
        else setMinutesAsStr(EMPTY+ minutes);
    }
    public void setMinutesAsStr(String minutesAsStr) { this.minutesAsStr = minutesAsStr; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public void setSeconds(int seconds) {
        this.seconds = seconds;
        if (seconds < 10) setSecondsAsStr(ZERO+ seconds);
        else setSecondsAsStr(EMPTY+ seconds);
    }
    public void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; }
    public void setName(String name) { this.name = name; }
    public void setTimerGoingOff(boolean timerGoingOff) { this.timerGoingOff = timerGoingOff; }
    public void setHasBeenStarted(boolean hasBeenStarted) { this.hasBeenStarted = hasBeenStarted; }
    public void setHasBeenTriggered(boolean hasBeenTriggered) { this.hasBeenTriggered = hasBeenTriggered; }
    public void setStopTimer(boolean stopTimer) { this.stopTimer = stopTimer; }

    @Override
    public int compareTo(Timer o) {
        return 0;
    }
}
