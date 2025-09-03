package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;

public class Lap implements Serializable, Comparable<Lap>
{
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Lap.class);
    private int lapNumber;
    private Duration lapTime;

    public Lap(int lapNumber, Duration lapTime)
    {
        setLapNumber(lapNumber);
        setLapTime(lapTime);
    }

    @Override
    public int compareTo(Lap o)
    { return Integer.compare(this.lapNumber, o.lapNumber); }

    /* Getters */
    public int getLapNumber() { return lapNumber; }
    public Duration getLapTime() { return lapTime; }

    /* Setters */
    public void setLapNumber(int lapNumber) { this.lapNumber = lapNumber; logger.debug("lapNumber set to {}", lapNumber); }
    public void setLapTime(Duration lapTime) { this.lapTime = lapTime; logger.debug("lapTime set to {}", lapTime); }
}
