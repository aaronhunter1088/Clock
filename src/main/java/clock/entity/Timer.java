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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * A Timer object that can be set to go off
 * after a specific amount of time
 *
 * @author michael ball
 *  @version 2.0
 */
public class Timer  implements Serializable, Comparable<Timer>
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Timer.class);
    public static long timersCounter = 0L;
    private int hours, minutes, seconds;
    private String hoursAsStr, minutesAsStr, secondsAsStr, name;
    private boolean timerGoingOff, paused,
            hasBeenStarted, hasBeenTriggered,
            stopTimer;
    private Clock clock;
    private Thread self;
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
        if (timersCounter == 100L) {
            logger.info("Restarting counter for timers");
            timersCounter = 0L;
        }
        logger.debug("Timer {} created", timersCounter);
        logger.info("Timer created");
    }

    /**
     * Returns a string representation of the Timer object.
     * If there is a name set, it will print the name as well
     * @return (Name) Hours:Minutes:Seconds
     */
    @Override
    public String toString()
    {
        if (name == null || name.isBlank())
        { return getCountdown(); }
        else
        { return "(" + name + ")" + SPACE + getCountdown(); }
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
        if (!hasBeenStarted && !paused) { // was paused && !hasBeenStarted
            hasBeenStarted = true;
            innerCountDown();
        } else if (!paused) {
            innerCountDown();
        }
    }

    public void innerCountDown()
    {
        logger.info("{} ticking down...", this);
        if (countDown.getSecond() > 0 || countDown.getMinute() > 0 || countDown.getHour() > 0)
        {
            countDown = countDown.minusSeconds(1);
        }
        logger.debug("CountDown: {}:{}:{}", countDown.getHour(), countDown.getMinute(), countDown.getSecond());
        if (countDown.getHour() == 0 && countDown.getMinute() == 0 && countDown.getSecond() == 0)
        {
            logger.info("{} has reached zero", this);
            timerGoingOff = true;
        }
    }

    /**
     * Pauses the timer
     */
    public void pauseTimer()
    {
        logger.info("pausing {}", this);
        paused = true;
    }

    public void resumeTimer()
    {
        logger.info("resuming {}", this);
        paused = false;
    }

    public void resetTimer()
    {
        logger.info("resetting {}", this);
        paused = false;
        hasBeenStarted = false;
        hasBeenTriggered = false;
        setHoursAsStr(ZERO + getHours());
        setMinutesAsStr(ZERO + getMinutes());
        setSecondsAsStr(ZERO + getSeconds());
        countDown = LocalTime.of(getHours(), getMinutes(), getSeconds());
        timerGoingOff = false;
        logger.info("{} timer reset", this);
    }

    /**
     * Stop the timer
     */
    public void stopTimer()
    {
        logger.info("stopping {}", this);
        musicPlayer = null;
        timerGoingOff = false;
        logger.info("{} timer stopped", this);
    }

    /**
     * Sets a timer to go off
     */
    public void triggerTimer()
    {
        try
        {
            if (!paused) {
                logger.debug("triggering timer...");
                setupMusicPlayer();
                musicPlayer.play();
            }
        }
        catch (Exception e)
        {
            logger.error(e.getCause().getClass().getName() + " - " + e.getMessage());
        }
    }

    /* Getters */
    public Clock getClock() { return clock; }
    public int getHours() { return hours; }
    public String getHoursAsStr() { return hoursAsStr; }
    public int getMinutes() { return minutes; }
    public String getCountdown() {
        String countdownHours = countDown.getHour() < 10 ? ZERO + countDown.getHour() : String.valueOf(countDown.getHour());
        String countdownMinutes = countDown.getMinute() < 10 ? ZERO + countDown.getMinute() : String.valueOf(countDown.getMinute());
        String countdownSeconds = countDown.getSecond() < 10 ? ZERO + countDown.getSecond() : String.valueOf(countDown.getSecond());
        return String.format("%s:%s:%s", countdownHours, countdownMinutes, countdownSeconds);
    }
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
