package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static clock.util.Constants.STOPWATCH_READING_FORMAT;

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
 * @version since 2.9
 */
public class Stopwatch implements Serializable, Comparable<Stopwatch>
{
    @Serial
    private static final long serialVersionUID = 1L;
    /** The logger */
    private static final Logger logger = LogManager.getLogger(Stopwatch.class);
    /** Running count of total Stopwatch instances created; resets at 100. */
    public static long stopwatchCounter = 0L;
    /** The name of the stopwatch */
    private String name;
    /** Indicates if the stopwatch is paused or not */
    private boolean paused;
    /** Indicates if the stopwatch is started or not */
    private boolean started;
    /** The start time in milliseconds */
    private long startMilli = 0L;
    /** The time accumulated across previous runs */
    private long accumMilli = 0L;
    /** The elapsed time since the last lap */
    private long lastLapMarkMilli = 0L;
    /** The total paused time accumulated */
    private long pausedAccumMilli = 0L;
    /** The total paused time with previous pauses */
    private long totalPausedMilli = 0L;
    /** The total currently paused time */
    private long pausedMilli = 0L;
    /** The laps belonging to this stopwatch */
    private List<Lap> laps;
    /** The time expressed as a duration */
    private transient Duration duration;
    /** Reference to the clock */
    private transient Clock clock;
    /** The scheduled future */
    private transient ScheduledFuture<?> scheduledFuture;

    /**
     * The main constructor for creating a Stopwatch
     * @param name the name of the stopwatch
     * @param started whether the stopwatch has been started
     * @param paused whether the stopwatch is paused
     * @param clock the clock object associated with this stopwatch
     * @throws IllegalArgumentException if the input values are invalid
     */
    public Stopwatch(String name, boolean started, boolean paused, Clock clock)
    {
        setName(name);
        setStarted(started);
        setPaused(paused);
        setClock(clock);
        setLaps(new ArrayList<>());
        setDuration(Duration.ZERO);
        stopwatchCounter++;
        logger.debug("Total stopwatches created: {}", stopwatchCounter);
        if (stopwatchCounter == 100L)
        {
            logger.info("Restarting counter for stopwatchCounter");
            stopwatchCounter = 0L;
        }
    }

