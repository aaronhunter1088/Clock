package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;

/**
 * Lap
 * <p>
 * A Lap is the time elapsed between one start and stop event of a Stopwatch.
 * A Stopwatch can have multiple laps, each representing a segment of time
 * recorded between pressing the lap button. When a lap is recorded, the
 * stopwatch continues to run, allowing for multiple laps to be recorded
 * without stopping the overall time measurement.
 *
 * @author michael ball
 * @version 2.9
 */
public class Lap implements Serializable, Comparable<Lap>, Cloneable
{
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Lap.class);
    private int lapNumber;
    private long duration;
    private long lapTime;
    private Stopwatch stopwatch;

    /**
     * The main constructor for creating a Lap
     * @param lapNumber the lap number
     * @param recordedAt the recordedAt of the stopwatch at the time of the lap
     * @param lapTime the recordedAt of the lap itself
     * @param stopwatch the stopwatch to which this lap belongs
     */
    public Lap(int lapNumber, long recordedAt, long lapTime, Stopwatch stopwatch)
    {
        setLapNumber(lapNumber);
        setDuration(recordedAt);
        setLapTime(lapTime);
        setStopwatch(stopwatch);
    }

    /**
     * Formats the duration of the lap into a string representation
     * @return the formatted duration as mm:ss.SSS
     */
    public String getFormattedDuration()
    {
        long msTotal = duration;
        long minutes = msTotal / 60_000;
        long seconds = (msTotal % 60_000) / 1000;
        long hundredths = msTotal % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, hundredths);
    }

    /**
     * Formats the lap time into a string representation
     * @return the formatted lap time as mm:ss.SSS
     */
    public String getFormattedLapTime()
    {
        long msTotal = lapTime;
        long minutes = msTotal / 60_000;
        long seconds = (msTotal % 60_000) / 1000;
        long hundredths = msTotal % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, hundredths);
    }

    /**
     * Compares this Lap to another Lap based on their lapNumber.
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer
     */
    @Override
    public int compareTo(Lap o)
    { return Integer.compare(this.lapNumber, o.lapNumber); }

    /**
     * Provides a string representation of a Lap, including
     * the name of the associated stopwatch, its lap number
     * and the formatted lap time.
     * @return a string representation of the Lap
     */
    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("Lap ");
        sb.append("for stopwatch ").append(stopwatch.getName()).append(" {");
        sb.append("lapNumber=").append(lapNumber);
        sb.append(", lapTime=").append(getFormattedLapTime());
        sb.append('}');
        return sb.toString();
    }

    /** Get the lap number */
    public int getLapNumber() { return lapNumber; }
    /** Get the duration */
    public long getDuration() { return duration; }
    /** Get the lap time */
    public long getLapTime() { return lapTime; }
    /** Get the stopwatch */
    public Stopwatch getStopwatch() { return stopwatch; }

    /** Set the lap number */
    public void setLapNumber(int lapNumber) { this.lapNumber = lapNumber; logger.debug("lapNumber set to {}", lapNumber); }
    /** Set the duration */
    public void setDuration(long duration) { this.duration = duration; logger.debug("duration set to {} seconds", duration); }
    /** Set the lap time */
    public void setLapTime(long lapTime) { this.lapTime = lapTime; logger.debug("lapTime set to {}", lapTime); }
    /** Set the stopwatch */
    public void setStopwatch(Stopwatch stopwatch) { this.stopwatch = stopwatch; logger.debug("stopwatch set to {}", stopwatch); }

    /**
     * Creates and returns a copy of this Lap.
     * Used in tests.
     */
    @Override
    public Lap clone() {
        try {
            Lap clone = (Lap) super.clone();
            clone.setStopwatch(new Stopwatch(stopwatch.getName(), stopwatch.isStarted(), stopwatch.isPaused(), stopwatch.getClock()));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
