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
import java.util.Objects;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * Timer
 * <p>
 * A Timer is basically a countdown LocalTime object.
 * Given a name, hours, minutes and seconds, it will
 * create the countdown and start counting down.
 *
 * @author michael ball
 * @version 2.0
 */
public class Timer implements Serializable, Comparable<Timer>, Runnable
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Timer.class);
    public static long timersCounter = 0L;
    private int hours,
                minutes,
                seconds;
    private String name,
                   hoursAsStr,
                   minutesAsStr,
                   secondsAsStr;
    private boolean timerGoingOff, paused,
                    hasBeenStarted, hasBeenTriggered,
                    stopped;
    private Clock clock;
    private Thread selfThread;
    private LocalTime countDown;
    private AdvancedPlayer musicPlayer;

    /**
     * Creates a new Timer object with default values
     */
    public Timer()
    {
        this(0, 0, 0, null, false, false, false, null);
    }

    /**
     * Creates a new Timer object with minimum values
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     */
    public Timer(int hours, int minutes, int seconds)
    {
        this(hours, minutes, seconds, null, false, false, false, null);
    }

    /**
     * Creates a new Timer with time values
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @param clock the clock object associated with this timer
     */
    public Timer(int hours, int minutes, int seconds, Clock clock)
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
     */
    public Timer(int hours, int minutes, int seconds, String name, Clock clock)
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
     * @throws IllegalArgumentException if the input values are invalid
     */
    public Timer(int hours, int minutes, int seconds, String name,
                 boolean timerGoingOff, boolean paused, boolean hasBeenStarted, Clock clock)
    {
        if (hours < 0 || hours > 12) throw new InvalidInputException("Hours must be between 0 and 12");
        if (minutes < 0 || minutes > 59) throw new InvalidInputException("Minutes must be between 0 and 59");
        if (seconds < 0 || seconds > 59) throw new InvalidInputException("Seconds must be between 0 and 59");
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
        setCountDown(LocalTime.of(getHours(), getMinutes(), getSeconds()));
        setTimerGoingOff(timerGoingOff);
        setPaused(paused);
        setHasBeenStarted(hasBeenStarted);
        setClock(clock);
        setName(StringUtils.isBlank(name) ? TIMER  + (timersCounter + 1) : name);
        setupMusicPlayer();
        timersCounter++;
        logger.debug("Total timers created: {}", timersCounter);
        if (timersCounter == 100L) {
            logger.info("Restarting counter for timers");
            timersCounter = 0L;
        }
        logger.info("Timer created");
    }

    /**
     * Defines the music player object
     */
    private void setupMusicPlayer()
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
    private void printStackTrace(Exception e, String message)
    {
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        { logger.error(ste.toString()); }
    }

    /**
     * This method begins the thread
     * that runs the timer.
     */
    public void startTimer()
    {
        if (selfThread == null)
        {
            setSelfThread(new Thread(this));
            selfThread.start();
        }
    }

    /**
     * This method starts the timer
     */
    @Override
    public void run()
    {
        while (selfThread != null)
        {
            try {
                if (!timerGoingOff && !stopped) {
                    performCountdown();
                    sleep(1000);
                } else if (timerGoingOff && !stopped) {
                    triggerTimer();
                    sleep(1000);
                } else { //if (stopTimer) {
                    sleep(1000);
                }


            }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
        }
    }

    /**
     * This method performs the countdown
     * by reducing the seconds, minutes and hours
     * accordingly. If the timer reaches zero,
     * it will set the timerGoingOff flag to true.
     */
    private void performCountdown()
    {
        if (!hasBeenStarted || !paused) {
            setHasBeenStarted(true);
            logger.info("{} ticking down...", this);
            if (countDown.getSecond() > 0 || countDown.getMinute() > 0 || countDown.getHour() > 0)
            {
                countDown = countDown.minusSeconds(1);
            }
            logger.debug("CountDown: {}", getCountdownString());
            if (countDown.getHour() == 0 && countDown.getMinute() == 0 && countDown.getSecond() == 0)
            {
                logger.info("{} has reached zero", this);
                setTimerGoingOff(true);
            }
        }
    }

    /**
     * Pauses the timer
     */
    public void pauseTimer()
    {
        logger.info("pausing {}", this);
        setPaused(true);
    }

    /**
     * Resumes the timer
     */
    public void resumeTimer()
    {
        logger.info("resuming {}", this);
        setPaused(false);
    }

    /**
     * Resets the timer to its initial state.
     */
    public void resetTimer()
    {
        logger.info("resetting {}", this);
        setPaused(false);
        setHasBeenStarted(false);
        setHasBeenTriggered(false);
        setHoursAsStr(ZERO + getHours());
        setMinutesAsStr(ZERO + getMinutes());
        setSecondsAsStr(ZERO + getSeconds());
        setCountDown(LocalTime.of(getHours(), getMinutes(), getSeconds()));
        setTimerGoingOff(false);
        logger.info("{} timer reset", this);
    }

    /**
     * Stop the timer
     */
    public void stopTimer()
    {
        logger.info("stopping {}", this);
        setMusicPlayer(null);
        setTimerGoingOff(false);
        setHasBeenStarted(false);
        setHasBeenTriggered(false);
        setStopped(true);
        logger.info("{} timer stopped", this);
    }

    /**
     * Plays the timer sound as long
     * as the timer is not paused.
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
    public String getMinutesAsStr() { return minutesAsStr; }
    public LocalTime getCountDown() { return countDown; }
    public String getCountdownString() {
        String countdownHours = countDown.getHour() < 10 ? ZERO + countDown.getHour() : String.valueOf(countDown.getHour());
        String countdownMinutes = countDown.getMinute() < 10 ? ZERO + countDown.getMinute() : String.valueOf(countDown.getMinute());
        String countdownSeconds = countDown.getSecond() < 10 ? ZERO + countDown.getSecond() : String.valueOf(countDown.getSecond());
        return String.format("%s:%s:%s", countdownHours, countdownMinutes, countdownSeconds);
    }
    public boolean isPaused() { return paused; }
    public int getSeconds() { return seconds; }
    public String getSecondsAsStr() { return secondsAsStr; }
    public String getName() { return name; }
    public boolean isTimerGoingOff() { return timerGoingOff; }
    public boolean isHasBeenStarted() { return hasBeenStarted; }
    public boolean isHasBeenTriggered() { return hasBeenTriggered; }
    public AdvancedPlayer getMusicPlayer() { return musicPlayer; }
    public boolean isStopped() { return stopped; }
    public Thread getSelfThread() { return selfThread; }

    /* Setters */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    public void setHours(int hour) {
        this.hours = hour;
        if (hour < 10) setHoursAsStr(ZERO+hour);
        else setHoursAsStr(EMPTY+hour);
        logger.debug("hours set to {}", hour);
    }
    public void setHoursAsStr(String hoursAsStr) { this.hoursAsStr = hoursAsStr; logger.debug("hoursAsStr set to {}", hoursAsStr); }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
        if (minutes < 10) setMinutesAsStr(ZERO+ minutes);
        else setMinutesAsStr(EMPTY+ minutes);
        logger.debug("minutes set to {}", minutes);
    }
    public void setMinutesAsStr(String minutesAsStr) { this.minutesAsStr = minutesAsStr; logger.debug("minutesAsStr set to {}", minutesAsStr); }
    public void setSeconds(int seconds) {
        this.seconds = seconds;
        if (seconds < 10) setSecondsAsStr(ZERO+ seconds);
        else setSecondsAsStr(EMPTY+ seconds);
        logger.debug("seconds set to {}", seconds);
    }
    public void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; logger.debug("secondsAsStr set to {}", secondsAsStr); }
    public void setCountDown(LocalTime countDown) { this.countDown = countDown; logger.debug("countDown set to {}", countDown); }
    public void setPaused(boolean paused) { this.paused = paused; logger.debug("paused set to {}", paused); }
    public void setName(String name) { this.name = name; logger.debug("name set to {}", name); }
    public void setTimerGoingOff(boolean timerGoingOff) { this.timerGoingOff = timerGoingOff; logger.debug("timerGoingOff set to {}", timerGoingOff); }
    public void setHasBeenStarted(boolean hasBeenStarted) { this.hasBeenStarted = hasBeenStarted; logger.debug("hasBeenStarted set to {}", hasBeenStarted); }
    public void setHasBeenTriggered(boolean hasBeenTriggered) { this.hasBeenTriggered = hasBeenTriggered; logger.debug("hasBeenTriggered set to {}", hasBeenTriggered); }
    public void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; logger.debug("musicPlayer set"); }
    public void setStopped(boolean stopped) { this.stopped = stopped; logger.debug("stopTimer set to {}", stopped); }
    public void setSelfThread(Thread selfThread) { this.selfThread = selfThread; logger.debug("selfThread set to {}", selfThread); }

    /**
     * Compares this timer to another timer based
     * on the string representation of the timer.
     * Used for sorting timers.
     * @return a negative integer, zero, or a positive integer
     */
    @Override
    public int compareTo(Timer o)
    { return this.toString().compareTo(o.toString()); }

    /**
     * Checks if two timers are equals.
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Timer timer)) return false;
        return Objects.equals(getName(), timer.getName()) &&
                getHours() == timer.getHours() &&
                getMinutes() == timer.getMinutes() &&
                getSeconds() == timer.getSeconds();
    }

    @Override
    public int hashCode()
    { return Objects.hash(getHours(), getMinutes(), getSeconds(), getName()); }

    /**
     * Returns a string representation of the Timer object.
     * @return "(Timer1) 08:30:00"
     */
    @Override
    public String toString()
    { return "(" + name + ")" + SPACE + getCountdownString(); }
}
