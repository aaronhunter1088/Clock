package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

import static clock.util.Constants.EMPTY;
import static clock.util.Constants.ZERO;
import static java.lang.Thread.sleep;

/**
 * Stopwatch
 * <p>
 * A Stopwatch is similar to a Timer, but instead of counting down,
 * it counts up from zero. It is used to measure the time elapsed between
 * a start and stop event. When you start a stopwatch, it will begin counting
 * up until you stop it. Stopping a stopwatch could mean one of two things:
 * 1) The user has hit the stop button, which completely stops the stopwatch,
 * or 2) the user has hit the lap button, which will record the current time
 * between the last start-stop event and begin counting up from zero on a new
 * lap. The main count up time will continue to be visible will all times
 * shown together.
 * A lap is the time elapsed between one start and stop event.
 *
 * @author michael ball
 * @version 2.9
 */
public class Stopwatch implements Serializable, Comparable<Stopwatch>, Runnable
{
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Stopwatch.class);
    public static long stopwatchCounter = 0L;
    private int hours,
                minutes,
                seconds;
    private String name,
                   hoursAsStr,
                   minutesAsStr,
                   secondsAsStr;
    private boolean paused,
                    started,
                    stopped;
    private Clock clock;
    private Thread selfThread;
    private LocalTime countUp;

    /**
     * The main constructor for creating a Stopwatch
     * @param name the name of the stopwatch
     * @param started whether the stopwatch has been started
     * @param paused whether the stopwatch is paused
     * @param stopped whether the stopwatch has been stopped
     * @param clock the clock object associated with this stopwatch
     * @throws IllegalArgumentException if the input values are invalid
     */
    public Stopwatch(String name, boolean started, boolean paused, boolean stopped, Clock clock)
    {
        setName(name);
        setStarted(started);
        setStopped(stopped);
        setPaused(paused);
        setClock(clock);
    }

    /**
     * Compares this stopwatch to another stopwatch based
     * on the string representation of the stopwatch.
     * Used for sorting stopwatches.
     * @return a negative integer, zero, or a positive integer
     */
    @Override
    public int compareTo(Stopwatch o)
    { return this.toString().compareTo(o.toString()); }

    /**
     * This method starts the stopwatch
     */
    @Override
    public void run()
    {
        while (selfThread != null)
        {
            try {
                if (!paused && !stopped) {
                    performCountUp();
                    sleep(10);
                } else { //if (paused || stopped) {
                    sleep(1000);
                }
            }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
        }
    }

    /**
     * This method begins the thread
     * that runs the stopwatch.
     */
    public void startStopwatch()
    {
        if (selfThread == null)
        {
            setSelfThread(new Thread(this));
            countUp = LocalTime.of(hours, minutes, seconds);
            selfThread.start();
        }
    }

    /**
     * Stop the stopwatch
     */
    public void stopStopwatch()
    {
        logger.info("stopping {}", this);
        setStopped(true);
        //setSelfThread(null); // TODO: Check if this is best way to stop thread
        logger.info("{} stopwatch stopped", this);
    }

    /**
     * Pauses the stopwatch
     */
    public void pauseStopwatch()
    {
        logger.info("pausing {}", this);
        setPaused(true);
    }

    /**
     * Resumes the stopwatch
     */
    public void resumeStopwatch()
    {
        logger.info("resuming {}", this);
        setPaused(false);
    }

    /**
     * Resets the stopwatch to its initial state.
     */
    public void resetStopwatch()
    {
        logger.info("resetting {}", this);
        setStarted(false);
        setStopped(false);
        setPaused(false);
        setCountUp(LocalTime.of(getHours(), getMinutes(), getSeconds()));
        logger.info("{} stopwatch reset", this);
    }

    /**
     * This method performs the count up
     * by increasing the seconds, minutes and hours
     * accordingly. If the stopwatch reaches 24 hours,
     * it will be deleted.
     */
    private void performCountUp()
    {
        if (!started)
        {
            setStarted(true);
        }
        logger.info("{} ticking up...", this);
        countUp = countUp.plusSeconds(1);
        logger.debug("CountUp: {}", getCountUpString());
        if (countUp.getHour() == 23 && countUp.getMinute() == 59 && countUp.getSecond() == 59)
        {
            logger.info("{} has reached maximum", this);
            stopStopwatch();
        }
        else
        {
            setSeconds(countUp.getSecond());
            setMinutes(countUp.getMinute());
            setHours(countUp.getHour());
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

    /* Getters */
    public Clock getClock() { return clock; }
    public int getHours() { return hours; }
    public String getHoursAsStr() { return hoursAsStr; }
    public int getMinutes() { return minutes; }
    public String getMinutesAsStr() { return minutesAsStr; }
    public LocalTime getCountUp() { return countUp; }
    public String getCountUpString() {
        String countupHours = countUp.getHour() < 10 ? ZERO + countUp.getHour() : String.valueOf(countUp.getHour());
        String countupMinutes = countUp.getMinute() < 10 ? ZERO + countUp.getMinute() : String.valueOf(countUp.getMinute());
        String countupSeconds = countUp.getSecond() < 10 ? ZERO + countUp.getSecond() : String.valueOf(countUp.getSecond());
        return String.format("%s:%s:%s", countupHours, countupMinutes, countupSeconds);
    }
    public boolean isPaused() { return paused; }
    public int getSeconds() { return seconds; }
    public String getSecondsAsStr() { return secondsAsStr; }
    public String getName() { return name; }
    public boolean isStarted() { return started; }
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
    public void setCountUp(LocalTime countUp) { this.countUp = countUp; logger.debug("countUp set to {}", countUp); }
    public void setPaused(boolean paused) { this.paused = paused; logger.debug("paused set to {}", paused); }
    public void setName(String name) { this.name = name; logger.debug("name set to {}", name); }
    public void setStarted(boolean started) { this.started = started; logger.debug("started set to {}", started); }
    public void setStopped(boolean stopped) { this.stopped = stopped; logger.debug("stopped set to {}", stopped); }
    public void setSelfThread(Thread selfThread) { this.selfThread = selfThread; logger.debug("selfThread set to {}", selfThread); }
}
