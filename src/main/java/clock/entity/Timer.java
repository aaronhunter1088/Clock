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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static clock.util.Constants.*;

/**
 * Timer
 * <p>
 * A Timer is basically a countdown LocalTime object.
 * Given a name, hours, minutes and seconds, it will
 * create the countdown and start counting down.
 *
 * @author michael ball
 * @version since 2.0
 */
public class Timer implements Serializable, Comparable<Timer>
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(Timer.class);
    /** Running count of total Timer instances created; resets at 100. */
    public static long timersCounter = 0L;
    /** The hours of the timer */
    private int hours;
    /** The minutes of the timer */
    private int minutes;
    /** The seconds of the timer */
    private int seconds;
    /** The hours of the timer as a string */
    private String hoursAsStr;
    /** The minutes of the timer as a string */
    private String minutesAsStr;
    /** The seconds of the timer as a string */
    private String secondsAsStr;
    /** The name of the timer */
    private String name;
    /** Indicates if this timer is actively playing its sound, or going off */
    private boolean timerGoingOff;
    /** Indicates if the timer is paused or not */
    private boolean paused;
    /** Indicates if the timer has been started or not */
    private boolean started;
    /** Indicates if the timer has been triggered to begin playing its sound */
    private boolean triggered;
    /** Reference to the clock */
    private transient Clock clock;
    /** The time to count down for this timer expressed as a LocalTime */
    private transient LocalTime countDown;
    /** The countdown future */
    private transient ScheduledFuture<?> countdownFuture;
    /** The sound future */
    private transient ScheduledFuture<?> soundFuture;
    /** The music player */
    private transient AdvancedPlayer musicPlayer;

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
    public Timer(int hours, int minutes, int seconds) { this(hours, minutes, seconds, null, false, false, false, null); }

    /**
     * Creates a new Timer with time values
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @param clock the clock object associated with this timer
     */
    public Timer(int hours, int minutes, int seconds, Clock clock) { this(hours, minutes, seconds, null, false, false, false, clock); }

    /**
     * Creates a new Timer with time values and name
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @param name the name of the timer
     * @param clock the clock object associated with this timer
     */
    public Timer(int hours, int minutes, int seconds, String name, Clock clock) { this(hours, minutes, seconds, name, false, false, false, clock); }

    /**
     * The main constructor for creating a Timer
     * @param hours the hours for the timer
     * @param minutes the minutes for the timer
     * @param seconds the seconds for the timer
     * @param name the name of the timer
     * @param timerGoingOff whether the timer is going off
     * @param paused whether the timer is paused
     * @param started whether the timer has been started
     * @param clock the clock object associated with this timer
     * @throws IllegalArgumentException if the input values are invalid
     */
    public Timer(int hours, int minutes, int seconds, String name,
                 boolean timerGoingOff, boolean paused, boolean started, Clock clock)
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
        setStarted(started);
        setClock(clock);
        setName(StringUtils.isBlank(name) ? TIMER + (timersCounter + 1) : name);
        timersCounter++;
        logger.debug("Total timers created: {}", timersCounter);
        if (timersCounter == 100L) {
            logger.info("Restarting counter for timers");
            timersCounter = 0L;
        }
        logger.info("Timer created");
    }

    /**
     * This method starts the timer.
     * @param scheduler the executor service used to schedule the countdown task
     */
    public synchronized void startTimer(ScheduledExecutorService scheduler)
    {
        if (countdownFuture == null || countdownFuture.isDone() || countdownFuture.isCancelled())
        {
            logger.debug("starting {}", this);

            setStarted(true);
            setPaused(false);

            countdownFuture = scheduler.scheduleAtFixedRate(
                    () -> performCountDown(scheduler),
                    0,
                    1,
                    TimeUnit.SECONDS
            );
        }
    }

    private synchronized void performCountDown(ScheduledExecutorService scheduler)
    {
        if (!started || paused || timerGoingOff)
        {
            return;
        }

        logger.debug("{} ticking down...", this);

        if (countDown.getSecond() > 0 || countDown.getMinute() > 0 || countDown.getHour() > 0)
        {
            countDown = countDown.minusSeconds(1);
        }

        //logger.debug("CountDown: {}", getCountDownString());

        if (countDown.getHour() == 0 && countDown.getMinute() == 0 && countDown.getSecond() == 0)
        {
            logger.debug("{} has reached zero", this);

            setTimerGoingOff(true);
            setTriggered(true);

            if (countdownFuture != null)
            {
                countdownFuture.cancel(false);
                countdownFuture = null;
            }

            triggerTimer(scheduler);
        }
    }

    private synchronized void triggerTimer(ScheduledExecutorService scheduler)
    {
        if (soundFuture != null && !soundFuture.isDone())
        {
            return;
        }

        logger.debug("triggering timer...");

        soundFuture = scheduler.scheduleWithFixedDelay(
                this::playSoundOnce,
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    /**
     * Sets up a music player for the timer.
     */
    private void setupMusicPlayer()
    {
        logger.info("setup music player");

        InputStream inputStream = null;

        try
        {
            inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");

            if (inputStream != null)
            {
                musicPlayer = new AdvancedPlayer(inputStream);
            }
            else
            {
                throw new NullPointerException();
            }
        }
        catch (NullPointerException | JavaLayerException e)
        {
            logger.error("Music Player not set!");

            if (inputStream == null)
            {
                printStackTrace(e, "An issue occurred while reading the timer sound file.");
            }
            else
            {
                printStackTrace(e, "A JavaLayerException occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Plays a sound using the music player.
     */
    private void playSoundOnce()
    {
        try
        {
            setupMusicPlayer();

            if (musicPlayer != null)
            {
                musicPlayer.play();
            }
        }
        catch (Exception e)
        {
            printStackTrace(e, "Error while playing timer sound");
        }
    }

    /**
     * Stops the soundFuture and the music player.
     */
    private synchronized void stopSound()
    {
        if (soundFuture != null)
        {
            soundFuture.cancel(true);
            soundFuture = null;
        }

        if (musicPlayer != null)
        {
            musicPlayer.close();
            musicPlayer = null;
        }
    }

    /** Pauses the timer */
    public synchronized void pauseTimer()
    {
        logger.debug("pausing {}", this);
        setPaused(true);
    }

    /** Resumes a paused timer */
    public synchronized void resumeTimer()
    {
        if (started && !timerGoingOff)
        {
            logger.debug("resuming {}", this);
            setPaused(false);
            startTimer(clock.getScheduledExecutorService());
        }
    }

    /** Resets the timer to its initial state. */
    public synchronized void resetTimer()
    {
        logger.info("resetting {}", this);
        stopTimer();
        setPaused(false);
        setStarted(false);
        setTriggered(false);
        setTimerGoingOff(false);
//        setHours(getHours());
//        setMinutes(getMinutes());
//        setSeconds(getSeconds());
        setCountDown(LocalTime.of(getHours(), getMinutes(), getSeconds()));
        logger.info("{} timer reset", this);
    }

    /** Stops the timer */
    public synchronized void stopTimer()
    {
        logger.info("stopping {}", this);
        if (countdownFuture != null)
        {
            countdownFuture.cancel(false);
            countdownFuture = null;
        }
        stopSound();
        setStarted(false);
        setPaused(false);
        setTriggered(false);
        setTimerGoingOff(false);
        logger.info("{} timer stopped", this);
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     * @param message a custom message to print out
     */
    public void printStackTrace(Exception e, String message)
    {
        if (message != null)
            logger.error(message);
        if (e.getMessage() != null)
            logger.error(e.getMessage());
        for (StackTraceElement ste : e.getStackTrace())
        {
            logger.error(ste.toString());
        }
    }

    /**
     * Returns the clock
     * @return the clock reference
     */
    public Clock getClock() { return clock; }
    /**
     * Returns the hours
     * @return the hours
     */
    public int getHours() { return hours; }
    /**
     * Returns the hours as a string
     * @return the hours as a string
     */
    public String getHoursAsStr() { return hoursAsStr; }
    /**
     * Returns the minutes
     * @return the minutes
     */
    public int getMinutes() { return minutes; }
    /**
     * Returns the minutes as a string
     * @return the minutes as a string
     */
    public String getMinutesAsStr() { return minutesAsStr; }
    /**
     * Returns the seconds
     * @return the seconds
     */
    public int getSeconds() { return seconds; }
    /**
     * Returns the seconds as a string
     * @return the seconds as a string
     */
    public String getSecondsAsStr() { return secondsAsStr; }
    /**
     * Returns the countdown
     * @return the countdown as a LocalTime
     */
    public LocalTime getCountDown() { return countDown; }
    /**
     * Returns the countdown as a formatted string HH:MM:SS
     * @return the countdown string
     */
    public String getCountDownString()
    {
        String countdownHours = countDown.getHour() < 10 ? ZERO + countDown.getHour() : String.valueOf(countDown.getHour());
        String countdownMinutes = countDown.getMinute() < 10 ? ZERO + countDown.getMinute() : String.valueOf(countDown.getMinute());
        String countdownSeconds = countDown.getSecond() < 10 ? ZERO + countDown.getSecond() : String.valueOf(countDown.getSecond());
        return String.format("%s:%s:%s", countdownHours, countdownMinutes, countdownSeconds);
    }
    /**
     * Returns paused
     * @return true if the timer is paused
     */
    public boolean isPaused() { return paused; }
    /**
     * Returns the name of the timer
     * @return the name of the timer
     */
    public String getName() { return name; }
    /**
     * Returns the timerGoingOff flag
     * @return true if the timer is going off
     */
    public boolean isTimerGoingOff() { return timerGoingOff; }
    /**
     * Returns the started flag
     * @return true if the timer has been started
     */
    public boolean isStarted() { return started; }
    /**
     * Returns the triggered flag
     * @return true if the timer has been triggered
     */
    public boolean isTriggered() { return triggered; }
    /**
     * Returns the music player
     * @return the music player object
     */
    public AdvancedPlayer getMusicPlayer() { return musicPlayer; }
    /**
     * Returns the countdown future
     * @return the countdown scheduled future
     */
    public ScheduledFuture<?> getCountdownFuture() { return countdownFuture; }
    /**
     * Returns the sound future
     * @return the sound scheduled future
     */
    public ScheduledFuture<?> getSoundFuture() { return soundFuture; }

    /**
     * Sets the clock
     * @param clock the clock to set
     */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    /**
     * Sets the hours, also updates the hoursAsStr
     * @param hour the hours to set
     */
    public void setHours(int hour)
    {
        this.hours = hour;
        setHoursAsStr(hour < 10 ? ZERO + hour : EMPTY + hour);
        logger.debug("hours set to {}", hour);
    }
    /**
     * Sets the hours as a string
     * @param hoursAsStr the hours as a string to set
     */
    public void setHoursAsStr(String hoursAsStr) { this.hoursAsStr = hoursAsStr; }
    /**
     * Sets the minutes, also updates the minutesAsStr
     * @param minutes the minutes to set
     */
    public void setMinutes(int minutes)
    {
        this.minutes = minutes;
        setMinutesAsStr(minutes < 10 ? ZERO + minutes : EMPTY + minutes);
        logger.debug("minutes set to {}", minutes);
    }
    /**
     * Sets the minutes as a string
     * @param minutesAsStr the minutes as a string to set
     */
    public void setMinutesAsStr(String minutesAsStr) { this.minutesAsStr = minutesAsStr; }
    /**
     * Sets the seconds, also updates the secondsAsStr
     * @param seconds the seconds to set
     */
    public void setSeconds(int seconds)
    {
        this.seconds = seconds;
        setSecondsAsStr(seconds < 10 ? ZERO + seconds : EMPTY + seconds);
        logger.debug("seconds set to {}", seconds);
    }
    /**
     * Sets the seconds as a string
     * @param secondsAsStr the seconds as a string to set
     */
    public void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; }
    /**
     * Sets the countdown
     * @param countDown the countdown to set
     */
    public void setCountDown(LocalTime countDown) { this.countDown = countDown; }
    /**
     * Sets the paused flag
     * @param paused the paused flag to set
     */
    public void setPaused(boolean paused) { this.paused = paused; }
    /**
     * Sets the name of the timer
     * @param name the name of the timer to set
     */
    public void setName(String name) { this.name = name; }
    /**
     * Sets the timerGoingOff flag
     * @param timerGoingOff the timerGoingOff flag to set
     */
    public void setTimerGoingOff(boolean timerGoingOff) { this.timerGoingOff = timerGoingOff; }
    /**
     * Sets the started flag
     * @param started the started flag to set
     */
    public void setStarted(boolean started) { this.started = started; }
    /**
     * Sets the triggered flag
     * @param triggered the triggered flag to set
     */
    public void setTriggered(boolean triggered) { this.triggered = triggered; }
    /**
     * Sets the music player
     * @param musicPlayer the music player to set
     */
    public void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    /**
     * Sets the countdown future
     * @param countdownFuture the countdown future to set
     */
    public void setCountdownFuture(ScheduledFuture<?> countdownFuture) { this.countdownFuture = countdownFuture; }
    /** Sets the sound future
     * @param soundFuture the sound scheduled future to set
     */
    public void setSoundFuture(ScheduledFuture<?> soundFuture) { this.soundFuture = soundFuture; }

    /**
     * Compares this timer to another timer based
     * on the string representation of the timer.
     * Used for sorting timers.
     * @return a negative integer, zero, or a positive integer
     */
    @Override
    public int compareTo(Timer o)
    {
        return this.toString().compareTo(o.toString());
    }

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

    /**
     * Generates a hash code for the timer.
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(getHours(), getMinutes(), getSeconds(), getName());
    }

    /**
     * Returns a string representation of the Timer object.
     * @return "(Timer1) 08:30:00"
     */
    @Override
    public String toString()
    {
        return "(" + name + ")" + SPACE + getCountDownString();
    }
}