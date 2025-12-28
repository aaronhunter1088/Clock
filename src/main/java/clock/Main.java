package clock;

import clock.entity.Clock;
import clock.exception.InvalidInputException;
import clock.panel.ClockFrame;
import clock.entity.Panel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static clock.util.Constants.*;

/**
 * Main class to start the application.
 *
 * @author michael ball
 * @version since 1.0
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
    public static void main(String[] args) throws Exception
    {
        startMain(args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clockFrame.stop();
            logger.info("Closing Clock application");
        }));
    }

    /**
     * Starts the clock application based on the provided arguments.
     * - 0 args for default start
     * - 1 args: a specific panel to start up in
     * - 7 args: month, dateOfMonth, year, hours, minutes, seconds, am/pm
     * @param args command line arguments
     * @throws Exception if the number of arguments is invalid
     * or if the arguments are provided incorrectly.
     */
    public static void startMain(String[] args) throws Exception
    {
        int startValue = args != null ? args.length : 0;
        if (startValue == 0) {
            defaultStart();
        }
        else if (startValue == 1) {
            specificPanelStart(args);
        }
        else if (startValue == 7) {
            specificClockStart(args);
        }
        else {
            throw new InvalidInputException("Invalid number of arguments provided.");
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
        Panel type = Panel.valueOf(List.of(args).getFirst().toUpperCase());
        SwingUtilities.invokeLater(() -> clockFrame = ClockFrame.createAndShowGUI(type));
    }

    /**
     * Starts the clock application at a specific time.
     * The args should be in the format:
     * month day year hour minute second ampm
     * This is a space separated list of values.
     * Last option will enforce military time if hours >= 12 so, the
     * AM/PM will be ignored in that case. But if it is less than 12, it
     * could be either or, so AM/PM
     * @param args list of strings containing the clock settings
     */
    public static void specificClockStart(String[] args)
    {
        List<String> values = Arrays.stream(args).toList();
        Month month = Month.valueOf(values.get(0).toUpperCase());
        int dayOfMonth = Integer.parseInt(values.get(1));
        int year = Integer.parseInt(values.get(2));
        LocalDate localDate = LocalDate.of(year, month, dayOfMonth);

        int hours = Integer.parseInt(values.get(3)); // hours more than 12 will enforce showMilitaryTime
        int minutes = Integer.parseInt(values.get(4));
        int seconds = Integer.parseInt(values.get(5));

        String ampm = hours < 12 ? values.get(6).toUpperCase() : PM;
        Clock testClock = new Clock(hours, minutes, seconds, month, localDate.getDayOfWeek(), dayOfMonth, year, ampm);
        SwingUtilities.invokeLater(() -> clockFrame = ClockFrame.createAndShowGUI(testClock));
    }
}