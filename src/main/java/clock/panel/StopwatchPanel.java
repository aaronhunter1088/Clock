package clock.panel;

import clock.entity.Clock;
import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import static clock.panel.DisplayTimePanel.startText;
import static clock.util.Constants.*;

/**
 * Stopwatch Panel
 * <p>
 * The stopwatch panel is used to set a 'timer' that counts up.
 * You can have multiple stopwatches, each with their own name,
 * however a default name of "Sw" + the current count of
 * stopwatches + 1, will be set if no name is provided. This will
 * allow you to have multiple stopwatches for various purposes.
 * Once you click the start button, the stopwatch will begin
 * counting up until the user pauses it. (although it will
 * be stopped automatically if the stopwatch were to reach 1
 * hour). The left side of the panel will show the stopwatch's
 * time in digital or analogue format. The right side of the
 * panel will show the Laps that have been recorded. You can
 * reverse the order of the laps if you desire.
 *
 * @author michael ball
 * @version since 2.9
 */
public class StopwatchPanel extends ClockPanel
{
    private static final Logger logger = LogManager.getLogger(StopwatchPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private ClockFrame clockFrame;
    private Clock clock;
    private DisplayTimePanel displayTimePanel; // Used to display the stopwatch time in two modes
    private DisplayLapsPanel displayLapsPanel; // Used to display the laps recorded for all stopwatches
    private JButton lapButton,   // toggles to reset when stopwatch is stopped
                    startButton; // toggles to stop when stopwatch is started
    private JTextField stopwatchNameField;
    private Stopwatch currentStopwatch;

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
    private void initialize(ClockFrame clockFrame)
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
        lapButton.addActionListener(this::executeButtonAction);

        stopwatchNameField = new JTextField("Sw" + (Stopwatch.stopwatchCounter + 1), 4);
        stopwatchNameField.setFont(ClockFrame.font20);
        stopwatchNameField.setOpaque(true);
        stopwatchNameField.setName(STOPWATCH + TEXT_FIELD);
        stopwatchNameField.setBackground(Color.BLACK);
        stopwatchNameField.setForeground(Color.WHITE);
        stopwatchNameField.setBorder(new LineBorder(Color.ORANGE));
        stopwatchNameField.setToolTipText("Enter a Name");
        stopwatchNameField.addFocusListener(new FocusListener() {
           @Override
           public void focusGained(FocusEvent e) {
               if (currentStopwatch.isPaused())
               {
                   if (stopwatchNameField.getText().equals("Sw" + (Stopwatch.stopwatchCounter + 1)))
                   {
                       stopwatchNameField.setText(EMPTY);
                   }
                   updateStartButtonText();
                   logger.debug("Focus gained on name field");
               }
           }

           @Override
           public void focusLost(FocusEvent e) {
               if (stopwatchNameField.getText().isBlank())
               {
                   stopwatchNameField.setText("Sw" + (Stopwatch.stopwatchCounter + 1));
               }
               else
               {
                   updateStartButtonText();
               }
               logger.debug("Focus lost on name field");
           }
       });

        startButton = new JButton(START);
        startButton.setFont(ClockFrame.font20);
        startButton.setOpaque(true);
        startButton.setName(START + BUTTON);
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(Color.BLUE);
        startButton.addActionListener(this::executeButtonAction);

        displayTimePanel = new DisplayTimePanel(this);
        displayLapsPanel = new DisplayLapsPanel(this);
    }

    /**
     * Updates the text of the start button based on
     * whether the text field matches an existing stopwatch name
     * or not. If it matches, it shows Resume, otherwise Start.
     */
    private void updateStartButtonText()
    {
        String newTextName = stopwatchNameField.getText();
        String currentName = currentStopwatch != null ? currentStopwatch.getName() : EMPTY;
        List<String> allNames = clock.getListOfStopwatches().stream().map(Stopwatch::getName).toList();
        if (currentName.equals(newTextName))
        {
            startButton.setText(RESUME);
        }
        else if (allNames.contains(newTextName))
        {
            currentStopwatch = clock.getListOfStopwatches().stream().filter(stopwatch -> stopwatch.getName().equals(newTextName)).findFirst().get();
            startButton.setText(RESUME);
        }
        else
        {
            startButton.setText(START);
        }
    }

