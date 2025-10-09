package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
    private String name;
    private boolean paused,
                    started;
    private Clock clock;
    private volatile Thread selfThread;
    private long startMilli = 0L;        // start time in milliseconds
    private long accumMilli = 0L;        // time accumulated across previous runs
    private long accumMilliInSec = 0L;   // accum in seconds for logging
    private long lastLapMarkMilli = 0L;  // elapsed ns at last lap
    private long pausedAccumMilli = 0L;  // total paused duration accumulated
    private long totalPausedMilli = 0L;  // total paused duration accumulated
    private long pauseStartMilli = 0L;   // when Pause was pressed
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
        setSelfThread(null);
        logger.info("{} stopwatch stopped", this);
    }

    /** Pauses the stopwatch */
    public synchronized void pauseStopwatch()
    {
        pauseStartMilli = System.currentTimeMillis();
        setPaused(true);
        logger.info("{} paused", this);
    }

    /**
     * Resumes the stopwatch
     */
    public synchronized void resumeStopwatch()
    {
        if (pauseStartMilli != 0L)
        {
            pauseStartMilli = 0L;
            totalPausedMilli += pausedAccumMilli;
        }
        setPaused(false);
        logger.info("resuming {}", this);
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
        if (pauseStartMilli != 0L) {
            // we were paused; add paused duration
            pausedAccumMilli = (now - pauseStartMilli);
        } else if (startMilli == 0L) {
            // first start
            startMilli = now;
            lastLapMarkMilli = now;
        } else {
            accumMilli = (now - startMilli) - totalPausedMilli;
            long accumMilliAsSeconds = Duration.ofMillis(accumMilli).getSeconds();
            if (accumMilliInSec < accumMilliAsSeconds)
            {
                accumMilliInSec = accumMilliAsSeconds;
            }
            logger.info("{} elapsed time: {}", this.getName(), elapsedFormatted());
            endIfMaxAccumMilli();
        }
    }

    /**
     * This method checks if the accumulated time has reached the maximum value.
     * If it has, the stopwatch is stopped.
     */
    private void endIfMaxAccumMilli()
    {
        if (accumMilli >= Duration.of(1, ChronoUnit.HOURS).toMillis())
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
        Lap lap = new Lap(laps.size() + 1, (now - startMilli - totalPausedMilli),
                          (now - lastLapMarkMilli - pausedAccumMilli), this);
        lastLapMarkMilli = now;
        pausedAccumMilli = 0L; // reset
        String mmssms = lap.getFormattedDuration();
        logger.info("Recording lap #{}, time: {} for stopwatch:{}", lap.getLapNumber(), mmssms, this.getName());
        laps.add(lap);
    }

    /**
     * Returns the elapsed time as a formatted string in MM:SS.MiS format.
     * @return the elapsed time as a formatted string in MM:SS.MiS format
     */
    public String elapsedFormatted()
    {
        logger.debug("elapsedFormatted");
        long msTotal = accumMilli;
        long minutes = msTotal / 60_000;
        long seconds = (msTotal % 60_000) / 1000;
        long hundredths = msTotal % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, hundredths);
    }

    /**
     * Returns the accumulated elapsed time as a string in MM:SS:MiS format.
     * @return the accumulated elapsed time as a string in MM:SS:MiS format
     */
    public String elapsedAccumulated()
    {
        logger.debug("elapsedAccumulated");
        long msTotal = accumMilli;
        long minutes = msTotal / 60_000;
        long seconds = (msTotal % 60_000) / 1000;
        long hundredths = msTotal % 1000;
        return String.format("%02d:%02d:%03d", minutes, seconds, hundredths);
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
        sb.append(", accumulatedNano=").append(accumMilli);
        sb.append(", laps=").append(laps.size());
        sb.append('}');
        return sb.toString();
    }

    /* Getters */
    public Clock getClock() { return clock; }
    public boolean isPaused() { return paused; }
    public String getName() { return name; }
    public boolean isStarted() { return started; }
    public Thread getSelfThread() { return selfThread; }
    public List<Lap> getLaps() { return laps; }
    public long getPauseStartMilli() { return pauseStartMilli; }
    public long getTotalPausedMilli() { return totalPausedMilli; }
    public long getLastLapMarkMilli() { return lastLapMarkMilli; }

    /* Setters */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    public void setPaused(boolean paused) { this.paused = paused; logger.debug("paused set to {}", paused); }
    public void setName(String name) { this.name = name; logger.debug("name set to {}", name); }
    public void setStarted(boolean started) { this.started = started; logger.debug("started set to {}", started); }
    public void setSelfThread(Thread selfThread) { this.selfThread = selfThread; logger.debug("selfThread set to {}", selfThread); }
    public void setLaps(List<Lap> laps) { this.laps = laps; logger.debug("laps set"); }
}

