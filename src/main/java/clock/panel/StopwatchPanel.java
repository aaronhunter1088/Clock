package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

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
    private int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;
    private ClockFrame clockFrame;
    private Clock clock;
    private String clockText = EMPTY;
    // Used to display the stopwatch time in two modes
    private JPanel displayTimePanel,
                   displayLapsPanel;
                   //lapsPanel;
                   //stopwatchesPanel;
    private JButton lapButton, // toggles to reset when stopwatch is stopped
                    startButton; // toggles to stop when stopwatch is started
    private JTextField stopwatchNameField;
    private boolean showAnaloguePanel;

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
        setMaximumSize(ClockFrame.clockDefaultSize);
        setPreferredSize(ClockFrame.clockDefaultSize);
        setGridBagLayout(new GridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setupComponents();
        setupDefaults();
        addComponentsToPanel();
        logger.info("Finished initializing StopwatchPanel");
    }

    /**
     * Sets up the components for the stopwatch panel
     */
    private void setupComponents()
    {
        logger.debug("Setting up components for StopwatchPanel");
        lapButton = new JButton(LAP);
        lapButton.setFont(ClockFrame.font20);
        lapButton.setOpaque(true);
        lapButton.setName(LAP + BUTTON);
        lapButton.setBackground(Color.BLACK);
        lapButton.setForeground(Color.BLUE);

        stopwatchNameField = new JTextField("Name", 4);
        stopwatchNameField.setFont(ClockFrame.font20);
        stopwatchNameField.setOpaque(true);
        stopwatchNameField.setName(STOPWATCH + TEXT_FIELD);
        stopwatchNameField.setBackground(Color.BLACK);
        stopwatchNameField.setForeground(Color.WHITE);
        stopwatchNameField.setBorder(new LineBorder(Color.ORANGE));
        stopwatchNameField.setToolTipText("Enter Stopwatch Name");

        startButton = new JButton(START);
        startButton.setFont(ClockFrame.font20);
        startButton.setOpaque(true);
        startButton.setName(START + BUTTON);
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(Color.BLUE);

        createDisplayTimePanel();
        // Laps
        createLapsPanel();
    }

    /**
     * Sets up the default values for the stopwatch panel
     */
    public void setupDefaults()
    {
        logger.debug("Setting up default values for StopwatchPanel");
        setupSettingsMenu();
        setBackground(Color.BLACK);
        //setForeground(Color.WHITE);
        clockFrame.setTitle(STOPWATCH+SPACE+PANEL);
        //setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * Add components to the panels as needed
     */
    private void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        // First row: displayTimePanel (col 0-2), displayLapsPanel (col 3-5)
        addComponent(displayTimePanel, 0, 0, 3, 1, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(displayLapsPanel, 0, 3, 3, 2, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(0,0,0,0));

        // Second row: lapButton, stopwatchNameField, startButton (col 0,1,2)
        addComponent(lapButton, 1, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
addComponent(stopwatchNameField, 1, 1, 1, 1, 0, 0, 1, 0, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0));
        addComponent(startButton, 1, 2, 1, 1, 0, 0, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        revalidate();
        repaint();
    }

    /**
     * The main method used for adding
     * components to the setup timer panel
     * @param cpt       the component to add
     * @param gridy     the y position or the row
     * @param gridx     the x position or the column
     * @param gwidth    the width how many columns it takes up
     * @param gheight   the height how many rows it takes up
     * @param ipadx     the x padding
     * @param ipady     the y padding
     * @param weightx   the x weight
     * @param weighty   the y weight
     * @param fill      the fill
     * @param insets    the insets
     */
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight,
                             int ipadx, int ipady, double weightx, double weighty, int fill, Insets insets)
    {
        logger.debug("add component");
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = (int)Math.ceil(gwidth);
        constraints.gridheight = (int)Math.ceil(gheight);
        constraints.fill = fill;
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        constraints.insets = insets;
        constraints.weightx = Math.max(weightx, 0);
        constraints.weighty = Math.max(weighty, 0);
        layout.setConstraints(cpt,constraints);
        add(cpt);
    }

    @Override
    public void setupSettingsMenu()
    {
        // Implement
        clockFrame.clearSettingsMenu();
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getShowAnalogueTimePanel());
    }

    private void createDisplayTimePanel()
    {
        displayTimePanel = new DisplayTimePanel();
        // Set layouts and properties for each panel
    }

    private void createLapsPanel()
    {
        displayLapsPanel = new DisplayLapsPanel();
        // Set layouts and properties for each panel
    }

    public void switchPanels()
    {
        boolean current = ((DisplayTimePanel)clockFrame.getStopwatchPanel().getDisplayTimePanel()).isShowAnaloguePanel();
        logger.debug("switching panels from {} to {}", current, !current);
        ((DisplayTimePanel)clockFrame.getStopwatchPanel().getDisplayTimePanel()).setShowAnaloguePanel(!current);
    }

    /**
     * Starts the analogue clock panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting stopwatch panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the timer panel thread. */
    public void stop()
    {
        logger.debug("stopping stopwatch panel");
        thread = null;
        ((DisplayTimePanel)displayTimePanel).stop();
        ((DisplayLapsPanel)displayLapsPanel).stop();
    }

    /** Repaints the stopwatch panel */
    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                if ( ((DisplayTimePanel)displayTimePanel).thread == null) ((DisplayTimePanel)displayTimePanel).start();
                if ( ((DisplayLapsPanel)displayLapsPanel).thread == null) ((DisplayLapsPanel)displayLapsPanel).start();
                sleep(1000);
            }
            catch (InterruptedException e)
            { printStackTrace(e, e.getMessage()); }
        }
    }

    @Override
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    public void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    public void setStopwatchLayout(GridBagLayout gridBagLayout) { this.layout = gridBagLayout; logger.debug("constraints set"); }
    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    public void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    public void setShowAnaloguePanel(boolean showAnaloguePanel) { this.showAnaloguePanel = showAnaloguePanel; logger.debug("showAnaloguePanel set to {}", showAnaloguePanel); }

    public Clock getClock() { return clock; }
    public ClockFrame getClockFrame() { return clockFrame; }
    public GridBagLayout getStopwatchLayout() { return layout; }
    public GridBagConstraints getGridBagConstraints() { return constraints; }
    public JPanel getDisplayTimePanel() { return displayTimePanel; }
    public JPanel getDisplayLapsPanel() { return displayLapsPanel; }
    public boolean isShowAnaloguePanel() { return showAnaloguePanel;  }

}


