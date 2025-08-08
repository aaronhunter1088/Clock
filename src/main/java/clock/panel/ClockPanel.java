package clock.panel;

import clock.contract.IClockPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract class for clock panels, implementing the IClockPanel interface.
 * <p>
 * @author Michael Ball
 * @version since 2.9
 */
public abstract class ClockPanel extends JPanel implements IClockPanel {

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
}
