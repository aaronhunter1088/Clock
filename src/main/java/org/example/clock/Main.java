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
        logger.info(clock.getDate());
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

//    private static Runnable updateTheTime(Clock clock) {
//        return () -> {
//            LocalDateTime currentTime = formatCurrentTimeToNonMilitaryTime(clock);
//            LocalDateTime clockTime = clock.getCurrentTime();
//            logger.debug("time now: {}", currentTime);
//            logger.debug("clock time: {}", clockTime);
//            long secondsBetween = Duration.between(currentTime, clockTime).getSeconds();
//            logger.debug("seconds between: {}", secondsBetween);
//            //long secondsBetween2 = ChronoUnit.SECONDS.between(clockTime, currentTime);
//            //logger.debug("seconds between2: {}", secondsBetween2);
//            //if (secondsBetween != 0) {
//            if (secondsBetween > 10) {
//                logger.warn("Clock is off by more than 10 seconds. Resetting the time.");
//                clock.setTheTime(currentTime);
//            }
//        };
//    }
//
//    private static LocalDateTime formatCurrentTimeToNonMilitaryTime(Clock clock) {
//        ZoneId zoneId = clock.getTimezone(); //ZoneId.of("America/Chicago");
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
//        DateTimeFormatter ampmFormatter = DateTimeFormatter.ofPattern("a");
//        String ampm = zonedDateTime.format(ampmFormatter);
//        if (Time.PM.getStrValue().equals(ampm)) {
//            zonedDateTime = zonedDateTime.minusHours(12);
//        }
//        return zonedDateTime.toLocalDateTime();
//    }
}
