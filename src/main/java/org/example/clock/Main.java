package org.example.clock;

import javax.swing.*;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;
import static java.time.DayOfWeek.*;
import static java.time.Month.*;

/**
 * Main application to start Clock
 *
 * @author Michael Ball
 * @version 2.6
 */
public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void main(String[] args) throws ParseException, InvalidInputException {

        logger.info("Starting Clock...");
        Clock clock = new Clock();
        //new Clock(1, 59, 50, MARCH, SUNDAY, 10, 2024, Time.AM);
        logger.info("Clock started. Date {}", clock.getDate());
        logger.info("Clock started. Time {}", DateTimeFormatter.ofPattern("hh:mm:ss a").format(clock.getCurrentTime()));
        SwingUtilities.invokeLater(() -> SwingUtilities.updateComponentTreeUI(clock));
        try {
            while (true) {
                clock.tick();
                sleep(250);
                // check alarms
                clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
                // check timers
                clock.getTimerPanel().checkIfTimerHasConcluded();
                sleep(250);
                if (clock.isNewYear()) {
                    clock.setIsNewYear(false);
                    logger.info("Happy New Year. Here's wishing you a healthy, productive " + clock.getYear() + ".");
                }
                sleep(250);
                clock.setCurrentTime();
                sleep(250);
            }
        }
        catch (Exception e)
        { logger.error("Exception in clock: {}", e.getMessage()); }
    }
}
