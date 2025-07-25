package clock.panel;

import clock.contract.IClockPanel;
import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.panel.ClockPanel.PANEL_STOPWATCH;
import static clock.panel.ClockPanel.PANEL_TIMER2;

/**
 * This is the Stopwatch panel.
 * The stopwatch panel is used to set a countdown timer.
 * Given some Hours, some Minutes, some Seconds, it will
 * begin counting down. When the timer reaches '00:00:00',
 * it will make a short alarm sound, then turn off.
 *
 * @author michael ball
 * @version 2.9
 */
public class StopwatchPanel extends JPanel implements IClockPanel
{
    private static final Logger logger = LogManager.getLogger(StopwatchPanel.class);
    private final GridBagLayout layout;
    private final GridBagConstraints constraints;
    private JTextArea textArea;
    private Clock clock;
    private JPanel setupStopwatchPanel;

    /**
     * Main constructor for creating the StopwatchPanel
     * @param clock the clock object reference
     */
    public StopwatchPanel(Clock clock)
    {
        super();
        setClock(clock);
        this.clock.setClockPanel(PANEL_STOPWATCH);
        setSize(Clock.panelSize);
        this.layout = new GridBagLayout();
        setLayout(layout);
        this.constraints = new GridBagConstraints();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating StopwatchPanel");
    }

    @Override
    public void addComponentsToPanel() {

    }

    @Override
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void setupSettingsMenu() {

    }

    @Override
    public void printStackTrace(Exception e, String message) {

    }
}
