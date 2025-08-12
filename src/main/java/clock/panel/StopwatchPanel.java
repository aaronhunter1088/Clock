package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.util.Constants.*;

/**
 * Stopwatch Panel
 * <p>
 * The stopwatch panel is used to set a 'timer' that counts up.
 * You can have multiple stopwatches, each with their own name,
 * however a default name of "Stopwatch" + the current count of
 * stopwatches + 1, will be set if no name is provided. This will
 * allow you to have multiple stopwatches for various purposes.
 * Once you click the start button, the stopwatch will begin
 * counting up until the user stops it. (although it will
 * be stopped automatically if the stopwatch were to reach 24
 * hours). The left side of the panel will show the stopwatch's
 * time in digital and analogue format, allowing you to choose
 * which mode to view the time in. The right side of the panel
 * will show the Laps that have been recorded, and all the stop
 * watches that have been created.
 *
 * @author michael ball
 * @version 2.9
 */
public class StopwatchPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(StopwatchPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread;
    private ClockFrame clockFrame;
    private Clock clock;
    // Used to display the stopwatch time in two modes
    private JPanel analogueTimePanel,
                   digitalTimePanel;

    /**
     * Main constructor for creating the StopwatchPanel
     * @param clockFrame the clockFrame object reference
     */
    public StopwatchPanel(ClockFrame clockFrame)
    {
        super();
        initialize(clockFrame);
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating StopwatchPanel");
    }

    /**
     * Sets up the default actions for the analogue clock panel
     * @param clockFrame the clockFrame reference
     */
    public void initialize(ClockFrame clockFrame)
    {
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setGridBagLayout(new GridBagLayout());
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        clockFrame.setTitle(STOPWATCH+SPACE+PANEL);
        addComponentsToPanel();
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
    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; logger.debug("GridBagLayout set"); }
    public void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }

    public Clock getClock() { return clock; }
    public ClockFrame getClockFrame() { return clockFrame; }
    public GridBagLayout getStopwatchLayout() { return layout; }
    public GridBagConstraints getGridBagConstraints() { return constraints; }

}
