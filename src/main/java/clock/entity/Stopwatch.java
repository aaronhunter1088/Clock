package clock.entity;

import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

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
public class Stopwatch implements Serializable, Comparable<Alarm>, Runnable
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
                    hasBeenStarted,
                    stopped;
    private Clock clock;
    private Thread selfThread;
    private LocalTime countup;
}
