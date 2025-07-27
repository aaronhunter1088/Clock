package clock;

import javax.swing.*;

import clock.entity.Clock;
import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
        Clock clock = new Clock();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Closing Clock")));

        SwingUtilities.invokeLater(() -> {
            try {
                var scheduler = clock.getScheduler();

                // Wrap tasks to prevent exceptions from killing scheduled execution
                Function<Runnable, Runnable> taskRunner = task -> () -> {
                    try {
                        task.run();
                    } catch (Exception e) {
                        logger.error("Scheduled task failed: {}", task, e);
                    }
                };

                scheduler.scheduleAtFixedRate(taskRunner.apply(clock::tick), 0, 1, TimeUnit.SECONDS);
                scheduler.scheduleAtFixedRate(taskRunner.apply(clock::setActiveAlarms), 0, 1, TimeUnit.SECONDS);
                scheduler.scheduleAtFixedRate(taskRunner.apply(clock::triggerAlarms), 0, 1, TimeUnit.SECONDS);
                scheduler.scheduleAtFixedRate(taskRunner.apply(clock::checkIfAnyTimersAreGoingOff), 0, 1, TimeUnit.SECONDS);
                scheduler.scheduleAtFixedRate(taskRunner.apply(clock::checkIfItIsNewYears), 0, 1, TimeUnit.SECONDS);
                scheduler.scheduleAtFixedRate(taskRunner.apply(clock::setTheCurrentTime), 0, 1, TimeUnit.SECONDS);

            } catch (Exception e) {
                logger.error("Error scheduling tasks in Clock", e);
            }
        });
    }

}