package clock;

import javax.swing.*;

import clock.entity.Clock;
import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static clock.util.Constants.AM;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.Month.JULY;

/**
 * Main application to start Clock
 *
 * @author Michael Ball
*  @version 1.0
 */
public class Main
{
    private final static Logger logger = LogManager.getLogger();

    /**
     * Main method to start the Clock
     * @param args command line arguments
     * @throws InvalidInputException if there is an invalid input
     */
    public static void main(String[] args) throws InvalidInputException {
        logger.info("Starting Clock...");
        Clock clock = null;
        // Start the default clock:
        clock = new Clock();
        // Start the clock at a specific time:
        //clock = new Clock(11, 59, 50, JULY, SUNDAY, 27, 2025, AM);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Closing Clock")));

        Clock finalClock = clock;
        SwingUtilities.invokeLater(() -> {
            try {
                finalClock.start();
            } catch (Exception e) {
                logger.error("Error scheduling tasks in Clock", e);
            }
        });
    }

}