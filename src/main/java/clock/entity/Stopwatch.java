package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static clock.util.Constants.STOPWATCH_READING_FORMAT;
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
 * @version since 2.9
 */
public class Stopwatch implements Serializable, Comparable<Stopwatch>, Runnable
{
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Stopwatch.class);
    public static long stopwatchCounter = 0L;
    private String name;
    private boolean paused,
                    started;
    private Clock clock;
    private volatile Thread selfThread;
    private long startMilli = 0L;        // start time in milliseconds
    private long accumMilli = 0L;        // time accumulated across previous runs
    private long lastLapMarkMilli = 0L;  // elapsed ns at last lap
    private long pausedAccumMilli = 0L;  // total paused duration accumulated
    private long totalPausedMilli = 0L;  // total paused duration with previous pauses
    private long pausedMilli = 0L;        // when Pause was pressed in milliseconds
    private Duration duration;
    private List<Lap> laps;

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
        if (stopwatchCounter == 100L) {
            logger.info("Restarting counter for stopwatchCounter");
            stopwatchCounter = 0L;
        }
    }

    /** This method begins the thread that runs the stopwatch. */
    public synchronized void startStopwatch()
    {
        if (selfThread == null)
        {
            setSelfThread(new Thread(this));
            selfThread.start();
            started = true;
        }
    }

    /**
     * Stop the stopwatch
     */
    public synchronized void stopStopwatch()
    {
        setName(null);
        setPaused(false);
        setStarted(false);
        setClock(null);
        setSelfThread(null);
        startMilli = 0L;
        accumMilli = 0L;
        lastLapMarkMilli = 0L;
        pausedAccumMilli = 0L;
        totalPausedMilli = 0L;
        pausedMilli = 0L;
        laps = null;
        logger.info("{} stopwatch stopped", this);
    }

    /** Pauses the stopwatch */
    public synchronized void pauseStopwatch()
    {
        pausedMilli = System.currentTimeMillis();
        setPaused(true);
        logger.info("{} paused", this);
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
            logger.info("resuming {}", this);
        }
    }

    /**
     * This method executes the logic for the stopwatch
     */
    @Override
    public void run()
    {
        while (selfThread != null)
        {
            try {
                performCountUp(System.currentTimeMillis());
                sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method performs the count up logic for the stopwatch.
     * It checks if the stopwatch is paused, started, or stopped,
     * and updates the accumulated time accordingly.
     */
    private void performCountUp(long now)
    {
        if (paused) {
            logger.debug("{} paused", this);
            pausedAccumMilli = (now - pausedMilli);
        } else if (startMilli == 0L) {
            // first start
            startMilli = now;
            lastLapMarkMilli = now;
        } else {
            setDuration(Duration.ofMillis(now - startMilli - totalPausedMilli));
            accumMilli = duration.toMillis();
            logger.info("{} elapsed time: {}", this.getName(), elapsedFormatted(accumMilli, STOPWATCH_READING_FORMAT));
            endIfMaxAccumMilli();
        }
    }

    /**
     * This method checks if the stopwatch has been running
     * for 1 hour (the maximum allowed time). If it has,
     * the stopwatch is stopped.
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
    public void recordLap()
    {
        long now = System.currentTimeMillis();
        long lastRecordedDuration = 0L;
        if (!laps.isEmpty()) lastRecordedDuration = laps.getLast().getDuration();
        long thisDuration = (now - startMilli - totalPausedMilli);
        Lap lap = new Lap(laps.size() + 1, thisDuration,
                          thisDuration - lastRecordedDuration, this);
        lastLapMarkMilli = now;
        String mmssms = lap.getFormattedDuration();
        logger.info("Recording lap #{}, time: {} for stopwatch:{}", lap.getLapNumber(), mmssms, this.getName());
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
        logger.debug("elapsedFormatted");
        long minutes = millis / 60_000;
        long seconds = (millis % 60_000) / 1000;
        long hundredths = millis % 1000;
        return String.format(format, minutes, seconds, hundredths);
    }

    /**
     * Compares this stopwatch to another stopwatch based
     * on the stopwatch name.
     * @return a negative integer, zero, or a positive integer
     */
    @Override
    public int compareTo(Stopwatch o)
    { return this.getName().compareTo(o.getName()); }

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
        if (paused) sb.append(", pausedAccumMilli=").append(elapsedFormatted(pausedAccumMilli, STOPWATCH_READING_FORMAT));
        sb.append(", elapsed=").append(elapsedFormatted(accumMilli, STOPWATCH_READING_FORMAT));
        sb.append(", laps=").append(laps.size());
        sb.append('}');
        return sb.toString();
    }

    /** Returns the clock */
    public Clock getClock() { return clock; }
    /** Returns paused */
    public boolean isPaused() { return paused; }
    /** Returns the name */
    public String getName() { return name; }
    /** Returns started */
    public boolean isStarted() { return started; }
    /** Returns the selfThread */
    public Thread getSelfThread() { return selfThread; }
    /** Returns the list of laps */
    public List<Lap> getLaps() { return laps; }
    /** Returns the total paused milliseconds */
    public long getTotalPausedMilli() { return totalPausedMilli; }
    /** Returns the last lap mark in milliseconds */
    public long getLastLapMarkMilli() { return lastLapMarkMilli; }
    /** Returns the duration */
    public Duration getDuration() { return duration; }
    /** Returns the start time in milliseconds */
    public long getStartMilli() { return startMilli; }
    /** Returns when Pause was pressed in milliseconds */
    public long getPausedMilli() { return pausedMilli; }
    /** Returns the accumulated paused milliseconds */
    public long getAccumMilli() { return accumMilli; }
    /** Returns the accumulated paused milliseconds */
    public long getPausedAccumMilli() { return pausedAccumMilli; }

    /** Set the clock */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    /** Set paused */
    public void setPaused(boolean paused) { this.paused = paused; logger.debug("paused set to {}", paused); }
    /** Set the name */
    public void setName(String name) { this.name = name; logger.debug("name set to {}", name); }
    /** Set started */
    public void setStarted(boolean started) { this.started = started; logger.debug("started set to {}", started); }
    /** Set the selfThread */
    public void setSelfThread(Thread selfThread) { this.selfThread = selfThread; logger.debug("selfThread set to {}", selfThread); }
    /** Set the laps */
    public void setLaps(List<Lap> laps) { this.laps = laps; logger.debug("laps set"); }
    /** Set the duration */
    public void setDuration(Duration duration) { this.duration = duration; logger.debug("duration set to {}", duration); }
    /** Set the accumulated paused milliseconds */
    public void setPausedAccumMilli(long pausedAccumMilli) { this.pausedAccumMilli = pausedAccumMilli; logger.debug("pausedAccumMilli set to {}", pausedAccumMilli); }

}

