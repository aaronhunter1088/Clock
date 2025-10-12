package clock;

import clock.entity.Clock;
import clock.exception.InvalidInputException;
import clock.panel.ClockFrame;
import clock.entity.Panel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;

import static clock.util.Constants.COMMA;

/**
 * Main class to start the application.
 *
 * @author Michael Ball
 * @version 1.0
 */
public class Main
{
    private final static Logger logger = LogManager.getLogger(Main.class);

    private static ClockFrame clockFrame;

    /**
     * Main method to start the Clock
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        startMain(args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clockFrame.stop();
            logger.info("Closing Clock application");
        }));
    }

    /**
     * Starts the clock application based on the provided arguments.
     * The first argument determines the start type:
     * - "0" for default start
     * - "1" for specific panel start with an additional argument
     * - "2" for specific clock start with additional arguments
     *
     * @param args command line arguments
     */
    public static void startMain(String[] args)
    {
        int startValue = 0;
        try {
            int starting = Integer.parseInt(List.of(args[0].split(COMMA)).getFirst());
            startValue = Math.max(0, starting);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("No arguments provided, starting default clock.");
        } catch (Exception e) {
            logger.warn("Error: {}", e.getMessage());
        }
        if (startValue == 0) {
            defaultStart();
        }
        else if (startValue == 1) {
            try {
                specificPanelStart(args);
            } catch (InvalidInputException | IllegalArgumentException e) {
                defaultStart();
            }

        }
        else if (startValue == 2) {
            try {
                specificClockStart(args);
            } catch (InvalidInputException | IllegalArgumentException e) {
                defaultStart();
            }
        }
    }

    /**
     * Starts the clock application with the default panel and clock settings.
     */
    public static void defaultStart()
    { SwingUtilities.invokeLater(() -> clockFrame = ClockFrame.createAndShowGUI()); }

    /**
     * Starts the clock application with a specific panel.
     * args should be in the format: "1","panel_alarm" or
     * whichever panel you want to start with.
     * @param args list of strings
     */
    public static void specificPanelStart(String[] args)
    {
        Panel type = Panel.valueOf(List.of(args[0].split(COMMA)).get(1).toUpperCase());
        SwingUtilities.invokeLater(() -> clockFrame = ClockFrame.createAndShowGUI(type));
    }

    /**
     * Starts the clock application at a specific time.
     * The args should be in the format:
     * "2","23","55","10","JULY","WEDNESDAY","30","2025","PM"
     * where: "2" is the start value
     * "23" is the hour (in 24-hour format),
     * "55" is the minutes,
     * "10" is the seconds,
     * "JULY" is the month,
     * "WEDNESDAY" is the day of the week,
     * "30" is the day of the month,
     * "2025" is the year,
     * "PM" is the AM/PM designation.
     * @param args list of strings containing the clock settings
     */
    public static void specificClockStart(String[] args)
    {
        List<String> values = List.of(args[0].split(COMMA));
        int hours = Integer.parseInt(values.get(1)); // hours more than 12 will enforce showMilitaryTime
        int minutes = Integer.parseInt(values.get(2));
        int seconds = Integer.parseInt(values.get(3));
        Month month = Month.valueOf(values.get(4).toUpperCase());
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(values.get(5).toUpperCase());
        int dayOfMonth = Integer.parseInt(values.get(6));
        int year = Integer.parseInt(values.get(7));
        String ampm = values.get(8).toUpperCase();
        Clock testClock = new Clock(hours, minutes, seconds, month, dayOfWeek, dayOfMonth, year, ampm);
        SwingUtilities.invokeLater(() -> clockFrame = ClockFrame.createAndShowGUI(testClock));
    }
}