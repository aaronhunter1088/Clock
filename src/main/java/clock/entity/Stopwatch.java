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
    private String name,
                   hoursAsStr,
                   minutesAsStr,
                   secondsAsStr;
    private boolean paused,
                    started,
                    stopped;
    private Clock clock;
    private Thread selfThread;
    private long startMilli = 0L;           // last start() nano timestamp
    private long accumMilli = 0L;     // time accumulated across previous runs
    private long lastLapMarkMilli = 0L;     // elapsed ns at last lap
    private long pausedAccumMilli = 0L;  // total paused duration accumulated
    private long totalPausedMilli = 0L;   // total paused duration accumulated
    private long pauseStartMilli = 0L;   // when Pause was pressed
    private List<Lap> laps;

    // Convenience
    public String elapsedFormatted() {
        logger.debug("elapsedFormatted");
        long msTotal = accumMilli;
        long minutes = msTotal / 60_000;
        long seconds = (msTotal % 60_000) / 1000;
        long hundredths = msTotal % 1000; // 3 decimals: .000
        return String.format("%02d:%02d.%03d", minutes, seconds, hundredths);
    }

    /**
     * Returns the accumulated elapsed time as a string in MM:SS:MS format
     * for easy reading and parsing.
     * @return the accumulated elapsed time as a string in MM:SS:MS format
     */
    public String elapsedAccumulated() {
        logger.debug("elapsedAccumulated");
        long msTotal = accumMilli;
        long minutes = msTotal / 60_000;
        long seconds = (msTotal % 60_000) / 1000;
        long hundredths = msTotal % 1000; // 3 decimals: .000
        return String.format("%02d:%02d:%03d", minutes, seconds, hundredths);
    }

    // end here

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
        setLaps(new ArrayList<>());
        stopwatchCounter++;
        logger.debug("Total stopwatches created: {}", stopwatchCounter);
        if (stopwatchCounter == 100L) {
            logger.info("Restarting counter for stopwatchCounter");
            stopwatchCounter = 0L;
        }
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
     * This method executes the logic for the stopwatch
     */
    @Override
    public void run()
    {
        while (selfThread != null)
        {
            try {
                performCountUp(System.currentTimeMillis());
//                if (!paused && !stopped) {
//                    performCountUp(System.currentTimeMillis());
//                } else {
//                    if (paused) {
//                        //logger.debug("{} is paused", this.getName());
//                    } else { // if (stopped) {
//                        //logger.debug("{} is stopped", this.getName());
//                    }
//                }
                sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method performs the count up
     * by increasing the seconds, minutes and hours
     * accordingly. If the stopwatch reaches 24 hours,
     * it will be deleted.
     */
    private void performCountUp(long now)
    {
        if (pauseStartMilli != 0L) {
            logger.debug("{} paused", this.getName());
            // we were paused; add paused duration
            pausedAccumMilli = (now - pauseStartMilli);
            //pauseStartMilli = 0L;
        } else if (startMilli == 0L) {
            // first ever start
            logger.debug("startMilli set to {}", now);
            startMilli = now;
            lastLapMarkMilli = now;
        } else {
            logger.debug("{} ticking up...", this.getName());
            accumMilli = elapsedMillis(now); // elapsed(now);
        }
    }

    // True elapsed, independent of refresh rate
    public long elapsedMillis(long now)
    {
        return (now - startMilli) - totalPausedMilli;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("Stopwatch {");
        sb.append("name='").append(name).append('\'');
        sb.append(", started=").append(started);
        sb.append(", paused=").append(paused);
        sb.append(", stopped=").append(stopped);
        sb.append(", accumulatedNano=").append(accumMilli);
        sb.append(", laps=").append(laps.size());
//        sb.append("hours=").append(hours);
//        sb.append(", minutes=").append(minutes);
//        sb.append(", seconds=").append(seconds);
//        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    /**
     * This method begins the thread
     * that runs the stopwatch.
     */
    public synchronized void startStopwatch()
    {
        if (selfThread == null)
        {
            setSelfThread(new Thread(this));
            selfThread.start();
        }
    }

    /**
     * Stop the stopwatch
     */
    public synchronized void stopStopwatch()
    {
        logger.info("stopping {}", this);
        setStopped(true);
        setSelfThread(null); // TODO: Check if this is best way to stop thread
        logger.info("{} stopwatch stopped", this);
    }

    /**
     * Pauses the stopwatch
     */
    public synchronized void pauseStopwatch()
    {
        logger.info("pausing {}", this);
        pauseStartMilli = System.currentTimeMillis();
        setPaused(true);
    }

    /**
     * Resumes the stopwatch
     */
    public synchronized void resumeStopwatch()
    {
        logger.info("resuming {}", this);
        if (pauseStartMilli != 0L) {
            pauseStartMilli = 0L;
            totalPausedMilli += pausedAccumMilli;
        }
        setPaused(false);
    }

    /**
     * Resets the stopwatch to its initial state.
     */
    public synchronized void resetStopwatch()
    {
        logger.info("resetting {}", this);
        setStarted(false);
        setStopped(false);
        setPaused(false);
        startMilli = 0L;
        accumMilli = 0L;
        pausedAccumMilli = 0L;
        pauseStartMilli = 0L;
        logger.info("{} stopwatch reset", this);
    }

    /**
     * Records a lap for the stopwatch.
     * TODO: Fix lap to include length of lap. Currently recording time lap was recorded.
     * Logic: Take the time (now), get the minutes, seconds and milliseconds since last lap.
     * Subtract the now from the last lap to get duration of lap.
     */
    public void recordLap()
    {
        long now = System.currentTimeMillis();
        //long el = elapsed(now); // now - startMilli
        //Duration elapsed = Duration.ofMillis(el);
        //Duration length = Duration.ofMillis(now - lastLapMarkMilli);
        Lap lap = new Lap(laps.size() + 1, elapsed(now), (now - lastLapMarkMilli), this);
        lastLapMarkMilli = now;
        String mmssms = lap.getFormattedDuration();
        logger.info("Recording lap {}, time: {} for stopwatch:{}", lap.getLapNumber(), mmssms, this.getName());
        laps.add(lap);
    }

    /**
     * This method returns the elapsed time in milliseconds
     * since the last start or lap event. It also updates
     * the accumulated time.
     * @return the elapsed time since the last event
     */
    public synchronized long elapsed(long now)
    {
        return now - startMilli; // currentElapsed
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
    public boolean isPaused() { return paused; }
    public String getSecondsAsStr() { return secondsAsStr; }
    public String getName() { return name; }
    public boolean isStarted() { return started; }
    public boolean isStopped() { return stopped; }
    public Thread getSelfThread() { return selfThread; }
    public List<Lap> getLaps() { return laps; }

    /* Setters */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    public void setSecondsAsStr(String secondsAsStr) { this.secondsAsStr = secondsAsStr; logger.debug("secondsAsStr set to {}", secondsAsStr); }
    public void setPaused(boolean paused) { this.paused = paused; logger.debug("paused set to {}", paused); }
    public void setName(String name) { this.name = name; logger.debug("name set to {}", name); }
    public void setStarted(boolean started) { this.started = started; logger.debug("started set to {}", started); }
    public void setStopped(boolean stopped) { this.stopped = stopped; logger.debug("stopped set to {}", stopped); }
    public void setSelfThread(Thread selfThread) { this.selfThread = selfThread; logger.debug("selfThread set to {}", selfThread); }
    public void setLaps(List<Lap> laps) { this.laps = laps; logger.debug("laps set"); }
}

