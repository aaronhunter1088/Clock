package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

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
    private String name,
                   hoursAsStr,
                   minutesAsStr,
                   secondsAsStr;
    private boolean paused,
                    started,
                    stopped;
    private Clock clock;
    private Thread selfThread;
    private long startNano = 0L;           // last start() nano timestamp
    private long accumulatedNano = 0L;     // time accumulated across previous runs
    private long lastLapMarkNano = 0L;     // elapsed ns at last lap
    private long pausedAccumNano;  // total paused duration accumulated
    private long pauseStartNano;   // when Pause was pressed
    private List<Lap> laps;

    // start here

    // True elapsed, independent of refresh rate
    public long elapsedNanos() {
        if (startNano == 0L)
        {
            logger.debug("startNano is zero");
            return 0L;
        }
        long now = System.nanoTime();
        long paused = pausedAccumNano + (started ? 0L : (pauseStartNano == 0L ? 0L : (now - pauseStartNano)));
        return now - startNano - paused;
    }

    // Convenience
    public String elapsedFormatted() {
        logger.debug("elapsedFormatted");
        long ns = elapsedNanos();
        long msTotal = ns / 1_000_000L;
        long minutes = msTotal / 60_000;
        long seconds = (msTotal % 60_000) / 1000;
        long hundredths = msTotal % 1000; // 3 decimals: .000
        return String.format("%02d:%02d.%03d", minutes, seconds, hundredths);

//        String hhmmssms = String.format("%02d:%02d:%02d.%03d",
//                ns.getLapTime() / 3600,
//                (lap.getLapTime() % 3600) / 60,
//                lap.getLapTime() % 60,
//                (int)((lap.getLapTime() / 1_000_000) % 1000)); // Output: 00:01:15.123
    }

    /**
     * Returns the total elapsed time as a string in HH:MM:SS format.
     */
    public String elapsedTotalTimeString() {
        long ns = elapsedNanos();
        long msTotal = ns / 1_000_000L;
        long seconds = (msTotal / 1000) % 60;
        long minutes = (msTotal / 60_000) % 60;
        long hours = msTotal / 3_600_000;
        return String.format("%02d:%02d.%02d", minutes, seconds, msTotal);
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
//    @Override
//    public void run()
//    {
//        while (selfThread != null)
//        {
//            try {
//                if (!paused && !stopped) {
//                    performCountUp();
//                    sleep(1);
//                } else { //if (paused || stopped) {
//                    sleep(1000);
//                }
//            }
//            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
//        }
//    }

    @Override
    public void run()
    {
        long lastUpdate = 0L;
        while (selfThread != null)
        {
            try {
                if (!paused && !stopped) {
                    long now = System.nanoTime();
                    if (now - lastUpdate >= 1_000_000 || startNano == 0L) { // update every millisecond
                        performCountUp();
                        lastUpdate = now;
                    }
                    sleep(1);
                } else {
                    if (paused) {
                        logger.debug("{} is paused", this.getName());
                    } else { // if (stopped) {
                        logger.debug("{} is stopped", this.getName());
                    }
                    sleep(1000);
                }
            }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage()); }
        }
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("Stopwatch {");
        sb.append("name='").append(name).append('\'');
        sb.append(", started=").append(started);
        sb.append(", paused=").append(paused);
        sb.append(", stopped=").append(stopped);
        sb.append(", accumulatedNano=").append(accumulatedNano);
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
        //setSelfThread(null); // TODO: Check if this is best way to stop thread
        logger.info("{} stopwatch stopped", this);
    }

    /**
     * Pauses the stopwatch
     */
    public synchronized void pauseStopwatch()
    {
        logger.info("pausing {}", this);
        pauseStartNano = System.nanoTime();
        setPaused(true);
    }

    /**
     * Resumes the stopwatch
     */
    public synchronized void resumeStopwatch()
    {
        logger.info("resuming {}", this);
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
        startNano = 0L;
        pausedAccumNano = 0L;
        pauseStartNano = 0L;
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
        logger.debug("{} ticking up...", this.getName());
        long now = System.nanoTime();
        if (pauseStartNano != 0L) {
            // we were paused; add paused duration
            pausedAccumNano += (now - pauseStartNano);
            pauseStartNano = 0L;
        } else if (startNano == 0L) {
            // first ever start
            logger.debug("startNano now set");
            startNano = now;
        }

    }

    public void recordLap()
    {
        Duration elapsed = Duration.ofNanos(elapsed());
        Lap lap = new Lap(laps.size() + 1, elapsed, this);
        String hhmmssms = String.format("%02d:%02d:%02d.%03d",
                lap.getLapTime() / 3600,
                (lap.getLapTime() % 3600) / 60,
                lap.getLapTime() % 60,
                (int)((lap.getLapTime() / 1_000_000) % 1000)); // Output: 00:01:15.123
        logger.info("Recording lap {}, time: {} for stopwatch:{}", lap.getLapNumber(), hhmmssms, this.getName());
        laps.add(lap);
    }

    public synchronized long elapsed() {
        long now  = System.nanoTime();
        long currentElapsed = now - startNano;
        accumulatedNano += currentElapsed;
        startNano = now;
        return currentElapsed;
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

