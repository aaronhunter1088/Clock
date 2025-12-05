package clock.panel;

import clock.contract.IClockPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract class for clock panels, implementing the IClockPanel interface.
 * <p>
 * @author michael ball
 * @version since 2.9
 */
public abstract class ClockPanel extends JPanel implements IClockPanel
{
    private static final Logger logger = LogManager.getLogger(ClockPanel.class);

    /**
     * Displays a popup message with the given title and message.
     * @param title the title of the popup message
     * @param message the message to be displayed in the popup
     * @param optionPane the JOptionPane used to display the message
     *        examples: ERROR(0), INFO(1), WARNING(2), QUESTION(3), PLAIN(4)
     */
    public void displayPopupMessage(String title, String message, int optionPane) {
        Window window = SwingUtilities.getWindowAncestor(this);
        int jOptionPane = switch (optionPane) {
            case 0 -> JOptionPane.ERROR_MESSAGE; // ERROR
            case 1 -> JOptionPane.INFORMATION_MESSAGE; // INFO
            case 2 -> JOptionPane.WARNING_MESSAGE; // WARNING
            case 3 -> JOptionPane.QUESTION_MESSAGE; // QUESTION
            default -> JOptionPane.PLAIN_MESSAGE; // PLAIN
        };
        JOptionPane.showMessageDialog(
                window,
                message,
                title,
                jOptionPane);
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     * @param message the message to print
     */
    @Override
    public void printStackTrace(Exception e, String message)
    {
        if (null != message) logger.error(message);
        else logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        { logger.error(ste.toString()); }
    }

}
