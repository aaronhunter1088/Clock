package com.example.clock;

import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

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
    public static void main(String[] args) throws InvalidInputException
    {
        logger.info("Starting Clock...");
        Clock clock = new Clock();
        /*
        new Clock(1, 59, 50, MARCH, SUNDAY, 10, 2024, AM); // for testing DST on
        new Clock(1, 59, 50, NOVEMBER, SUNDAY, 3, 2024, AM); // for testing DST off
        */
        SwingUtilities.invokeLater(() -> {
            try {
                clock.getScheduler().scheduleAtFixedRate(clock::tick, 0, 1, TimeUnit.SECONDS);
                clock.getScheduler().scheduleAtFixedRate(clock.getAlarmPanel()::checkIfAnyAlarmsAreGoingOff, 0, 1, TimeUnit.SECONDS);
                clock.getScheduler().scheduleAtFixedRate(clock.getTimerPanel2()::checkIfAnyTimersAreGoingOff, 0, 1, TimeUnit.SECONDS);
                clock.getScheduler().scheduleAtFixedRate(clock::checkIfItIsNewYears, 0, 1, TimeUnit.SECONDS);
                clock.getScheduler().scheduleAtFixedRate(clock::setTheCurrentTime, 0, 1, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("Error in Clock: ", e);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Closing Clock")));
    }

}