    /**
     * This method begins the thread that runs the stopwatch.
     * @param scheduler the executor service used to schedule the count-up task
     */
    public synchronized void startStopwatch(ScheduledExecutorService scheduler)
    {
        if (scheduledFuture == null || scheduledFuture.isDone() || scheduledFuture.isCancelled())
        {
            logger.debug("starting {}", this);

            setStarted(true);
            setPaused(false);


            scheduledFuture = scheduler.scheduleAtFixedRate(
                    () -> performCountUp(System.currentTimeMillis()),
                    0,
                    1,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    /**
     * Stop the stopwatch
     */
    public synchronized void stopStopwatch()
    {
        if (scheduledFuture != null)
        {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
        setName(null);
        setPaused(false);
        setStarted(false);
        setClock(null); // ? should we nullify the clock?
        setDuration(Duration.ZERO);
        setLaps(null);
        startMilli = 0L;
        accumMilli = 0L;
        lastLapMarkMilli = 0L;
        pausedAccumMilli = 0L;
        totalPausedMilli = 0L;
        pausedMilli = 0L;
        logger.debug("{} stopwatch stopped", this);
    }

    /** Pauses the stopwatch */
    public synchronized void pauseStopwatch()
    {
        if (started && !paused)
        {
            pausedMilli = System.currentTimeMillis();
            setPaused(true);
            logger.debug("{} paused", this);
        }
    }

    /**
     * Resumes the stopwatch
     */
    public synchronized void resumeStopwatch()
    {
        if (paused)
        {
            logger.debug("paused for {} seconds", Duration.ofMillis(pausedAccumMilli).getSeconds());
            totalPausedMilli += pausedAccumMilli;
            setPausedAccumMilli(0L);
            setPaused(false);
            logger.debug("resuming {}", this);
        }
    }

    /**
     * This method performs the count up logic for the stopwatch.
     * It checks if the stopwatch is paused, started, or stopped,
     * and updates the accumulated time accordingly.
     */
    private synchronized void performCountUp(long now)
    {
        if (!started)
        {
            logger.debug("{} not started", this);
            return;
        }
        if (paused)
        {
            //logger.debug("{} paused", this);
            pausedAccumMilli = now - pausedMilli;
            return;
        }
        if (startMilli == 0L)
        {
            //logger.debug("{} started for the first time", this);
            startMilli = now;
            lastLapMarkMilli = now;
            return;
        }
        setDuration(Duration.ofMillis(now - startMilli - totalPausedMilli));
        accumMilli = duration.toMillis();

        //logger.info("{} elapsed time: {}", this.getName(), elapsedFormatted(accumMilli, STOPWATCH_READING_FORMAT));
        endIfMaxAccumMilli();
    }

    /**
     * This method checks if the stopwatch has been running
     * for 1 hour (the maximum allowed time). If it has,
     * the stopwatch is stopped.
     * TODO: Nice to have, allow the user to set this, to some limit, so maybe they want the new default to be 3 hours...
     */
    private void endIfMaxAccumMilli()
    {
        if (Duration.of(1, ChronoUnit.HOURS).minus(duration).isZero() ||
                Duration.of(1, ChronoUnit.HOURS).minus(duration).isNegative())
        {
            logger.info("{} has reached max time of 1 hour, stopping", this);
            stopStopwatch();
        }
    }

    /**
     * Records a lap for the stopwatch.
     * Logic: Take the time (now), get the minutes, seconds and milliseconds since last lap.
     * Subtract the now from the last lap to get duration of lap.
     */
    public synchronized void recordLap()
    {
        if (!started || laps == null)
        {
            return;
        }
        long now = System.currentTimeMillis();
        long lastRecordedDuration = 0L;
        if (!laps.isEmpty())
        {
            lastRecordedDuration = laps.getLast().getRecordedAt();
        }
        long thisDuration = now - startMilli - totalPausedMilli;

        Lap lap = new Lap(
                laps.size() + 1,
                thisDuration,
                thisDuration - lastRecordedDuration,
                this
        );
        lastLapMarkMilli = now;

        logger.info("Recording lap #{}, time: {} for stopwatch:{}",
                lap.getLapNumber(),
                lap.getFormattedRecordedAt(),
                this.getName());
        laps.add(lap);
    }

    /**
     * Returns the elapsed time as the specified formatted string
     * @param millis the elapsed time in milliseconds
     * @param format the format string, e.g. "%02d:%02d.%03d" for mm:ss.SSS
     * @return the elapsed time as the specified formatted string
     */
    public synchronized String elapsedFormatted(long millis, String format)
    {
        //logger.debug("elapsedFormatted");
        long minutes = millis / 60_000;
        long seconds = (millis % 60_000) / 1000;
        long hundredths = millis % 1000;
        return String.format(format, minutes, seconds, hundredths);
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     * @param message a custom message to print out
     */
    public void printStackTrace(Exception e, String message)
    {
        if (message != null) logger.error(message);
        if (e.getMessage() != null) logger.error(e.getMessage());

        for (StackTraceElement ste : e.getStackTrace())
        {
            logger.error(ste.toString());
        }
    }

    /**
     * Compares this stopwatch to another stopwatch based
     * on the stopwatch name.
     * @return a negative integer, zero, or a positive integer
     */
    @Override
    public int compareTo(Stopwatch o)
    {
        if (this.getName() == null && o.getName() == null) return 0;
        if (this.getName() == null) return -1;
        if (o.getName() == null) return 1;
        return this.getName().compareTo(o.getName());
    }

    /**
     * Checks if two stopwatches are equal based on name.
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Stopwatch sw)) return false;
        return Objects.equals(getName(), sw.getName());
    }

    /**
     * Generates a hash code for the stopwatch.
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(getName());
    }

    /**
     * Provides a string representation of the Stopwatch
     * @return a string representation of the Stopwatch
     */
    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("Stopwatch {");
        sb.append("name='").append(name).append('\'');
        sb.append(", started=").append(started);
        sb.append(", paused=").append(paused);
        if (paused)
        {
            sb.append(", pausedAccumMilli=")
                    .append(elapsedFormatted(pausedAccumMilli, STOPWATCH_READING_FORMAT));
        }
        sb.append(", elapsed=").append(elapsedFormatted(accumMilli, STOPWATCH_READING_FORMAT));
        sb.append(", laps=").append(laps == null ? 0 : laps.size());
        sb.append('}');
        return sb.toString();
    }

    /**
     * Returns the clock
     * @return the clock reference
     */
    public Clock getClock() { return clock; }
    /**
     * Returns paused
     * @return true if the stopwatch is paused
     */
    public boolean isPaused() { return paused; }
    /**
     * Returns the name
     * @return the name of the stopwatch
     */
    public String getName() { return name; }
    /**
     * Returns started
     * @return true if the stopwatch has been started
     */
    public boolean isStarted() { return started; }
    /**
     * Returns the list of laps
     * @return the list of laps recorded
     */
    public List<Lap> getLaps() { return laps; }
    /**
     * Returns the total paused milliseconds
     * @return the total paused duration in milliseconds
     */
    public long getTotalPausedMilli() { return totalPausedMilli; }
    /**
     * Returns the last lap mark in milliseconds
     * @return the elapsed milliseconds at the last lap
     */
    public long getLastLapMarkMilli() { return lastLapMarkMilli; }
    /**
     * Returns the duration
     * @return the elapsed duration
     */
    public Duration getDuration() { return duration; }
    /**
     * Returns the start time in milliseconds
     * @return the start time in milliseconds
     */
    public long getStartMilli() { return startMilli; }
    /**
     * Returns when Pause was pressed in milliseconds
     * @return the timestamp when pause was pressed
     */
    public long getPausedMilli() { return pausedMilli; }
    /**
     * Returns the accumulated paused milliseconds
     * @return the total accumulated milliseconds
     */
    public long getAccumMilli() { return accumMilli; }
    /**
     * Returns the accumulated paused milliseconds
     * @return the accumulated paused duration in milliseconds
     */
    public long getPausedAccumMilli() { return pausedAccumMilli; }
    /**
     * Returns the scheduled future
     * @return the scheduled future
     */
    public ScheduledFuture<?> getScheduledFuture() { return scheduledFuture; }

    /**
     * Set the clock
     * @param clock the clock to set
     */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    /**
     * Set paused
     * @param paused the paused state to set
     */
    public void setPaused(boolean paused) { this.paused = paused; logger.debug("paused set to {}", paused); }
    /**
     * Set the name
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; logger.debug("name set to {}", name); }
    /**
     * Set started
     * @param started the started state to set
     */
    public void setStarted(boolean started) { this.started = started; logger.debug("started set to {}", started); }
    /**
     * Set the laps
     * @param laps the list of laps to set
     */
    public void setLaps(List<Lap> laps) { this.laps = laps; if (laps != null) logger.debug("laps set"); else logger.debug("laps set to null"); }
    /**
     * Set the duration
     * @param duration the duration to set
     */
    public void setDuration(Duration duration) { this.duration = duration; /* logger.debug("duration set to {}", duration); */ }
    /**
     * Set the accumulated paused milliseconds
     * @param pausedAccumMilli the accumulated paused milliseconds to set
     */
    public void setPausedAccumMilli(long pausedAccumMilli) { this.pausedAccumMilli = pausedAccumMilli; /* logger.debug("pausedAccumMilli set to {}", pausedAccumMilli); */ }
    /**
     * Set the scheduled future
     * @param scheduledFuture the scheduled future to set
     */
    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) { this.scheduledFuture = scheduledFuture; logger.debug("scheduledFuture set"); }
}