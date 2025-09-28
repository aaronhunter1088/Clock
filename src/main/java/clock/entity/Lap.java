package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Lap implements Serializable, Comparable<Lap>
{
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Lap.class);
    private int lapNumber;
    private Duration duration;
    private long lapTime;
    private Stopwatch stopwatch;

    public Lap(int lapNumber, Duration duration, Stopwatch stopwatch)
    {
        setLapNumber(lapNumber);
        setDuration(duration);
        setLapTime(duration.getSeconds());
        setStopwatch(stopwatch);
    }

    @Override
    public int compareTo(Lap o)
    { return Integer.compare(this.lapNumber, o.lapNumber); }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Lap ");
        sb.append("for stopwatch ").append(stopwatch.getName()).append(" {");
        sb.append("lapNumber=").append(lapNumber);
        //sb.append(", duration=").append(duration);
        sb.append(", lapTime=").append(lapTime);
        sb.append('}');
        return sb.toString();
    }

    /* Getters */
    public int getLapNumber() { return lapNumber; }
    public Duration getDuration() { return duration; }
    public long getLapTime() { return lapTime; }
    public Stopwatch getStopwatch() { return stopwatch; }

    /* Setters */
    public void setLapNumber(int lapNumber) { this.lapNumber = lapNumber; logger.debug("lapNumber set to {}", lapNumber); }
    public void setDuration(Duration duration) { this.duration = duration; logger.debug("lapTime set to {}", duration); }
    public void setLapTime(long lapTime) { this.lapTime = lapTime; logger.debug("lapTime set to {}", lapTime); }
    public void setStopwatch(Stopwatch stopwatch) { this.stopwatch = stopwatch; logger.debug("stopwatch set to {}", stopwatch); }
}