    /**
     * Sets up the default values for the stopwatch panel
     */
    public void setupDefaults()
    {
        logger.debug("Setting up default values for StopwatchPanel");
        setupSettingsMenu();
        setBackground(Color.BLACK);
        clockFrame.setTitle(STOPWATCH+SPACE+PANEL);
        startButton.setText(START);
        lapButton.setText(LAP);
        displayTimePanel.setClockText(currentStopwatch != null ? currentStopwatch.elapsedFormatted(currentStopwatch.getAccumMilli(), STOPWATCH_READING_FORMAT) : startText);
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
    private void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight,
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

    /** Sets up the settings menu for the stopwatch panel */
    @Override
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getShowAnalogueTimePanel());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getReverseLaps());
    }

    /** Switches the display time panel between analogue and digital mode */
    public void toggleStopwatchClockPanel()
    {
        boolean current = displayTimePanel.isShowAnaloguePanel();
        logger.debug("switching panels from {} to {}", current, !current);
        displayTimePanel.setShowAnaloguePanel(!current);
        displayTimePanel.repaint();
        displayTimePanel.revalidate();
    }

    /** Stops the stopwatch panel thread. */
    public void stop()
    {
        logger.debug("stopping stopwatch panel");
        displayTimePanel.stop();
        startButton.setText(RESUME);
        lapButton.setText(RESET);
    }

    /**
     * Executes the appropriate action depending
     * on the text of the button since this button
     * toggles between lap and reset.
     * @param e the action event
     *          Package private
     */
    void executeButtonAction(ActionEvent e)
    {
        String buttonText = ((JButton)e.getSource()).getText();
        switch (buttonText)
        {
            case START -> startStopwatch(); // sets startButton to STOP
            case RESUME -> resumeStopwatch(); // sets startButton to STOP
            case PAUSE -> pauseStopwatchPanel(); // sets startButton to RESUME
            case LAP -> recordLap();
            case RESET -> resetStopwatchPanel(); // sets startButton to START
        }
        repaint();
        revalidate();
    }

    /**
     * Starts a new stopwatch and changes
     * the start button to a stop button
     */
    void startStopwatch()
    {
        String name = stopwatchNameField.getText();
        if (currentStopwatch != null && currentStopwatch.getName().equals(name))
        {
            currentStopwatch.resumeStopwatch();
        }
        else
        {
            Stopwatch stopwatch = new Stopwatch(name, false, false, clock);
            clock.getListOfStopwatches().add(stopwatch);
            setCurrentStopwatch(stopwatch);
        }
        displayTimePanel.start();
        displayTimePanel.setClockText(currentStopwatch.elapsedFormatted(currentStopwatch.getAccumMilli(), STOPWATCH_READING_FORMAT));
        displayLapsPanel.updateLabelsAndStopwatchTable();
        startButton.setText(PAUSE);
        lapButton.setText(LAP);
        logger.info("Started new stopwatch with name: {}", name);
    }

    /** Pauses the current stopwatch */
    void pauseStopwatchPanel()
    {
        displayTimePanel.stop(); // also pauses the stopwatch
        startButton.setText(RESUME);
        lapButton.setText(RESET);
        logger.info("Paused stopwatch with name: {}", currentStopwatch.getName());
    }

    /** Resumes the current stopwatch */
    void resumeStopwatch()
    {
        String chosenName = stopwatchNameField.getText();
        // sets the current stopwatch to the chosen name if it exists otherwise currentStopwatch remains the same
        clock.getListOfStopwatches().stream().filter(stopwatch -> stopwatch.getName().equals(chosenName))
                .findFirst().ifPresent(resumeSpecificWatch -> currentStopwatch = resumeSpecificWatch);
        // because the chosenName may not be the same as the resuming stopwatch, update it there
        stopwatchNameField.setText(currentStopwatch.getName());
        displayTimePanel.start();
        currentStopwatch.resumeStopwatch();
        displayLapsPanel.updateLabelsAndStopwatchTable();
        displayTimePanel.setClockText(currentStopwatch.elapsedFormatted(currentStopwatch.getAccumMilli(), STOPWATCH_READING_FORMAT));
        startButton.setText(PAUSE);
        lapButton.setText(LAP);
        logger.info("Resuming stopwatch with name: {}", currentStopwatch.getName());
    }

    /** Records a lap for the current stopwatch */
    void recordLap()
    {
        if (currentStopwatch != null)
        {
            logger.debug("recording lap");
            currentStopwatch.recordLap();
            displayLapsPanel.updateLabelsAndStopwatchTable();
        }
        else
        {
            logger.warn("No current stopwatch to record lap for");
        }
    }

    /** Resets the stopwatch panel to its default state */
    void resetStopwatchPanel()
    {
        logger.debug("resetting stopwatch panel");
        getClock().getListOfStopwatches().forEach(Stopwatch::stopStopwatch);
        getClock().getListOfStopwatches().clear();
        Stopwatch.stopwatchCounter = 0L;
        stopwatchNameField.setText("Sw" + (Stopwatch.stopwatchCounter + 1));
        currentStopwatch = null;
        displayTimePanel.stop();
        displayTimePanel.setClockText(startText);
        displayTimePanel.repaint();
        displayLapsPanel.resetPanel();
        displayLapsPanel.setDefaultLayout();
        startButton.setText(START);
        lapButton.setText(LAP);
    }

    /** Returns the clock */
    public Clock getClock() { return clock; }
    /** Returns the clockFrame */
    public ClockFrame getClockFrame() { return clockFrame; }
    /** Returns the displayTimePanel */
    public DisplayTimePanel getDisplayTimePanel() { return displayTimePanel; }
    /** Returns the displayLapsPanel */
    public DisplayLapsPanel getDisplayLapsPanel() { return displayLapsPanel; }
    /** Returns the start button */
    public JButton getStartButton() { return startButton; }
    /** Returns the lap button */
    public JButton getLapButton() { return lapButton; }
    /** Returns the stopwatch name text field */
    public JTextField getStopwatchNameField() { return stopwatchNameField; }
    /** Returns the current stopwatch */
    public Stopwatch getCurrentStopwatch() { return currentStopwatch; }

    /** Sets the clock */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    /** Sets the clockFrame */
    public void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    /** Sets the layout manager */
    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    /** Sets the grid bag constraints */
    public void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    /** Sets the current stopwatch */
    public void setCurrentStopwatch(Stopwatch currentStopwatch) { this.currentStopwatch = currentStopwatch; logger.debug("currentStopwatch set"); }
}


