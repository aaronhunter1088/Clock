package clock;

import clock.entity.Clock;
import clock.panel.ClockFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

import static clock.util.Constants.AM;
import static clock.util.Constants.PM;
import static java.time.DayOfWeek.*;
import static java.time.Month.*;

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
     */
    public static void main(String[] args)
    {
        // Start the clock application
        SwingUtilities.invokeLater(ClockFrame::createAndShowGUI);
        // Start the clock at a specific time
        //Clock testClock = new Clock(23, 59, 55, JULY, WEDNESDAY, 30, 2025, PM);
        //SwingUtilities.invokeLater(() -> ClockFrame.createAndShowGUI(testClock));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Closing Clock")));
    }
}