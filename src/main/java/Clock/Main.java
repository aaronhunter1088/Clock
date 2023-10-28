package Clock;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.text.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;

/**
 * Main application to start Clock
 *
 * @author Michael Ball
 * @version 2.5
 */
public class Main
{
    private final static Logger logger = LogManager.getLogger(Main.class);
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void main(String[] args) throws ParseException, InvalidInputException
    {
        logger.info("Starting main...");
        Clock clock = new Clock();
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clock.setBounds(200, 200, 700, 300);
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.updateComponentTreeUI(clock);
        });
        try
        {
            while (true) {
                clock.tick();
                // check alarms
                sleep(250);
                clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
                clock.getTimerPanel().checkIfTimerHasConcluded();
                if (clock.isNewYear()) {
                    clock.setDaylightSavingsTimeDates();
                    clock.setIsNewYear(false);
                }
                sleep(750);
            }
        }
        catch (Exception e)
        {
            for(StackTraceElement ste : e.getStackTrace())
            { logger.error(ste); }
        }
    }
}
