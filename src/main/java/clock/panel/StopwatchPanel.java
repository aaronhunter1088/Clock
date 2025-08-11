package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.util.Constants.*;

/**
 * Stopwatch Panel: TODO: Implement
 * <p>
 * The stopwatch panel is used to set a timer that counts up.
 * Given some Hours, some Minutes, some Seconds, it will begin
 * counting up. This will continue until the user stops it
 * (although it will be stopped automatically if the stopwatch
 * were to reach 24 hours).
 *
 * @author michael ball
 * @version 2.9
 */
public class StopwatchPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(StopwatchPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private ClockFrame clockFrame;
    private Clock clock;
    private JPanel setupStopwatchPanel;

    /**
     * Main constructor for creating the StopwatchPanel
     * @param clockFrame the clockFrame object reference
     */
    public StopwatchPanel(ClockFrame clockFrame)
    {
        super();
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        this.layout = new GridBagLayout();
        setLayout(layout);
        this.constraints = new GridBagConstraints();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        clockFrame.setTitle(STOPWATCH+SPACE+PANEL);
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating StopwatchPanel");
    }

    public void addComponentsToPanel() {
        // Implement
    }

    @Override
    public void setupSettingsMenu() {
        // Implement
    }

    @Override
    public void printStackTrace(Exception e, String message) {

    }

    @Override
    public void run() {

    }

    @Override
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    public void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    public void setStopwatchLayout(GridBagLayout gridBagLayout) { this.layout = gridBagLayout; logger.debug("constraints set"); }
    public void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }

    public Clock getClock() { return clock; }
    public ClockFrame getClockFrame() { return clockFrame; }
    public GridBagLayout getStopwatchLayout() { return layout; }
    public GridBagConstraints getGridBagConstraints() { return constraints; }

}
