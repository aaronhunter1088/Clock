package com.example.clock;

import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.example.clock.ClockConstants.AM;
import static com.example.clock.ClockConstants.PM;
import static java.lang.Thread.sleep;
import static java.time.DayOfWeek.*;
import static java.time.Month.*;

/**
 * Main application to start Clock
 *
 * @author Michael Ball
*  @version 2.8
 */
public class Main
{
    static { System.setProperty("appName", Main.class.getSimpleName()); }
    private final static Logger logger = LogManager.getLogger(Main.class);

    /**
     * Main method to start the Clock
     * @param args command line arguments
     * @throws InvalidInputException if there is an invalid input
     */
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void main(String[] args) throws InvalidInputException
    {
        logger.info("Starting Clock...");
        Clock clock = new Clock();
        /*
        new Clock(1, 59, 50, MARCH, SUNDAY, 10, 2024, AM); // for testing DST on
        new Clock(1, 59, 50, NOVEMBER, SUNDAY, 3, 2024, AM); // for testing DST off
        */
        SwingUtilities.invokeLater(() -> SwingUtilities.updateComponentTreeUI(clock));
        try
        {
            while (true)
            {
                clock.tick();
                sleep(250);
                // check alarms
                clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
                // check timers
                clock.getTimerPanel().checkIfTimerHasConcluded();
                sleep(250);
                if (clock.isNewYear())
                {
                    clock.setIsNewYear(false);
                    logger.info("Happy New Year. Here's wishing you a healthy, productive {}.", clock.getYear());
                }
                sleep(250);
                clock.setTheCurrentTime();
                sleep(250);
            }
        }
        catch (Exception e)
        { logger.error("Exception in clock: {}", e.getMessage()); }
    }

}